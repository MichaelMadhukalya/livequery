#include <stdio.h>
#include <stdlib.h>
#include <errno.h>
#include <poll.h>
#include <sys/inotify.h>
#include <unistd.h>

#ifndef MAX_BUFFER_SIZE
    #define MAX_BUFFER_SIZE 4096
#endif

/**
 * Handle events from inotify file descriptor (fd). The watch descriptor (wd)
 * indicates whether emitted events are related to the watched dir that we
 * are listening for events. The third argument path specifies the watched
 * dir path. Each successful read retuns a buffer that contains items having
 * the following structure:
 *
 * struct inotify_event {
 *     int wd;          // watch descriptor
 *     uint32_t mask;   // Mask describing event
 *     uint32_t cookie; // Unique cookie associating related events (rename(2))
 *     uint32_t len;    // Size of name field
 *     char name[];     // Optional null-terminated name
 * }
 *
 * The last argument of the above structure indicates the name of the file
 * inside the watched dir for which the watched event was emitted. For events
 * that are only related to a dir this argument will be empty.
 *
 * More details about inotify events can be found here:
 * http://man7.org/linux/man-pages/man7/inotify.7.html
 */
void handle (int fd, int *wd, const char *path)
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

int main (int argc, char *argv[])
{
    /* Only a single dir will be watched */
    int num_watched = 1;

    int *wd = malloc (sizeof (int) * num_watched);
    nfds_t nfds = num_watched;
    struct pollfd fds[num_watched];

    /* Create inotify instnace */
    int fd = inotify_init1 (IN_NONBLOCK);
    if (fd == -1)
    {
        perror ("Error initialization inotify instance");
        exit (EXIT_FAILURE);
    }

    /* Check args */
    if (argc > 2)
    {
        perror ("Only a single dir can be watched");
        exit (EXIT_FAILURE);
    }

    /* Add watch descriptors with dir name to event mapping */
    *wd = inotify_add_watch (fd, argv[1],
                             IN_ACCESS |
                             IN_ATTRIB |
                             IN_CLOSE_WRITE |
                             IN_CLOSE_NOWRITE |
                             IN_CREATE |
                             IN_DELETE |
                             IN_DELETE_SELF |
                             IN_MODIFY |
                             IN_MOVE_SELF |
                             IN_MOVED_FROM | IN_MOVED_TO | IN_OPEN);

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

    exit(EXIT_SUCCESS);
}


