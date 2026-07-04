# Distribuirani sistemi – Komunikacija 2
## Proširen sažetak + najbitnija pitanja i odgovori

---

# DEO 1 – PROŠIREN SAŽETAK

## 1. OO pristup – RMI (Remote Method Invocation)

RMI je RPC prilagođen objektno-orijentisanom pristupu – omogućava pozivanje metoda objekata koji se nalaze na drugom hostu. Jedna od najvažnijih karakteristika objekta je da skriva svoju unutrašnjost (podatke/stanje i metode) od spoljašnjeg okruženja uz pomoć dobro definisanog **Interfejsa** – ova osobina je ključna za skrivanje heterogenosti u distribuiranim sistemima.

Objekat može imati skup metoda koje se mogu pozivati sa udaljenih računara – definisane su u **remote interface** fajlu (objekat može imati i druge, čisto lokalne metode). Ne postoji nijedan drugi način da se pristupi podacima unutar objekta osim preko metoda dostupnih kroz Interfejs:
- Interfejs predstavlja grupu metoda sa **praznim telom** (samo definicije metoda).
- Samo **server** ima implementacioni kod za objekat (i za lokalne i za udaljene metode).
- Pristup objektima preko Interfejsa je ključan faktor za DS: Interfejsi mogu biti locirani na jednoj mašini a objekat na drugoj – ova organizacija se zove **distribuirani (udaljeni) objekti**. Sam objekat nije distribuiran (nalazi se na jednoj mašini), **distribuiran je Interfejs**.

### Mehanizam poziva (proxy/skeleton)

Kada proces na jednoj mašini pozove metod (RMI) objekta koji se nalazi na drugoj mašini:
- Poziv se prosleđuje **proxy-ju** – proxy je predstavnik server objekta u adresnom prostoru klijenta.
- Proxy implementira isti Interfejs kao server (ali na drugi način).
- Proxy je analogan klijent stub-u: poziv metoda pakuje u poruku i prosleđuje serveru (poruka sadrži referencu udaljenog objekta, identifikator metode i parametre poziva).
- Objekat se nalazi na serveru, na kome se nalazi isti Interfejs kao na klijentskoj strani.
- Dolazni poziv se prvo prosleđuje serverskom stub-u (**skeleton**), koji raspakuje poruku u poziv odgovarajućeg metoda na server strani.

### Referenca udaljenog objekta

Poruka koja se šalje serverskom procesu mora specificirati objekat čiji se metod poziva – koristi se **identifikator udaljenog objekta jedinstven u čitavom distribuiranom sistemu** (remote object reference). Referenca mora garantovati jedinstvenost u prostoru i vremenu, a jedan od načina da se to postigne je da referenca sadrži:
- IP adresu računara i broj porta procesa koji je kreirao objekat, vreme kreiranja, lokalni broj objekta (inkrementira se pri svakom kreiranju objekta u procesu),
- opciono i informacije o Interfejsu udaljenog objekta (npr. ime Interfejsa).

Struktura (primer): `Internet address | port number | time | object number | interface of remote object` (svako polje 32 bita).

### RMI Middleware – primeri

Primeri middleware-a baziranih na pozivu udaljenih metoda: **CORBA** (Common Object Request Broker Architecture), **Java RMI**, **DCOM** (Distributed Component Object Model).

### Java RMI – detaljno

Ideja: logička separacija između interfejsa i objekta omogućava njihovu fizičku separaciju.
- **Udaljeni interfejs**: specificira skup metoda koji se mogu udaljeno pozivati. Međutim, **unutrašnje stanje udaljenog objekta nije distribuirano**.
- **Udaljeni objekat**: instanca klase koja implementira udaljeni interfejs.
- Objekti na udaljenim mašinama mogu pozvati samo metode koje pripadaju udaljenom interfejsu objekta.
- Lokalni objekti mogu pozvati i te metode i druge metode koje implementira udaljeni objekat.
- Udaljeni interfejsi, kao i svi interfejsi, **nemaju konstruktore**.

Pravila za udaljeni interfejs (Echo primer): nasleđuje `java.rmi.Remote`; svaka udaljena metoda mora biti deklarisana da baca `RemoteException` (radi rukovanja neuspelim komunikacijama); vraća samo 1 rezultat i ima 0, 1 ili više ulaznih parametara. Prenos parametara: po vrednosti za primitivne tipove i objekte klase koja implementira `java.io.Serializable`; po referenci za udaljene objekte.

Klasa udaljenog objekta (server): implementira udaljeni interfejs; nasleđuje `UnicastRemoteObject`; `super()` poziva konstruktor `UnicastRemoteObject` koji izvršava inicijalizacije potrebne da server čeka i uslužuje zahteve; implementira metodu deklarisanu u interfejsu.

### Povezivanje klijenta i servera – binder

Kako klijent doznaje referencu udaljenog objekta? Koristi se poseban distribuirani servis – **binder**:
- U binder-u postoji tabela sa preslikavanjem tekstualnog imena u referencu udaljenog objekta.
- Server registruje svoje udaljene objekte u binder-u.
- Klijent koristi binder da pronađe referencu udaljenog objekta.

**RMI registar** je binder za Java RMI – omogućava serveru da objavi uslugu, a klijentu da dobije stub za pristup njoj. Pristupa mu se putem metoda klase `Naming`, čiji metodi kao argument uzimaju URL-formatiran niz: `//imeRačunara:port/nazivObjekta`.

Koraci povezivanja (1–6):
1. U okviru serverske aplikacije se izvodi registrovanje udaljenog objekta u RMI registru pod određenim imenom (vrednost vezana za ime je referenca na udaljeni objekat).
2. Kada klijentska aplikacija želi da pristupi udaljenom objektu, kontaktira RMI registar sa imenom objekta i dobija referencu koja se koristi prilikom instanciranja stuba na klijentskoj strani.
3. Klijent poziv metoda udaljenog objekta upućuje stub-u (sintaksa za udaljeni poziv je identična lokalnoj).
4. Stub serijalizuje informacije potrebne za poziv (ID metode i ulazne parametre) i šalje ih skeletonu u poruci.
5. Skeleton prima poruku i deserijalizuje primljene podatke, prosleđuje poziv ka objektu koji implementira metod, metod se izvršava i generiše rezultat, skeleton serijalizuje povratnu vrednost i šalje je stub-u u poruci.
6. Stub prima poruku, deserijalizuje povratnu vrednost i vraća rezultat klijentu.

Napomena: `bind`/`rebind` se koriste u klasi `Naming` – `rebind` zamenjuje već postojeće povezivanje; vrši se za svaki serverski objekat koji se registruje, pri čemu se svaki identifikuje svojim logičkim imenom. Klijent koristi `lookup` metod klase `Naming` da bi pretražio i dobio instancu stub-a udaljenog objekta. Poziv udaljene metode je sinhroni blokirajući poziv.

