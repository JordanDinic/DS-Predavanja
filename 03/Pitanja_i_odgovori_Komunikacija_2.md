# Distribuirani sistemi – Komunikacija 2
## Pitanja i odgovori za pripremu ispita

---

## 1. OO pristup – Poziv udaljenih metoda (RMI)

**P: Šta je RMI?**
O: RMI (Remote Method Invocation) je RPC na objektno-orijentisan način – omogućava pozivanje metoda objekata koji se nalaze na drugom hostu.

**P: Zašto je bitno da objekat skriva svoju unutrašnjost preko Interfejsa?**
O: Zato što je skrivanje unutrašnjosti (podataka i metoda) od spoljašnjeg okruženja putem dobro definisanog Interfejsa ključno za skrivanje heterogenosti u distribuiranim sistemima.

**P: Šta sadrži remote interface fajl?**
O: Skup metoda objekta koje se mogu pozivati sa udaljenih računara (objekat može imati i druge metode koje se pozivaju samo lokalno).

**P: Kako se pristupa podacima unutar udaljenog objekta?**
O: Isključivo preko metoda koji su dostupni kroz Interfejs – ne postoji nijedan drugi način pristupa.

**P: Šta predstavlja Interfejs u kontekstu RMI-ja?**
O: Grupu metoda sa praznim telom (sadrži samo definicije metoda); implementacioni kod poseduje samo server.

**P: Šta znači da je "distribuiran Interfejs, a ne objekat"?**
O: Interfejsi mogu biti locirani na jednoj mašini, a objekat na drugoj – ova organizacija se naziva distribuirani (udaljeni) objekti. Sam objekat nije distribuiran (nalazi se na jednoj mašini), distribuiran je Interfejs.

---

## 2. Distribuirani objekti – mehanizam poziva

**P: Šta se dešava kada proces pozove metod (RMI) objekta na drugoj mašini?**
O: Poziv se prosleđuje proxy-ju (predstavniku server objekta u adresnom prostoru klijenta), koji ga pakuje u poruku i šalje serveru; server-strana skeleton raspakuje poruku i poziva odgovarajući metod na server objektu.

**P: Šta je proxy i čemu je analogan?**
O: Proxy implementira isti Interfejs kao server (na drugi način) i analogan je klijent stub-u – poziv metoda pakuje u poruku (referenca udaljenog objekta, identifikator metode, parametri) i šalje je serveru.

**P: Šta je skeleton?**
O: Serverski stub koji prima dolaznu poruku, raspakuje je i poziva odgovarajući metod na server strani.

**P: Šta mora sadržati poruka kojom se poziva metod udaljenog objekta?**
O: Mora specificirati objekat čiji se metod poziva – koristi se identifikator udaljenog objekta jedinstven u čitavom distribuiranom sistemu (remote object reference).

**P: Kako se garantuje jedinstvenost reference udaljenog objekta u prostoru i vremenu?**
O: Referenca sadrži IP adresu računara, broj porta procesa koji je kreirao objekat, vreme kreiranja, lokalni broj objekta (inkrementira se pri svakom kreiranju objekta), a može sadržati i informaciju o Interfejsu (npr. ime Interfejsa).

**P: Koja su tri primera middleware-a baziranog na pozivu udaljenih metoda?**
O: CORBA, Java RMI, DCOM.

---

## 3. Java RMI

**P: Koja je osnovna ideja Java RMI-ja?**
O: Logička separacija između interfejsa i objekta omogućava njihovu fizičku separaciju.

**P: Šta specificira udaljeni interfejs, a šta je udaljeni objekat?**
O: Udaljeni interfejs specificira skup metoda koje se mogu udaljeno pozivati (unutrašnje stanje objekta nije distribuirano); udaljeni objekat je instanca klase koja implementira udaljeni interfejs.

