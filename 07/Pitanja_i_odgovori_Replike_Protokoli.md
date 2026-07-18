# Distribuirani sistemi – Konzistentnost, pozicioniranje replika i protokoli konzistencije
## Skripta pitanja i odgovora

---

## 1. Konzistentnost sa stanovišta klijenta (Client-centric consistency)

**P: Šta podrazumeva konzistentnost sa stanovišta klijenta (client-centric consistency)?**
O: Naglasak je na održavanju konzistentnog pogleda na skladište podataka sa stanovišta pojedinačnog klijentskog procesa. Klijent je mobilan i može sa različitih lokacija čitati ili modifikovati kopije skladišta podataka, pa se postavlja pitanje koje promene klijent može videti kada promeni lokaciju sa koje pristupa podacima.

**P: Koja četiri modela konzistencije postoje posmatrano u odnosu na klijenta?**
O: Monotona čitanja (monotonic reads), monotoni upisi (monotonic writes), čitaj svoje upise (read-your-writes) i upisi nakon čitanja (writes-follow-reads).

**P: Šta označava oznaka xi[t]?**
O: Označava verziju podatka x u lokalnoj kopiji Li u trenutku t.

**P: Šta označava WS xi[t]?**
O: Skup write operacija na lokalnoj kopiji Li koje su dovele do verzije xi promenljive x u trenutku t.

**P: Šta znači oznaka WS(xi[t1], xj[t2])?**
O: Da se skup operacija WS xi[t1] obavio na lokalnoj kopiji Li, a zatim je propagiran i primenjen i na lokalnoj kopiji Lj, gde je nakon toga obavljeno i novo ažuriranje u trenutku t2.

---

## 2. Monotona čitanja (Monotonic Reads)

**P: Šta garantuje model monotonih čitanja?**
O: Ako proces pročita vrednost podatka x na jednoj lokaciji, svako sledeće čitanje tog podatka će vratiti istu ili noviju vrednost, bez obzira na kojoj lokaciji se to čitanje obavlja.

**P: Koji je klasičan primer monotonih čitanja?**
O: Čitanje email-a dok je klijent u pokretu – svaki put kada se klijent poveže na drugi e-mail server, treba da vidi sve mailove koje je video prethodni put, a može videti i novije.

**P: Kako se garantuje monotono čitanje kada proces prvo čita x na lokaciji L1, a zatim na lokaciji L2?**
O: Skup operacija WS(x1), koji je doveo do vrednosti pročitane na L1, mora biti propagiran na lokaciju L2 pre nego što se na njoj obavi drugo čitanje – tj. na L2 mora postojati WS(x1, x2), gde je ažuriranje WS(x2) izvršeno nakon WS(x1).

**P: Kada konzistencija tipa monotonic-read NIJE garantovana?**
O: Kada su na lokaciji L2, sa koje proces P čita x=x2, obavljene samo operacije WS(x2), bez garancije da taj skup sadrži sve operacije iz skupa WS(x1) koje su prethodno viđene na lokaciji L1.

---

## 3. Monotoni upisi (Monotonic Writes)

**P: Koji uslov mora biti ispunjen kod modela monotonih upisa?**
O: Ako proces P obavi write operaciju nad podatkom x na lokaciji L1, proces P može obaviti sledeću write operaciju nad x na lokaciji L2 tek nakon što je na L2 obavljeno ažuriranje koje potiče sa lokacije L1.

**P: Kako izgleda primer monotonih upisa?**
O: Proces P obavlja write operaciju W(x1) na lokaciji L1. Kasnije, proces P obavlja drugu write operaciju W(x2) na lokaciji L2. Da bi bila ispoštovana konzistencija monotonih upisa, WS(x1) mora biti propagiran i primenjen na L2 pre nego što se izvrši W(x2).

**P: Naveden je i praktičan primer monotonih upisa – koji?**
O: Ažuriranje softvera – nove verzije/izmene se moraju primenjivati redosledom koji odgovara prethodnim ažuriranjima, kako ne bi došlo do nekonzistentnog stanja između lokacija.

