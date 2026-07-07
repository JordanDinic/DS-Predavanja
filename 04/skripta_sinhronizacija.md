# Sinhronizacija u distribuiranim sistemima — pitanja i odgovori

## 1. Osnovni problemi sinhronizacije u DS

**P: Zašto je sinhronizacija procesa važna u distribuiranim sistemima (DS)?**
O: Jer procesi ne treba istovremeno da pristupaju deljivom resursu (npr. štampaču), ponekad se moraju usaglasiti oko redosleda događaja (npr. koja je poruka poslata pre koje) i ponekad se moraju usaglasiti oko izbora koordinatora.

**P: Koji je osnovni problem koji komplikuje sinhronizaciju u DS i RM (računarskim mrežama)?**
O: Ne postoji pojam globalnog vremena — svaka mašina ima svoj sopstveni časovnik, pa procesi na različitim mašinama imaju različitu predstavu o vremenu.

**P: Šta se rešava algoritmima izbora (election algorithms)?**
O: Usaglašavanje svih procesa oko izbora zajedničkog koordinatora.

**P: Zašto je striktna sinhronizacija časovnika bitna u realnovremenskim sistemima?**
O: Jer neke aktivnosti moraju biti obavljene u tačno određenom trenutku, a održavanje konzistentnosti distribuiranih podataka često se bazira na vremenu kada je izvršena određena modifikacija.

**P: Ko je i zašto uveo pojam logičkog časovnika?**
O: Lamport je uveo logički časovnik da bi se postigla sinhronizacija međusobno zavisnih događaja bez potrebe da se sinhronizuju fizički časovnici.

## 2. Primer problema — UNIX make program

**P: Kako radi make program u UNIX-u i zašto je bitna sinhronizacija vremena?**
O: Make poredi vreme generisanja izvornog fajla (.c) i objektnog fajla (.o). Ako je .c fajl stariji od .o fajla, nema potrebe za rekompilacijom; u suprotnom fajl treba rekompilirati.

**P: Šta se dešava ako se kreiranje/editovanje programa i kompajliranje obavljaju na dve različite mašine sa nesinhronizovanim časovnicima?**
O: Make može pogrešno zaključiti da fajl ne treba rekompilirati, jer je izmenjeni .c fajl dobio raniju vremensku oznaku (jer časovnik na toj mašini kasni) od .o fajla generisanog na drugoj, „bržoj" mašini.

## 3. Fizički časovnik — kako radi

**P: Šta je tajmer (timer) i kako fizički funkcioniše?**
O: Tajmer je kvarcni kristal koji pod naponom osciluje na poznatoj frekvenciji; brzina oscilacije zavisi od vrste kristala, načina sečenja i veličine napona.

**P: Koji registri su pridruženi kristalu i kako oni funkcionišu?**
O: Brojač (counter) i holding registar. Svaka oscilacija dekrementira brojač; kada brojač dođe do 0, generiše se prekid i brojač se ponovo puni vrednošću iz holding registra.

**P: Kako se zove prekid koji generiše tajmer i gde se čuva vreme sistema?**
O: Zove se otkucaj časovnika. Vreme se čuva u CMOS RAM-u koji se napaja baterijom (pa se ne mora unositi pri svakom paljenju), a inkrementira se softverski časovnik pri svakom otkucaju.

**P: Zašto sinhronizacija nije problem kada postoji samo jedan procesor sa jednim časovnikom?**
O: Jer svi procesi na toj mašini koriste isti časovnik, pa su interno konzistentni, bez obzira da li je časovnik pomeren u odnosu na realno vreme.

**P: Zašto sinhronizacija postaje problem kod više procesora/računara?**
O: Jer svaki kristal osciluje na neznatno različitoj frekvenciji, pa softverski časovnici postepeno „ispadnu" iz sinhronizma i daju različite vrednosti pri očitavanju vremena.

## 4. Merenje vremena — od solarnog do atomskog

**P: Šta je solarni dan i koliko sekundi ima?**
O: Solarni dan je vreme između dva uzastopna prolaska Sunca kroz zenit; traje 24h, odnosno 86400 sekundi (1 sec = 1/86400 solarnog dana).

**P: Šta je otkriveno 1940. godine u vezi sa rotacijom Zemlje?**
O: Da period rotacije Zemlje nije konstantan (npr. pre 300 miliona godina zemaljska godina je imala 400 dana).

**P: Šta je omogućio pronalazak atomskog časovnika 1948. godine?**
O: Mnogo pouzdanije i od rotacije Zemlje nezavisno merenje vremena, brojanjem tranzicija u atomu cezijuma-133.