### Prenos parametara

- **Po vrednosti**: primitivni tipovi podataka i serijalizovani objekti. Serijalizovani objekat čije se stanje može razlikovati na različitim lokacijama prenosi se po vrednosti; objekat se serijalizuje, šalje primaocu i deserijalizuje kako bi se izgradila lokalna kopija. U procesu primaocu se kreira **novi** objekat čije metode mogu izmeniti njegovo stanje nezavisno od originala.
- **Po udaljenoj referenci**: udaljeni objekti. Objekat koji je vezan za lokaciju u kojoj se izvršava (server) prenosi se putem udaljene reference – njegov stub se prosleđuje drugoj strani.

---

## 2. Tipovi komunikacija

Podela se vrši u odnosu na:
- **Postojanost (persistency)** – tranzijentna naspram perzistentne (istrajne) komunikacije.
- **Sinhronizaciju** – sinhrona naspram asinhrone komunikacije.
- **Vremensku zavisnost** – diskretna naspram strimovane (streaming) komunikacije.

### Perzistentne vs. tranzijentne
- **Perzistentne (istrajne)** – poruka se pamti u komunikacionom serveru (middleware-u) onoliko dugo koliko je potrebno da bi se isporučila odredištu.
- **Tranzijentne (privremene)** – poruka se odbacuje ako komunikacioni server nije u stanju da je isporuči sledećem serveru ili odredištu.

Kod perzistentne komunikacije, izvor (pošiljalac) ne mora biti aktivan nakon što dostavi poruku komunikacionom serveru, a prijemnik (primalac) se ne mora izvršavati u trenutku kada izvor pošalje poruku – zato aplikacija koja je poslala poruku ne mora nastaviti da se izvršava nakon predaje, a korisnički agent na prijemnoj strani ne mora da se izvršava kada poruka stigne.

**Primer: E-MAIL** – tipičan primer perzistentne komunikacije. Korisnički agent (aplikacija za kreiranje/slanje/prijem poruka) se izvršava na hostu; svaki host je povezan na tačno jedan mail server (odgovara komunikacionom serveru). Korisnički agent prosleđuje poruku svom hostu → mail serveru, gde se privremeno skladišti. Mail server pronalazi adresu odredišnog mail servera i uspostavlja vezu; ako je odredišni server privremeno nedostupan, lokalni mail server nastavlja da čuva poruku u baferu; u suprotnom, odredišni server pamti poruku u ulaznom baferu (mail box-u primaoca). Korisnički agent pristupa lokalnom mail serveru i kopira poruke iz mail box-a u lokalni bafer.

Kod tranzijentnih komunikacija poruke se skladište samo dok se izvorna i odredišna aplikacija izvršavaju; ako agenti ne izvršavaju, poruke se odbacuju. Komunikacioni server je u ovom slučaju **store-and-forward ruter** – ako ne može da prosledi poruku sledećem ruteru ili hostu, poruka se odbacuje (npr. istekao timeout).

### Sinhrone vs. asinhrone
- **Asinhrone** – pošiljalac nastavlja rad odmah nakon što prosledi poruku za slanje; poruka se pamti ili u lokalnom baferu ili u komunikacionom serveru.
- **Sinhrone** – pošiljalac je blokiran sve dok se ne potvrdi prihvatanje njegovog zahteva.

Kod sinhrone komunikacije postoje alternative koliko dugo je pošiljalac blokiran:
1. dok middleware ne preuzme predavanje zahteva,
2. dok poruka ne stigne do primaoca,
3. dok poruka nije potpuno obrađena od strane primaoca (do trenutka kad primalac pošalje odgovor pošiljaocu).

### Diskretne vs. streaming
- **Diskretna komunikacija** – svaka poruka čini kompletan skup informacija.
- **Streaming komunikacija** – uključuje slanje više poruka, u vremenskom odnosu ili povezanih redosledom slanja, što je potrebno za rekonstrukciju kompletnih informacija (npr. video streaming).

### Kombinacije perzistentnosti i sinhronizacije

Postoji nekoliko mogućih kombinacija: **perzistentne asinhrone**, **perzistentne sinhrone**, **tranzijentne asinhrone**, **tranzijentne sinhrone**.

- **a) Perzistentne asinhrone** (e-mail, Teams Chat) – poruka je zapamćena ili u baferu lokalnog hosta ili u prvom komunikacionom serveru sve dok se ne isporuči; pošiljalac nije blokiran dok čeka na isporuku.
- **b) Perzistentne sinhrone** – poruka mora biti zapamćena u odredišnom hostu da bi se pošiljalac deblokirao; nije neophodno da se aplikacija na odredišnoj strani izvršava. Slabiji zahtev je blokiranje izvora dok se poruka ne zapamti u komunikacionom serveru odredišnog hosta.
- **c) Tranzijentne asinhrone** – poruka se privremeno pamti u lokalnom baferu izvornog hosta, nakon čega aplikacija nastavlja sa izvršenjem; paralelno, komunikacioni sistem prosleđuje poruku do odredišta; ako prijemnik nije aktivan u trenutku kada poruka stiže, poruka je izgubljena.
- **Tranzijentne sinhrone** se javljaju u nekoliko oblika:
  - **d) Receipt-based synchronous** (najslabija forma) – zahteva blokiranje izvora dok se poruka ne zapamti u baferu odredišnog hosta; kada primi potvrdu (ACK) izvor se deblokira.
  - **e) Delivery-based synchronous** – pošiljalac je blokiran sve dok se poruka ne prosledi odredišnom procesu za dalju obradu.
  - **f) Response-based synchronous** – pošiljalac je blokiran sve dok od odredišnog procesa ne primi odgovor; **sinhroni RPC i RMI funkcionišu po ovoj šemi**.

---

## 3. Middleware bazirani na razmeni poruka (message-oriented communication)

Pozivi udaljenih procedura i pozivi udaljenih objekata doprinose sakrivanju komunikacije u distribuiranim sistemima, tj. omogućavaju **transparentnost pristupa**. Nažalost, ovaj mehanizam nije uvek prikladan – posebno kada se ne može pretpostaviti da će strana koja prima zahtev izvršavati u trenutku kada je zahtev izdat, potrebne su alternativne komunikacione usluge. Isto tako, inherentna sinhrona priroda RPC-ova (klijent blokiran dok se njegov zahtev ne obradi) može biti zamenjena nečim drugim.

Taj mehanizam se bazira na **razmeni poruka**. Postoje sistemi za razmenu poruka koji pretpostavljaju da strane koje učestvuju u komunikaciji izvršavaju u trenutku komunikacije, i postoje sistemi koji omogućavaju procesima da razmenjuju poruke čak i ako druga strana nije aktivna u trenutku pokretanja komunikacije.

