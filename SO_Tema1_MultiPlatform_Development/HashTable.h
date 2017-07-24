/*
 * Autor : Groapa Mihai
 * Grupa : 334 CA
 */

#include <stdio.h>
#include <string.h>
#include "LinkedList.h"
#include "hash.h"

typedef struct HashTable {
	struct LinkedList *table;
	unsigned int size;
	boolean created;
} HashTable;

void create_def_hash_table(HashTable *hashTable);
void create_hash_table(HashTable *hashTable, unsigned int size);
void add_element(HashTable *hashTable, char *element);
boolean remove_element(HashTable *hashTable, char *element);
void print_table(FILE *file, HashTable *hashTable);
void print_bucket_at(FILE *file, HashTable *hashTable, int index);
boolean find_element(HashTable *hashTable, char *element);
void clear_table(HashTable *hashTable);
void free_table(HashTable *hashTable);
HashTable *double_size(HashTable *hashTable);
HashTable *halve_size(HashTable *hashTable);
