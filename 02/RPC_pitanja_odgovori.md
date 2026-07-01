# Distribuirani sistemi – Komunikacija (RPC)
## Pitanja i odgovori za pripremu ispita

---

## 1. Osnovni koncept RPC-a

**P1: Šta je osnovni problem sa send/receive primitivama u distribuiranim sistemima?**
Send/receive primitive eksplicitno otkrivaju komunikaciju programeru, što narušava transparentnost pristupa – programer mora biti svestan da se komunikacija odvija preko mreže.

**P2: Ko je i kada predložio RPC kao rešenje ovog problema?**
Birell i Nelson su 1984. godine predložili RPC (Remote Procedure Call) – potpuno novi način komunikacije koji dozvoljava programima da pozivaju procedure locirane na drugoj mašini.

**P3: Šta se dešava kada proces na mašini A pozove proceduru na mašini B?**
Pozivni proces na mašini A se suspenduje, a izvršenje pozvane procedure se odvija na mašini B. Parametri procedure se prenose kao mrežne poruke, a za programera nikakvo slanje poruka nije vidljivo.

**P4: Koji programski model je osnova RPC-a?**
Klijent-server model: klijent pristupa udaljenom servisu pozivanjem odgovarajuće procedure koja implementira taj servis.

**P5: Koji su glavni problemi koje RPC mora da reši?**
- Kako preneti parametre (po vrednosti ili po referenci)
- Klijent i server mašine mogu koristiti različite formate predstavljanja podataka
- Kako pronaći mašinu koja implementira udaljenu proceduru
- Koja semantika važi u slučaju greške (obe mašine mogu otkazati)

**P6: Navedi primere poznatih RPC sistema.**
Sun RPC, DCE RPC, gRPC, Remote Python Call (RPyC), Java RMI, MS DCOM.

---

## 2. Prenos parametara

**P7: Koje su tri osnovne tehnike prenosa parametara kod konvencionalnih procedura?**
- Prenos po vrednosti (call-by-value)
- Prenos po referenci (call-by-reference)
- Call-by-copy/restore

**P8: Objasni prenos po vrednosti.**
Vrednosti parametara se kopiraju u stek. Za pozvanu proceduru ovi parametri predstavljaju inicijalne vrednosti lokalnih promenljivih. Pozvana procedura može modifikovati ove vrednosti, ali to ne utiče na originalne vrednosti na strani pozivaoca.

**P9: Objasni prenos po referenci.**
U stek se upisuje adresa parametra, a ne vrednost. Ako pozvana procedura modifikuje preneti parametar, promeniće se i originalna vrednost na strani pozivaoca (npr. polja se u C-u uvek prenose po referenci).

**P10: Objasni call-by-copy/restore.**
Parametri se kopiraju u stek kao kod poziva po vrednosti. Nakon okončanja poziva, vrednosti se upisuju preko originalnih vrednosti parametara, kao kod poziva po referenci. Ada kompajleri koriste ovaj metod za inout parametre.

**P11: Zašto je prenos po referenci teško implementirati kod RPC-a?**
Nema smisla proslediti adresu udaljenoj mašini, jer se adresni prostori klijenta i servera razlikuju. Zato se prenos po referenci simulira pomoću copy/restore metode – podaci se kopiraju u poruku, obrađuju na serveru, a zatim se rezultat vraća i prepisuje preko originalnih podataka kod klijenta.

**P12: Kako se u RPC-u prenosi po vrednosti?**
Jednostavno – vrednosti se direktno kopiraju u mrežne poruke.

**P13: Zašto je bitno "standardno" kodiranje podataka kod RPC-a?**
Zato što klijent i server mašine mogu imati različite načine predstavljanja brojeva (big-endian, little-endian) i karaktera (EBCDIC, ASCII). Klijentski i serverski stub moraju obavljati konverzije iz lokalnog u standardni format podataka i obrnuto.

---

## 3. Kako RPC funkcioniše – klijent/server stub