Prvi sistemi su se bazirali na **tranzijentnim komunikacijama** – pružali su podršku za komunikaciju između procesa koji se jednovremeno izvršavaju. Ovakve komunikacije često nisu adekvatne ako se ima u vidu potencijalno velika geografska distribucija procesa. Potreba za **perzistentnim** komunikacionim uslugama se pojavila kada je bilo potrebno integrisati aplikacije koje su se izvršavale na geografski jako udaljenim računarima (WAN).

---

## 4. Tranzijentne komunikacije: MPI

**MPI (Message Passing Interface)** – middleware sistem koji se bazira na prenosu poruka, namenjen za podršku komunikacijama u paralelnim sistemima (klasteri, mreža radnih stanica, multiračunari, multiprocesori).

Socket-i podržavaju komunikaciju između distribuiranih procesa razmenom poruka, ali su projektovani za TCP/IP protokol stek – nisu pogodni za interprocesnu komunikaciju kada se koriste drugi protokoli. Pre pojave MPI standarda, svaki proizvođač paralelnih sistema je nudio svoju varijantu sistema za interprocesnu komunikaciju razmenom poruka, pa se javio problem prenosivosti programa. Pojavila se potreba za definisanjem standarda nezavisnog od hardverske platforme – MPI podržava tranzijentne komunikacije između procesa.

MPI podržava komunikaciju između konkurentnih procesa: **point-to-point** i **grupnu komunikaciju**. Implementiran je kao biblioteka funkcija koje se mogu pozivati iz konvencionalnih jezika (Fortran, C, C++); MPI 3 standard sadrži preko 440 funkcija. MPI podrazumeva da se komunikacija obavlja u okviru poznate grupe procesa: svakoj grupi se dodeljuje identifikator, svakom procesu unutar grupe se dodeljuje (lokalni) identifikator; par (identifikator_grupe, identifikator_procesa) na jedinstveni način identifikuje izvor ili odredište poruke i koristi se umesto transportnih adresa. U sistemu može postojati više (i preklapajućih) grupa procesa koje se izvršavaju u isto vreme. MPI podržava skoro sve oblike tranzijentnih komunikacija.

MPI middleware održava svoje sopstvene bafere, a sinhronizacija je često povezana sa trenutkom kada su podaci kopirani iz pozivajuće aplikacije u middleware.

### MPI komunikacione primitive

| Primitiva | Značenje |
|---|---|
| `MPI_bsend` | Kopiraj izlaznu poruku u lokalni bafer MPI sistema i nastavi sa radom (neblokirajuća – tranzijentna asinhrona komunikacija) |
| `MPI_send` | Pošalji poruku i čekaj dok se ne iskopira u bafer odredišnog hosta (izvor se blokira dok poruka ne bude smeštena u MPI bafer odredišnog hosta) |
| `MPI_ssend` | Pošalji poruku i čekaj dok ne krene prijem (izvor blokiran dok ne krene prijem) |
| `MPI_sendrecv` | Pošalji poruku i čekaj odgovor (izvor blokiran dok ne stigne odgovor iz odredišta) – ponaša se isto kao RPC |
| `MPI_isend` | Prosledi referencu na izlaznu poruku i nastavi sa izvršenjem (nema potrebe za kopiranjem poruke iz korisničkog bafera u bafer lokalnog MPI sistema); postoji mogućnost provere da li je komunikacija završena ili blokiranja dok se ne završi |
| `MPI_issend` | Prosledi referencu na izlaznu poruku i čekaj dok ne krene prijem |
| `MPI_recv` | Prihvati poruku; prijemnik se blokira dok ne stigne poruka |
| `MPI_irecv` | Prijemnik je spreman da prihvati poruku – može provera da li je poruka stigla ili čekanje dok ne stigne |

Zašto ovoliko različitih primitiva? Pružaju mogućnost projektantu programa da optimizira performanse.

Sa `MPI_Isend`, pošiljalac predaje pokazivač na poruku (adresa odakle kreće slanje, broj podataka i tip podataka) nakon čega se MPI sistem brine o komunikaciji – pošiljalac odmah nastavlja; kako bi se sprečilo prepisivanje poruke pre završetka komunikacije, MPI nudi operacije za proveru završetka ili blokiranje ako je potrebno. Slično, sa `MPI_Issend`, pošiljalac takođe predaje samo pokazivač MPI sistemu za izvršavanje. Operacija `MPI_Recv` blokira pozivaoca dok poruka ne stigne; kod `MPI_Irecv` primalac naznačava da je spreman da prihvati poruku i može proveriti da li je poruka zaista stigla ili blokirati dok ne stigne.

---

## 5. Perzistentne komunikacije: sistemi sa redovima poruka (MQS)

MQS su važna klasa middleware sistema zasnovana na slanju poruka. Pružaju podršku **perzistentnim asinhronim** komunikacijama; namenjeni su aplikacijama kod kojih je dozvoljeno da prenos poruka traje i nekoliko minuta (umesto nekoliko sekundi ili milisekundi). Ovi sistemi posjeduju smeštajne kapacitete za usputno pamćenje poruka i ne zahtevaju ni od izvora ni od odredišta da budu aktivni za vreme prenosa poruka.

Aplikacije komuniciraju smeštanjem poruka u posebne redove čekanja (**queues**):
- Pošiljaocu se samo garantuje da će njegova poruka biti upisana u red čekanja primaoca.
- Ne pružaju se garancije da li će (i kada će) poruka biti pročitana od strane primaoca.
- Ne postoji potreba da se pošiljalac izvršava u trenutku kad se poruka preuzima od strane primaoca.
- Pošiljalac i primalac se izvršavaju potpuno nezavisno jedan od drugog.
- Jednom kad se poruka isporuči u red čekanja, ostaje tamo dok se ne ukloni, nezavisno od toga da li se pošiljalac ili primalac izvršavaju.

Ovo omogućava četiri kombinacije u odnosu na način izvršenja pošiljaoca i primaoca:
- a) Pošiljalac i primalac se izvršavaju za celokupno vreme prenosa poruke.
- b) Samo se pošiljalac izvršava, dok je primalac pasivan (isporučivanje poruke nije moguće, ali pošiljalac i dalje može slati poruke).
- c) Pasivni pošiljalac i aktivni primalac – primalac može čitati poruke, ali nije neophodno da pošiljalac bude aktivan.
- d) Sistem skladišti (i moguće prenosi) poruke dok su i pošiljalac i primalac pasivni – neki smatraju da kad je ova konfiguracija podržana, sistem zaista podržava perzistentnu komunikaciju.

### Interfejs za pristup redu poruka (4 operacije)

