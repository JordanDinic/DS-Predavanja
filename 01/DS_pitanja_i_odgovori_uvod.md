# Distribuirani sistemi – Uvod
## Skripta: pitanja i odgovori za pripremu ispita

---

## 1. Osnovni pojmovi

**P1. Šta je distribuirani sistem?**
Distribuirani sistem je skup nezavisnih računara koji se korisnicima predstavlja kao jedinstven, koherentan sistem (Tanenbaum, van Steen). Alternativno, to je sistem u kome hardverske ili softverske komponente na umreženim računarima komuniciraju i koordinišu svoje akcije isključivo razmenom poruka (Coulouris, Dollimore, Kindberg).

**P2. Zašto je Lamportova definicija distribuiranog sistema duhovita, a opet tačna?**
Lamport kaže: "To je sistem na kome ne mogu ništa da uradim jer je neka mašina za koju nikad nisam čuo otkazala." Ona ističe realan problem DS – zavisnost od velikog broja komponenti, od kojih otkaz bilo koje (čak i nepoznate korisniku) može onemogućiti rad celog sistema.

**P3. Kako izgleda model distribuiranog sistema?**
DS se sastoji od više procesora (P), od kojih svaki ima svoju memoriju (M), povezanih komunikacionom mrežom (WAN/LAN). Dakle, DS povezuje procesore pomoću komunikacione podmreže – ne postoji zajednička (deljena) memorija.

**P4. Koje dve tehnološke promene sredinom 1980-ih su omogućile razvoj distribuiranih sistema?**
1) Pojava mikroprocesora – omogućila je gradnju računara po niskoj ceni uz bolje performanse (manji, jeftiniji, brži, energetski efikasniji računari).
2) Razvoj veoma brzih računarskih mreža – omogućile su razmenu poruka između računara brzinama i do 100 Gbit/s, čime je povezivanje velikog broja računara postalo isplativo.

---

## 2. Primeri distribuiranih sistema

**P5. Navedi primere distribuiranih sistema.**
- Internet i Web
- HPC (High Performance Computing) sistemi
- Cloud (sistemi u oblaku)
- Sistemi veštačke inteligencije (federativno učenje, distribuirano treniranje osnovnih modela)

**P6. Zašto je AI doživeo veliki proboj zahvaljujući distribuiranim sistemima?**
Zbog mogućnosti distribuiranih izračunavanja i pristupačnih troškova računarstva u oblaku i skladištenja podataka, što omogućava treniranje modela koji zahtevaju ogromne računarske resurse (npr. federativno učenje, distribuirano treniranje osnovnih modela).

---

## 3. Prednosti distribuiranih sistema nad centralizovanim

**P7. Koje su četiri glavne prednosti DS nad centralizovanim sistemom?**
1. Ekonomske prednosti – bolji odnos cena/performanse
2. Brzina – veća ukupna moć obrade
3. Pouzdanost – otkaz jednog dela sistema ne obara ceo sistem
4. Inkrementalni rast, deljenje resursa, olakšana komunikacija i efikasno korišćenje resursa

**P8. Objasni Grace Hopperovu izreku o "volovima" i njenu vezu sa DS.**
"U pionirskim danima su koristili volove za teško vučenje, a kada jedan vo nije mogao da pomeri kladu, nisu pokušavali da uzgoje većeg vola. Ne bismo trebalo da težimo ka većim računarima, već ka sistemima od više računara." Poruka: umesto jednog skupog super-računara, isplativije je i praktičnije izgraditi klaster jeftinijih računara koji zajedno daju istu (ili veću) moć obrade.

**P9. Kako DS poboljšava pouzdanost u odnosu na centralizovani sistem?**
Kod centralizovanog sistema, ako računar otkaže, aplikacija prestaje da radi. Kod dobro projektovanog DS, ako npr. 15% mašina otkaže, sistem i dalje radi, samo sa degradacijom performansi od približno 15% (primer: Google – hiljade čvorova mogu biti van funkcije, a usluga pretraživanja ostaje dostupna).

**P10. Kako Google pretraživač ilustruje prednosti DS?**
Google koristi distribuirani sistem sa milionima servera koji paralelno obrađuju delove podeljenih baza podataka (podeljene baze → paralelna obrada → tolerancija na otkaz servera → relevantni rezultati), omogućavajući odziv za manje od 0,5 sekunde.

**P11. Šta je inkrementalni rast?**
Sposobnost sistema da se postupno proširuje dodavanjem novih računara, kako raste količina podataka za obradu ili broj korisnika.

