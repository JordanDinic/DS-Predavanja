# Konzistentnost i replikacija — pitanja i odgovori

## Replikacija — uvod

**1. Koji su glavni razlozi za replikaciju u distribuiranim sistemima?**
Povećanje pouzdanosti i poboljšanje performansi. Pouzdanost raste jer se u slučaju kvara jedne kopije rad može nastaviti prebacivanjem na drugu kopiju. Performanse rastu jer je u geografski razuđenim sistemima moguće smanjiti vreme pristupa podacima (npr. replicirani Web serveri).

**2. Kako replikacija pomaže rešavanju problema skalabilnosti?**
Replikacija i keširanje su prihvaćene tehnike skaliranja sistema uz očuvanje performansi. Smeštanjem podataka bliže procesima koji ih koriste smanjuje se vreme pristupa, čime se ublažava problem skalabilnosti.

**3. Koji problemi nastaju kao posledica replikacije?**
Glavni problem je konzistentnost kopija. Da bi kopije bile usaglašene, ažuriranja moraju biti izvedena tako da se privremena nekonzistentnost ne primeti, a to ažuriranje dovodi do degradacije performansi (povećan mrežni saobraćaj) i može ugroziti skalabilnost.

**4. Kada se kopije podataka smatraju konzistentnim?**
Kada read operacija nad bilo kojom kopijom uvek vraća isti rezultat — tj. kada se ažuriranje na jednoj kopiji izvrši, sve ostale kopije moraju biti ažurirane pre nego što se izvrši bilo koja sledeća operacija.

**5. U čemu se sastoji dilema replikacije?**
Problem skalabilnosti se rešava replikacijom i keširanjem, ali održavanje kopija konzistentnim zahteva globalnu sinhronizaciju, koja je skupa u pogledu performansi — dakle "lek može biti opasniji od bolesti". Kada i kako se vrši ažuriranje kopija određuje cenu replikacije.

## Modeli konzistencije — osnovni pojmovi

**6. Šta je "skladište podataka" (data store) u kontekstu modela konzistencije?**
Termin koji označava deljive podatke koji mogu biti u deljivoj memoriji, deljivoj (distribuiranoj) bazi podataka ili distribuiranom fajl sistemu. Skladište može fizički biti raspoređeno na više mašina, a svaki proces ima lokalnu (ili obližnju) kopiju celog skladišta.

**7. Kako se razlikuju write i read operacije u ovom kontekstu?**
Operacija koja menja podatak označava se kao write, dok se operacija koja ne menja podatak označava kao read. Write operacije se propagiraju (prenose) ka drugim kopijama.

**8. Zašto je teško održati konzistentnost distribuiranih skladišta podataka?**
Zbog konačnog vremena potrebnog za distribuciju ažuriranja (komunikaciono kašnjenje) i zbog nepostojanja globalnog časovnika. Bez globalnog časovnika teško je odrediti koja write operacija je zaista poslednja, pa se moraju definisati modeli konzistencije koji ne zavise od globalnog vremena.

**9. Koji modeli konzistencije se obrađuju u prezentaciji?**
Striktna, sekvencijalna, kauzalna (uslovna) i FIFO konzistencija.

## Striktna konzistencija

**10. Kako glasi definicija striktne konzistencije?**
Bilo koja operacija čitanja nad podatkom X vraća rezultat poslednje write operacije nad X. Ova definicija je intuitivna, ali implicitno pretpostavlja postojanje globalnog vremena kako bi se nedvosmisleno odredila "poslednja" write operacija.

**11. Da li jednoprocesorski sistemi podržavaju striktnu konzistenciju?**
Da — jednoprocesorski sistemi tradicionalno podržavaju striktnu konzistenciju. Na primer, u nizu naredbi a:=1; a:=2; print(a), rezultat print(a) uvek mora biti 2.

**12. Zašto je striktnu konzistenciju nemoguće postići u distribuiranom sistemu?**
Ako proces Pi ažurira x sa 4 na 5 u trenutku t1, a proces Pj čita x u trenutku t2 > t1, striktna konzistencija zahteva da Pj uvek pročita 5, bez obzira koliko je (t2-t1) malo. Ako je razlika reda 1 nanosekunde a mašine su udaljene 3 metra, poruka o ažuriranju bi morala putovati 10 puta brže od brzine svetlosti, što je po specijalnoj teoriji relativiteta nemoguće. Zaključak: striktna konzistencija u DS je nemoguća jer se zasniva na apsolutnom globalnom vremenu.

