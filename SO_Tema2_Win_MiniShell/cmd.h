/**
 * Operating Sytems 2013-2017 - Assignment 2
 *
 * Groapa Mihai, 334 CA
 *
 */

#ifndef _CMD_H
#define _CMD_H
#include <windows.h>

#include "parser.h"

#define SHELL_EXIT -100

typedef struct {
	command_t *cmd;
	int level;
	command_t *father;
	HANDLE *hIn;
	HANDLE *hOut;
} thread_parameters;

/**
 * Indeplineste rol de constructor pentru thread_parameters
 */
thread_parameters *CreateParam(command_t *, int, command_t *,
	HANDLE *, HANDLE *);

/**
 * Indeplineste rol de destructor pentru thread_parameters
 */
void DestroyParam(thread_parameters *);

/**
 * Parse and execute a command.
 */
int parse_command(command_t *, int, command_t *, HANDLE *, HANDLE *);

#endif /* _CMD_H */