**P12. Navedi primer deljenja resursa u DS.**
Distribuirani fajl sistemi (npr. GFS, HDFS) i Web omogućavaju da više korisnika pristupa istim podacima i periferijama.

---

## 4. Mane distribuiranih sistema

**P13. Koje su tri glavne mane DS u odnosu na centralizovane sisteme?**
1. Veća kompleksnost (softver je teže razviti, debagiranje je otežano jer postoji više aktivnih procesa koji komuniciraju)
2. Zavisnost od umrežavanja (mreža može otkazati ili se zagušiti)
3. Bezbednosni problemi (lakša dostupnost resursa vodi ka bezbednosnim rizicima, npr. slanje lozinki nešifrovano kroz mrežu)

**P14. Zašto je debagiranje DS teže nego kod centralizovanih sistema?**
Zato što postoji više aktivnih procesa koji međusobno komuniciraju, pa je teško pratiti i reprodukovati greške – to može biti "noćna mora" za programera.

---

## 5. Osnovne osobine (izazovi) pri projektovanju DS

**P15. Navedi četiri osnovna izazova/cilja pri projektovanju distribuiranih sistema.**
Heterogenost, transparentnost, otvorenost, skalabilnost.

### Heterogenost

**P16. Kroz šta se ogleda heterogenost DS?**
- Hardver računara (različit skup instrukcija, big-endian/little-endian redosled bajtova)
- Operativni sistemi (različit interfejs za razmenu poruka, npr. Socket API kod UNIX/SPARC vs. Winsock API kod WINTEL platforme)
- Programski jezici (različita reprezentacija karaktera i struktura podataka)
- Implementacije različitih proizvođača (dok se ne usvoje zajednički standardi, različite implementacije ne mogu međusobno komunicirati – OSI model rešava ovaj problem)

### Transparentnost

**P17. Šta je transparentnost distribucije?**
To je svojstvo DS da izgleda kao jedinstveni koherentni sistem, gde je distribucija objekata (procesa i resursa) nevidljiva korisnicima i aplikacijama. Cilj je sakriti činjenicu da su procesi i resursi fizički distribuirani.

**P18. Navedi i objasni tipove transparentnosti.**
- **Pristupna** – skriva razlike u načinu pristupa i reprezentaciji podataka (npr. razlike u imenovanju fajlova različitih OS).
- **Lokacijska** – korisnik ne zna gde je resurs fizički lociran (postiže se logičkim imenima, npr. URL).
- **Migraciona** – resurs može promeniti lokaciju a da klijent to ne primeti (npr. mobilni telefoni tokom poziva).
- **Konkurencije** – više procesa istovremeno koristi isti resurs bez međusobnog uticaja; konzistentnost se postiže zaključavanjem i sinhronizacijom.
- **Replikacije** – postoji više kopija istog resursa (radi performansi i pouzdanosti), a korisnik ne zna za njih; sve kopije imaju isto ime, pa replikacija automatski podrazumeva i lokacijsku transparentnost.
- **Za otkaze** – korisnik ne zna da je resurs u kvaru; sistem mora automatski da realocira resurse na ispravne delove sistema.
- **Paralelizacije** – paralelizacija procesa se odvija transparentno za programera i korisnika (primer: Hadoop).

**P19. Zašto je teško postići transparentnost za otkaze?**
Jer je često nemoguće razlučiti da li je server (usluga) nedostupan ili je samo mreža zagušena – ovo je jedan od najtežih problema kod DS.

### Otvorenost

**P20. Šta znači da je DS otvoren?**
Otvoren DS nudi komponente koje se lako mogu koristiti ili integrisati u druge sisteme, pridržavajući se standardnih pravila koja definišu sintaksu i semantiku usluga – obično definisanih putem interfejsa.

**P21. Čemu služi IDL (Interface Definition Language)?**
Služi za opis interfejsa – sadrži definicije dostupnih funkcija zajedno sa tipovima parametara, tipom rezultata i mogućim izuzecima, omogućavajući proizvoljnom procesu da pristupi usluzi.

**P22. Šta su interoperabilnost i prenosivost?**
- **Interoperabilnost** – stepen u kome dve implementacije sistema/komponenti različitih proizvođača mogu međusobno da sarađuju oslanjajući se samo na zajednički standard.
- **Prenosivost** – mera u kojoj se aplikacija razvijena za jedan DS (A) može izvršavati bez izmena na drugom DS (B) koji implementira iste interfejse (npr. MPICH i OpenMPI implementacije).

