# Distribuirani sistemi – Sinhronizacija

*Pitanja i odgovori za pripremu ispita*

## 1. Logički časovnici

**P: Zašto u distribuiranim sistemima često nije važno fizičko vreme nastanka događaja?**
O: Jer je za mnoge aplikacije (npr. make program) bitan samo relativni redosled događaja, a ne njihovo apsolutno fizičko vreme. Dovoljno je da mašine koje međusobno komuniciraju usaglase relativni redosled, koji ne mora da se poklapa sa fizičkim vremenom.

**P: Na čemu se zasniva relativno uređenje događaja u distribuiranim sistemima?**
O: Na takozvanim logičkim časovnicima.

## 2. Lamportov algoritam sinhronizacije

**P: Šta određuje Lamportov algoritam, a šta ne radi?**
O: Određuje redosled događaja (koristi se za sinhronizaciju logičkih časovnika), ali ne vrši sinhronizaciju fizičkih (realnih) časovnika.

**P: Šta predstavlja Lamportova relacija „desilo se pre” (happened-before), u oznaci A → B?**
O: Znači da se svi procesi slažu da je događaj A nastupio pre događaja B.

**P: U kojim slučajevima relacija „desilo se pre” važi?**
O: 1) Ako su a i b događaji u istom procesu i a nastupa pre b, relacija a→b je tačna. 2) Ako je a slanje poruke iz jednog procesa, a b prijem te iste poruke u drugom procesu, relacija a→b je takođe tačna, jer poruka ne može biti primljena pre nego što je poslata.

**P: Kakva je osobina relacije „desilo se pre” u pogledu tranzitivnosti?**
O: Relacija je tranzitivna: ako a→b i b→c, onda važi a→c.

**P: Kada se za dva događaja kaže da su konkurentni?**
O: Kada se dogode u dva procesa koji ne razmenjuju poruke (ni direktno ni posredno preko trećeg procesa), pa ne važi ni x→y ni y→x.

**P: Koji je cilj sinhronizacije logičkih časovnika prema Lamportu?**
O: Ako važi relacija a→b, treba obezbediti da vremenske markice zadovoljavaju T(a) < T(b); svakom događaju se dodeljuje vremenska markica (timestamp), a između svaka dva događaja sat mora otkucati bar jednom. Vreme se koriguje samo uvećanjem vrednosti, nikad unazad.

**P: Navedi tri pravila Lamportovog algoritma.**
O: 1) Logički časovnik Li se uvećava (Li = Li+1) pre svake aktivnosti procesa Pi (slanje poruke, isporuka aplikaciji, interni događaj). 2) Kada Pi šalje poruku m, u nju upisuje trenutnu vrednost t = Li. 3) Kada Pj primi poruku (m, t), podešava svoj časovnik na Lj = max(Lj, t), primeni pravilo 1, a zatim prosledi poruku aplikaciji.

**P: Kako se u praksi proverava i koriguje časovnik pri prijemu poruke sa markicom?**
O: Ako je lokalni_sat < vremenska_markica_poruke, lokalni sat se postavlja na (vremenska_markica + 1); u suprotnom nije potrebna nikakva korekcija. Time se obezbeđuje da sat otkuca bar jednom između bilo koja dva događaja.

**P: Koji je osnovni problem Lamportovog algoritma?**
O: Moguće je da više konkurentnih (međusobno nezavisnih) događaja dobiju identičnu vremensku markicu, što otežava donošenje jedinstvene odluke svih procesa kada je poredak događaja nebitan, ali odluka mora biti ista za sve.

**P: Kako se rešava problem istih vremenskih markica?**
O: Definiše se globalna, jedinstvena markica u obliku para (Ti, i), gde je Ti lokalna Lamportova markica, a i globalno jedinstveni identifikator procesa (npr. adresa hosta ili ID procesa). Ovim se markice porede leksikografski i uvek se dobija jedinstven poredak.

## 3. Primer: potpuno uređena grupna komunikacija

