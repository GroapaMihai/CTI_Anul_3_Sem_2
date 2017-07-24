/**
 * Operating Systems 2013-2017 - Assignment 2
 *
 * Groapa Mihai, 334 CA
 *
 */

#include <stdio.h>
#include <string.h>
#include <assert.h>
#include <stdlib.h>

#include <sys/types.h>
#include <sys/stat.h>
#include <sys/wait.h>

#include <fcntl.h>
#include <unistd.h>

#include "cmd.h"
#include "utils.h"

#define READ		0
#define WRITE		1

/*
 * Schimbare director curent.
 * Return value : -1 daca nu s-a putut schimba directorul
 * 0 in caz de succes.
 */
static bool shell_cd(word_t *dir)
{
	if (dir == NULL)
		return -1;

	if (chdir(dir->string) == -1) {
		printf("[cd] %s : No such file or directory\n", dir->string);
		return -1;
	}

	return 0;
}

/*
 * Actiunea de exit/quit.
 */
static int shell_exit(void)
{
	return SHELL_EXIT;
}

/*
 * Setare variabile de mediu.
 * Return value : 0 daca variabila a fost setata
 * cu succes, -1 daca instructiunea nu presupune
 * o astfel de operatie.
 */
static int set_env_variable(simple_command_t *s)
{
	int feedback;

	if (s->verb->next_part == NULL)
		return -1;

	const char *var_name = s->verb->string;
	const char *equal = s->verb->next_part->string;
	const char *value = s->verb->next_part->next_part->string;

	if (strcmp(equal, "=") == 0) {
		feedback = setenv(var_name, value, 1);
		DIE(feedback == -1, "Environment variable failure\n");
		return 0;
	}

	return -1;
}

/*
 * Functie ajutatoare la schimbarea directorului.
 * Precede schimbarea efectiva a directorului curent.
 */
static int change_directory(simple_command_t *s, char *word)
{
	char *file_name;
	int fd;

	if (s->out != NULL) {
		file_name = get_word(s->out);
		fd = open(file_name, O_WRONLY | O_CREAT | O_TRUNC, 0644);
		free(file_name);
		DIE(fd < 0, "[change_directory] File open failed!\n");
	}

	return shell_cd(s->params);
}

/*
 * Redirectare STDIN din fisier.
 */
static void in_redirect(simple_command_t *s)
{
	char *file_name;
	int fd, feedback;

	file_name = get_word(s->in);
	fd = open(file_name, O_RDONLY);
	free(file_name);
	DIE(fd < 0, "[in_redirect] File open failed!\n");

	feedback = dup2(fd, STDIN_FILENO);
	DIE(feedback == -1, "[in_redirect] Dup2 call failed!\n");
}

/*
 * Redirectare STDOUT in fisier.
 * Se face fie in modul APPEND, fie scriere la inceput de fisier.
 */
static void out_redirect(simple_command_t *s)
{
	char *file_name;
	int fd, feedback;

	file_name = get_word(s->out);

	if (!(s->io_flags && IO_OUT_APPEND) && s->err == NULL)
		fd = open(file_name, O_WRONLY | O_CREAT | O_TRUNC, 0644);
	else
		fd = open(file_name, O_WRONLY | O_CREAT | O_APPEND, 0644);

	free(file_name);
	DIE(fd < 0, "[out_redirect] File open failed!\n");
	feedback = dup2(fd, STDOUT_FILENO);

	DIE(feedback == -1, "[out_redirect] Dup2 call failed!\n");
}

/*
 * Redirectare STDERR in fisier.
 * Se face fie in modul APPEND, fie scriere la inceput de fisier.
 */
static void err_redirect(simple_command_t *s)
{
	char *file_name;
	int fd, feedback;

	file_name = get_word(s->err);

	/*
	 * Daca flagul IO_ERR_APPEND este activ,
	 * redirectarea se face in mod APPEND.
	 */
	if (s->io_flags && IO_ERR_APPEND && s->out == NULL)
		fd = open(file_name, O_WRONLY | O_CREAT | O_APPEND, 0644);
	else
		fd = open(file_name, O_WRONLY | O_CREAT | O_TRUNC, 0644);

	free(file_name);
	DIE(fd < 0, "[err_redirect] File open failed!\n");
	feedback = dup2(fd, STDERR_FILENO);

	DIE(feedback == -1, "[err_redirect] Dup2 call failed!\n");
}