### Skalabilnost

**P23. Šta je skalabilnost i kroz koje dimenzije se posmatra?**
Skalabilnost je svojstvo sistema da održi adekvatan nivo performansi bez obzira na porast broja korisnika/resursa, geografske udaljenosti ili broja administrativnih domena. Posmatra se kroz tri dimenzije:
1. Skalabilnost u odnosu na broj korisnika i resursa
2. Skalabilnost u odnosu na geografsku udaljenost
3. Administrativna skalabilnost (upravljanje kroz više administrativnih domena)

**P24. Zbog čega centralizovane usluge, podaci i algoritmi predstavljaju problem za skalabilnost?**
- Centralizovana usluga na jednom serveru postaje usko grlo sa porastom broja korisnika.
- Centralizovani podaci (npr. da DNS nije distribuiran) bi doveli do preopterećenja jednog servera.
- Centralizovani algoritmi (npr. izračunavanje optimalnih ruta na jednoj mašini) postaju procesorski neizvodljivi kada broj zahteva premaši određeni nivo.

**P25. Koje osobine imaju decentralizovani algoritmi?**
- Nijedna mašina nema kompletnu informaciju o stanju sistema.
- Mašine odlučuju samo na osnovu lokalnih podataka.
- Otkaz jedne mašine ne narušava rad sistema.
- Ne postoji pretpostavka o globalnom časovniku.

**P26. Zašto je pretpostavka o nepostojanju globalnog časovnika važna?**
Zato što u DS svaki računar ima sopstveni lokalni časovnik – oni mogu imati različite brzine otkucavanja, kasniti ili žuriti, i ne mogu biti apsolutno precizno sinhronizovani zbog mrežnih kašnjenja. Algoritam koji bi zahtevao globalno tačno vreme (npr. "tačno u 12:00:00...") ne bi korektno funkcionisao. Što je sistem veći, neslaganje časovnika je veće.

**P27. Objasni razliku između vertikalnog i horizontalnog skaliranja.**
- **Vertikalno skaliranje (scale up)** – povećanje kapaciteta postojećih resursa (dodavanje memorije, jači procesor, brži mrežni moduli).
- **Horizontalno skaliranje (scale out)** – proširivanje sistema dodavanjem većeg broja računara.

**P28. Zašto sinhrona komunikacija dobro funkcioniše u LAN, a slabo u WAN?**
Kod sinhrone komunikacije klijent se blokira dok čeka odgovor od servera. U LAN je kašnjenje kratko (nekoliko mikrosekundi), dok je u WAN i do tri reda veličine duže, što je neprihvatljivo za interaktivne aplikacije.

**P29. Navedi tri tehnike skaliranja i objasni ih.**
1. **Skrivanje komunikacionog kašnjenja** – korišćenje asinhrone komunikacije (klijent ne čeka odgovor, već radi drugi posao) ili prebacivanje dela obrade na klijentsku stranu (npr. JavaScript validacija formi umesto slanja svakog polja serveru).
2. **Distribucija** – deljenje komponenti na manje delove koji se raspoređuju na više mašina (primer: DNS, Web, Hadoop MapReduce, Apache Spark).
3. **Replikacija** – postojanje više kopija resursa radi veće dostupnosti i balansiranja opterećenja (primer: HDFS, replikovani Web serveri, Dropbox).

**P30. Koji je glavni problem koji uvode tehnike skaliranja (posebno replikacija)?**
Problem konzistentnosti – modifikacija jedne kopije dovodi do neslaganja sa ostalim kopijama. Globalna sinhronizacija svih kopija je gotovo nemoguća (ili neisplativa) u DS, zbog čega DS mora tolerisati izvestan nivo nekonzistentnosti.

---

## 6. Middleware

**P31. Šta je middleware i zašto je potreban?**
Middleware je softverski sloj koji se nalazi između mrežnog operativnog sistema i distribuiranih aplikacija. Njegov cilj je da sakrije heterogenost platforme (hardvera, OS, mreže) od aplikacije i da oslobodi programera detalja vezanih za interprocesnu komunikaciju, sinhronizaciju i bezbednost.

**P32. Šta nedostaje OSI referentnom modelu, a middleware to rešava?**
OSI modelu nedostaje jasna granica između aplikacije, aplikativno-specifičnih protokola (npr. FTP, HTTP) i protokola opšte namene (middleware protokoli) koji pružaju usluge poput autentifikacije, autorizacije i sinhronizacije, a nezavisni su od konkretne aplikacije.