**P: Koji problem ilustruje primer replicirane bankarske aplikacije (NY i SF)?**
O: Ako se dve nezavisne transakcije (dodavanje depozita u SF i obračun kamate u NY) primene različitim redosledom na dve replike, replike ostaju u nekonzistentnom stanju (npr. jedna sadrži $1.111, a druga $1.110).

**P: Šta je potpuno uređena grupna komunikacija (multicast) i zašto je potrebna?**
O: To je operacija kojom se sve poruke isporučuju svim primaocima u istom redosledu. Potrebna je da bi operacije ažuriranja bile primenjene istim redosledom na svim replikama, čime se obezbeđuje konzistentnost, bez obzira koji je redosled zaista izabran.

**P: Opiši osnovni tok algoritma za potpuno uređenu grupnu komunikaciju.**
O: Poruka ažuriranja se obeležava logičkim vremenom izvora i šalje (multicast) svim članovima grupe, uključujući i pošiljaoca. Po prijemu, poruka se smešta u lokalni red čekanja uređen po markicama, a zatim se svim članovima šalje potvrda (ack), takođe multicast-om. Poruke iz istog izvora stižu FIFO redosledom.

**P: Kada se poruka konačno prosleđuje aplikaciji?**
O: Tek kada se nalazi na vrhu lokalnog reda čekanja i kada je potvrđena (ack) od strane svih procesa u grupi.

**P: Pod kojim uslovima proces Pi šalje potvrdu (ack) za poruku primljenu od Pj?**
O: Ako Pi sam nije poslao sopstvenu poruku ažuriranja, ili ako je identifikator Pi veći od identifikatora Pj, ili ako je zahtev za ažuriranjem procesa Pi već obrađen.

**P: Šta garantuje ovaj algoritam?**
O: Garantuje totalno (potpuno) uređenje događaja u svim procesima grupe, korišćenjem Lamportovog algoritma logičkih časovnika.

## 4. Vektorski časovnici

**P: Koji je osnovni nedostatak Lamportovih markica koji rešavaju vektorski časovnici?**
O: Na osnovu Lamportovih markica ne može se pouzdano utvrditi da li su dva događaja međusobno uslovljena ili konkurentna – iz T(a) < T(b) ne sme se zaključiti da a→b.

**P: Šta je vektorski časovnik u sistemu sa N procesa?**
O: Vektor od N celobrojnih elemenata; svaki proces ima sopstveni vektorski časovnik kojim beleži lokalne događaje, a vektor se šalje uz svaku poruku, slično Lamportovoj markici.

**P: Navedi pravila ažuriranja vektorskog časovnika.**
O: 1) Svi vektori se inicijalizuju na nulu: Vi[j] = 0 za sve i, j. 2) Proces Pi pre obeležavanja događaja uvećava sopstveni element: Vi[i] = Vi[i] + 1. 3) Uz poruku koju šalje Pi ide i njegov vektor Vi. 4) Kada Pj primi poruku, poredi element po element svoj i primljeni vektor i za svaki element uzima veću vrednost.

**P: Kako se pomoću vektora zaključuje da su dva događaja konkurentna?**
O: Ako se ne može uspostaviti ni relacija V(e) ≤ V(e') ni V(e) ≥ V(e'), događaji e i e' su konkurentni, tj. nisu međusobno uslovljeni.

**P: Šta predstavlja element Vi[i], a šta Vi[j] (za j različito od i) u vektoru procesa Pi?**
O: Vi[i] predstavlja broj događaja koji su se desili u samom procesu Pi. Ako je Vi[j] = k, to znači da Pi zna da se u procesu Pj desilo tačno k događaja.

**P: Kako se vektorski časovnici koriste za uređenje međusobno zavisnih događaja (npr. poruka m1 i njen odgovor m2)?**
O: Pošto je m2 odgovor na m1, sistem mora osigurati da svi procesi prvo obrade m1, a tek zatim m2. Ako m2 stigne pre m1, mora se baferovati dok m1 ne stigne. Časovnik se pri tome uvećava samo pri slanju poruke.

