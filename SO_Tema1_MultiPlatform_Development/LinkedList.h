/*
 * Autor : Groapa Mihai
 * Grupa : 334 CA
 */

#include <stdio.h>
#include <string.h>
#include "debug.h"

typedef struct LinkedList {
	Node *first;
} LinkedList;

void create_linked_list(LinkedList *list);
Node *create_node(char *element);
void add_list_element(LinkedList *list, char *element);
boolean is_empty(LinkedList *list);
boolean contains(LinkedList *list, char *element);
boolean remove_list_element(LinkedList *list, char *element);
void print_list(FILE *file, LinkedList *list);
void free_list(LinkedList *list);
