#include <stdio.h>
#include <stdbool.h>

/* Max events */
#ifndef MAX_NUM_EVENTS
   #define MAX_NUM_EVENTS 500
#endif

/* File events */
struct file_event
{
   char path [4096];
   char file_name [255];
   char* event_name;
   unsigned cookie;
   bool is_path_event;
   bool is_file_event;
};

static struct file_event file_events[MAX_NUM_EVENTS];

