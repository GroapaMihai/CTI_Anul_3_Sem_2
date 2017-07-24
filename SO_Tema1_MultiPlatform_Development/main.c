/*
 * Autor : Groapa Mihai
 * Grupa : 334 CA
 */

#include "HashTable.h"
#include <ctype.h>

#define LINE_SIZE 20000

/*
 * True -> cuvantul este numar
 * False -> cuvantul nu este numar
 */
boolean isNumber(const char *token)
{
	unsigned int i;

	for (i = 0; i < strlen(token); i++)
		if (!isdigit(token[i]))
			return false;

	return true;
}

/*
 * Intoarce o lista formata din tokeni obtinut
 * prin spargerea sirului operatie dupa delimitatori
 */
LinkedList *splitIntoTokens(char *operation)
{
	char *token;
	LinkedList *tokens = malloc(sizeof(LinkedList));
	char delimit[] = " \t\r\n\v\f ";

	create_linked_list(tokens);
	token = strtok(operation, delimit);
	add_list_element(tokens, token);

	while (token != NULL) {
		add_list_element(tokens, token);
		token = strtok(NULL, delimit);
	}

	return tokens;
}

/* 
 * Parseaza operatia si o aplica tabelului. In cazul unei operatii
 * gresite sau inexistente intoarce NULL, altfel intoarce adresa
 * tabelului modificat.
 */
HashTable *applyOperation(HashTable *hashTable, char *operation)
{
	LinkedList *tokens = splitIntoTokens(operation);
	Node *iterator = tokens->first;
	FILE *out = NULL;
	boolean writeInFile = false;
	boolean wrongCommand = false;

	if (strcmp(iterator->word, "add") == 0) {
		iterator = iterator->next;

		if (iterator == NULL)
			wrongCommand = true;
		else
			add_element(hashTable, iterator->word);
	} else if (strcmp(iterator->word, "remove") == 0) {
		iterator = iterator->next;
		remove_element(hashTable, iterator->word);
	} else if (strcmp(iterator->word, "find") == 0) {
		iterator = iterator->next;

		if (iterator->next == NULL)
			out = stdout;
		else {
			out = fopen(iterator->next->word, "a");
			writeInFile = true;
		}

		if (out != NULL) {
			if (find_element(hashTable, iterator->word) == true)
				fprintf(out, "True\n");
			else
				fprintf(out, "False\n");
		}
	} else if (strcmp(iterator->word, "clear") == 0) {
		clear_table(hashTable);
	} else if (strcmp(iterator->word, "print_bucket") == 0) {
		iterator = iterator->next;

		if (isNumber(iterator->word) == true) {
			if (iterator->next == NULL)
				out = stdout;
			else {
				out = fopen(iterator->next->word, "a");
				writeInFile = true;
			}

			print_bucket_at(out, hashTable, atoi(iterator->word));
		} else
			wrongCommand = true;
	} else if (strcmp(iterator->word, "print") == 0) {
		if (iterator->next == NULL)
			out = stdout;
		else {
			out = fopen(iterator->next->word, "a");
			writeInFile = true;
		}

		print_table(out, hashTable);
	} else if (strcmp(iterator->word, "resize") == 0) {
		iterator = iterator->next;

		if (strcmp(iterator->word, "double") == 0)
			hashTable = double_size(hashTable);
		else if (strcmp(iterator->word, "halve") == 0)
			hashTable = halve_size(hashTable);
	} else
		wrongCommand = true;

	if (writeInFile == true)
		fclose(out);

	free_list(tokens);
	free(tokens);

	if (wrongCommand == true) {
		free_table(hashTable);
		return NULL;
	}

	return hashTable;
}

/*
 * Citeste din fisierul/de la consola linie cu linie si
 * apeleaza functia care modifica tabelul pasandu-i ca parametru
 * sirul citit. Daca dupa aplicarea operatiei se obtine NULL,
 * se inchide fisierul de intrare. Intoarce tabelul modificat
 * prin aplicarea tuturor operatiilor din fisier.
 */
HashTable *readFromSource(FILE *source, HashTable *hashTable)
{
	char buffer[LINE_SIZE];

	while (fgets(buffer, LINE_SIZE, source) != NULL) {
		if (strcmp(buffer, "\n") != 0) {
			hashTable = applyOperation(hashTable, buffer);

			if (hashTable == NULL) {
				fclose(source);
				return NULL;
			}
		}
	}

	return hashTable;
}

int main(int argc, char *argv[])
{
	HashTable *hashTable = malloc(sizeof(HashTable));
	FILE *source;
	int i = 0;
	int initialSize = 0;

	create_def_hash_table(hashTable);

	if (argc < 2) {
		free(hashTable);
		DIE(argc < 2, "Initial Hashtable size not specified\n");
	}

	if (isNumber(argv[1]) == false) {
		free(hashTable);
		DIE((!isNumber(argv[1])), "Size must be positive Integer\n");
	}

	initialSize = atoi(argv[1]);

	if (initialSize < 0) {
		free(hashTable);
		DIE(initialSize < 0, "Not a valid size\n");
	}

	create_hash_table(hashTable, initialSize);

	/*
	 * Citirea se face de la stdin, altfel se
	 * citesc comenzile din fisierele specificate, in ordine
	 */
	if (argc == 2)
		hashTable = readFromSource(stdin, hashTable);
	else {
		for (i = 2; i < argc; i++) {
			source = fopen(argv[i], "r");
			if (source == NULL)
				continue;
			hashTable = readFromSource(source, hashTable);
			if (hashTable == NULL)
				DIE("Invalid Command\n", "Invalid Command\n");

			fclose(source);
		}
	}

	free_table(hashTable);

	return 0;
}