**P: Navedi dva uslova koja moraju biti zadovoljena da bi poruka m sa markicom tm, poslata od Pi, bila prosleđena procesu Pj.**
O: A) tm[i] = Vj[i] + 1 – obezbeđuje da je Pj primio baš sve prethodne poruke koje je Pi poslao pre poruke m. B) tm[k] ≤ Vj[k] za svako k ≠ i – obezbeđuje da je Pj već primio sve poruke koje je Pi primio pre slanja poruke m. Ako uslovi nisu ispunjeni, poruka se baferuje.

## 5. Uzajamno isključivanje u distribuiranim sistemima

**P: Zašto se u distribuiranim sistemima uzajamno isključivanje ne može ostvariti semaforima ili monitorima?**
O: Jer ti mehanizmi zahtevaju zajedničku memoriju, a u distribuiranim sistemima procesi nemaju zajedničku memoriju – jedini način koordinacije je razmena poruka.

**P: Na koje tri kategorije se dele algoritmi uzajamnog isključivanja u DS?**
O: Centralizovani, distribuirani i algoritmi bazirani na žetonima (token).

### 5.1 Centralizovani algoritam

**P: Kako funkcioniše centralizovani algoritam uzajamnog isključivanja?**
O: Jedan proces se bira za koordinatora i oponaša se jednoprocesorski sistem. Kada proces želi pristup kritičnoj sekciji, šalje zahtev koordinatoru. Ako niko drugi nije u toj kritičnoj sekciji, koordinator odmah šalje dozvolu; u suprotnom ne odgovara i proces se blokira, dok pri izlasku šalje poruku oslobađanja, nakon čega koordinator dozvoljava pristup sledećem čekajućem procesu.

**P: Koje su prednosti, a koje mane centralizovanog algoritma?**
O: Prednosti: jednostavan za implementaciju, zahteva razmenu samo tri poruke (request, OK, release) i fer je (zahtevi se opslužuju po redosledu prijema). Mane: koordinator može postati usko grlo performansi, a njegov otkaz zaustavlja sistem; proces ne može razlikovati otkaz koordinatora od običnog čekanja, što se rešava tako što koordinator eksplicitno šalje poruku odbijanja („permission denied”).

### 5.2 Distribuirani algoritam (Ricart & Agrawala)

**P: Šta koristi Ricart–Agrawala algoritam?**
O: Koristi multicast (grupnu komunikaciju) i logičke časovnike.

**P: Šta radi proces koji želi da uđe u kritičnu sekciju?**
O: Kreira poruku sa identifikatorom (host_ID, proces_ID), imenom resursa i vrednošću logičkog časovnika, šalje zahtev svim procesima u grupi, čeka dozvolu od svih, pa tek onda ulazi u kritičnu sekciju.

**P: Kako proces reaguje kada primi tuđi zahtev za pristup resursu?**
O: Ako nije zainteresovan za resurs, odmah šalje potvrdu (OK). Ako je sam trenutno u kritičnoj sekciji, ne odgovara i smešta zahtev u red čekanja. Ako je i sam upravo poslao sopstveni zahtev, poredi vremenske markice – proces sa manjom markicom „pobeđuje” i dobija potvrdu, dok se odgovor „pobednika” odlaže smeštanjem tuđeg zahteva u red čekanja.

**P: Šta se dešava kada proces završi korišćenje kritične sekcije?**
O: Šalje potvrdu (OK) svim zahtevima koji su u međuvremenu ostali u njegovom redu čekanja.

**P: Koje su glavne osobine i mane Ricart–Agrawala algoritma?**
O: Garantuje uzajamno isključivanje bez deadlocka i izgladnjivanja, ali zahteva razmenu 2(n−1) poruka po pristupu (n−1 zahtev i n−1 potvrda); greška u bilo kom procesu blokira ceo sistem jer je potrebna potvrda svih n−1 procesa.

**P: Kako se algoritam može poboljšati u pogledu pouzdanosti?**
O: Proces uvek odgovara na zahtev (dozvolom ili odbijanjem); pošiljalac zahteva koristi timeout i ponavlja zahtev dok ne dobije odgovor ili ne zaključi da je ciljni proces mrtav. Takođe, umesto potvrde svih n−1 procesa, može se tražiti potvrda samo od većine m > n/2 procesa.

### 5.3 Algoritam sa prstenom i žetonima (token ring)