**P14: Šta je "parameter marshaling"?**
Pakovanje argumenata procedure u mrežnu poruku od strane klijentskog stub-a (uključuje i eventualnu konverziju u standardni format).

**P15: Šta je uloga klijentskog stub-a?**
Klijentski stub preuzima parametre procedure od klijenta, pakuje ih u poruku (marshaling), dodaje ime/broj procedure koja se poziva, i šalje poruku serverskom stub-u preko lokalnog OS-a (send), zatim čeka odgovor (receive).

**P16: Šta je uloga serverskog stub-a?**
Serverski stub prima poruku (receive), raspakuje je (unmarshaling), izvlači argumente, poziva odgovarajuću lokalnu (serversku) proceduru, pakuje rezultat u poruku i šalje je nazad klijentu.

**P17: Nabroj svih 10 koraka izvršenja RPC poziva.**
1. Klijent poziva lokalnu proceduru (klijent stub)
2. Klijent stub pakuje argumente u poruku (marshaling) i poziva lokalni OS
3. Lokalni OS šalje poruku udaljenom OS-u (TCP/UDP)
4. Udaljeni OS prosleđuje poruku serverskom stub-u, koji je raspakuje (unmarshaling)
5. Serverski stub poziva serversku proceduru sa dobijenim parametrima
6. Server izvršava proceduru i vraća rezultat stub-u
7. Serverski stub pakuje rezultat u poruku i poziva lokalni OS
8. Serverski OS šalje poruku klijentskom OS-u
9. Lokalni (klijentski) OS prosleđuje poruku klijent stub-u
10. Klijent stub izvlači rezultat iz poruke i vraća ga klijentskom procesu

**P18: Zašto poziv stub funkcije "izgleda" kao lokalni poziv?**
Zato što stub funkcija ima isti interfejs kao originalna procedura, ali u suštini sadrži kod za slanje i prijem poruka kroz mrežu – to je ono što obezbeđuje transparentnost pristupa.

---

## 4. Lociranje udaljenog servera (Binding)

**P19: Koja dva rešenja postoje za lociranje udaljenog servera i procedure?**
Statičko povezivanje (static binding) i dinamičko povezivanje (dynamic binding).

**P20: Objasni statičko povezivanje – prednosti i mane.**
Klijent zna koji host treba da kontaktira (adresa je "ušivena" u klijent stub). Poseban program (portmapper) pamti preslikavanje imena programa/verzije u broj porta.
- Prednosti: jednostavnost, efikasnost, nema potrebe za dodatnom infrastrukturom
- Mane: klijent i server su čvrsto povezani (ako server otkaže, klijent ne radi); ako server promeni IP adresu, klijent mora da se rekompilira

**P21: Objasni dinamičko povezivanje.**
Postoji centralizovana baza podataka (name i directory serveri) koja lokira host sa željenim servisom. Klijentski stub kontaktira server imena da dobije adresu, a serverski stub se registruje pri startovanju. Veći je overhead, ali se dobija transparentnost i fleksibilnost.

---

## 5. Problemi i semantika grešaka

**P22: Zašto se transparentnost RPC-a "završava" kod grešaka?**
Kod lokalnog poziva procedura se izvršava tačno jednom ili se ceo proces uništi ako nastupi greška. Kod RPC-a postoji više mogućih tačaka otkaza: server može generisati grešku, mreža može imati problem, server ili klijent mogu otkazati u toku izvršavanja. Aplikacija mora biti pripremljena za ove probleme.

**P23: Koje semantike izvršenja podržava RPC?**
- "Bar jednom" (at-least-once) – za idempotentne funkcije (npr. čitanje datuma, statičkih podataka), koje se mogu bezbedno ponoviti
- "Samo jednom" (at-most-once/exactly-once) – za funkcije koje nisu idempotentne (npr. modifikovanje fajla), gde ponavljanje izaziva neželjene efekte

**P24: Koliko puta se udaljena procedura može izvršiti i zašto?**
- 0 puta – ako server otkaže pre izvršenja
- Jednom – normalan slučaj
- Jednom ili više puta – ako je kašnjenje veliko ili se izgubi odgovor pa klijent izvrši retransmisiju

