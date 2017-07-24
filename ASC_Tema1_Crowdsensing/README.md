    Computer Systems Architecture Course

    Faculty of Automatic Control and Computers, 
    University Politehnica of Bucharest

    Assignment 1 - Crowdsensing
    March 2017


    Autor : Groapa Mihai
    Grupa : 334 CA


-> Ca elemente de sincronizare folosesc o lista de lock-uri, cate unul
pentru fiecare locatie, o bariera de sincronizare la nivel de thread-uri
si alta inter - device-uri. Bariera de sincronizare intre thread-uri este
proprie fiecarui device, celelalte sunt partajate de device-uri.
-> Am considerat ca un device are 8 thread-uri.
-> Fiecare device detine de asemenea si o lista cu referinte catre 
toate celelalte dispozitive.
-> Setarile initiale ale dispozitivelor sunt facute de device-ul Master.
Acesta preia de la toate dispozitivele locatiile pentru care detin
informatii, obtine id-ul maxim al locatiei vizitate si astfel pot crea
vectorul de lock-uri, stiind ca locatiile sunt numerotate de la 0 la
max_locatie. Acest dispozitiv va crea si bariera inter - device-uri
si o va partaja cu celelalte impreuna cu vectorul de lock-uri.
-> Lista de lock-uri ofera posibilitatea unui singur device sa prelucreze
date de la o locatie prin acapararea acesteia. Totusi dispozitivele pot
rula simultan scripturi dar pe locatii diferite.
-> Bariera de sincronizare dintre dispozitive asigura echilibru intre timepoint
-urile la care au ajuns dispozitivele (diferenta de maxim un timepoint).
-> Bariera de sincronizare dintre thread-uri asigura faptul ca ultimul thread
ajuns la ea va face cererea de get_neighbours(), restulthread-urile asteapta 
la bariera mentionata.
-> Thread-urile unui dispozitiv ii executa script-urile dupa urmatoarea regula:
Thread i : script[i], script[i + num_threads], script[i + 2 * num_threads] etc.
Astfel ma asigur ca acelasi script nu este executat de thread-uri distincte.
-> Cele 2 bariere folosite sunt implementate de clasa ReusableBarrier.
Pentru a evita cod duplicat (sa scriu 2 clase, una per bariera), am trimis un
paramaetru suplimentar, flag, care este True pentru bariera la nivel de
thread-uri dispozitiv si obtine lista vecinilor, repectiv False pentru
cealalta.