**P33. Da li middleware upravlja pojedinačnim čvorom u mreži?**
Ne. Time se bavi lokalni operativni sistem. Middleware nudi kompletan skup usluga aplikaciji i ne dozvoljava "preskakanje" na niže nivoe (npr. direktno korišćenje soketa mimo middleware-a).

**P34. Navedi modele middleware komunikacije.**
- RPC (Remote Procedure Call)
- RMI (Remote Method Invocation)
- MOM (Message Oriented Middleware, npr. MPI)
- Distribuirani objekti (CORBA i sl.)

**P35. Objasni princip rada RPC modela.**
Resursi se modeluju kao procedure. Proces poziva proceduru čija je implementacija na drugoj mašini; parametri se transparentno prenose udaljenoj mašini, procedura se izvršava, a rezultat se vraća pozivaocu. Proces ima utisak da se procedura izvršila lokalno (transparentnost lokacije, implementacije i jezika).

**P36. Koji su glavni problemi kod RPC modela?**
- Kako preneti parametre
- Povezivanje (pronalaženje udaljene procedure)
- Semantika u slučaju grešaka

**P37. Objasni princip rada objektno orijentisanog (distribuiranog objekta) pristupa.**
Resursi se modeluju kao objekti koji sadrže podatke i metode. Svaki objekat implementira interfejs koji skriva unutrašnje detalje; objekat je lociran na jednoj mašini, a interfejs je raspoloživ na drugim mašinama. Kada proces pozove metod, lokalni interfejs transformiše poziv u poruku koja se šalje objektu, objekat izvršava metod i vraća rezultat.

---

## 7. Tipovi distribuiranih sistema

**P38. Kako se DS dele na arhitekturnom nivou?**
Klijent-server i peer-to-peer arhitektura.

**P39. Kako se DS dele prema oblasti primene?**
- Distribuirani računarski sistemi (klasteri, grid)
- Distribuirani informacioni sistemi (sistemi za obradu transakcija, sistemi za integraciju poslovnih aplikacija)
- Ugrađeni (sveprisutni) DS (mobilni uređaji, senzorske mreže)

### Klasteri i grid

**P40. Šta karakteriše klaster i po čemu se razlikuje od grid sistema?**
- **Klaster** – grupa sličnih (homogenih) radnih stanica/PC na malom rastojanju, povezanih brzom lokalnom mrežom; svi čvorovi izvršavaju isti OS; koristi se za paralelno izračunavanje.
- **Grid** – federacija računarskih sistema koji mogu biti u različitim administrativnim domenima i razlikovati se po hardveru, softveru i mrežnoj tehnologiji; ne pretpostavlja se homogenost.

**P41. Objasni Beowulf arhitekturu klastera.**
Klaster baziran na Linux OS, organizovan po master-slave paradigmi: master čvor prikuplja poslove korisnika, deli ih na zadatke i raspoređuje ih po računarskim čvorovima, i predstavlja korisnički interfejs (izvršava middleware za pokretanje i upravljanje klasterom). Ostali čvorovi imaju standardni OS proširen middleware funkcijama (komunikacija, skladištenje).

**P42. Šta je MPI?**
Message Passing Interface – najčešći tip middleware kod klastera; biblioteka funkcija koja obezbeđuje efikasne mehanizme za razmenu poruka između čvorova koji paralelno obrađuju delove aplikacije.

**P43. Šta je virtuelna organizacija u kontekstu grid sistema?**
Kolaborativna grupa korisnika koja udružuje i deli resurse iz različitih administrativnih domena. Middleware grid sistema mora omogućiti pristup resursima iz različitih domena samo korisnicima/aplikacijama koji pripadaju toj virtuelnoj organizaciji, uz mehanizme autentifikacije.

### Distribuirani informacioni sistemi

**P44. Šta je transakcija?**
Skup operacija koji se izvršava kao jedna nedeljiva (atomična) operacija – ili se sve operacije izvrše bez greške, ili se ne izvrši nijedna, čime sistem (baza podataka, fajl sistem) ostaje u konzistentnom stanju.

**P45. Navedi osnovne komande za rad sa transakcijama.**
- `Begin_transaction` – početak transakcije
- `End_transaction` – kraj transakcije (potvrda)
- `Abort_transaction` – prekid transakcije i vraćanje na prethodno stanje
- `Read` – čitanje podataka
- `Write` – upis podataka