**P: Koje metode mogu pozivati objekti na udaljenim mašinama?**
O: Samo metode koje pripadaju udaljenom interfejsu objekta. Lokalni objekti mogu pozivati i te metode i sve ostale koje implementira udaljeni objekat.

**P: Da li udaljeni interfejsi imaju konstruktore?**
O: Ne – kao i svi interfejsi, nemaju konstruktore.

**P: Koji interfejs mora naslediti udaljeni interfejs u Javi i šta mora deklarisati svaka udaljena metoda?**
O: Mora naslediti `java.rmi.Remote`; svaka udaljena metoda mora biti deklarisana da baca `RemoteException` (zbog rukovanja neuspelim komunikacijama), vraća tačno 1 rezultat i ima 0 ili više ulaznih parametara.

**P: Kako se prenose parametri udaljenom metodu?**
O: Po vrednosti – za primitivne tipove i objekte koji implementiraju `java.io.Serializable`; po referenci – za udaljene objekte.

**P: Šta klasa udaljenog objekta mora da uradi (implementacija servera)?**
O: Implementira udaljeni interfejs, nasleđuje klasu `UnicastRemoteObject`, poziva `super()` u konstruktoru (inicijalizacija za čekanje/uslugu zahteva), i implementira metode deklarisane u interfejsu.

---

## 4. Povezivanje klijenta i servera (binder / RMI registry)

**P: Kako klijent doznaje referencu udaljenog objekta?**
O: Koristi se poseban distribuirani servis – binder. Binder sadrži tabelu preslikavanja tekstualnog imena u referencu udaljenog objekta; server registruje svoje objekte, a klijent pretražuje binder da bi pronašao referencu.

**P: Šta je RMI registar i kako mu se pristupa?**
O: To je binder za Java RMI – omogućava serveru da objavi uslugu, a klijentu da dobije stub za pristup toj usluzi. Pristupa mu se preko metoda klase `Naming`, koji kao argument uzimaju URL string oblika `//imeRačunara:port/nazivObjekta`.

**P: Koji su koraci povezivanja klijenta i servera (1–6)?**
O:
1. Server registruje udaljeni objekat u RMI registru pod određenim imenom (bind/rebind).
2. Klijent kontaktira registar sa imenom objekta i dobija referencu, koja se koristi za instanciranje stub-a na klijentskoj strani.
3. Klijent upućuje poziv metode stub-u (sintaksa identična lokalnom pozivu).
4. Stub serijalizuje ID metode i parametre i šalje ih skeletonu u poruci.
5. Skeleton deserijalizuje podatke, prosleđuje poziv objektu, izvršava metod, serijalizuje rezultat i vraća ga stub-u.
6. Stub deserijalizuje povratnu vrednost i vraća rezultat klijentu.

**P: Koja je razlika između `bind` i `rebind` u klasi `Naming`?**
O: `rebind` zamenjuje već postojeće povezivanje za dato ime, dok `bind` to ne radi (izaziva grešku ako ime već postoji).

**P: Kako izgleda poziv iz primera EchoInterface?**
O: `public interface EchoInterface extends Remote { String getEcho(String echo) throws RemoteException; }`

---

## 5. Prenos parametara u RMI

**P: Šta znači prenos "po vrednosti" kod serijalizovanog objekta?**
O: Objekat čije se stanje može razlikovati na različitim lokacijama se serijalizuje, šalje primaocu i deserijalizuje – u procesu primaocu se kreira novi (lokalni) objekat čije se stanje može posle nezavisno menjati u odnosu na original.

**P: Šta znači prenos "po udaljenoj referenci"?**
O: Objekat vezan za lokaciju izvršavanja (server) se prenosi tako što se njegov stub prosleđuje drugoj strani.

---

## 6. Tipovi komunikacija – osnovna podela

**P: Po kojim dimenzijama delimo tipove komunikacija?**
O: Po postojanosti (persistentna vs. tranzijentna), po sinhronizaciji (sinhrona vs. asinhrona) i po vremenskoj zavisnosti (diskretna vs. strimovana/streaming).