- **`put`** – dodaje poruku u specificirani red; poziva se od strane pošiljaoca; **neblokirajući** poziv.
- **`get`** – **blokirajući** poziv koji omogućava autorizovanom procesu da pribavi iz reda poruku koja je najduže u njemu; proces se blokira ako je red prazan; varijacije omogućavaju traženje specifične poruke (po prioritetu ili pattern-u), a ako takva ne postoji, proces se blokira.
- **`poll`** – **neblokirajuća** varijanta `get` operacije; ako je red prazan ili specifična poruka ne postoji, pozivajući proces nastavlja sa izvršenjem (ne blokira se).
- **`notify`** – omogućeno je procesu da instalira handler (callback funkciju) koji se automatski poziva kad god je poruka dodata u red; može se iskoristiti da automatski startuje proces koji će pribavljati poruke iz reda ako se nijedan proces u tom trenutku ne izvršava. **Proces NE čeka, proces NE proverava red stalno, sistem SAM poziva callback funkciju.**

Adresiranje je omogućeno obezbeđivanjem jedinstvenog imena odredišnog reda u distribuiranom sistemu. Veoma bitan aspekt je da su poruke u sistemu pravilno adresirane.

Više procesa može slati poruke u isti red, i isto tako, više primaoca mogu uklanjati poruke iz reda. Politika upravljanja redom je uglavnom **FIFO**, ali većina implementacija takođe podržava koncept **prioriteta**, pri čemu se poruke višeg prioriteta dostavljaju prvo (poruke se baferuju u redosledu kako su poslate, a onda preuređuju po prioritetu). Poruka se sastoji od odredišta (jedinstvenog identifikatora koji određuje odredišni red), metapodataka (prioritet, režim isporuke) i samog tela poruke.

### Point-to-Point model u sistemima sa redovima poruka

Sistemi sa redovima poruka podržavaju Point-to-Point model razmene poruka, implementiran na sledeće načine:
- Jedan pošiljalac i jedan primalac povezani preko reda.
- Jedan pošiljalac i više primalaca povezani preko reda.
- Više pošiljalaca i jedan primalac povezani preko reda.
- Više pošiljalaca i više primalaca povezani preko reda.

**Nevezano za broj pošiljalaca i primalaca, poruka poslata od strane jednog pošiljaoca može biti primljena od strane samo jednog primaoca** – u P-to-P modelu poruka od jednog pošiljaoca ne može biti preneta do više primalaca.

### Arhitektura – rutiranje poruka

Redovima se upravlja pomoću upravljača redova (uglavnom zaseban proces). Aplikacija može stavljati poruke samo u lokalni red; čitanje je moguće samo izvlačenjem iz lokalnog reda. Kao posledica, upravljač reda (npr. QMA za aplikaciju A) i sama aplikacija A će uglavnom biti smešteni na istom računaru. Svaka poruka mora nositi informacije o svom odredištu, a zadatak upravljača redova je da se pobrine da poruka stigne do svog odredišta.

Tri pitanja rutiranja:
1. **Adresiranje odredišnog reda** – poželjno je da redovi imaju logička, lokacijski nezavisna imena (transparentnost lokacije). Svako ime treba da bude povezano sa kontakt adresom (računar, port), a preslikavanje imena u adresu treba da bude lako dostupno upravljaču redova.
2. **Kako preslikavanje učiniti dostupnim** – čest pristup je implementirati preslikavanje kao tabelu za pretraživanje i kopirati je svim upravljačima, što dovodi do problema održavanja (svaki put kada se dodaje novi red, mnoge tabele treba ažurirati).
3. **Efikasno održavanje preslikavanja** – kontaktna adresa svakog upravljača redova treba da bude poznata svim ostalim, što dovodi do problema skalabilnosti kod velikih sistema. U praksi postoje posebni upravljači redova koji funkcionišu kao **ruteri**: prosleđuju dolazne poruke drugim upravljačima redova, tako da sistem može postepeno da raste u potpunu, na nivou aplikacije, preklapajuću mrežu.

Primer: kada pošiljalac A stavi poruku za odredište B u svoj lokalni red, poruka se prvo prenosi do najbližeg rutera R1. Ruter R1 može izvesti iz imena B da poruku treba proslediti ruteru R2. Na ovaj način, samo ruteri treba da budu ažurirani kada se redovi dodaju ili uklanjaju, dok svaki drugi menadžer redova mora znati samo gde je najbliži ruter.

---

## 6. Publish-Subscribe sistemi za razmenu poruka

Umesto pošiljaoca i primaoca imamo **Publisher** (Izdavač) i **Subscriber** (Pretplatnik). Za razliku od Point-to-Point sistema gde se poruka prima samo od strane jednog primaoca, kod Publish-Subscribe sistema poruka može biti primljena od **više Subscriber-a**.

- Publisher kreira poruku i publikuje (objavljuje) je u **Topic-u** (temi) – destinacija je Topic umesto reda (queue).
- Više različitih Subscriber-a se može prijaviti (pretplatiti) za Topic i koristiti poruke koje su objavljene u tom Topic-u.
- Subscriber može primiti poruku objavljenu u Topic-u samo ako se već prijavio za taj Topic – svaka poruka poslata pre njegove prijave neće biti primljena.
- Implementacija ovog modela može naložiti i da poruka ne bude primljena ako Subscriber nije aktivan, mada većina implementacija ne zahteva ovo ograničenje.

Višestruki pretplatnici (potrošači) mogu se pretplatiti na temu sa ili bez filtera. Pretplate se prikupljaju pomoću komponente za raspoređivanje događaja (**event dispatcher**), odgovorne za usmeravanje događaja svim odgovarajućim pretplatnicima. Događaj sadrži informacije o određenom dešavanju ili promeni stanja unutar sistema, i izdavači distribuiraju događaje pretplatnicima koji su se pretplatili da primaju ažuriranja tih informacija.

### Filtriranje po temi vs. po sadržaju

- **Filtriranje po temi (topic-based)**: pretplatnici izražavaju interes za primanje događaja koji se odnose na određene teme. Izdavači kategorizuju događaje u različite teme, a pretplatnici se pretplaćuju na teme koje ih zanimaju; primaju obaveštenja za sve događaje objavljene pod temama na koje su pretplaćeni.
  - *Primer*: sistem vesti sa temama poput politika, sport, tehnologija – izdavač objavljuje članak sa temom "Tehnologija", a pretplatnici te teme dobijaju obaveštenja.
- **Filtriranje po sadržaju (content-based)**: pretplatnici specificiraju kriterijume zasnovane na sadržaju događaja koje želi da prime. Izdavači objavljuju događaje sa povezanim sadržajem, a pretplatnici postavljaju filtere koji definišu specifične atribute ili uslove; primaju samo događaje koji odgovaraju navedenim filterima.
  - *Primer*: sistem za praćenje berze – pretplatnici specificiraju filtere kao "Obavesti me ako cena akcije Apple pređe 150 dolara" ili "Obavesti me ako se volumen akcije Tesla udvostruči".

### Interfejs P/S sistema (operacije)