**P46. Objasni ACID svojstva transakcija.**
- **Atomicity (atomarnost)** – transakcija se obavlja kompletno ili se uopšte ne obavlja.
- **Consistency (konzistentnost)** – transakcija ne narušava skup invarijanti/ograničenja sistema (npr. ne dozvoljava negativno stanje računa).
- **Isolation (izolovanost)** – konkurentne transakcije se izvršavaju kao da su serijalizovane, bez međusobnog uticaja.
- **Durability (trajnost)** – kada je transakcija jednom potvrđena, njeni efekti se ne mogu poništiti.

**P47. Šta je ugnježdena (distribuirana) transakcija?**
Transakcija sastavljena od više podtransakcija raspoređenih na više servera. Ugnježdena transakcija se može uspešno potvrditi (komitovati) samo ako su sve njene podtransakcije uspešno izvršene.

**P48. Šta je TP monitor i koji protokol koristi?**
Monitor za obradu transakcija (Transaction Processing monitor) je middleware koji omogućava aplikaciji da pristupi većem broju servera/baza podataka kroz transakcioni programski model. Koordinira potvrdu podtransakcija koristeći protokol **2PC (Two-Phase Commit)**.

**P49. Objasni primer distribuirane transakcije rezervacije putovanja kroz 2PC.**
Transakcija T (planiranje putovanja) sastoji se od podtransakcija T1, T2, T3 (rezervacije pojedinačnih letova), od kojih se svaka izvršava na drugom serveru. TP monitor šalje zahteve svim serverima i prikuplja njihove glasove (vote-commit ili vote-abort):
- Ako sve podtransakcije uspeju → TP monitor radi konačni **commit**, sve rezervacije postaju važeće.
- Ako bilo koja podtransakcija ne uspe (npr. nema mesta u avionu) → TP monitor radi **rollback** svih podtransakcija, sve rezervacije se poništavaju.

### Sveprisutni distribuirani sistemi

**P50. Po čemu se sveprisutni DS razlikuju od klasičnih (klaster/grid/informacioni) sistema?**
Klasični DS karakteriše relativna stabilnost (fiksni čvorovi, stalna kvalitetna mrežna konekcija). Sveprisutni DS (mobilni i ugrađeni sistemi) karakteriše nestabilnost kao normalno ponašanje: uređaji su mali, baterijski napajani, mobilni, sa samo bežičnim vezama, i bez administrativnog upravljanja – uređaji sami moraju da otkriju okruženje, integrišu se u njega i prilagode se promenama.

**P51. Navedi primere sveprisutnih distribuiranih sistema.**
- Kućni sistemi (automatsko paljenje svetla, navodnjavanje, alarmni sistemi)
- Elektronski sistemi za monitoring pacijenata (praćenje vitalnih parametara)
- Senzorske mreže (merenje temperature, vlažnosti i slanje podataka baznoj stanici)

---

## 8. Dodatna pitanja (za proveru razumevanja)

**P52. Objasni razliku između sinhrone i asinhrone komunikacije i njihovu vezu sa skalabilnošću.**
Kod sinhrone komunikacije klijent se blokira dok čeka odgovor servera, što dobro funkcioniše u LAN, ali loše u WAN zbog dužih kašnjenja. Asinhrona komunikacija omogućava klijentu da nastavi rad dok čeka odgovor (obaveštava se prekidom kada odgovor stigne), čime se kašnjenje "sakriva" i poboljšava geografska skalabilnost. Kod interaktivnih aplikacija bolje rešenje je prebacivanje dela obrade na klijentsku stranu.

**P53. Zašto replikacija automatski podrazumeva lokacijsku transparentnost?**
Zato što sve replike (kopije) resursa imaju isto ime/identitet – korisnik pristupa resursu preko istog imena bez obzira na to koja se kopija zapravo koristi, što je upravo definicija lokacijske transparentnosti.

**P54. Uporedi vertikalno i horizontalno skaliranje sa aspekta troška i mogućnosti.**
Vertikalno skaliranje (scale up) ima brzo rastuću cenu u odnosu na dobijeni kapacitet i ograničeno je fizičkim limitima jedne mašine. Horizontalno skaliranje (scale out) raste u ceni približno linearno sa kapacitetom, jer se postiže dodavanjem jeftinijih, standardnih mašina, i teorijski nema gornju granicu.

---

*Napomena: Skripta je pripremljena na osnovu prezentacije "Distribuirani sistemi – uvod" (VIII semestar, Elektronski fakultet Niš, predmet Distribuirani sistemi).*