**P: Definiši perzistentnu i tranzijentnu komunikaciju.**
O: Perzistentna (istrajna) – poruka se pamti u komunikacionom serveru (middleware-u) onoliko dugo koliko je potrebno da se isporuči odredištu. Tranzijentna (privremena) – poruka se odbacuje ako komunikacioni server nije u stanju da je odmah isporuči sledećem serveru/odredištu.

**P: Zašto pošiljalac/primalac ne moraju biti aktivni kod perzistentne komunikacije?**
O: Zato što se poruka koja treba da se pošalje pamti u komunikacionom serveru onoliko dugo koliko je potrebno da stigne do prijemnika, pa aplikacija ne mora nastaviti da se izvršava nakon predaje poruke, niti primalac mora raditi u trenutku slanja.

**P: Zašto je e-mail primer perzistentne komunikacije?**
O: Korisnički agent šalje poruku svom mail serveru, koji je privremeno skladišti, pronalazi adresu odredišnog mail servera i uspostavlja vezu; ako je odredišni server nedostupan, poruka ostaje u baferu pošiljaoca dok se ne isporuči; korisnik kasnije kopira poruke iz mail box-a.

**P: Definiši sinhronu i asinhronu komunikaciju.**
O: Kod asinhrone komunikacije pošiljalac nastavlja sa radom odmah nakon slanja poruke (poruka se pamti u lokalnom baferu ili komunikacionom serveru). Kod sinhrone komunikacije pošiljalac je blokiran sve dok se ne potvrdi prihvatanje njegovog zahteva.

**P: Koje su tri alternative za dužinu blokiranja kod sinhrone komunikacije?**
O: 1) Dok middleware ne preuzme predavanje zahteva; 2) dok poruka ne stigne do primaoca; 3) dok poruka nije potpuno obrađena od strane primaoca (do odgovora).

**P: Razlikuj diskretnu i streaming komunikaciju.**
O: Diskretna – svaka poruka čini kompletan skup informacija. Streaming – uključuje slanje više poruka povezanih vremenskim odnosom/redosledom, neophodnih za rekonstrukciju kompletne informacije (npr. video streaming).

**P: Nabroj četiri kombinacije perzistentnosti i sinhronizacije.**
O: Perzistentne asinhrone, perzistentne sinhrone, tranzijentne asinhrone, tranzijentne sinhrone.

**P: Objasni perzistentne asinhrone komunikacije i daj primer.**
O: Poruka se pamti u lokalnom baferu ili prvom komunikacionom serveru dok se ne isporuči; pošiljalac nije blokiran. Primeri: e-mail, Teams Chat.

**P: Objasni perzistentne sinhrone komunikacije.**
O: Poruka mora biti zapamćena u odredišnom hostu da bi se pošiljalac deblokirao (nije neophodno da odredišna aplikacija radi). Slabija varijanta: blokiranje izvora dok se poruka ne zapamti u komunikacionom serveru odredišnog hosta.

**P: Objasni tranzijentne asinhrone komunikacije.**
O: Poruka se privremeno pamti u lokalnom baferu izvornog hosta, aplikacija nastavlja izvršenje, a komunikacioni sistem paralelno prosleđuje poruku; ako prijemnik nije aktivan kada poruka stigne, poruka je izgubljena.

**P: Nabroj i objasni tri oblika tranzijentne sinhrone komunikacije (d, e, f).**
O:
- d) Receipt-based synchronous – najslabija forma; izvor blokiran dok se poruka ne zapamti u baferu odredišnog hosta (deblokira se po prijemu potvrde/ACK).
- e) Delivery-based synchronous – izvor blokiran dok se poruka ne isporuči odredišnom procesu za dalju obradu.
- f) Response-based synchronous – izvor blokiran dok od odredišnog procesa ne primi odgovor; ovako funkcionišu sinhroni RPC i RMI.

