#include <stdio.h>
#include <stdbool.h>

/* Max events */
#ifndef MAX_NUM_EVENTS
   #define MAX_NUM_EVENTS 500
#endif

/* Attribute lengths */
#define MAX_PATH_LENGTH 4096
#define MAX_FILE_LENGTH 255
#define MAX_EVENT_NAME_LENGTH 32

/* File events */
struct file_event
{
   char path [MAX_PATH_LENGTH];
   char file_name [MAX_FILE_LENGTH];
   char event_name [MAX_EVENT_NAME_LENGTH];
   unsigned cookie;
   bool is_path_event;
   bool is_file_event;
};

static struct file_event file_events[MAX_NUM_EVENTS];