- **`Publish(event)`** – izdavači distribuiraju događaje u sistem; emituje događaj svim pretplatnicima koji su izrazili interesovanje za taj tip; izdavači ne moraju znati identitete pojedinačnih pretplatnika.
- **`Subscribe(filter)`** – pretplatnici izražavaju interesovanje za primanje određenih tipova događaja; specificira filter zasnovan na tipovima događaja, atributima ili drugim kriterijumima.
- **`Notify(event)`** – kada izdavač objavi događaj, sistem se pobrine da svi pretplatnici čiji filteri odgovaraju događaju dobiju obaveštenje; omogućava odvojenu (decoupled) komunikaciju između izdavača i pretplatnika.
- **`Unsubscribe(filter)`** – pretplatnici povlače interesovanje za primanje događaja koji odgovaraju određenom filteru; sistem uklanja pretplatu, osiguravajući da budući događaji koji odgovaraju filtru neće biti isporučeni tom pretplatniku.

U okviru modela objavljivanja/pretplate nema ograničenja u vezi sa ulogom klijenta – klijent može biti i proizvođač i potrošač teme. Ova odvojenost poboljšava skalabilnost, fleksibilnost i modularnost u distribuiranim sistemima.

---

## 7. Java Messaging Service (JMS)

JMS je specifikacija standardizovanog načina za komunikaciju između distribuiranih Java programa. Specifikacija objedinjuje paradigme objavljivanja i pretplate (publish-subscribe) i redova poruka (message queue), podržavajući **teme (topics)** i **redove (queues)** kao alternativne destinacije za poruke.

### Ključne uloge

- **JMS klijent** – Java program ili komponenta koja proizvodi ili konzumira poruke.
- **JMS proizvođač** – program koji kreira i proizvodi poruke.
- **JMS potrošač** – program koji prima i konzumira poruke.
- **JMS provajder** – jedan od sistema koji implementiraju JMS specifikaciju.
- **JMS poruka** – objekat koji se koristi za komunikaciju informacija između JMS klijenata (od proizvođača ka potrošačima).
- **JMS destinacija** – objekat koji podržava komunikaciju u JMS-u; to je ili JMS tema ili JMS red.

**JNDI (Java Name Directory Interface) provajder**: sadrži povezivanja za fabrike konekcija i destinacije (vraća odgovore na lookup zahteve na osnovu imena). **JMS provajder**: implementira JMS interfejs.

### Administrativni objekti

Administrativni objekti su unapred konfigurisani JMS objekti koje kreira administrator za upotrebu od strane klijenata – konfigurisani su i upravljani od strane administratora, umesto da se dinamički kreiraju od strane kôda aplikacije. Ovi objekti služe kao "most" između kôda klijenta i JMS provajdera.

**Fabrike konekcija** omogućavaju klijentima da uspostave konekcije sa JMS provajderom; enkapsuliraju detalje o kreiranju konekcija, sesija i proizvođača/potrošača poruka. Korišćenjem fabrike konekcija, klijenti mogu dobiti konekciju sa JMS provajderom bez potrebe da znaju detalje implementacije niskog nivoa.

### Programiranje sa JMS-om

Da bi se komuniciralo sa JMS provajderom, prvo je neophodno uspostaviti vezu između klijentskog programa i JMS provajdera – ovo se postiže kroz fabriku konekcija. Rezultujuća konekcija je logički kanal između klijenta i JMS provajdera. Mogu se uspostaviti dva tipa veza: `TopicConnection` ili `QueueConnection`.

Konekcije se mogu koristiti za kreiranje jedne ili više **sesija** – sesija je serija operacija koje uključuju kreiranje, slanje i prijem poruka vezanih za logički zadatak. Objekat sesije takođe podržava operacije za kreiranje **transakcija** (grupiše seriju operacija zajedno u jednu atomičnu), podržavajući sve-ili-ništa izvršavanje serije operacija. Postoji jasna razlika između sesija za teme i sesija za redove: `TopicConnection` može podržati jednu ili više sesija za teme, a `QueueConnection` može podržati jednu ili više sesija za redove.

**Transakcije (Producer, JMS Queue)**:
- `rollback()` čisti sve neposlate (nepotvrđene) poruke transakcije iz predajnog reda (JMS transmit queue).
- `commit()` uzrokuje da JMS red pošalje sve neposlate poruke transakcije.

**Transakcije (Consumer)**:
- `rollback()` nalaže JMS redu da ignoriše sve isporučene poruke (isporuka poruka počinje ponovo od poruke M0).
- `commit()` govori JMS redu da je potrošač uspešno primio sve poruke – JMS red briše te poruke iz reda za prijem.

Objekat sesije je centralan za rad JMS-a – podržava metode za kreiranje poruka, kreiranje proizvođača poruka i kreiranje potrošača poruka.

### Struktura JMS poruke

Poruka se sastoji od tri dela: **zaglavlja**, **seta svojstava (property)** i **tela poruke**.
- **Zaglavlje** sadrži sve informacije potrebne za identifikaciju i rutiranje poruke, uključujući odredište (referencu na temu ili red), prioritet poruke, datum isteka, ID poruke i vremensku oznaku. Većina ovih polja se kreira od strane JMS provajdera, ali neka mogu biti specifično popunjena od strane korisnika.
- **Svojstva (property)** su definisana od strane korisnika i mogu se koristiti za povezivanje drugih metapodataka aplikacije sa porukom (npr. sistem svestan konteksta može koristiti svojstva za polje lokacije).
- **Telo** može biti: tekstualna poruka, niz bajtova, serijalizovani Java objekat, niz primitivnih Java vrednosti ili složeniji skup parova imena/vrednosti: `Message`, `TextMessage`, `BytesMessage`, `ObjectMessage`, `StreamMessage`, `MapMessage`.

### Proizvođač i potrošač poruka

- **Proizvođač poruka** je objekat koji se koristi za objavljivanje poruka pod određenom temom ili slanje poruka u red.
- **Potrošač poruka** je objekat koji se koristi za pretplatu na poruke koje se tiču određene teme ili za primanje poruka iz reda.

Potrošač je složeniji od proizvođača iz dva razloga:
1. Moguće je povezati filtere sa potrošačima poruka specificiranjem tzv. **selektora poruke** – predikata definisanog preko vrednosti u delovima zaglavlja i svojstava poruke (ne tela). To se može koristiti npr. za filtriranje poruka sa određene lokacije.
2. Postoje dva moda za primanje poruka: program može ili blokirati korišćenjem operacije prijema (`receive()`), ili može uspostaviti objekat slušaoca poruke (`MessageListener`) koji mora obezbediti metodu `onMessage` koja se poziva kada god se identifikuje odgovarajuća dolazeća poruka.

### JMS P2P implementacija

Glavni interfejsi: `QueueConnectionFactory`, `Queue`, `QueueConnection`, `QueueSession`, `Message`, `QueueSender`, `QueueReceiver`.

