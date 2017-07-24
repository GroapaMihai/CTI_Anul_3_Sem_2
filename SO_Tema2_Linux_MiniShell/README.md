
	Groapa Mihai
	334 CA

						Tema 2 SO - Mini-shell


	Am implementat folosind parserul oferit in arhiva. Ideea de baza e
	spargerea comenzilor in comenzi cat mai simple, care pot fi executate
	direct si inlantuirea lor prin intermediul operatorilor. Aceasta
	functionalitate este asigurata de functia parse_simple().
	Se verifica tipul comenzii pentru cele standard, de exemplu quit/exit, cd
	precum si cele de setare a variabilelor de mediu. Pentru variabilele de mediu,
	urmaresc sa vad daca comanda respecta pattern-ul 'nume'='valoare' si apelez
	'setenv' in aces scop. Tot in aceasta functie se asigura redirectarile si se 
	apeleaza functia execute pentru a incarca comanda in procesul copil si a o
	executa. Pentru edirectari am folosit 3 functii:
		- in_redirect : pentru a redirecta STDIN din fisier. Face deschiderea
		fisierului in mod Read Only si face apel dup2 astfel incat citirea
		de la standard input sa se faca din fisier.
		- out_redirect : pentru a redirecta STDOUT in fisier. Deschide fisierul
		in mod Write Only, si in functie de starea flag-ului IO_OUT_APPEND
		scrie in mod APPEND sau face trunc pe fisier.
		- err_redirect : pentru redirectarea iesirii de eroare standard in fisier.
	In afara functiei parse_simple() si a functiei parse_command() a carei
	functionalitate este evidenta, mai folosesc functiile do_in_parallel() care
	creeaza 2 procese copil folosind apeluri fork() pentru a executa cate o comanda
	si do_on_pipe() care creeaza alte 2 procese, unul pentru a rula prima comanda
	si a redirecta iesirea catre intrarea celui de-al doilea, si cel din urma
	pentru a executa a doua comanda cu intrarea aferenta.
	Aceste metode folosesc la randul lor altele mai "mici", provenite din
	impartirea in functionalitati. Exemplu :
		- create_children() : creeaza un proces copil si incarca executabilul in el.
		- read_end(), write_end() si parent() pentru functia do_on_pipe().
	In toate metodele fie apelez direc DIE in caz de eroare, fie intorc codul de
	eroare -1 si fac actiunea in caz de eroare in functia apelanta.