---

## 7. Middleware bazirani na razmeni poruka (message-oriented communication)

**P: Zašto RPC/RMI nisu uvek prikladni kao mehanizam komunikacije?**
O: Zato što se ne može uvek pretpostaviti da će strana koja prima zahtev izvršavati u trenutku kada je zahtev izdat, a inherentna sinhrona priroda RPC-a (klijent blokiran do obrade zahteva) nekad nije poželjna – potrebne su alternativne (message-oriented) usluge.

**P: Koje dve vrste sistema za razmenu poruka postoje s obzirom na aktivnost strana?**
O: Sistemi koji pretpostavljaju da strane izvršavaju u trenutku komunikacije (tranzijentna komunikacija), i sistemi koji dozvoljavaju razmenu čak i ako druga strana nije aktivna u trenutku pokretanja komunikacije (perzistentna komunikacija).

**P: Zašto se javila potreba za perzistentnim komunikacionim uslugama?**
O: Tranzijentne komunikacije nisu adekvatne za potencijalno veliku geografsku distribuciju procesa; potreba se javila pri integraciji aplikacija koje se izvršavaju na geografski udaljenim računarima (WAN).

---

## 8. Tranzijentne komunikacije: MPI

**P: Šta je MPI i za šta je namenjen?**
O: Message Passing Interface – middleware sistem baziran na prenosu poruka, namenjen podršci komunikaciji u paralelnim sistemima (klasteri, mreže radnih stanica, multiračunari, multiprocesori).

**P: Zašto socket-i nisu bili dovoljno rešenje pre MPI-ja?**
O: Socket-i podržavaju komunikaciju razmenom poruka, ali su projektovani za TCP/IP protokol stek, pa nisu pogodni za interprocesnu komunikaciju kada se koriste drugi protokoli; svaki proizvođač je nudio sopstvenu varijantu, što je izazivalo problem prenosivosti programa.

**P: Kako MPI organizuje procese koji komuniciraju?**
O: Komunikacija se odvija u okviru poznate grupe procesa; svaka grupa ima identifikator, a svaki proces unutar grupe lokalni identifikator; par (identifikator_grupe, identifikator_procesa) jedinstveno identifikuje izvor/odredište poruke umesto transportnih adresa.

**P: Koje vrste komunikacije podržava MPI?**
O: Point-to-point i grupnu komunikaciju; implementiran je kao biblioteka funkcija (Fortran, C, C++; MPI 3 ima preko 440 funkcija) i podržava skoro sve oblike tranzijentnih komunikacija.

**P: Objasni `MPI_Bsend`.**
O: Tranzijentna asinhrona komunikacija – poruka se kopira u lokalni bafer MPI sistema, nakon čega pošiljalac nastavlja rad; lokalni MPI runtime uklanja poruku iz bafera i prenosi je čim primalac pozove operaciju prijema.

**P: Objasni `MPI_Send`.**
O: Blokirajuća operacija čija semantika zavisi od implementacije – može blokirati pozivaoca dok poruka nije kopirana u MPI bafer odredišnog hosta, ili dok primalac nije pokrenuo operaciju prijema.

**P: Objasni `MPI_Ssend`.**
O: Sinhronizovana komunikacija – pošiljalac se blokira dok se njegov zahtev ne prihvati za dalju obradu (dok ne krene prijem).

**P: Objasni `MPI_Sendrecv`.**
O: Najjača forma sinhronizovane komunikacije – pošiljalac šalje zahtev i blokira se dok ne dobije odgovor od primaoca; suštinski odgovara sinhronom RPC-u i obezbeđuje da je kompletna razmena završena pre nastavka izvršavanja.

