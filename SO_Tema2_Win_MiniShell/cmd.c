/**
 * Operating Systems 2013-2017 - Assignment 2
 *
 * Groapa Mihai, 334 CA
 *
 */

#include <windows.h>
#include <assert.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include "cmd.h"
#include "utils.h"

#undef _UNICODE
#undef UNICODE

/**
 * Obiectul de tip 'thread_parameters' este folosit de catre
 * thread-urile create la comenzi de executie paralela
 */
thread_parameters *CreateParam(command_t *c, int level, command_t *father,
	HANDLE *hIn, HANDLE *hOut)
{
	thread_parameters *param = malloc(sizeof(thread_parameters));

	param->cmd = c;
	param->level = level;
	param->father = father;
	param->hIn = hIn;
	param->hOut = hOut;

	return param;
}

void DestroyParam(thread_parameters *param)
{
	free(param);
}

/**
 * Aceasta functie este executata de thread-urile
 * create la executia unei comenzi in paralel.
 */
DWORD WINAPI thread_routine(LPVOID lpParameter)
{
	thread_parameters *param = (thread_parameters *) lpParameter;
	int feedback;

	feedback = parse_command(param->cmd, param->level, param->father,
		param->hIn, param->hOut);

	if (feedback == -1)
		return false;
	return true;
}

/**
 * Actiunea de exit/quit.
 */
static int shell_exit(void)
{
	return SHELL_EXIT;
}

/**
 * Setare variabile de mediu.
 * Return value : 0 daca variabila a fost setata
 * cu succes, -1 daca instructiunea nu presupune
 * o astfel de operatie.
 */
static int set_env_variable(simple_command_t *s)
{
	int feedback;
	const char *var_name;
	const char *equal;
	const char *value;

	if (s->verb->next_part == NULL)
		return -1;

	var_name = s->verb->string;
	equal = s->verb->next_part->string;
	value = s->verb->next_part->next_part->string;

	if (strcmp(equal, "=") == 0) {
		feedback = SetEnvironmentVariable(var_name, value);
		DIE(feedback == 0, "Environment variable failure\n");
		return 0;
	}

	return -1;
}

void CloseProcess(LPPROCESS_INFORMATION lppi)
{
	CloseHandle(lppi->hThread);
	CloseHandle(lppi->hProcess);
}

/**
 * Intoarce un handle la fisier.
 */
static HANDLE MyOpenFile(PCSTR filename, DWORD access, DWORD creation_disp)
{
	SECURITY_ATTRIBUTES sa;

	ZeroMemory(&sa, sizeof(sa));
	sa.bInheritHandle = TRUE;

	return CreateFile(
		filename,
		access,
		FILE_SHARE_READ | FILE_SHARE_WRITE,
		&sa,
		creation_disp,
		FILE_ATTRIBUTE_NORMAL,
		NULL
	);
}

/**
 * Redirectare handlere standard.
 */
static void RedirectHandle(STARTUPINFO *psi, HANDLE hFile, INT opt)
{
	if (hFile == INVALID_HANDLE_VALUE)
		return;

	switch (opt) {
	case STD_INPUT_HANDLE:
		psi->hStdInput = hFile;
		break;
	case STD_OUTPUT_HANDLE:
		psi->hStdOutput = hFile;
		break;
	case STD_ERROR_HANDLE:
		psi->hStdError = hFile;
		break;
	}
}

/**
 * Schimbare director curent.
 * Return value : -1 daca nu s-a putut schimba directorul
 * 0 in caz de succes.
 */
static bool shell_cd(simple_command_t *s)
{
	bool feedback;
	char *word = get_word(s->params);

	feedback = SetCurrentDirectory(word);
	free(word);

	return feedback;
}

/**
 * Handle pentru STDIN
 */
static void in_handle(simple_command_t *s, HANDLE *hIn)
{
	*hIn = MyOpenFile(get_word(s->in), GENERIC_READ, OPEN_EXISTING);
}

/**
 * Handle pentru STDOUT + setare cursor
 * la sfarsit de fisier la modul APPEND
 */
static void out_handle(simple_command_t *s, HANDLE *hOut, DWORD disp_out)
{
	*hOut = MyOpenFile(get_word(s->out), GENERIC_WRITE, disp_out);
	if (disp_out == OPEN_ALWAYS)
		SetFilePointer(*hOut, 0, NULL, FILE_END);
}

/**
 * Handle pentru STDERR + setare cursor
 * la sfarsit de fisier la modul APPEND
 */
static void err_handle(simple_command_t *s, HANDLE *hErr, DWORD disp_err)
{
	*hErr = MyOpenFile(get_word(s->err), GENERIC_WRITE, disp_err);
	if (disp_err == OPEN_ALWAYS)
		SetFilePointer(*hErr, 0, NULL, FILE_END);
}