**P: Kada konzistencija monotonic-write NIJE garantovana?**
O: Kada nedostaje propagiranje ažuriranja W(x1) sa lokacije L1 na lokaciju L2, pa nema garancije da kopija podatka x na kojoj se obavlja druga write operacija ima istu ili noviju vrednost od W(x1).

---

## 4. Čitaj svoje upise (Read-Your-Writes)

**P: Šta garantuje read-your-writes konzistencija?**
O: Efekat write operacije koju obavlja proces P nad podatkom x biće uvek viđen kasnijim čitanjem tog istog podatka od strane istog procesa P, bez obzira na kojoj lokaciji se čitanje obavlja.

**P: Kako se ovaj model razlikuje/upoređuje sa monotonim čitanjima?**
O: Model je veoma blizak monotonom čitanju, ali dok monotono čitanje garantuje redosled među čitanjima, read-your-writes garantuje da će sopstveni upisi procesa uvek biti vidljivi u njegovim kasnijim čitanjima.

**P: Šta označava zapis WS(x1; x2) u kontekstu read-your-writes?**
O: Označava da je write operacija W(x1), obavljena na lokaciji L1, obuhvaćena skupom WS(x2) na lokaciji L2 – tj. da je efekat W(x1) propagiran na L2 pre kasnijeg čitanja.

**P: Kada read-your-writes konzistencija nije zadovoljena?**
O: Kada W(x1) nije obuhvaćen skupom WS(x2), što znači da efekat prethodne write operacije procesa P nije propagiran na lokaciju sa koje proces kasnije čita.

---

## 5. Upis sledi čitanje (Writes-Follow-Reads)

**P: Šta garantuje writes-follow-reads konzistencija?**
O: Write operacija koju proces P obavlja nad podatkom x, nakon što je prethodno pročitao taj podatak, obavlja se nad istom ili novijom vrednošću podatka x koju je proces pročitao.

**P: Opišite primer writes-follow-reads konzistencije koji je zadovoljen.**
O: Proces čita x na lokalnoj kopiji L1 (dobijajući R(x1) iz WS(x1)). Kada isti proces kasnije obavlja write na lokaciji L2, taj WS(x1) mora biti sadržan u WS(x1;x2) na L2, čime se garantuje da je W(x2) urađen na kopiji koja je konzistentna sa onom koju je proces pročitao na L1.

**P: Kada writes-follow-reads konzistencija nije garantovana?**
O: Kada nema garancije da je W(x2) izvršen na kopiji koja je konzistentna sa kopijom koju je proces prethodno pročitao na lokaciji L1.

---

## 6. Pozicioniranje replika – vrste replika

**P: Koje je ključno pitanje u distribuiranom sistemu koji podržava replikaciju?**
O: Gde, kada i ko može obavljati repliciranje, i koji mehanizmi se koriste za održavanje konzistencije replika.

**P: Koje tri vrste replika postoje prema tome ko ih inicira/kreira?**
O: Permanentne replike, replike inicirane od strane servera i replike inicirane od strane klijenta.

**P: Šta su permanentne replike?**
O: Početni skup replika koje formiraju distribuirano skladište podataka; njihov broj je obično mali.

**P: Kroz koja dva oblika se javlja distribucija Web sajta kao primer permanentnih replika?**
O: Kroz repliciranje fajlova Web sajta na ograničenom broju servera u okviru jedne lokacije (klaster servera, sa balansiranjem zahteva npr. round-robin metodom) i kroz mirroring, gde se sajt kopira na geografski razmeštene mirror servere, a klijent bira jedan od njih.

**P: Šta su replike inicirane od strane servera i zašto se formiraju?**
O: To su privremene replike koje se formiraju na inicijativu vlasnika skladišta podataka radi poboljšanja performansi (vremena odziva) ili rasterećenja servera kada sa neke udaljene lokacije stiže veliki broj zahteva; nazivaju se i push replike.

**P: Na osnovu čega algoritam dinamičke replikacije odlučuje gde i kada kreirati ili obrisati repliku?**
O: Na osnovu broja obraćanja fajlu i lokacije odakle zahtevi dolaze – ako broj pristupa fajlu F sa servera P na serveru Q pređe prag replikacije rep(Q,F), fajl F se replicira na server P.

