/*
 * Autor : Groapa Mihai
 * Grupa : 334 CA
 */

#include "HashTable.h"

/*
 *Creeaza un tabel fara bucket-uri
 */
void create_def_hash_table(HashTable *hashTable)
{
	hashTable->table = NULL;
	hashTable->size = 0;
	hashTable->created = false;
}

/*
 * Creeaza un tabel cu size bucket-uri si initializeaza
 * fiecare bucket
 */
void create_hash_table(HashTable *hashTable, unsigned int size)
{
	unsigned int i = 0;

	hashTable->table = malloc(size * sizeof(struct LinkedList));
	hashTable->size = size;

	for (i = 0; i < size; i++)
		create_linked_list(&hashTable->table[i]);

	hashTable->created = true;
}

/*
 * Adauga cuvantul dat in tabela. Calculeaza indexul
 * bucket-ului ce va stoca elementul folosind functia hash.
 */
void add_element(HashTable *hashTable, char *element)
{
	unsigned int hashIndex = hash(element, hashTable->size);

	add_list_element(&hashTable->table[hashIndex], element);
}

/*
 * Sterge cuvantul dat din tabela. Indexul bucket-ului in
 * care ar trebui sa se gaseasca acesta este calculat folosind
 * metoda hash. Intoarce True daca elementul a fost gasit
 * si sters, False altfel.
 */
boolean remove_element(HashTable *hashTable, char *element)
{
	unsigned int hashIndex = hash(element, hashTable->size);

	return remove_list_element(&hashTable->table[hashIndex], element);
}

/*
 * Afiseaza tabelul bucket cu bucket, cate unul pe linie,
 * in ordine. Un bucket gol nu se afiseaza.
 */
void print_table(FILE *file, HashTable *hashTable)
{
	unsigned int i;

	for (i = 0; i < hashTable->size; i++)
		print_list(file, &hashTable->table[i]);
}

/*
 * Afiseaza pe o linie bucket-ul specificat prin index,
 * daca acesta nu este gol.
 */
void print_bucket_at(FILE *file, HashTable *hashTable, int index)
{
	print_list(file, &hashTable->table[index]);
}

/*
 * Calculeaza index-ul in care ar trebui sa se afle
 * elementul cautat, intoarce True daca a fost gasit in
 * bucket-ul corespunzator, False altfel.
 */
boolean find_element(HashTable *hashTable, char *element)
{
	unsigned int hashIndex = hash(element, hashTable->size);

	return contains(&hashTable->table[hashIndex], element);
}

/*
 *Elibereaza fiecare bucket
 */
void clear_table(HashTable *hashTable)
{
	unsigned int i;

	for (i = 0; i < hashTable->size; i++)
		free_list(&hashTable->table[i]);
}

/*
 * Elibereaza fiecare bucket si dezaloca
 * memoria de stocare a tabelului
 */
void free_table(HashTable *hashTable)
{
	clear_table(hashTable);
	free(hashTable->table);
	free(hashTable);
}

/*
 * Creeaza un tabel nou, de dimensiune dubla.
 * Copiaza toate elementele din vechiul tabel in cel
 * proaspat creat calculand pentru fiecare noul
 * bucket de stocare. Elibereaza vechiul tabel
 * si il face sa pointeze catre cel nou.
 */
HashTable *double_size(HashTable *hashTable)
{
	HashTable *doubled = malloc(sizeof(HashTable));
	Node *iterator;
	struct LinkedList *currentBucket;
	struct LinkedList *doubledBucket;
	int hashIndex;
	unsigned int i;

	create_hash_table(doubled, 2 * hashTable->size);

	for (i = 0; i < hashTable->size; i++) {
		currentBucket = &hashTable->table[i];
		iterator = currentBucket->first;

		while (iterator != NULL) {
			hashIndex = hash(iterator->word, doubled->size);
			doubledBucket = &doubled->table[hashIndex];
			add_list_element(doubledBucket, iterator->word);
			iterator = iterator->next;
		}
	}

	free_table(hashTable);

	return doubled;
}

/*
 * Comportament similar cu cel al functiei double, cu
 * mentiunea ca noul tabel are de 2 ori mai putine bucket-uri
 */
HashTable *halve_size(HashTable *hashTable)
{
	HashTable *halved = malloc(sizeof(HashTable));
	Node *iterator;
	struct LinkedList *currentBucket;
	struct LinkedList *halvedBucket;
	int hashIndex;
	unsigned int i;

	create_hash_table(halved, hashTable->size / 2);

	for (i = 0; i < hashTable->size; i++) {
		currentBucket = &hashTable->table[i];
		iterator = currentBucket->first;

		while (iterator != NULL) {
			hashIndex = hash(iterator->word, halved->size);
			halvedBucket = &halved->table[hashIndex];
			add_list_element(halvedBucket, iterator->word);
			iterator = iterator->next;
		}
	}

	free_table(hashTable);

	return halved;
}