/**
 * Parseaza, executa si intoarce rezultatul
 * comenzilor simple.
 */
static int parse_simple(simple_command_t *s, int level, command_t *father,
	HANDLE *hRead, HANDLE *hWrite)
{
	char *word;

	STARTUPINFO si;
	PROCESS_INFORMATION pi;
	DWORD word_feed;
	BOOL bool_feed;
	HANDLE hIn, hOut, hErr;
	DWORD disp_out = CREATE_ALWAYS, disp_err = CREATE_ALWAYS;
	BOOL use_hIn = false, use_hOut = false, use_hErr = false;

	if (s->verb == NULL)
		return -1;

	ZeroMemory(&si, sizeof(si));
	si.cb = sizeof(si);
	si.dwFlags |= STARTF_USESTDHANDLES;
	ZeroMemory(&pi, sizeof(pi));

	hIn = GetStdHandle(STD_INPUT_HANDLE);
	hOut = GetStdHandle(STD_OUTPUT_HANDLE);
	hErr = GetStdHandle(STD_ERROR_HANDLE);

	if (hRead != NULL)
		hIn = *hRead;

	if (hWrite != NULL)
		hOut = *hWrite;

	word = get_word(s->verb);

	/**
	 * Parasire shell.
	 */
	if ((strcmp(word, "quit") == 0) || strcmp(word, "exit") == 0) {
		free(word);
		return shell_exit();
	}

	/**
	 * Schimbare director de lucru.
	 */
	if (strcmp(word, "cd") == 0) {
		free(word);
		if (!shell_cd(s))
			return -1;
		return 0;
	}

	/**
	 * Setare variabile de mediu (daca e cazul).
	 */
	if (set_env_variable(s) == 0) {
		free(word);
		return 0;
	}

	if (s->io_flags & IO_OUT_APPEND)
		disp_out = OPEN_ALWAYS;

	if (s->io_flags & IO_ERR_APPEND)
		disp_err = OPEN_ALWAYS;

	/**
	 * Obtinere handle STDIN
	 */
	if (s->in != NULL) {
		in_handle(s, &hIn);
		use_hIn = true;
	}

	/**
	 * Obtinere handles STDOUT si STDERR
	 */
	if (s->out == NULL && s->err != NULL) {
		err_handle(s, &hErr, disp_err);
		use_hErr = true;
	} else if (s->out != NULL && s->err == NULL) {
		out_handle(s, &hOut, disp_out);
		use_hOut = true;
	} else if (s->out != NULL && s->err != NULL) {
		out_handle(s, &hOut, disp_out);
		use_hOut = true;
		if (strcmp(get_word(s->out), get_word(s->err)) != 0) {
			err_handle(s, &hErr, disp_err);
			use_hErr = true;
		} else
			hErr = hOut;
	}

	/**
	 * Redirectare HANDLES
	 */
	RedirectHandle(&si, hIn, STD_INPUT_HANDLE);
	RedirectHandle(&si, hOut, STD_OUTPUT_HANDLE);
	RedirectHandle(&si, hErr, STD_ERROR_HANDLE);

	
	/**
	 * Creare proces copil pentru executie comanda.
	 */
	bool_feed = CreateProcess(NULL, get_argv(s), NULL, NULL, TRUE,
						 0, NULL, NULL, &si, &pi);

	/**
	 * Comanda inexistenta sau executie esuata.
	 */
	if (!bool_feed) {
		fprintf(stderr, "Execution failed for '%s'\n", word);
		free(word);
		fflush(stderr);
		return -1;
	}

	/**
	 * Asteptare terminare proces, verificare corectitudine
	 * incheiere + inchidere.
	 */
	word_feed = WaitForSingleObject(pi.hProcess, INFINITE);
	DIE(word_feed == WAIT_FAILED, "WaitForSingleObject");
	bool_feed = GetExitCodeProcess(pi.hProcess, &word_feed);
	DIE(bool_feed == 0, "GetExitCodeProcess");
	CloseProcess(&pi);
	free(word);

	/**
	 * Inchidere handlere existente.
	 */
	if (use_hIn)
		CloseHandle(hIn);
	if (use_hOut)
		CloseHandle(hOut);
	if (use_hErr)
		CloseHandle(hErr);

	if (word_feed)
		return -1;

	return 0;
}

/**
 * Creeaza doua procese copil pentru a executa in paralel
 * cmd1 si cmd2.
 */
