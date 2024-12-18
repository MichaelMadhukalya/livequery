#include <jni.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdbool.h>
#include <errno.h>
#include <poll.h>
#include <sys/inotify.h>
#include <unistd.h>

#include "com_livequery_agent_filesystem_core_FileChangeProcessor.h"
#include "file_event.h"

#ifndef MAX_BUFFER_SIZE
  #define MAX_BUFFER_SIZE 4096
#endif

#ifndef MAX_EVENT_SIZE
  #define MAX_EVENT_SIZE 10
#endif

#ifndef NUM_WATCHED_DIR
  #define NUM_WATCHED_DIR 1
#endif

#ifndef SPACE_CHAR
  #define SPACE_CHAR ' '
#endif

/* initialization */
static bool is_init = true;

/* Watched directory path */
static char path [4096];

/* File descriptor and watched descriptor for inotify */
static int fd;
static int* wd;

/* Additional inotify structures */
static nfds_t nfds = NUM_WATCHED_DIR;
static struct pollfd fds[NUM_WATCHED_DIR];

/* Align buffer */
char buffer[MAX_BUFFER_SIZE]
__attribute__((aligned (__alignof__ (struct inotify_event))));

/* File event names */
static char* file_event_names [] =
{
    "IN_ACCESS",
    "IN_ATTRIB",
    "IN_CLOSE_NOWRITE",
    "IN_CLOSE_WRITE",
    "IN_CREATE",
    "IN_DELETE",
    "IN_DELETE_SELF",
    "IN_IGNORED",
    "IN_ISDIR",
    "IN_MODIFY",
    "IN_MOVE_SELF",
    "IN_MOVED_FROM",
    "IN_MOVED_TO",
    "IN_OPEN",
    "IN_Q_OVERFLOW",
    "IN_UNMOUNT"
};

char* get_event_name(uint32_t mask)
{
    int b = 0;

    if (mask & IN_ACCESS) b = 0;
    else if (mask & IN_ATTRIB) b = 1;
    else if (mask & IN_CLOSE_NOWRITE) b = 2;
    else if (mask & IN_CLOSE_WRITE) b = 3;
    else if (mask & IN_CREATE) b = 4;
    else if (mask & IN_DELETE) b = 5;
    else if (mask & IN_DELETE_SELF) b = 6;
    else if (mask & IN_IGNORED) b = 7;
    else if (mask & IN_ISDIR) b = 8;
    else if (mask & IN_MODIFY) b = 9;
    else if (mask & IN_MOVE_SELF) b = 10;
    else if (mask & IN_MOVED_FROM) b = 11;
    else if (mask & IN_MOVED_TO) b = 12;
    else if (mask & IN_OPEN) b = 13;
    else if (mask & IN_Q_OVERFLOW) b = 14;
    else if (mask & IN_UNMOUNT) b = 15;

    return file_event_names[b];
}

void flush_buffer()
{
    for(int i = 0; i < MAX_BUFFER_SIZE; i++)
    {
      path[i] = SPACE_CHAR;
    }
}

void call_Java_producer(JNIEnv* env, jobject thisObj, jobjectArray objArr)
{
    jclass cls = (*env)->FindClass(env, "com/livequery/agent/filesystem/core/FileChangeProcessor");
    jmethodID methodId = (*env)->GetMethodID(env, cls, "produce", "([Ljava/lang/Object;)V");
    if (methodId == NULL)
    {
        perror("Unable to get method reference for object");
        exit(EXIT_FAILURE);
    }

    /* Java producer method invocation */
    (*env)->CallVoidMethod(env, thisObj, methodId, objArr);
}