/*
 * Procesul copil foloseste aceasta functie pentru
 * a rula o comanda, aka executabil.
 */
static void execute(simple_command_t *s, char *word)
{
	int argc, feedback;
	char **argv;

	/*
	 * Incarcare executabil
	 */
	argv = get_argv(s, &argc);
	word = get_word(s->verb);
	feedback = execvp(word, argv);

	if (feedback < 0) {
		printf("Execution failed for \'%s\'\n", word);
		free(word);
		free(argv);
		exit(EXIT_FAILURE);
	}

	free(word);
	free(argv);
}

/*
 * Parseaza, executa si intoarce rezultatul
 * comenzilor simple.
 */
static int parse_simple(simple_command_t *s, int level,
	command_t *father)
{
	int feedback, status;
	pid_t pid;
	char *word;

	if (s->verb == NULL)
		return -1;

	word = get_word(s->verb);

	/*
	 * Parasire shell.
	 */
	if ((strcmp(word, "quit") == 0) || (strcmp(word, "exit")) == 0) {
		free(word);
		return shell_exit();
	}

	/*
	 * Schimbare director de lucru.
	 */
	if (strcmp(word, "cd") == 0) {
		feedback = change_directory(s, word);
		free(word);
		return feedback;
	}

	/*
	 * Setare variabile de mediu (daca e cazul).
	 */
	feedback = set_env_variable(s);
	if (feedback == 0) {
		free(word);
		return 0;
	}

	pid = fork();

	switch (pid) {
	case -1:
		DIE(pid == -1, "Fork failed!\n");
		free(word);
		break;

	/*
	 * Proces copil.
	 * Redirectari + rulare executabil.
	 */
	case 0:
		if (s->in != NULL)
			in_redirect(s);

		if (s->out != NULL)
			out_redirect(s);

		if (s->err != NULL)
			err_redirect(s);

		execute(s, word);
		free(word);
		exit(EXIT_SUCCESS);
		break;

	default:
		free(word);
		break;
	}

	/*
	 * Procesul parinte asteapta copilul sa termine.
	 */
	feedback = waitpid(pid, &status, 0);
	DIE(feedback == -1, "Child finishing failed!\n");

	return status;
}

/*
 * Functie folosita pentru a crea un proces copil,
 * in cadrul metodei do_in_parallel. Procesul
 * creat va executa apoi comanda aferenta.
 * Se trimit pointeri catre pid si status, astfel incat
 * modificarile vor fi vizibile din functia apelanta.
 */
static void create_children(pid_t *childPID, int *status,
	command_t *cmd, int level, command_t *father)
{
	*childPID = fork();

	 /*
	  * Fork was successful
	  */
	if (*childPID >= 0) {
		if (*childPID == 0) {
			*status = parse_command(cmd, level, father);
			DIE(*status == -1, "Parse command failed!\n");
			exit(EXIT_FAILURE);
		}
	} else // Fork failed
		DIE(*childPID == -1, "Fork failed!\n");
}

/*
 * Creeaza doua procese copil pentru a executa in paralel
 * cmd1 si cmd2.
 */
static int do_in_parallel(command_t *cmd1, command_t *cmd2,
	int level, command_t *father)
{
	pid_t childPID1, childPID2;
	int status, status1, status2;

	/*
	 * Se creeaza 2 procese copil si se da start executiei comenzilor.
	 */
	create_children(&childPID1, &status1, cmd1, level, father);
	create_children(&childPID2, &status2, cmd2, level, father);

	/*
	 * Se asteapta terminarea executiei copiilor.
	 */
	status1 = waitpid(childPID1, &status, 0);
	status2 = waitpid(childPID2, &status, 0);

	if (status1 == -1 || status2 == -1)
		return -1;

	return 0;
}