---

## 6. Programiranje sa RPC – IDL

**P25: Zašto je potreban poseban kompajler i IDL za RPC programiranje?**
Većina jezika (C, C++...) ne podržava koncept udaljenih procedura niti generisanje stub funkcija. Zato se koristi IDL (Interface Definition Language) da se definiše interfejs, a poseban kompajler generiše klijent i server stub-ove na osnovu te definicije.

**P26: Šta sadrži definicija interfejsa u IDL-u?**
Skup funkcija (procedura) zajedno sa ulaznim parametrima i povratnim vrednostima – slično deklaraciji prototipa funkcije.

---

## 7. Sun RPC

**P27: Koje komponente obezbeđuje Sun RPC sistem?**
XDR (eXternal Data Representation) – jezik za definisanje interfejsa, i rpcgen – interfejs kompajler koji generiše klijent i server stub funkcije.

**P28: Koja tri fajla generiše rpcgen kompajler i šta svaki sadrži?**
- **primer.h** (header) – jedinstveni identifikator interfejsa, definicije tipova, konstanti i prototipova funkcija (uključuje se i u klijent i u server kod)
- **primer_clnt.c** (klijent stub) – procedure za pakovanje parametara, slanje/prijem poruka i vraćanje rezultata klijentu
- **primer_svc.c** (server stub) – procedure koje se pozivaju kada stigne poruka i koje pozivaju odgovarajuću serversku proceduru

**P29: Kako se imenuju udaljene procedure na klijentskoj i serverskoj strani u Sun RPC-u?**
Na klijentu: `imeprocedure_brojverzije` (npr. `saberi_1`)
Na serveru: `imeprocedure_brojverzije_svc` (npr. `saberi_1_svc`)

**P30: Koliko ulaznih parametara može imati poziv udaljene procedure kod Sun RPC-a?**
Samo jedan argument (ako je potrebno više vrednosti, obično se pakuju u strukturu).

**P31: Šta sadrži identifikator RPC programa kod Sun RPC-a?**
32-bitni identifikator u heksadecimalnoj notaciji, koji se sastoji od: broja programa, broja verzije i broja procedure. Opsezi brojeva programa su podeljeni (npr. 0x00000000–0x1fffffff definiše Sun, 0x20000000–0x3fffffff definiše korisnik).

**P32: Šta je uloga portmapper-a (rpcbind) kod Sun RPC-a i na kom portu radi?**
Portmapper osluškuje na portu 111 i održava dinamičku tabelu RPC servisa na host mašini – svaka linija sadrži trojku {prognum, versnum, protokol} i odgovarajući port. Server registruje svoje procedure u portmapper preko funkcije `svc_register`. Klijent kroz `clnt_create` kontaktira portmapper da dobije odgovarajući port pre poziva procedure.

**P33: Koje su prednosti Sun RPC-a?**
- Aplikacija ne mora da vodi računa o dobijanju jedinstvene transportne adrese (broja porta) – dovoljan je jedinstveni broj programa
- Veća portabilnost
- Aplikacija ne mora da vodi računa o veličini poruke, fragmentaciji i reasembliranju
- Aplikacija treba da zna samo adresu port mappera

**P34: Koje komande se koriste za kompajliranje i pokretanje Sun RPC aplikacije?**
```
rpcgen –C primer.x                      (generisanje stub-ova)
cc –o klijent klijent.c primer_clnt.c   (kompajliranje klijenta)
cc –o server server.c primer_svc.c      (kompajliranje servera)
./server                                (pokretanje servera)
./klijent –h remus 12 15                (pokretanje klijenta)
```

---

## 8. DCE (Distributed Computing Environment)

**P35: Šta je DCE i ko ga je razvio?**
DCE (Distributed Computing Environment) je middleware baziran na RPC-u, razvijen od strane Open Software Foundation (OSF), danas Open Group (OG). To je skup servisa i alata koji se instalira iznad postojećeg OS-a i služi kao platforma za gradnju distribuiranih aplikacija.