**P: Objasni `MPI_Isend` i `MPI_Issend`.**
O: `MPI_Isend` – pošiljalac predaje samo pokazivač na poruku (adresa, broj i tip podataka) i odmah nastavlja rad; MPI se brine o transferu (postoje operacije za proveru/blokiranje do završetka, kako bi se izbeglo prepisivanje poruke). `MPI_Issend` – analogno, ali čeka da krene prijem pre nego se smatra završenim.

**P: Objasni `MPI_Recv` i `MPI_Irecv`.**
O: `MPI_Recv` – blokirajući poziv, prijemnik čeka dok poruka ne stigne. `MPI_Irecv` – prijemnik naznačava spremnost za prijem; može proveriti da li je poruka stigla ili blokirati dok ne stigne.

**P: Zašto MPI ima toliko različitih primitiva za slanje/prijem?**
O: Da bi projektantu programa pružile mogućnost optimizacije performansi (izbor između kopiranja/bez kopiranja, blokiranja na različitim tačkama sinhronizacije).

---

## 9. Perzistentne komunikacije: sistemi sa redovima poruka (MQS)

**P: Za šta su namenjeni sistemi sa redovima poruka (Message Queuing Systems)?**
O: Pružaju podršku perzistentnim asinhronim komunikacijama; namenjeni su aplikacijama kod kojih je prenos poruka dozvoljen da traje i nekoliko minuta (umesto sekundi/milisekundi); poseduju kapacitete za skladištenje poruka i ne zahtevaju da izvor i odredište budu aktivni istovremeno.

**P: Kako aplikacije komuniciraju u MQS-u?**
O: Smeštanjem poruka u posebne redove čekanja (queues). Pošiljaocu se garantuje samo da će poruka biti upisana u red primaoca, bez garancije da li i kada će biti pročitana; pošiljalac i primalac se izvršavaju potpuno nezavisno.

**P: Koje su četiri operacije osnovnog interfejsa i njihovo ponašanje (blokirajuće/neblokirajuće)?**
O:
- `put` – dodaje poruku u specificirani red (neblokirajuća).
- `get` – blokirajuća; vraća najstariju poruku iz reda, blokira se ako je red prazan; varijacije dozvoljavaju traženje specifične poruke po prioritetu/pattern-u.
- `poll` – neblokirajuća varijanta `get`-a; ako red/poruka ne postoji, proces nastavlja dalje.
- `notify` – instalira handler (callback) koji se automatski poziva kad je poruka dodata u red; proces ne čeka i ne proverava red – sistem sam poziva callback.

**P: Da li više procesa može pisati/čitati iz istog reda i koja je politika upravljanja redom?**
O: Da, više procesa može slati poruke u isti red i više primaoca može uklanjati poruke. Politika je uglavnom FIFO, ali većina implementacija podržava i prioritete (poruke višeg prioriteta se dostavljaju prvo).

**P: Koje P2P konfiguracije MQS podržava?**
O: Jedan pošiljalac – jedan primalac; jedan pošiljalac – više primalaca; više pošiljalaca – jedan primalac; više pošiljalaca – više primalaca. Bez obzira na broj učesnika, poruku od jednog pošiljaoca prima samo JEDAN primalac (Point-to-Point model).

**P: Šta se dešava ako upravljač reda (QMA) radi kao zaseban proces?**
O: Aplikacija može stavljati/čitati poruke samo u/iz lokalnog reda, pa su procesi QMA i aplikacija A uglavnom smešteni na istom računaru; svaka poruka mora nositi informaciju o svom odredištu, a zadatak upravljača redova je da poruku dopremi do cilja.

**P: Koja tri pitanja se javljaju kod rutiranja poruka u MQS?**
O:
1. Kako adresirati odredišni red (logička, lokacijski nezavisna imena radi transparentnosti lokacije) – svako ime se povezuje sa kontakt adresom (računar, port).
2. Kako preslikavanje imena u adresu učiniti dostupnim upravljaču redova (npr. tabela za pretraživanje kopirana svim upravljačima – problem održavanja).
3. Kako efikasno održavati to preslikavanje na velikoj skali – rešenje su posebni upravljači-ruteri koji prosleđuju poruke drugim upravljačima, čime sistem raste u preklapajuću (overlay) mrežu na nivou aplikacije.