**P: Kada se replika servera briše, i koje pravilo pri tome mora biti ispoštovano?**
O: Kada broj obraćanja fajlu na toj lokaciji padne ispod praga brisanja del(P,F), replika se može ukloniti, pod uslovom da to nije poslednja (jedina) preostala kopija tog fajla – uvek mora postojati bar jedna replika.

**P: Šta se dešava ako je broj zahteva između praga brisanja i praga replikacije?**
O: Fajl samo migrira sa jednog servera na drugi, bez kreiranja nove replike ili brisanja.

**P: Da li se serverski inicirane replike mogu menjati?**
O: Ne – one se najčešće koriste kao read-only kopije za klijente; jedino se permanentne replike mogu menjati radi održavanja konzistentnosti sistema.

**P: Šta su replike inicirane od strane klijenta i kako se drugačije nazivaju?**
O: To su replike poznate kao klijentski keš – lokalna memorija koju klijent koristi da privremeno sačuva kopiju podataka koje je upravo zatražio i dobio; keš može biti na klijentskoj mašini ili posebnoj mašini u okviru klijentovog LAN-a.

**P: Ko upravlja klijentskim kešom i kakvu obavezu ima izvorno skladište podataka?**
O: Upravljanje kešom je u potpunosti prepušteno klijentu; skladište podataka nema nikakvu obavezu da održava konzistentnost keša, iako klijent uz učešće skladišta može utvrditi da li je keširana kopija ustajala ili važeća.

---

## 7. Distribucija ažuriranja i inicijator ažuriranja

**P: Šta se sve može propagirati kod ažuriranja replika (tri opcije)?**
O: (1) samo obaveštenje o obavljenom ažuriranju (invalidacija ostalih kopija), (2) ažurirani podaci koji se prenose replikama, i (3) operacije koje su izazvale ažuriranje (aktivno repliciranje).

**P: Kada protokoli sa invalidacijom najbolje funkcionišu, a kada prenos podataka?**
O: Protokoli sa invalidacijom dobro funkcionišu kada je broj ažuriranja mnogo veći od broja čitanja (jer se mnoga ažuriranja mogu izvršiti lokalno bez ponovne invalidacije), dok prenos ažuriranih podataka dobro funkcioniše kada je odnos čitanje/upis relativno visok, tj. kada je broj čitanja mnogo veći od broja upisa.

**P: Koja je prednost, a koji nedostatak prenošenja operacija (aktivno repliciranje)?**
O: Prednost je da ažuriranja mogu biti obavljena uz minimalan mrežni saobraćaj, jer se ne prenose podaci već operacije; nedostatak je što zahteva više procesorskog vremena, naročito ako su operacije kompleksne.

**P: Šta je push pristup ažuriranju i gde se najčešće koristi?**
O: Ažuriranje se prenosi replikama na inicijativu servera, iako to replike nisu tražile; koristi se između permanentnih i serverski iniciranih replika, kada je potrebno postići visok nivo konzistencije.

**P: Šta je pull pristup ažuriranju i gde se tipično koristi?**
O: Server ili klijent zahteva od drugog servera da mu prosledi ažuriranje ako postoji; tipično se koristi kod klijentskih (Web) keševa, koji prvo proveravaju da li su keširani podaci još uvek validni.

**P: Kako izgleda razmena poruka kod pull pristupa (Web keš)?**
O: Klijent šalje serveru poruku oblika {data X, timestamp ti, OK?}; server odgovara sa OK, ako podaci nisu promenjeni, ili šalje ažurirane podatke oblika {data X, timestamp ti+k} ako su podaci u međuvremenu bili modifikovani.

---

## 8. Protokoli konzistencije – opšta podela

**P: Šta opisuju protokoli konzistencije?**
O: Opisuju konkretnu implementaciju određenog modela konzistencije.