**Producer** koraci: dobiti referencu na `QueueConnectionFactory`, dobiti referencu na `Queue`, kreirati `QueueConnection`, kreirati `QueueSession`, kreirati `QueueSender`, kreirati `Message`, poslati poruku.

**Consumer** koraci: dobiti referencu na `QueueConnectionFactory`, dobiti referencu na `Queue`, kreirati `QueueConnection`, kreirati `QueueSession`, kreirati `QueueReceiver`, čekati poruku (implementirati interfejs `MessageListener`).

### JMS Publish-and-Subscribe implementacija

Glavni interfejsi: `TopicConnectionFactory`, `Topic`, `TopicConnection`, `TopicSession`, `Message`, `TopicPublisher`, `TopicSubscriber`.

**Producer** koraci: dobiti referencu na `TopicConnectionFactory`, dobiti referencu na `Topic`, kreirati `TopicConnection`, kreirati `TopicSession`, kreirati `TopicPublisher`, kreirati `Message`, poslati poruku.

**Consumer** koraci: dobiti referencu na `TopicConnectionFactory`, dobiti referencu na `Topic`, kreirati `TopicConnection`, kreirati `TopicSession`, kreirati `TopicSubscriber`, čekati poruku (implementirati `MessageListener`).

Bitne napomene iz kôda:
- `tsub.setMessageListener(...)` postavlja listener.
- `tc.start()` pokreće isporuku poruka.
- Problem: ako program odmah završi, konekcija se zatvara i listener nikad ne dobije poruku – zato se koristi `System.in.read();` koji sprečava da se program odmah završi i omogućava da `onMessage()` prima i obrađuje poruke u pozadini.
- `tc.close()` zatvara konekciju.
- **Obrada poruke + transakcija (commit/rollback) treba da budu unutar `onMessage()`.**

### Različite implementacije

Sistemi sa redovima poruka (i/ili Publish-Subscribe sistemi) su implementirani u: **JMS, MSMQ, Apache ActiveMQ, Apache Kafka, Apache Pulsar, IBM MQ, NATS, RabbitMQ**.

---

# DEO 2 – NAJBITNIJA PITANJA I ODGOVORI

### RMI

**1. Šta je RMI i zašto je bitan koncept skrivanja unutrašnjosti objekta preko Interfejsa?**
RMI (Remote Method Invocation) je RPC na OO način – omogućava pozivanje metoda objekata koji se nalaze na drugom hostu. Skrivanje unutrašnjosti (podataka i metoda) preko dobro definisanog Interfejsa je ključno za skrivanje heterogenosti u distribuiranim sistemima – jedini način pristupa objektu je preko metoda dostupnih kroz Interfejs.

**2. Objasni koncept "distribuiranog objekta" – šta je tačno distribuirano, a šta nije?**
Interfejsi mogu biti locirani na jednoj mašini a objekat na drugoj – ova organizacija se zove distribuirani (udaljeni) objekti. Sam objekat nije distribuiran (nalazi se na jednoj mašini/serveru) – **distribuiran je Interfejs**.

**3. Objasni ulogu proxy-ja i skeletona u RMI mehanizmu poziva.**
Proxy je predstavnik server objekta u adresnom prostoru klijenta – implementira isti Interfejs kao server, ali na drugi način; analogan je klijent stub-u i pakuje poziv metoda u poruku (koja sadrži referencu udaljenog objekta, ID metode i parametre) i šalje je serveru. Skeleton je serverska strana koja prima dolaznu poruku i raspakuje je u poziv odgovarajućeg metoda na server strani.

**4. Šta sadrži referenca udaljenog objekta i zašto mora biti jedinstvena u prostoru i vremenu?**
Mora specificirati objekat čiji se metod poziva. Jedan od načina da se garantuje jedinstvenost je da referenca sadrži IP adresu računara, broj porta procesa koji je kreirao objekat, vreme kreiranja i lokalni broj objekta (inkrementiran pri svakom kreiranju objekta u procesu); može sadržati i informaciju o Interfejsu (npr. ime Interfejsa).

**5. Nabroj primere middleware-a baziranih na RMI pristupu.**
CORBA (Common Object Request Broker Architecture), Java RMI, DCOM (Distributed Component Object Model).

**6. Kako klijent u Java RMI-ju dobija referencu na udaljeni objekat? Objasni ulogu bindera/RMI registra.**
Koristi se poseban distribuirani servis – binder (kod Jave: RMI registar). Server registruje svoje udaljene objekte u binder-u pod određenim imenom (`bind`/`rebind` u klasi `Naming`); klijent koristi binder (metod `lookup` klase `Naming`) da pronađe referencu udaljenog objekta, koja se koristi za instanciranje stuba na klijentskoj strani. Pristupa mu se preko URL-formatiranog niza: `//imeRačunara:port/nazivObjekta`.

**7. Nabroj šest koraka povezivanja klijenta i servera u Java RMI-ju.**
1) Server registruje udaljeni objekat u RMI registru pod imenom. 2) Klijent kontaktira registar i dobija referencu (koristi se za instanciranje stuba). 3) Klijent upućuje poziv metode stub-u. 4) Stub serijalizuje ID metode i parametre i šalje skeletonu. 5) Skeleton deserijalizuje, prosleđuje poziv objektu, izvršava metod i vraća serijalizovanu povratnu vrednost stub-u. 6) Stub deserijalizuje rezultat i vraća ga klijentu.

**8. Koja pravila mora ispunjavati svaka udaljena metoda u Java RMI-ju (Echo primer)?**
Udaljeni interfejs mora naslediti `java.rmi.Remote`; svaka udaljena metoda mora biti deklarisana da baca `RemoteException` (radi rukovanja neuspelim komunikacijama); vraća samo 1 rezultat i ima 0 ili više ulaznih parametara.

**9. Šta mora da uradi klasa udaljenog objekta (server strana) u Java RMI-ju?**
Implementira udaljeni interfejs, nasleđuje klasu `UnicastRemoteObject`, poziva `super()` u konstruktoru (inicijalizacija koja omogućava serveru da čeka i uslužuje zahteve za usluge), i implementira metodu deklarisanu u interfejsu.

**10. Objasni razliku između prenosa parametara po vrednosti i po udaljenoj referenci.**
Po vrednosti se prenose primitivni tipovi podataka i serijalizovani objekti – objekat se serijalizuje, šalje primaocu i deserijalizuje kako bi se izgradila lokalna kopija (nova instanca čije se stanje kasnije može razlikovati od originala). Po udaljenoj referenci se prenose udaljeni objekti – objekat vezan za lokaciju izvršavanja (server) prosleđuje se putem svog stub-a drugoj strani.

### Tipovi komunikacija