**13. Šta znače oznake Wi(x)a i Ri(x)b?**
Wi(x)a označava da proces i izvršava write nad podatkom x posle čega je x=a. Ri(x)b označava da proces i izvršava read nad x i dobija vrednost b. Svaki podatak je inicijalno postavljen na NILL.

**14. Kako se rešava problem nemogućnosti striktne konzistencije u praksi?**
Uvode se slabiji modeli konzistencije koji nisu bazirani na apsolutnom vremenu. Iskustvo pokazuje da se programeri mogu izboriti sa slabijim modelima. U operativnim sistemima i paralelnim sistemima za sličan problem koriste se kritične sekcije i uzajamno isključivanje (npr. semafori), pri čemu se ne smeju praviti pretpostavke o brzini izvršavanja procesa.

## Sekvencijalna konzistencija

**15. Kako glasi definicija sekvencijalne konzistencije?**
Rezultat bilo kog izvršenja je isti kao da su sve read i write operacije svih procesa izvršene u nekom sekvencijalnom redosledu, pri čemu se operacije svakog pojedinačnog procesa pojavljuju u tom nizu u redosledu određenom njegovim programom. Bilo koje validno preplitanje operacija je prihvatljivo, ali svi procesi moraju videti isto preplitanje.

**16. Po čemu se sekvencijalna konzistencija razlikuje od striktne?**
U definiciji sekvencijalne konzistencije se ne spominje vreme, tj. ne postoji pojam "poslednje write operacije". Jedna moguća implementacija je korišćenje Lamportovih vremenskih markica.

**17. U primeru sa tri procesa (P1, P2, P3) i promenljivama x, y, z — koliko ukupno validnih sekvenci preplitanja postoji?**
Od 6! = 720 mogućih preplitanja šest naredbi, samo one sekvence koje poštuju programski redosled svakog procesa su validne. Razmatranjem 5! = 120 sekvenci koje počinju sa x=1, samo ¼ (30) je validno; isto važi za sekvence koje počinju sa y=1 (30) i z=1 (30) — ukupno 90 validnih sekvenci izvršenja.

**18. Šta predstavlja "potpis" (signature) izvršenja u primeru sa print naredbama?**
Ako se izlazi procesa P1, P2 i P3 navedu redom, dobija se 6-bitni niz koji karakteriše određeno preplitanje naredbi. Pošto svaka promenljiva može imeti vrednost 0 ili 1, teorijski postoji 2^6=64 mogućih potpisa, ali nisu svi validni pod sekvencijalnom konzistencijom.

**19. Zašto potpis 000000 nije validan?**
Zato što bi to značilo da se print naredba izvršila pre naredbe dodeljivanja u istom procesu, čime se narušava redosled naredbi definisan programom (sekvencijalna konzistencija zahteva da se operacije svakog procesa pojave u redosledu koji je zadao program).

**20. Zašto potpis 001001 nije moguć?**
Prva dva bita (00) znače da su y i z bili 0 kada je P1 štampao, što je moguće samo ako je P1 izvršio obe svoje naredbe pre nego što su P2 i P3 počeli. Sledeća dva bita (10) zahtevaju da P2 počne posle P1 ali pre P3. Poslednja dva bita (01) zahtevaju da se P3 završi pre nego što P1 počne — što je u suprotnosti sa zaključkom da P1 mora da počne prvi. Kontradikcija čini ovaj potpis nemogućim.

**21. U primeru sa promenljivama A, B, u, v, w — koje kombinacije vrednosti u, v, w nisu moguće pod sekvencijalnom konzistencijom?**
Nemoguće su kombinacije u kojima bi P2 morao da vidi A=1 pre A=0 (npr. kombinacija u=1, v=0, w=0), jer to narušava redosled po kome P1 prvo postavlja A=1, a zatim B=1, i taj redosled mora biti isti za sve procese.

## Kauzalna (uslovna) konzistencija

**22. Kako je definisana kauzalna (uslovna) konzistencija?**
Upisi koji su potencijalno uslovljeni (kauzalno povezani) moraju se videti u istom redosledu od strane svih procesa, dok konkurentni (nezavisni) upisi mogu biti viđeni u različitom redosledu u različitim procesima.

**23. Šta znači da su dva upisa "potencijalno uslovljena"?**
Ako postoji lanac operacija poput W(x)a → R(x)a → W(x)b (proces prvo pročita vrednost a, pa na osnovu toga upiše b), ili W(x)a → W(x)b od istog procesa, ti upisi su kauzalno povezani i svi procesi ih moraju videti u istom redosledu.

