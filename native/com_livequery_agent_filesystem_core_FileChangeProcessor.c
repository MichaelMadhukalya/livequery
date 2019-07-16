#include <jni.h>
#include <stdio.h>
#include "com_livequery_agent_filesystem_core_FileChangeProcessor.h"
#include "file_event.h"

static char* path = NULL;
static int* wd = malloc(sizeof(int));

void handle (int fd, int *wd)
{
    /* Align buffer */
    char buffer[MAX_BUFFER_SIZE]
    __attribute__((aligned (__alignof__ (struct inotify_event))));

    const struct inotify_event *event;

    ssize_t len;
    char *ptr;

    for (;;)
    {
        /* Read events */
        len = read (fd, buffer, MAX_BUFFER_SIZE);
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
            event = (const struct inotify_event *) ptr;
            if (event->wd == *wd)
            {
                if(event->name)
                {
                    /* File event */
                    fprintf (stdout,
                        "File %s event %d\n", event->name, event->mask);
                }
                else
                {
                    /* Dir event */
                    fprintf(stdout,
                        "Dir %s event %d\n",path, event->mask);
                }
            }

            ptr += sizeof (struct inotify_event *) + event->len;
        }
    }
}

void init(JNIEnv* env, jobject thisObj)
{
    /* If path is set then return */
    if(path != NULL)
       return;

    jclass thisClass = (*env)->GetObjectClass(env, thisObj);
    jmethodID getFileName = (*env)->GetMethodID(env, thisClass, "getFileName", "()Ljava/lang/String;");

    /* Exit if unable to get method reference */
    if (NULL == getFileName)
    {
        perror("Unable to execute object method from JNI function");
        exit(EXIT_FAILURE);
    }

    jstring d_name = (*env)->CallStringMethod(env, thisObj, getFileName);

    /* Covert to C style string */
    const char *path = (*env)->GetStringUTFChars(env, resultJNIStr, NULL);
    if (path == NULL)
    {
        perror("Unable to get watched dir name");
        exit(EXIT_FAILURE);
    }
}

void call_thisObj_method()
{
}

void release_resource()
{
}

JNIEXPORT void JNICALL Java_com_livequery_agent_filesystem_core_FileChangeProcessor_dpoll
(JNIEnv* env, jobject thisObj)
{
    nfds_t nfds = 1;
    struct pollfd fds[num_watched];

    /* Call Init */
    init();

    /* Create inotify instnace */
    int fd = inotify_init1 (IN_NONBLOCK);
    if (fd == -1)
    {
        perror ("Error initialization inotify instance");
        exit (EXIT_FAILURE);
    }

    /* Add watch descriptors with dir name to event mapping */
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
			     IN_OPEN);

    /* Create file descriptors for polling */
    fds[0].fd = fd;
    fds[0].events = POLLIN;

    while (1)
    {
        /* Start polling for events */
        int poll_num = poll (fds, nfds, -1);

        if (poll_num == -1)
        {
            if (errno == EINTR)
                continue;
            perror ("Polling error while polling inotify instance");
            exit (EXIT_FAILURE);
        }
        
	if (poll_num > 0)
        {
            if (fds[0].revents & POLLIN)
            {
                /* Handle new set of events for watched descriptors */
                handle (fd, wd, argv[1]);
            }
        }
    }
}


