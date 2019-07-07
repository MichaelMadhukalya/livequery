#include <stdio.h>
#include <stdlib.h>
#include <errno.h>
#include <poll.h>
#include <sys/inotify.h>
#include <unistd.h>

void handle (int fd, int *wd, const char *name)
{
    /* Align buffer */
    char buffer[4096]
    __attribute__((aligned (__alignof__ (struct inotify_event))));

    const struct inotify_event *event;

    ssize_t len;
    char *ptr;

    for (;;)
    {
        /* Read events */
        len = read (fd, buffer, 4096);
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
            if ((event->wd == *wd) && (event->mask & IN_MODIFY))
            {
                fprintf (stdout, "File %s modified", name);
            }

            ptr += sizeof (struct inotify_event *) + event->len;
        }
    }
}

int main (int argc, char *argv[])
{
    fprintf (stdout, "start");
    /* Only a single dir/file will be watched */
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
        perror ("Only a single dir/file can be watched");
        exit (EXIT_FAILURE);
    }

    fprintf (stdout, "hi");

    /* Add watch descriptors with dir/file name to event mapping */
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

    fprintf (stdout, "here\n");

    while (1)
    {
        /* Start polling for events */
        fprintf (stdout, "hello\n");
        int poll_num = poll (fds, nfds, -1);
        fprintf (stdout, "%d", poll_num);

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

    return 0;
}


