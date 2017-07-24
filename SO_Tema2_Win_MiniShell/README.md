
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
	'SetEnvironmentVariable' in aces scop. Tot in aceasta functie se asigura
	redirectarile si se incarca comanda in procesul copil pentru a o
	executa. Am folosit 3 functii pentru a obtine handler-ele si a seta cursorul
	de fisier pentru modul APPEND la sfarsit.
	In afara functiei parse_simple() si a functiei parse_command() a carei
	functionalitate este evidenta, mai folosesc functiile do_in_parallel() care
	creeaza 2 procese copil folosind apeluri CreateThread() pentru a executa cate
	o comanda si do_on_pipe() pentru a rula prima comanda si a redirecta iesirea 
	catre intrarea celei de-a doua comenzi. 
	Aceste metode folosesc la randul lor altele mai "mici", provenite din
	impartirea in functionalitati.