**P: Koje tri vrste protokola konzistencije postoje?**
O: Protokoli zasnovani na postojanju primarne kopije (i backup kopija), protokoli sa više ravnopravnih replika (replicirani write protokoli) i keš koherentni protokoli.

**P: Šta karakteriše protokole zasnovane na primarnoj kopiji?**
O: Write operacija se može izvršiti samo na primarnoj kopiji podatka, koja je zadužena za koordinaciju svih upisa nad tim podatkom.

**P: Šta karakteriše protokole sa više ravnopravnih replika?**
O: Write operacija može biti inicirana u bilo kojoj replici, bez postojanja jedinstvene primarne kopije.

**P: Ko inicira keš koherentne protokole?**
O: Klijent, a ne server.

**P: Koja vrsta protokola se najčešće koristi kada je potrebno postići sekvencijalnu konzistenciju?**
O: Protokoli zasnovani na primarnoj kopiji.

**P: Koja je razlika između remote-write i local-write protokola zasnovanih na primarnoj kopiji?**
O: Kod remote-write protokola primar je fiksiran na udaljenom serveru, pa se write operacije uvek šalju njemu; kod local-write protokola primar se pomera (migrira) na proces koji inicira write, pa se upis može obaviti lokalno.

---

## 9. Remote-write protokoli

**P: Kako funkcioniše najjednostavniji remote-write protokol?**
O: Sve read i write operacije se obavljaju isključivo na udaljenom (primarnom) serveru; podaci u suštini nisu replicirani, već se nalaze samo na jednom serveru, dok ostale kopije (ako postoje) služe samo kao backup.

**P: Kako funkcioniše modifikovani remote-write (primar-backup) protokol?**
O: Procesima je dozvoljeno da čitanje obavljaju na lokalno raspoloživoj kopiji, dok se write operacije uvek obavljaju na fiksnoj primarnoj kopiji; ovakvi protokoli se zovu primar-backup protokoli.

**P: Opišite korake primar-backup protokola prilikom write operacije.**
O: Proces šalje write operaciju primaru; primar obavlja ažuriranje na svojoj lokalnoj kopiji, zatim prosleđuje ažuriranje backup serverima; svaki backup obavlja ažuriranje i šalje potvrdu primaru; kada primar primi potvrde od svih backup servera, šalje potvrdu procesu koji je inicirao write.

**P: Koje su dve mogućnosti u pogledu blokiranja procesa koji inicira write operaciju?**
O: Proces može čekati sve dok svi backup serveri ne potvrde ažuriranje (potpuno blokirajuća operacija), ili se može blokirati samo dok primar ne obavi sopstveno ažuriranje, nakon čega nastavlja s radom dok se backup serveri ažuriraju u pozadini.

**P: Zašto remote-write protokoli baziran na primarnoj kopiji lako obezbeđuju sekvencijalnu konzistenciju?**
O: Zato što primar može urediti sve dolazne write zahteve na globalno jedinstven način, pa svi procesi vide sve write operacije u istom redosledu, bez obzira sa kog backup servera čitaju podatke.

**P: Gde se tradicionalno primenjuju remote-write protokoli?**
O: Kod distribuiranih baza podataka i fajl sistema koji zahtevaju visok nivo otpornosti na greške, pri čemu su replike najčešće locirane u istom LAN-u.

---

## 10. Local-write protokoli

**P: Kako funkcioniše local-write protokol?**
O: Primarna kopija migrira između procesa koji žele da obave write operaciju – kada proces želi da ažurira podatak X, prvo locira trenutnu primarnu kopiju i prebacuje je (privlači) na sopstvenu lokaciju, gde zatim obavlja upis.

**P: Koja je osnovna prednost local-write pristupa?**
O: Više uzastopnih write operacija od strane istog procesa može biti obavljeno lokalno, dok procesi koji samo čitaju mogu i dalje pristupati svojim lokalnim kopijama.

**P: Pod kojim uslovom je moguće ostvariti prednosti local-write pristupa?**
O: Samo ako se koristi neblokirajući protokol koji dozvoljava da se ažuriranja proslede replikama tek nakon što je primar (na novoj lokaciji) već obavio ažuriranje.