**11. Objasni razliku između perzistentne i tranzijentne komunikacije, uz primer.**
Perzistentna (istrajna) – poruka se pamti u komunikacionom serveru onoliko dugo koliko je potrebno da se isporuči odredištu; ni izvor ni prijemnik ne moraju biti aktivni istovremeno (primer: e-mail). Tranzijentna (privremena) – poruka se odbacuje ako komunikacioni server nije u stanju da je odmah isporuči sledećem serveru ili odredištu (npr. store-and-forward ruter odbacuje poruku ako istekne timeout).

**12. Objasni razliku između sinhrone i asinhrone komunikacije i tri alternative dužine blokiranja kod sinhrone.**
Asinhrona – pošiljalac nastavlja rad odmah nakon slanja poruke. Sinhrona – pošiljalac je blokiran dok se ne potvrdi prihvatanje zahteva; tri alternative: 1) dok middleware ne preuzme predaju zahteva, 2) dok poruka ne stigne do primaoca, 3) dok poruka nije potpuno obrađena od strane primaoca (do odgovora).

**13. Razlikuj diskretnu i streaming komunikaciju.**
Diskretna – svaka poruka čini kompletan skup informacija. Streaming – uključuje slanje više vremenski/redosledno povezanih poruka potrebnih za rekonstrukciju kompletne informacije (npr. video streaming).

**14. Objasni četiri kombinacije perzistentnosti i sinhronizacije komunikacija, sa primerima.**
Perzistentne asinhrone (e-mail, Teams Chat) – poruka zapamćena dok se ne isporuči, pošiljalac nije blokiran. Perzistentne sinhrone – poruka mora biti zapamćena u odredišnom hostu da bi se pošiljalac deblokirao. Tranzijentne asinhrone – poruka privremeno u lokalnom baferu, aplikacija nastavlja rad, poruka se gubi ako prijemnik nije aktivan kad stigne. Tranzijentne sinhrone – najjača forma (response-based) je osnova sinhronog RPC-a/RMI-ja.

**15. Objasni tri oblika tranzijentne sinhrone komunikacije (d, e, f).**
d) Receipt-based – najslabija forma, izvor blokiran dok se poruka ne zapamti u baferu odredišnog hosta (deblokira se po ACK-u). e) Delivery-based – izvor blokiran dok se poruka ne prosledi odredišnom procesu za obradu. f) Response-based – izvor blokiran dok od odredišnog procesa ne primi odgovor – ovako funkcionišu sinhroni RPC i RMI.

### MOM i MPI

**16. Zašto RPC/RMI pristup nije uvek adekvatan i šta ga zamenjuje?**
Ne može se uvek pretpostaviti da će strana koja prima zahtev izvršavati u trenutku kada je zahtev izdat, a inherentna sinhrona priroda RPC-a (blokiranje klijenta dok se zahtev ne obradi) često nije poželjna. Alternativa je mehanizam baziran na razmeni poruka (message-oriented middleware).

**17. Zašto je nastao MPI standard i koje probleme rešava?**
Socket-i su projektovani za TCP/IP protokol stek i nisu pogodni za druge protokole; pre MPI-ja, svaki proizvođač paralelnih sistema je imao svoju varijantu za interprocesnu komunikaciju, što je izazivalo problem prenosivosti programa. MPI je standard nezavisan od hardverske platforme, namenjen komunikaciji u paralelnim sistemima (klasteri, multiprocesori).

**18. Kako MPI identifikuje izvor/odredište poruke?**
Komunikacija se obavlja u okviru poznate grupe procesa – svakoj grupi se dodeljuje identifikator, a svakom procesu unutar grupe lokalni identifikator; par (identifikator_grupe, identifikator_procesa) jedinstveno identifikuje izvor ili odredište poruke, umesto transportnih adresa.

**19. Objasni razliku između `MPI_Send`, `MPI_Ssend`, `MPI_Sendrecv`, `MPI_Isend` i `MPI_Recv`.**
`MPI_Send` – blokira pošiljaoca dok poruka ne bude smeštena u MPI bafer odredišnog hosta (semantika zavisi od implementacije). `MPI_Ssend` – blokira pošiljaoca dok ne krene prijem. `MPI_Sendrecv` – blokira pošiljaoca dok ne stigne odgovor iz odredišta; ponaša se isto kao sinhroni RPC. `MPI_Isend` – neblokirajuće, prosleđuje samo referencu na poruku i nastavlja izvršenje (mogućnost provere/blokiranja da se izbegne prepisivanje). `MPI_Recv` – blokira prijemnika dok poruka ne stigne.

**20. Zašto MPI ima toliko različitih komunikacionih primitiva?**
Pružaju mogućnost projektantu programa da optimizira performanse (izbor između kopiranja poruke ili slanja pokazivača, i izbor tačke do koje se pošiljalac blokira).

### Sistemi sa redovima poruka

**21. Za koje aplikacije su namenjeni sistemi sa redovima poruka (MQS)?**
Za aplikacije kod kojih je dozvoljeno da prenos poruka traje i nekoliko minuta (umesto sekundi/milisekundi); ovi sistemi imaju kapacitete za skladištenje poruka i ne zahtevaju da izvor i odredište budu aktivni tokom prenosa – pružaju podršku perzistentnim asinhronim komunikacijama.

**22. Objasni četiri operacije osnovnog interfejsa MQS-a (put, get, poll, notify).**
`put` – neblokirajuće dodavanje poruke u red. `get` – blokirajuće pribavljanje najstarije poruke iz reda (blokira se ako je red prazan). `poll` – neblokirajuća varijanta `get`-a (ako je red prazan, proces nastavlja dalje). `notify` – instalira callback koji se automatski poziva kad je poruka dodata u red; proces ne čeka i ne proverava red – sistem sam poziva callback.

**23. Koje četiri Point-to-Point konfiguracije podržavaju sistemi sa redovima poruka i koje je ključno ograničenje?**
Jedan-jedan, jedan-više, više-jedan, više-više (pošiljalaca/primalaca povezanih preko reda). Ključno ograničenje: bez obzira na broj učesnika, **poruka od jednog pošiljaoca prima samo jedan primalac** – u P2P modelu poruka se ne može preneti do više primalaca.

**24. Koja je politika upravljanja redom poruka?**
Uglavnom FIFO (first-in, first-out) – sve poruke imaju isti prioritet i isporučuju se u redosledu slanja. Većina implementacija podržava i politiku po prioritetu – poruke se baferuju po redosledu slanja, a zatim preuređuju po prioritetu, pri čemu se poruke višeg prioriteta dostavljaju prvo.

**25. Objasni tri pitanja vezana za rutiranje poruka u MQS-u i rešenje problema skalabilnosti.**
1) Adresiranje odredišnog reda logičkim, lokacijski nezavisnim imenima (transparentnost lokacije). 2) Kako preslikavanje imena u adresu učiniti dostupnim upravljaču – naivno rešenje (kopiranje tabele svima) stvara problem održavanja. 3) Efikasno održavanje na velikoj skali – rešava se posebnim upravljačima-ruterima koji prosleđuju poruke drugim upravljačima, formirajući preklapajuću (overlay) mrežu na nivou aplikacije; samo ruteri se ažuriraju kad se redovi dodaju/uklanjaju.