**P36: Zbog čega je DCE značajan u istoriji middleware sistema?**
To je prvi middleware sistem projektovan kao nivo apstrakcije između mrežnog OS-a i distribuirane aplikacije.

**P37: Nabroj osnovne DCE servise.**
- RPC
- Direktorijumski servis (Cell Directory Service)
- Distribuirani fajl servis (DFS)
- Bezbednosni servis
- Servis distribuiranog vremena

**P38: Čemu služi direktorijumski servis (CDS) u DCE-u?**
Omogućava korišćenje logičkih imena unutar DCE ćelije – aplikacije identifikuju resurse po imenu bez potrebe da znaju gde su locirani (za razliku od statičkog povezivanja kod Sun RPC-a). DCE ćelije mogu međusobno komunicirati radi lociranja resursa van lokalne ćelije (slično DNS-u).

**P39: Čemu služi bezbednosni servis u DCE-u?**
Omogućava autentifikaciju (procesi na različitim mašinama utvrđuju identitet jedan drugog) i autorizaciju (server utvrđuje da li korisnik ima dozvolu pristupa resursu).

**P40: Šta pruža distribuirani fajl servis (DFS)?**
Bezbedan i uniforman pristup fajlovima sa bilo koje lokacije u mreži pod istim imenom, uz dodatne mogućnosti kao što su keširanje, bezbednost i skalabilnost.

**P41: Šta je uloga uuidgen programa u DCE-u?**
Generiše globalno jedinstveni identifikator interfejsa (UUID) – 128-bitni broj, jedinstven zahvaljujući kodiranju lokacije i trenutka kreiranja. Klijent šalje ovaj identifikator u prvoj RPC poruci, a server proverava da li takav interfejs postoji.

**P42: Koja tri fajla generiše DCE IDL kompajler?**
- Header fajl (npr. primer.h) – identifikator, tipovi, konstante, prototipovi
- Klijent stub (primer_cstub.c)
- Server stub (primer_sstub.c)

**P43: Koja tri programa piše DCE RPC aplikativni programer?**
- Klijent kod
- Server inicijalizacijski kod (registracija u direktorijumskom serveru)
- Server operativni kod

**P44: Koje dve semantičke opcije podržava DCE kod poziva udaljene procedure?**
"Bar jednom" semantiku (za idempotentne procedure, koje se moraju eksplicitno označiti kao `idempotent` u IDL-u) i "samo jednom" semantiku (za procedure koje nisu idempotentne).

**P45: Objasni proces lociranja i pozivanja servera u DCE-u (5 koraka).**
1. Server registruje broj porta (endpoint) u DCE deamonu (rpcd)
2. Server se registruje u direktorijumskom serveru (adresa mašine, ime/interfejs servera)
3. Klijent kontaktira direktorijumski server da dobije adresu server mašine
4. Klijent kontaktira DCE deamon da dobije broj porta servera
5. Klijent poziva udaljenu proceduru (RPC)

**P46: Na kom portu radi DCE deamon (rpcd) i šta on radi?**
Ima unapred poznati (well-known) broj porta i održava tabelu preslikavanja (endpoint mapa) između imena interfejsa i broja porta na kom taj server osluškuje na datoj mašini.

---

## Kratak rezime – ključne razlike Sun RPC vs DCE RPC

| Karakteristika | Sun RPC | DCE RPC |
|---|---|---|
| Jezik za definiciju interfejsa | XDR | IDL |
| Kompajler | rpcgen | idl |
| Lociranje servera | portmapper (port 111), statičko povezivanje | direktorijumski servis (CDS) + DCE deamon (rpcd), dinamičko povezivanje |
| Broj ulaznih parametara procedure | tačno jedan | fleksibilnije (definisano IDL-om) |
| Dodatni servisi | – | bezbednost, distribuirani fajl sistem, distribuirano vreme |
| Identifikacija interfejsa | broj programa + verzija (heksadecimalno) | UUID (128-bitni, generisan pomoću uuidgen) |
