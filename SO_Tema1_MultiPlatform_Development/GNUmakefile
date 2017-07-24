CC = gcc
CFLAGS = -Wall -fPIC

LDFLAGS = -L.

build:  tema1

tema1: main.o libhash.so
	$(CC) $(CFLAGS) -o $@ $< -lhash $(LDFLAGS)

libhash.so: HashTable.o LinkedList.o hash.o
	$(CC) -shared -o $@ $^

main.o: main.c
HashTable.o: HashTable.c
LinkedList.o: LinkedList.c
hash.o: hash.c

clean:
	rm -f *.o tema1 libhash.so