**P: Kako funkcioniše rutiranje kroz rutere R1, R2 u primeru?**
O: Kada pošiljalac A stavi poruku za odredište B u svoj lokalni red, poruka se prenosi do najbližeg rutera R1, koji iz imena B zaključuje da poruku treba proslediti ruteru R2 – samo ruteri se ažuriraju kad se redovi dodaju/uklanjaju, dok ostali upravljači znaju samo gde je najbliži ruter.

---

## 10. Publish-Subscribe sistemi

**P: Koje su glavne uloge u Publish-Subscribe modelu i po čemu se razlikuje od Point-to-Point?**
O: Umesto pošiljaoca/primaoca imamo Publisher (Izdavač) i Subscriber (Pretplatnik). Za razliku od Point-to-Point sistema (poruku prima tačno jedan primalac), kod Publish-Subscribe poruku može primiti više Subscriber-a.

**P: Kako Publisher distribuira poruku?**
O: Kreira poruku i objavljuje je u Topic-u (temi) – destinacija je Topic umesto reda (queue); više različitih Subscriber-a se može pretplatiti na Topic i koristiti objavljene poruke.

**P: Da li Subscriber prima poruke objavljene pre njegove prijave?**
O: Ne – Subscriber prima poruku objavljenu u Topic-u samo ako se već ranije prijavio za taj Topic; poruke poslate pre prijave se ne primaju (izuzev ako implementacija predviđa drugačije, mada većina ne zahteva da Subscriber bude aktivan).

**P: Koja je razlika između pretplate po temi (topic) i pretplate po sadržaju?**
O: Pretplata po temi – pretplatnici izražavaju interes za teme (izdavači kategorizuju događaje u teme, pretplatnici primaju sve događaje date teme). Pretplata po sadržaju – pretplatnici specificiraju kriterijume/filtere zasnovane na sadržaju događaja i primaju samo one koji odgovaraju filteru.

**P: Objasni operacije `Publish(event)`, `Subscribe(filter)`, `Notify(event)` i `Unsubscribe(filter)`.**
O:
- `Publish(event)` – izdavač emituje događaj svim pretplatnicima zainteresovanim za taj tip; izdavač ne mora znati identitet pojedinačnih pretplatnika.
- `Subscribe(filter)` – pretplatnik specificira filter (tipovi događaja, atributi i sl.) koji definiše koje događaje želi da prima.
- `Notify(event)` – sistem se stara da svi pretplatnici čiji filteri odgovaraju događaju dobiju obaveštenje (odvojena/decoupled komunikacija između izdavača i pretplatnika).
- `Unsubscribe(filter)` – pretplatnik povlači interesovanje; budući događaji koji odgovaraju tom filteru mu se više ne isporučuju.

**P: Koje prednosti donosi odvojenost (decoupling) izdavača i pretplatnika?**
O: Poboljšava skalabilnost, fleksibilnost i modularnost u distribuiranim sistemima; klijent može biti i proizvođač i potrošač teme – nema ograničenja uloge.

**P: Daj primer filtriranja po temi i po sadržaju.**
O: Po temi – sistem vesti gde se članci kategorišu (politika, sport, tehnologija), a korisnici se pretplaćuju na temu "Tehnologija". Po sadržaju – sistem za praćenje berze gde trgovac specificira filter poput "Obavesti me ako cena akcije Apple pređe 150 dolara".

---

## 11. Java Messaging Service (JMS)

**P: Šta je JMS?**
O: Specifikacija standardizovanog načina za komunikaciju između distribuiranih Java programa; objedinjuje paradigme publish-subscribe i redova poruka (message queue), podržavajući teme (topics) i redove (queues) kao alternativne destinacije za poruke.

