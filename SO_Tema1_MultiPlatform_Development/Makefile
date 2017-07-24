CFLAGS = /nologo /W3 /D_CRT_SECURE_NO_DEPRECATE

build: tema1.exe

tema1.exe: main.obj HashTable.obj LinkedList.obj hash.obj
	link /nologo /out:$@ $**

main.obj: main.c
	cl $(CFLAGS) /Fomain.obj /c main.c

hash.obj: hash.c
	cl $(CFLAGS)  /Fohash.obj /c hash.c

HashTable.obj: HashTable.c
	cl $(CFLAGS) /FoHashTable.obj /c HashTable.c

LinkedList.obj: LinkedList.c
	cl $(CFLAGS) /FoLinkedList.obj /c LinkedList.c

clean:
	del *.obj *.exe *.out