### Publish-Subscribe

**26. Koja je ključna razlika između Point-to-Point i Publish-Subscribe modela?**
U P2P modelu poruku prima tačno jedan primalac, bez obzira na broj pošiljalaca/primalaca. U Publish-Subscribe modelu poruka objavljena u Topic-u može biti primljena od strane **više Subscriber-a** koji su se pretplatili na taj Topic.

**27. Da li Subscriber prima poruke objavljene pre svoje prijave na Topic?**
Ne – Subscriber može primiti poruku objavljenu u Topic-u samo ako se već prijavio za taj Topic; svaka poruka poslata pre njegove prijave neće biti primljena (mada implementacija može zahtevati i da Subscriber bude aktivan, iako većina to ne zahteva).

**28. Objasni razliku između pretplate po temi i pretplate po sadržaju, sa primerima.**
Pretplata po temi (topic-based) – pretplatnici se pretplaćuju na kategorizovane teme i primaju sve događaje te teme (primer: pretplata na temu "Tehnologija" u sistemu vesti). Pretplata po sadržaju (content-based) – pretplatnici specificiraju filtere zasnovane na sadržaju/atributima događaja i primaju samo one koji odgovaraju filteru (primer: "Obavesti me ako cena akcije Apple pređe 150 dolara").

**29. Objasni četiri operacije Publish-Subscribe interfejsa.**
`Publish(event)` – izdavač emituje događaj svim zainteresovanim pretplatnicima, bez potrebe da poznaje njihove identitete. `Subscribe(filter)` – pretplatnik specificira filter (kriterijume) za događaje koje želi da prima. `Notify(event)` – sistem isporučuje događaj svim pretplatnicima čiji filter odgovara, omogućavajući odvojenu komunikaciju. `Unsubscribe(filter)` – pretplatnik povlači interesovanje; budući događaji koji odgovaraju tom filteru mu se ne isporučuju.

### JMS

**30. Šta je JMS i koje paradigme objedinjuje?**
Java Messaging Service – specifikacija standardizovanog načina za komunikaciju između distribuiranih Java programa; objedinjuje publish-subscribe (topics) i redove poruka/message queue (queues) kao alternativne destinacije za poruke.

**31. Nabroj i objasni ključne uloge koje JMS razlikuje.**
JMS klijent (proizvodi/konzumira poruke), JMS proizvođač (kreira poruke), JMS potrošač (prima poruke), JMS provajder (implementacija specifikacije), JMS poruka (objekat za razmenu informacija) i JMS destinacija (JMS tema ili JMS red).

**32. Objasni redosled kreiranja objekata u JMS programiranju.**
Fabrika konekcija (`ConnectionFactory`) kreira konekciju (`Connection`); konekcija kreira sesiju (`Session`); sesija kreira proizvođača poruka (`Producer`) i potrošača poruka (`Consumer`), koji šalju/primaju poruke prema/od destinacije (Topic ili Queue).

**33. Šta je sesija u JMS-u i kakvu razliku pravi TopicConnection u odnosu na QueueConnection?**
Sesija je serija operacija (kreiranje, slanje, prijem poruka) vezanih za logički zadatak; podržava i transakcije (sve-ili-ništa izvršavanje serije operacija). `TopicConnection` podržava jednu ili više sesija za teme, a `QueueConnection` jednu ili više sesija za redove – postoji jasna razlika između njih.

**34. Objasni značenje `commit()` i `rollback()` na strani proizvođača, a zatim na strani potrošača poruka.**
Proizvođač: `rollback()` čisti sve neposlate poruke transakcije iz predajnog reda; `commit()` uzrokuje da JMS red pošalje sve neposlate poruke transakcije. Potrošač: `rollback()` nalaže JMS redu da ignoriše sve isporučene poruke (isporuka počinje ponovo od prve poruke); `commit()` govori JMS redu da je potrošač uspešno primio sve poruke, pa se te poruke brišu iz reda.

**35. Od kojih delova se sastoji JMS poruka i šta sadrži svaki deo?**
Zaglavlje (header) – informacije za identifikaciju i rutiranje (odredište, prioritet, datum isteka, ID poruke, vremenska oznaka); svojstva (properties) – korisnički definisani metapodaci (npr. lokacija); telo (body) – tekstualna poruka, niz bajtova, serijalizovani objekat, niz primitivnih vrednosti ili skup parova imena/vrednosti (`TextMessage`, `BytesMessage`, `ObjectMessage`, `StreamMessage`, `MapMessage`).

**36. Zašto je potrošač poruka složeniji od proizvođača (dva razloga)?**
1) Moguće je povezati filtere sa potrošačima preko selektora poruke – predikata definisanog nad zaglavljem i svojstvima poruke (ne telom), npr. za filtriranje po lokaciji. 2) Postoje dva moda prijema poruka – blokirajući `receive()`, ili `MessageListener` objekat sa metodom `onMessage()` koja se poziva kad stigne odgovarajuća poruka.

**37. Nabroj glavne interfejse JMS P2P implementacije i korake Producer-a i Consumer-a.**
Interfejsi: `QueueConnectionFactory`, `Queue`, `QueueConnection`, `QueueSession`, `Message`, `QueueSender`, `QueueReceiver`. Producer: referenca na fabriku → referenca na red → `QueueConnection` → `QueueSession` → `QueueSender` → `Message` → slanje. Consumer: referenca na fabriku → referenca na red → `QueueConnection` → `QueueSession` → `QueueReceiver` → čekanje poruke (`MessageListener`).

**38. Nabroj glavne interfejse JMS Publish-and-Subscribe implementacije.**
`TopicConnectionFactory`, `Topic`, `TopicConnection`, `TopicSession`, `Message`, `TopicPublisher`, `TopicSubscriber`.

**39. Zašto se u primeru Subscriber-a koristi `System.in.read()` nakon `tc.start()`, i gde treba da bude obrada poruke/transakcije?**
`tc.start()` pokreće isporuku poruka listeneru. Bez `System.in.read()`, program bi odmah završio i zatvorio konekciju pre nego što listener primi poruku – ovaj pozив sprečava trenutni završetak i omogućava `onMessage()`-u da u pozadini prima i obrađuje poruke; `tc.close()` se poziva nakon toga. Obrada poruke i transakcija (commit/rollback) treba da budu implementirane unutar `onMessage()`.

**40. Nabroj poznate implementacije sistema sa redovima poruka i/ili Publish-Subscribe sistema.**
JMS, MSMQ, Apache ActiveMQ, Apache Kafka, Apache Pulsar, IBM MQ, NATS, RabbitMQ.