void handle (JNIEnv* env, jobject thisObj, struct pollfd* fds, int* wd)
{
    const struct inotify_event *event;

    ssize_t len;
    char *ptr;

    /* Create object array and initialize */
    jclass cls = (*env)->FindClass(env, "com/livequery/agent/filesystem/core/FileEvent");
    jobjectArray objArr = (*env)->NewObjectArray(env, (jsize) MAX_EVENT_SIZE, cls, NULL);

    int i = 0;
    for (;;)
    {
        /* Read events */
        len = read (fds[0].fd, buffer, MAX_BUFFER_SIZE);

        if (len == -1 && errno != EAGAIN)
        {
            perror ("Error reading inotify instances for events");
            exit (EXIT_FAILURE);
        }

        /* No events */
        if (len <= 0)
            break;

        for (ptr = buffer; ptr < buffer + len;)
        {
            if(i >= MAX_EVENT_SIZE)
            {
                call_Java_producer(env, thisObj, objArr);
                return;
            }

            event = (const struct inotify_event *) ptr;
            if (event->wd == *wd)
            {
                jmethodID methodId = (*env)->GetMethodID(env, cls, "<init>", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V");
                if(methodId == NULL)
                {
                    perror("Unable to get constructor reference for creating file change events");
                    exit (EXIT_FAILURE);
                }

                jobject object = (*env)->NewObject( env,
                                                    cls,
                                                    methodId,
                                                    (*env)->NewStringUTF(env, path),
                                                    (*env)->NewStringUTF(env, event->name),
                                                    (*env)->NewStringUTF(env, get_event_name(event->mask)));

                (*env)->SetObjectArrayElement(env, objArr, i++, object);
            }

            ptr += sizeof (struct inotify_event *) + event->len;
        }

        call_Java_producer(env, thisObj, objArr);
        break;
    }
}

void init(JNIEnv* env, jobject thisObj)
{
    /* If already initialized then return */
    if(is_init == false)
        return;

    jclass cls = (*env)->GetObjectClass(env, thisObj);
    jmethodID methodId = (*env)->GetMethodID(env, cls, "getFileName", "()Ljava/lang/String;");
    if (methodId == NULL)
    {
        perror("Unable to get method reference for class");
        exit(EXIT_FAILURE);
    }

    /* Convert to C style string and copy path */
    jstring dir_name = (*env)->CallObjectMethod(env, thisObj, methodId);
    const char* _path = (*env)->GetStringUTFChars(env, dir_name, NULL);
    if (_path == NULL)
    {
        perror("Unable to get watched dir name, exiting poller.");
        exit(EXIT_FAILURE);
    } else
    {
        strcpy(path, _path);
    }

    /* Create inotify instnace */
    int fd = inotify_init1 (IN_NONBLOCK);
    if (fd == -1)
    {
        perror ("Error initialization inotify instance");
        exit (EXIT_FAILURE);
    }

    /* Add watch descriptors with dir name to event mapping */
    wd = malloc(sizeof(int) * NUM_WATCHED_DIR);
    *wd = inotify_add_watch (fd, path,
                             IN_ACCESS |
                             IN_ATTRIB |
                             IN_CLOSE_WRITE |
                             IN_CLOSE_NOWRITE |
                             IN_CREATE |
                             IN_DELETE |
                             IN_DELETE_SELF |
                             IN_MODIFY |
                             IN_MOVE_SELF |
                             IN_MOVED_FROM |
                             IN_MOVED_TO |
                             IN_OPEN |
                             IN_Q_OVERFLOW |
                             IN_UNMOUNT);

    /* Create file descriptors for polling */
    fds[0].fd = fd;
    fds[0].events = POLLIN;

    /* init */
    is_init = false;
}

JNIEXPORT void JNICALL Java_com_livequery_agent_filesystem_core_FileChangeProcessor_dpoll
(JNIEnv* env, jobject thisObj)
{
    /* init */
    init(env, thisObj);

    /* start polling */
    int poll_num = poll (fds, nfds, -1);

    if (poll_num == -1)
    {
        if (errno == EINTR)
        {
            perror ("Error while polling for inotify instance");
            exit (EXIT_FAILURE);
        }
    }

    if (poll_num > 0)
    {
        if (fds[0].revents & POLLIN)
        {
            handle (env, thisObj, fds, wd);
        }
    }
}