**P: Kako fizičari definišu jednu sekundu?**
O: Kao vreme potrebno cezijumu-133 da izvrši 9.192.631.770 tranzicija (broj je izabran da se atomska sekunda izjednači sa solarnom).

**P: Šta je BIH i šta generiše?**
O: Bureau International de l'Heure (Internacionalni biro za vreme) u Parizu prikuplja podatke od oko 50 cezijumovih laboratorija o broju otkucaja od ponoći 1. januara 1958, pronalazi srednju vrednost i generiše Internacionalno atomsko vreme (TAI).

**P: Zašto dolazi do neslaganja TAI i solarnog vremena?**
O: Zbog oscilacija u brzini rotacije Zemlje — solarni dan je duži za 0.001 sec od TAI dana.

## 5. UTC i prestupna sekunda

**P: Kako BIH rešava neslaganje solarnog i atomskog vremena?**
O: Kada razlika dosegne 800ms, ubacuje se prestupna sekunda (leap second) u TAI vreme, pa taj dan traje 86400+1 = 86401 sekundu. Tako dobijeno vreme se zove UTC (Universal Coordinated Time).

**P: Šta je UTC zamenio?**
O: Ranije korišćen standard Srednje Griničko vreme (Greenwich Mean Time).

**P: Kako izgleda ubacivanje prestupne sekunde u praksi?**
O: Umesto da nakon 23:59:59 odmah dođe 00:00:00, ubacuje se dodatna sekunda 23:59:60, a zatim tek 00:00:00. Obično se ubacuje 31. decembra ili 30. juna.

**P: Koji servisi se koriste za precizno merenje UTC vremena?**
O: Radio stanice koje emituju kratke impulse na početku svake UTC sekunde (npr. WWV u SAD, MSF u Engleskoj) i geostacionarni sateliti, uz odgovarajući prijemnik podešen na frekvenciju predajnika.

## 6. GPS (Global Positioning System)

**P: Zašto je GPS relevantan za temu sinhronizacije časovnika?**
O: Da bi se koristio kao izvor tačnog vremena, neophodno je poznavati poziciju izvora i odredišta radi kompenzacije komunikacionog kašnjenja — GPS rešava problem određivanja pozicije.

**P: Od čega se sastoji GPS sistem?**
O: Od 29 satelita koji kruže u zemljinoj orbiti na visini od 20.000 km; svaki satelit ima 4 atomska časovnika koji se redovno kalibrišu sa zemaljske stanice, a satelit stalno emituje svoju poziciju i vreme [xi, yi, zi, si].

**P: Koliko satelita je minimalno potrebno da bi se odredio položaj objekta na Zemlji?**
O: Najmanje 4 satelita.

**P: Koje faktore treba uzeti u obzir u realnom GPS proračunu pozicije i vremena, koji komplikuju idealizovani model?**
O: Zemlja nije savršena sfera, atomski časovnici na satelitima nisu potpuno sinhronizovani, pozicija satelita nije poznata sasvim precizno, a brzina prostiranja signala nije konstantna (usporava u jonosferi).

**P: Kolika je preciznost koju u praksi postižu čak i jeftini GPS prijemnici?**
O: Preciznost lokacije u granicama 1–5 metara i preciznost vremena od nekoliko desetina nanosekundi.

## 7. Kristijanov algoritam (eksterna sinhronizacija)

**P: Kako funkcioniše Kristijanov algoritam?**
O: U sistemu postoji jedna mašina sa WWV prijemnikom (server vremena/time server). Svaka mašina periodično šalje serveru poruku tražeći trenutno vreme, a server odgovara vrednošću CUTC.

**P: Zašto klijent ne može jednostavno da postavi svoj časovnik na primljenu vrednost CUTC?**
O: Zato što vreme nikad ne sme ići unazad — ako je klijentov sat brži, CUTC bi bio manji od trenutne vrednosti klijentovog sata, što bi pravilo probleme (npr. kod make programa).

**P: Kako se rešava problem da vreme ne ide unazad?**
O: Promena časovnika se obavlja postepeno, usporavanjem ili ubrzavanjem lokalnog časovnika — npr. ako tajmer generiše 100 prekida/sec i svaki normalno dodaje 10ms, za usporavanje se svakom prekidu dodaje manje (npr. 9ms) dok se ne postigne sinhronizacija.

**P: Kako se kompenzuje propagaciono kašnjenje u Kristijanovom algoritmu?**
O: Klijent mери vreme od slanja zahteva (T0) do prijema odgovora (T1), i dobijenu vrednost vremena od servera koriguje za (T0-T1)/2 (odnosno vrednost se uvećava za pola izmerenog kašnjenja).

## 8. Berkeley algoritam (interna sinhronizacija)