**P: Nabroj ključne uloge koje JMS razlikuje.**
O: JMS klijent (proizvodi ili konzumira poruke), JMS proizvođač (kreira/šalje poruke), JMS potrošač (prima/konzumira poruke), JMS provajder (implementacija JMS specifikacije), JMS poruka (objekat za razmenu informacija), JMS destinacija (JMS tema ili JMS red).

**P: Šta rade JNDI provajder i JMS provajder?**
O: JNDI (Java Name Directory Interface) provajder sadrži povezivanja za fabrike konekcija i destinacije i vraća odgovore na lookup zahteve na osnovu imena. JMS provajder implementira sam JMS interfejs.

**P: Šta su administrativni objekti u JMS-u?**
O: Unapred konfigurisani JMS objekti koje kreira administrator za korišćenje od strane klijenata (fabrike konekcija, destinacije), umesto da se dinamički kreiraju u kodu aplikacije; služe kao "most" između koda klijenta i JMS provajdera.

**P: Šta rade fabrike konekcija?**
O: Omogućavaju klijentima da uspostave konekciju sa JMS provajderom, enkapsulirajući detalje kreiranja konekcija, sesija i proizvođača/potrošača poruka, bez potrebe da klijent zna detalje implementacije niskog nivoa.

**P: Koji je redosled objekata u JMS programiranju (fabrika → konekcija → sesija → poruka)?**
O: Connection factory kreira Connection; Connection kreira Session; Session kreira Message producer i Message consumer koji šalju/primaju poruke prema/od destinacije (Topic ili Queue).

**P: Šta je sesija (Session) u JMS-u i šta podržava?**
O: Serija operacija koje uključuju kreiranje, slanje i prijem poruka vezanih za logički zadatak; podržava i transakcije koje grupišu seriju operacija u jednu atomičnu (sve-ili-ništa izvršavanje). Postoji jasna razlika između sesija za teme i sesija za redove (TopicConnection podržava sesije za teme, QueueConnection sesije za redove).

**P: Šta radi `rollback()` a šta `commit()` na strani proizvođača poruka (JMS Queue)?**
O: `rollback()` briše sve neposlate (neposlate/uncommitted) poruke transakcije iz predajnog reda. `commit()` uzrokuje da JMS red pošalje sve neposlate poruke te transakcije.

**P: Šta radi `rollback()` a šta `commit()` na strani potrošača poruka?**
O: `rollback()` nalaže JMS redu da ignoriše sve isporučene poruke (isporuka ponovo počinje od prve poruke). `commit()` govori JMS redu da je potrošač uspešno primio sve poruke, pa ih JMS red briše iz reda za prijem.

**P: Iz kojih delova se sastoji JMS poruka?**
O: Zaglavlje (header), set svojstava (properties) i telo poruke (body).

**P: Šta sadrži zaglavlje JMS poruke?**
O: Sve informacije potrebne za identifikaciju i rutiranje poruke: odredište (referenca na temu ili red), prioritet poruke, datum isteka, ID poruke i vremensku oznaku. Većinu polja kreira JMS provajder, neka mogu popuniti korisnici.

**P: Čemu služe svojstva (properties) poruke?**
O: Definisana su od strane korisnika i mogu se koristiti za povezivanje dodatnih metapodataka aplikacije sa porukom (npr. polje za lokaciju u sistemu svesnom konteksta).

**P: Koje vrste tela poruke (message body) postoje u JMS-u?**
O: `Message`, `TextMessage` (tekstualna poruka), `BytesMessage` (niz bajtova), `ObjectMessage` (serijalizovani Java objekat), `StreamMessage` (niz primitivnih Java vrednosti), `MapMessage` (skup parova imena/vrednosti).