static int write_end(int *pipefd, command_t *cmd,
	int level, command_t *father)
{
	int feedback = 0;

	close(pipefd[0]);
	feedback = dup2(pipefd[1], STDOUT_FILENO);
	if (feedback == -1)
		return -1;

	parse_command(cmd, level, father);
	close(pipefd[1]);

	exit(EXIT_SUCCESS);
}

static int read_end(int pdWrite, int *pipefd, command_t *cmd,
	int level, command_t *father)
{
	int feedback = 0, status;

	close(pipefd[1]);
	feedback = dup2(pipefd[0], STDIN_FILENO);
	if (feedback == -1)
		return -1;

	parse_command(cmd, level, father);
	close(pipefd[0]);

	/*
	 * Asteapta scrierea in pipe
	 */
	feedback = waitpid(pdWrite, &status, 0);
	if (feedback == -1)
		return -1;

	exit(EXIT_SUCCESS);
}

static int parent(int pdRead, int *pipefd)
{
	int feedback = 0, status;

	close(pipefd[0]);
	close(pipefd[1]);
	feedback = waitpid(pdRead, &status, 0);
	if (feedback == -1)
		return -1;

	return 0;
}

/*
 * Run commands by creating an anonymous pipe (cmd1 | cmd2)
 * pipefd[0] = read end
 * pipefd[1] = write end
 */
static int do_on_pipe(command_t *cmd1, command_t *cmd2,
	int level, command_t *father)
{
	pid_t pdRead, pdWrite;
	int feedback, pipefd[2];
	char *verb = get_word(cmd2->scmd->verb);

	/*
	 * Pentru comanda false pe post de cmd2, intorc rezultat
	 * diferit de 0 indiferent de executia ei (nu face nimic)
	 */
	if (strcmp(verb, "false") == 0) {
		free(verb);
		return -1;
	}

	feedback = pipe(pipefd);
	DIE(feedback == -1, "Pipe failed!\n");

	pdRead = fork();
	DIE(pdRead == -1, "Fork failed!\n");

	/*
	 * Parintele procesului corespunzator capatului scriere
	 * este cel de la partea de citire a pipe-ului.
	 */
	if (pdRead == 0) {
		pdWrite = fork();
		DIE(pdWrite == -1, "Fork failed!\n");

		if (pdWrite == 0)
			return write_end(pipefd, cmd1, level, father);
		else
			return read_end(pdWrite, pipefd,
				cmd2, level, father);
	} else
		return parent(pdRead, pipefd);

	return 0;
}

/*
 * Parse and execute a command.
 */
int parse_command(command_t *c, int level, command_t *father)
{
	int feedback;

	/*
	 * sanity checks
	 */
	if (c == NULL)
		return -1;

	/*
	 * execute a simple command
	 */
	if (c->op == OP_NONE)
		return parse_simple(c->scmd, level, father);

	switch (c->op) {
	/*
	 * execute the commands one after the other
	 */
	case OP_SEQUENTIAL:
		parse_command(c->cmd1, level + 1, c);
		parse_command(c->cmd2, level + 1, c);
		break;

	/*
	 * execute the commands simultaneously
	 */
	case OP_PARALLEL:
		feedback = do_in_parallel(c->cmd1, c->cmd2, level + 1, c);
		DIE(feedback == -1, "Child execution failed!\n");
		break;

	/*
	 * execute the second command only if the first one
	 * returns non zero
	 */
	case OP_CONDITIONAL_NZERO:
		feedback = parse_command(c->cmd1, level + 1, c);
		if (feedback != 0)
			return parse_command(c->cmd2, level + 1, c);
		break;

	/*
	 * execute the second command only if the first one
	 * returns zero
	 */
	case OP_CONDITIONAL_ZERO:
		feedback = parse_command(c->cmd1, level + 1, c);
		if (feedback == 0)
			return parse_command(c->cmd2, level + 2, c);
		break;

	/*
	 * redirect the output of the first command to the
	 * input of the second
	 */
	case OP_PIPE:
		feedback = do_on_pipe(c->cmd1, c->cmd2, level + 1, c);
		if (feedback != 0)
			return -1;
		break;

	default:
		exit(EXIT_FAILURE);
	}

	return 0;
}