static int do_in_parallel(command_t *cmd1, command_t *cmd2, int level,
	command_t *father, HANDLE *hIn, HANDLE *hOut)
{
	DWORD feedback;
	HANDLE children[2];
	thread_parameters *param1 = CreateParam(cmd1, level, father, hIn, hOut);
	thread_parameters *param2 = CreateParam(cmd2, level, father, hIn, hOut);

	/**
	 * Creez cele 2 thread-uri, le pasez comanda de executat fiecaruia
	 * si executia se face apeland "thread_routine".
	 */
	children[0] = CreateThread(NULL, 0, thread_routine, param1, 0, NULL);
	children[1] = CreateThread(NULL, 0, thread_routine, param2, 0, NULL);

	/**
	 * Folosesc un vector de 2 procese pentru a putea sa le astept
	 * simultan la final folosind apelul "WaitForMultipleObjects".
	 */
	feedback = WaitForMultipleObjects(2, children, true, INFINITE);
	DIE(feedback == WAIT_FAILED, "WaitForMultipleObjects");

	CloseHandle(children[0]);
	CloseHandle(children[1]);
	DestroyParam(param1);
	DestroyParam(param2);

	return -1;
}

/**
 * Folosesc aceasta functie pentru a executa
 * comenzile de la capetele unui pipe
 */
static int exec_pipe_cmd(command_t *cmd, int level, command_t *father,
	HANDLE *h1, HANDLE *h2, HANDLE *closing_handle)
{
	int feedback = 0;

	if (cmd->scmd == NULL)
		feedback = parse_command(cmd, level, father, h1, h2);
	else
		parse_simple(cmd->scmd, level, father, h1, h2);

	CloseHandle(*closing_handle);

	if (feedback == -1)
		return -1;

	return 0;
}

/**
 * Run commands by creating an annonymous pipe (cmd1 | cmd2)
 */
static int do_on_pipe(command_t *cmd1, command_t *cmd2, int level,
	command_t *father, HANDLE *hIn, HANDLE *hOut)
{
	int feedback;
	char *verb = get_word(cmd2->scmd->verb);
	SECURITY_ATTRIBUTES attr;
	HANDLE hRead, hWrite;
	BOOL pipe_success;

	/**
	 * Pentru comanda false pe post de cmd2, intorc rezultat
	 * diferit de 0 indiferent de executia ei (nu face nimic)
	 */
	if (strcmp(verb, "false") == 0) {
		free(verb);
		return -1;
	}

	ZeroMemory(&attr, sizeof(attr));
	attr.bInheritHandle = TRUE;

	/**
	 * Creare pipe
	 */
	pipe_success = CreatePipe(&hRead, &hWrite, &attr, INFINITE);
	if (!pipe_success)
		return -1;

	/**
	 * Executa comanda 1
	 */
	feedback = exec_pipe_cmd(cmd1, level, father, hIn, &hWrite, &hWrite);
	if (feedback == -1)
		return -1;

	/**
	 * Executa comanda 2
	 */
	feedback = exec_pipe_cmd(cmd2, level, father, &hRead, hOut, &hRead);
	if (feedback == -1)
		return -1;

	return 0;
}

/**
 * Parse and execute a command.
 */
int parse_command(command_t *c, int level, command_t *father,
	HANDLE *hIn, HANDLE *hOut)
{
	int feedback;

	/**
	 * sanity checks
	 */
	if (c == NULL)
		return -1;

	/**
	 * execute a simple command
	 */
	if (c->op == OP_NONE)
		return parse_simple(c->scmd, level, father, hIn, hOut);

	switch (c->op) {
	/**
	 * execute the commands one after the other
	 */
	case OP_SEQUENTIAL:
		parse_command(c->cmd1, level + 1, c, hIn, hOut);
		parse_command(c->cmd2, level + 1, c, hIn, hOut);
		break;

	/**
	 * execute the commands simultaneously
	 */
	case OP_PARALLEL:
		feedback = do_in_parallel(c->cmd1, c->cmd2, level + 1,
			c, hIn, hOut);
		if (feedback == -1)
			return -1;
		break;

	/**
	 * execute the second command only if the first one
	 * returns non zero
	 */
	case OP_CONDITIONAL_NZERO:
		feedback = parse_command(c->cmd1, level + 1, c, hIn, hOut);
		if (feedback == -1)
			return parse_command(c->cmd2, level + 1, c, hIn, hOut);
		break;

	/**
	 * execute the second command only if the first one
	 * returns zero
	 */
	case OP_CONDITIONAL_ZERO:
		feedback = parse_command(c->cmd1, level + 1, c, hIn, hOut);
		if (feedback == 0)
			return parse_command(c->cmd2, level + 1, c, hIn, hOut);
		break;

	/**
	 * redirect the output of the first command to the
	 * input of the second
	 */
	case OP_PIPE:
		feedback = do_on_pipe(c->cmd1, c->cmd2, level + 1,
			c, hIn, hOut);
		if (feedback == -1)
			return -1;
		break;

	default:
		exit(EXIT_FAILURE);
	}

	return 0;
}