**24. Kako se kauzalna konzistencija razlikuje od sekvencijalne?**
Skladište podataka može biti kauzalno konzistentno, ali ne i sekvencijalno konzistentno — jer kauzalna konzistencija dozvoljava da nezavisni (konkurentni) upisi budu viđeni u različitom redosledu, dok sekvencijalna konzistencija to ne dozvoljava (svi procesi moraju videti identičan redosled svih operacija).

**25. Kako se u praksi implementira kauzalna konzistencija?**
Korišćenjem vektorskih časovnika (vector clocks) za uređenje međusobno zavisnih (kauzalno povezanih) događaja.

## FIFO konzistencija

**26. Kako glasi definicija FIFO konzistencije?**
Upisi koje obavi jedan proces vide svi ostali procesi u redosledu u kome su izdati, ali upisi različitih procesa mogu biti viđeni u različitom redosledu od strane različitih procesa. Jedino ograničenje je da upisi iz istog izvora moraju stići po redosledu.

**27. Kako se jednostavno implementira FIFO konzistencija?**
Svaki proces dodaje uz poruku ažuriranja par (id procesa, redni broj), a svi ostali procesi primenjuju ažuriranja od tog procesa po redosledu u kome su izvorno izdata.

**28. Zašto je potpis 001001 dozvoljen kod FIFO konzistencije, iako nije dozvoljen kod striktne konzistencije?**
Jer FIFO konzistencija ne garantuje ništa o redosledu viđenja upisa različitih procesa (samo da upisi istog procesa stižu po redu), pa je preplitanje koje daje potpis 001001 moguće pod FIFO pravilima, dok je zabranjeno pod strožim modelima poput striktne konzistencije.

## Sekvencijalna naspram FIFO konzistencije

**29. Koja je ključna razlika između sekvencijalne i FIFO konzistencije?**
Kod sekvencijalne konzistencije svi procesi vide događaje na isti način (isto globalno preplitanje). Kod FIFO konzistencije procesi mogu videti događaje u različitom redosledu, osim onih koji potiču od istog procesa.

**30. U primeru sa dve naredbe "kill" (P1: x=1; if(y==0) kill(P2); i P2: y=1; if(x==0) kill(P1);), koji su mogući ishodi pod sekvencijalnom, a koji pod FIFO konzistencijom?**
Pod sekvencijalnom konzistencijom mogući ishodi su: ubijen P1, ubijen P2, ili nijedan proces nije ubijen — nikada oba. Pod FIFO konzistencijom moguće je da oba procesa budu ubijena, jer različiti procesi mogu videti operacije u različitom redosledu.

## Slaba konzistencija

**31. Zašto je sekvencijalna konzistencija često previše restriktivna za realne aplikacije?**
Zato što zahteva da sve write operacije jednog procesa budu odmah vidljive u celom sistemu — npr. dok je proces u kritičnoj sekciji i menja podatke, svaka njegova write operacija mora biti prosleđena svim kopijama, iako drugi procesi u tom trenutku ionako ne mogu pristupiti tim podacima.

**32. Kako slaba konzistencija rešava taj problem?**
Dozvoljava se procesu da završi celu kritičnu sekciju, a tek onda se konačni rezultat prosleđuje ostalim kopijama, koristeći eksplicitnu sinhronizacionu promenljivu S i operaciju synchronize(S) koja sinhronizuje sve lokalne kopije skladišta.

**33. Šta se dešava kada proces P obavlja operacije nad svojom lokalnom kopijom pre sinhronizacije?**
Nema garancija kada će te izmene postati vidljive drugim procesima. Tek kada se skladište sinhronizuje, svi lokalni upisi procesa P se prosleđuju ostalim kopijama, a upisi drugih procesa se unose u P-ovu kopiju.

**34. Nad čim se, u suštini, ostvaruje konzistencija kod modela slabe konzistencije?**
Nad grupom operacija (celom kritičnom sekcijom, odnosno blokom operacija između dve sinhronizacije), a ne nad pojedinačnim read i write operacijama.

**35. Da li se privremena nekonzistentnost memorije javlja samo kod distribuiranih sistema?**
Ne — čak i kod centralizovanih (jednoprocesorskih) sistema postoji privremena nekonzistentnost memorije, npr. usled čuvanja međurezultata u registrima procesora pre nego što se upišu u glavnu memoriju.
