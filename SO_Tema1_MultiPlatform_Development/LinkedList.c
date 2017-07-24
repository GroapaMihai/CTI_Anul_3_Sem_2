/*
 * Autor : Groapa Mihai
 * Grupa : 334 CA
 */

#include "LinkedList.h"

void create_linked_list(LinkedList *list)
{
	list->first = NULL;
}

/*
 * Aloca memorie pentru nod si cuvantul sau,
 * copiaza cuvantul primit ca parametru in nodul
 * creat si intoarce adresa acestuia.
 */
Node *create_node(char *element)
{
	/*
	 * retin suplimentar caracterul terminator de sir
	 */
	long len = strlen(element) + 1;
	Node *newNode = malloc(sizeof(Node));
	char *newNodeWord = malloc(len * sizeof(char));

	memcpy(newNodeWord, element, len - 1);
	newNodeWord[len - 1] = '\0';
	newNode->word = newNodeWord;
	newNode->next = NULL;

	return newNode;
}

/*
 * Adauga elementul cu valoarea specificata la
 * sfaristul listei. Adaugarea se face dupa ce am
 * asigurat ca acesta este unic.
 */
void add_list_element(LinkedList *list, char *element)
{
	Node *iterator = list->first;
	Node *newNode;

	if (is_empty(list) == true) {
		list->first = create_node(element);
		return;
	}

	if (strcmp(iterator->word, element) == 0)
		return;

	while (iterator->next != NULL) {
		if (strcmp(iterator->word, element) == 0)
			return;

		iterator = iterator->next;
	}

	if (strcmp(iterator->word, element) == 0)
		return;

	newNode = create_node(element);
	iterator->next = newNode;
}

/*
 * True -> lista este goala
 * False -> altfel
 */
boolean is_empty(LinkedList *list)
{
	if (list->first == NULL)
		return true;
	return false;
}

/*
 * Parcurge lista si verifica daca element
 * apartie acesteia. Intoarce True in acest caz,
 * False altfel.
 */
boolean contains(LinkedList *list, char *element)
{
	Node *iterator = list->first;

	while (iterator != NULL) {
		if (strcmp(iterator->word, element) == 0)
			return true;

		iterator = iterator->next;
	}

	return false;
}

/*
 * Sterge din lista valoarea specificata (daca exista).
 * Restabileste legaturile dintre vecinii elementului sters.
 * Intoarce True in cazul gasirii elementului, False altfel.
 */
boolean remove_list_element(LinkedList *list, char *element)
{
	Node *iterator = list->first;
	Node *next = NULL;

	if (is_empty(list) == true)
		return false;

	if (strcmp(iterator->word, element) == 0) {
		list->first = iterator->next;
		free(iterator->word);
		free(iterator);
		return true;
	}

	next = iterator->next;

	while (next != NULL) {
		if (strcmp(next->word, element) == 0) {
			free(next->word);
			if (next->next != NULL)
				iterator->next = next->next;
			else
				iterator->next = NULL;
			free(next);
			return true;
		}

		iterator = iterator->next;
		next = iterator->next;
	}

	return false;
}

/*
 * Afiseaza fiecare cuvant din lista, separate
 * prin spatiu. La sfarsit termina output-ul
 * prin caracterul newline.
 */
void print_list(FILE *file, LinkedList *list)
{
	Node *iterator = list->first;

	if (is_empty(list) == true)
		return;

	while (iterator != NULL) {
		fprintf(file, "%s ", iterator->word);
		iterator = iterator->next;
	}

	fprintf(file, "\n");
}

/*
 * Elibereaza memoria in care sunt stocate
 * cuvintele, pointerul de tip Node si
 * asigneaza inceputului de lista valoarea NULL
 */
void free_list(LinkedList *list)
{
	Node *iterator = NULL;
	Node *next = NULL;

	if (is_empty(list))
		return;

	iterator = list->first;
	next = iterator->next;

	while (next != NULL) {
		free(iterator->word);
		free(iterator);
		iterator = next;
		next = iterator->next;
	}

	if (next == NULL) {
		free(iterator->word);
		free(iterator);
	}

	list->first = NULL;
}