**P: Gde se local-write pristup najčešće koristi i kako funkcioniše preko centralnog servera?**
O: Koristi se u distribuiranim fajl sistemima; može postojati centralni server preko koga prolaze sve write operacije – server privremeno dozvoljava jednoj replici da obavi seriju lokalnih upisa, a kada ta replika završi posao, ažuriranje se šalje centralnom serveru, koji ga dalje prosleđuje svim ostalim replikama.

---

## 11. Replicirani write protokoli

**P: Po čemu se replicirani write protokoli razlikuju od protokola zasnovanih na primarnoj kopiji?**
O: Write operacija se kod njih može obaviti na više replika istovremeno, umesto isključivo na jednoj (primarnoj) replici.

**P: Koje dve podvrste replicirаnih write protokola postoje?**
O: Aktivna replikacija (operacija se prosleđuje svim replikama) i protokoli zasnovani na kvorumu i većinskom glasanju.

**P: Šta karakteriše aktivnu replikaciju?**
O: Sve replike su ravnopravne, svaka ima proces koji obavlja ažuriranja, a ažuriranja se prosleđuju u obliku operacije (ne podataka) svim replikama.

**P: Koji je ključni problem kod aktivne replikacije i kako se rešava?**
O: Problem je što operacije moraju biti obavljene u istom redosledu na svim replikama, što zahteva mehanizam potpuno uređene grupne komunikacije – može se koristiti Lamportove vremenske markice ili centralni koordinator (sekvencer) koji svakoj operaciji dodeljuje jedinstven redni broj pre nego što je prosledi svim replikama.

**P: Koji nedostatak se javlja kod korišćenja centralnog sekvencera?**
O: Javlja se problem skaliranja, jer sekvencer predstavlja potencijalno usko grlo sistema.

**P: Šta je osnovna ideja protokola zasnovanih na kvorumu?**
O: Klijenti moraju zatražiti i dobiti dozvolu od više servera pre nego što obave čitanje ili upis repliciranih podataka.

**P: Koliko servera klijent mora kontaktirati da bi ažurirao fajl repliciran na N servera (osnovna, simetrična šema)?**
O: Najmanje N/2 + 1 (većinu) servera, i mora dobiti njihovu dozvolu; nakon postignutog konsenzusa, fajlu se dodeljuje novi, jedinstveni broj verzije.

**P: Zašto je bar jedan server, prilikom čitanja, garantovano da poseduje najnoviju verziju fajla?**
O: Jer klijent za čitanje takođe mora kontaktirati najmanje N/2 + 1 servera, pa se skup servera kontaktiranih za čitanje uvek preklapa sa skupom koji je učestvovao u poslednjem ažuriranju (write kvorumu).

**P: U čemu se sastoji opštija Giffordova šema kvoruma?**
O: Za N replika, čitanje zahteva postizanje read kvoruma od najmanje NR servera, dok modifikacija fajla zahteva write kvorum od najmanje NW servera, uz uslove koji sprečavaju preklapanje/konflikte između odgovarajućih skupova.

**P: Koje vrste konflikata sprečavaju uslovi za NR i NW?**
O: Prvi uslov (koji povezuje NR i NW) sprečava read-write konflikte, a drugi (koji povezuje NW sa N) sprečava write-write konflikte, tj. obezbeđuje preklapanje između svaka dva write skupa.

**P: Šta je ROWA protokol i po čemu je specifičan?**
O: ROWA (Read One, Write All) je izbor kod koga je NR postavljen na 1, što omogućava čitanje pronalaženjem bilo koje kopije fajla, ali zahteva da se ažuriranje izvrši na svim kopijama (NW = N).

**P: Zašto izbor NW ≤ N/2 može dovesti do write-write konflikta – objasnite na primeru?**
O: Ako jedan klijent izabere write skup {A,B,C,E,F,G}, a drugi klijent nezavisno izabere write skup {D,H,I,J,K,L} (bez preklapanja), oba ažuriranja mogu biti prihvaćena a da se konflikt uopšte ne detektuje, jer nijedan server nije bio deo oba skupa.