**P: Kako se formira logički prsten u token ring algoritmu?**
O: Procesima se dodeljuju redni brojevi koji formiraju softverski (logički) prsten, nezavistan od fizičkih veza između računara; svaki proces poznaje adresu svog desnog suseda u smeru kazaljke na satu.

**P: Kako žeton kruži i kako proces pristupa resursu?**
O: Proces 0 inicijalno dobija žeton za resurs R; žeton kruži od procesa do procesa u smeru kazaljke na satu. Proces koji želi pristup resursu čeka dok ne dobije žeton od levog suseda, zadržava ga dok koristi resurs (kritičnu sekciju), a zatim ga prosleđuje sledećem procesu u prstenu.

**P: Koje osobine ima token ring algoritam?**
O: Samo jedan proces u datom trenutku može posedovati žeton, čime je uzajamno isključivanje zagarantovano, redosled pristupa je dobro definisan i svakom procesu je garantovan pristup resursu – ali algoritam ne garantuje FIFO redosled pristupa.

**P: Šta se dešava ako se žeton izgubi?**
O: Neophodno je rekonfigurisati prsten kako bi se isključio otkazali proces, zatim regenerisati žeton i ponovo ga pustiti u prsten, što zahteva pokretanje algoritma izbora (election algorithm).

## 6. Algoritmi izbora koordinatora (election algorithms)

**P: Čemu služe algoritmi izbora?**
O: Služe da se izabere jedan proces koji će obavljati ulogu koordinatora ili servera u distribuiranom sistemu.

**P: Koje su polazne pretpostavke algoritama izbora?**
O: Svaki proces ima jedinstveni identifikator (npr. kombinacija adrese računara i ID procesa) i zna identifikatore svih ostalih procesa, ali ne zna da li su ti procesi trenutno aktivni (živi).

**P: Koji je cilj algoritma izbora i kroz koje se faze obično odvija?**
O: Cilj je pronaći aktivni proces sa najvećim identifikatorom i proglasiti ga koordinatorom, uz saglasnost svih procesa. Odvija se u dve faze: selekcija lidera sa najvećim identifikatorom i obaveštavanje svih procesa o pobedniku.

### 6.1 Bully algoritam

**P: Šta pokreće Bully algoritam?**
O: Proces Pi koji primeti da koordinator ne odgovara pokreće postupak izbora.

**P: Kako teče Bully algoritam korak po korak?**
O: Pi šalje izbornu poruku svim procesima sa većim identifikatorom i čeka odgovor. Ako niko od njih ne odgovori u zadatom vremenskom intervalu, znači da nijedan aktivniji (veći) proces ne postoji, pa Pi postaje pobednik i obaveštava sve procese da je on novi koordinator. Ako neki proces sa većim identifikatorom odgovori, Pi se povlači i čeka poruku od novog koordinatora.

**P: Odakle potiče naziv „bully” (siledžija) za ovaj algoritam?**
O: Naziv oslikava princip da uvek pobeđuje proces sa najvećim identifikatorom – „najsnažniji momak u gradu uvek pobeđuje”.

### 6.2 Ring algoritam izbora

**P: Kako je organizovan Ring algoritam izbora?**
O: Procesi formiraju logički prsten. Ako neki proces detektuje da koordinator ne funkcioniše, pokreće izbor slanjem izborne poruke, koja sadrži njegov ID, susedu u prstenu.

**P: Šta se dešava kada izborna poruka putuje kroz prsten?**
O: Ako sused nije aktivan, poruka se prosleđuje sledećem procesu. Svaki aktivan proces koji primi poruku dodaje joj svoj ID i prosleđuje je dalje sledećem procesu u prstenu.

**P: Kako se algoritam završava i bira novi koordinator?**
O: Kada se poruka vrati procesu koji je pokrenuo izbor (prepoznaje sopstveni ID u poruci), on na osnovu sadržaja poruke zna koji su svi aktivni članovi prstena, bira proces sa najvećim identifikatorom za koordinatora i šalje novu poruku „KOORDINATOR”, sa identifikatorom pobednika, da obavesti sve ostale procese.
