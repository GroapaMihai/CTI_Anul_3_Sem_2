/*
 * Autor : Groapa Mihai
 * Grupa : 334 CA
 */

#include "hash.h"

unsigned int hash(const char *str, unsigned int hash_length)
{
	unsigned int hash = 5381;
	int c;

	while ((c = *str++) != 0)
		hash = ((hash << 5) + hash) + c;

	return (hash % hash_length);
}