**P: Koja je ključna razlika između Berkeley algoritma i Kristijanovog algoritma?**
O: Kod Kristijanovog algoritma server vremena je pasivan (samo odgovara na zahteve), dok je kod Berkeley algoritma server (time daemon) aktivan — on sam periodično proziva sve mašine.

**P: Kako Berkeley algoritam izračunava tačno vreme?**
O: Time daemon prozove sve mašine i prikuplja njihova vremena (razlike u odnosu na svoje), izračunava srednju vrednost vremena i zatim saopštava svim mašinama kako da podese svoje časovnike.

**P: Za koje sisteme je Berkeley algoritam posebno pogodan?**
O: Za sisteme u kojima ne postoji WWV prijemnik.

## 9. NTP (Network Time Protocol)

**P: Za koje mreže su projektovani Kristijanov i Berkeley algoritam, a za koje NTP?**
O: Kristijanov i Berkeley algoritam su projektovani za sinhronizaciju u intranetu (privatnim mrežama) sa jednim serverom vremena, dok je NTP projektovan za sinhronizaciju na Internetu (ili drugim nepouzdanim mrežama) i koristi mnogo servera.

**P: Na kom nivou OSI/TCP-IP modela radi NTP i koji transportni protokol koristi?**
O: NTP je protokol aplikativnog nivoa, koristi UDP na transportnom nivou, a server osluškuje klijente na portu 123.

**P: Kako su serveri vremena organizovani u NTP mreži?**
O: Hijerarhijski, u slojeve nazvane stratum-i, numerisane od 0. Nivo 0 je izvor tačnog vremena (atomski časovnik), nivo 1 su serveri direktno povezani sa nivoom 0, nivo 2 sa nivoom 1 itd. Svi serveri zajedno čine sinhronizacionu podmrežu.

**P: Koja su tri režima sinhronizacije u NTP protokolu (RFC 5905)?**
O: 1) Simetrični režim — najprecizniji, koristi se za sinhronizaciju master servera, par servera razmenjuje poruke sa informacijama o vremenu; 2) klijent-server (RPC) režim — radi slično Kristijanovom algoritmu; 3) multicast (broadcast) režim — koristi se u brzim LAN mrežama, server periodično emituje vreme.

**P: Šta predstavlja polje Leap Indicator (LI) u NTP poruci?**
O: Ukazuje da li će zadnji sat toga dana imati prestupnu sekundu.

**P: Šta predstavlja polje Stratum u NTP poruci?**
O: Nivo NTP servera koji šalje poruku.

**P: Šta predstavlja polje Poll u NTP poruci?**
O: Ceo broj koji predstavlja maksimalni interval (izražen kao log2 sekunde) između dve uzastopne poruke — tipično 6 do 10.

**P: Šta predstavlja polje Precision, i koji je primer njegove interpretacije?**
O: Preciznost časovnika izraženu u log2 sekunde; npr. vrednost -18 (2⁻¹⁸ sec) odgovara preciznosti od oko 1 mikrosekunde.

**P: Šta predstavljaju polja Rootdelay, Refid i Reftime u NTP poruci?**
O: Rootdelay je kružno vreme propagacije do referentnog servera; Refid je 32-bitni identifikator servera koji se koristi kao referentni časovnik; Reftime je vreme kada je sistemski časovnik poslednji put postavljen ili korigovan.

## 10. RPC (klijent-server) režim NTP-a — detaljno

**P: Koje vremenske oznake učestvuju u RPC režimu sinhronizacije i šta svaka predstavlja?**
O:
- t0 — lokalno vreme klijenta u trenutku slanja zahteva
- t1 — vreme na serveru u trenutku prijema poruke
- t2 — vreme na serveru u trenutku slanja odgovora
- t3 — vreme na klijentu u trenutku prijema odgovora

**P: Šta klijent radi sa ove četiri vremenske oznake (t0, t1, t2, t3)?**
O: Na osnovu njih izračunava korekciju, odnosno procenjuje tačno (offset i round-trip) vreme, kombinujući razlike (t1-t0) i (t2-t3) da bi kompenzovao propagaciono kašnjenje.

---

### Napomena
Ova skripta prati sadržaj priloženog fajla (25 slajdova) koji se bavi fizičkim časovnicima i njihovom sinhronizacijom (GPS, Kristijanov algoritam, Berkeley algoritam, NTP). Naslovni slajd prezentacije pominje i teme *logički časovnici*, *uzajamno isključivanje* i *algoritmi izbora koordinatora* — te teme nisu obrađene u dostavljenom fajlu, pa nisu uključene u ovu skriptu. Ako imaš i taj deo materijala, rado ću dodati pitanja i za njega.