**P: Šta rade proizvođač i potrošač poruka u JMS-u?**
O: Proizvođač poruka objavljuje poruke pod određenom temom ili šalje poruke u red. Potrošač poruka se pretplaćuje na poruke određene teme ili prima poruke iz reda.

**P: Zašto je potrošač složeniji od proizvođača (dva razloga)?**
O: 1) Moguće je povezati filtere sa potrošačem specificiranjem selektora poruke – predikata definisanog nad vrednostima zaglavlja i svojstava (ne tela) poruke, npr. za filtriranje po lokaciji. 2) Postoje dva moda prijema poruka – blokirajući poziv `receive()`, ili uspostavljanje `MessageListener` objekta koji implementira `onMessage()` metodu pozvanu kad stigne odgovarajuća poruka.

**P: Koji su glavni interfejsi u JMS Point-to-Point (P2P) implementaciji?**
O: `QueueConnectionFactory`, `Queue`, `QueueConnection`, `QueueSession`, `Message`, `QueueSender`, `QueueReceiver`.

**P: Koji su koraci Producer-a u P2P implementaciji?**
O: Dobiti referencu na `QueueConnectionFactory`, dobiti referencu na `Queue`, kreirati `QueueConnection`, kreirati `QueueSession`, kreirati `QueueSender`, kreirati `Message`, poslati poruku (`send`).

**P: Koji su koraci Consumer-a u P2P implementaciji?**
O: Dobiti referencu na `QueueConnectionFactory`, dobiti referencu na `Queue`, kreirati `QueueConnection`, kreirati `QueueSession`, kreirati `QueueReceiver`, čekati poruku (implementirati `MessageListener`, odnosno pozvati `receive()`).

**P: Koji su glavni interfejsi u JMS Publish-and-Subscribe implementaciji?**
O: `TopicConnectionFactory`, `Topic`, `TopicConnection`, `TopicSession`, `Message`, `TopicPublisher`, `TopicSubscriber`.

**P: Zašto se u kodu Subscriber-a koristi `System.in.read()` nakon `tc.start()`?**
O: Bez toga bi program odmah završio i konekcija bi se zatvorila pre nego što listener stigne da primi poruku; `System.in.read()` sprečava trenutni završetak programa i omogućava da `onMessage()` u pozadini prima i obrađuje poruke, dok se `tc.close()` poziva tek nakon toga.

**P: Gde treba da se nalazi obrada poruke i transakcija (commit/rollback) kod `MessageListener`-a?**
O: Unutar metode `onMessage()`.

**P: Nabroj poznate implementacije sistema sa redovima poruka i/ili publish-subscribe sistema.**
O: JMS, MSMQ, Apache ActiveMQ, Apache Kafka, Apache Pulsar, IBM MQ, NATS, RabbitMQ.

---

## Kratak rezime ključnih pojmova

| Pojam | Definicija u kratkim crtama |
|---|---|
| RMI | RPC na OO način – poziv metoda udaljenih objekata preko Interfejsa |
| Proxy / Stub | Predstavnik server objekta na klijentskoj strani; pakuje poziv u poruku |
| Skeleton | Serverska strana koja raspakuje poruku i poziva pravi metod |
| Binder / RMI registry | Servis koji mapira logičko ime u referencu udaljenog objekta |
| Perzistentna komunikacija | Poruka se čuva u middleware-u dok se ne isporuči (npr. e-mail) |
| Tranzijentna komunikacija | Poruka se odbacuje ako se odmah ne može isporučiti (npr. MPI) |
| Sinhrona komunikacija | Pošiljalac blokiran do potvrde/odgovora |
| Asinhrona komunikacija | Pošiljalac nastavlja rad odmah po slanju |
| MOM / MQS | Middleware sa redovima poruka – put/get/poll/notify |
| Publish-Subscribe | Izdavač objavljuje u Topic, više pretplatnika prima poruke |
| JMS | Java specifikacija koja objedinjuje P2P (queues) i Pub/Sub (topics) |
