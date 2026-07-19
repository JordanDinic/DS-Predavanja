# MPI_Vezbanje

## Sta projekat radi

Ovo je jednostavan C++/MPI projekat koji demonstrira **paralelni rad nad fajlovima** pomocu biblioteke **MPI** (Message Passing Interface).

Program:

1. pokrece vise MPI procesa,
2. otvara ulazni fajl `input.txt`,
3. deli njegov sadrzaj na blokove,
4. svaki proces cita svoj deo fajla,
5. zatim svi procesi zajednicki upisuju podatke u `output.txt` koristeci MPI file view.

Sustina projekta je da se pokaze kako vise procesa moze da cita i pise fajl **paralelno**, pri cemu se raspored upisa kontrolise pomocu MPI izvedenog tipa (`MPI_Type_vector`).

## Glavna ideja programa

U kodu je definisano:

```cpp
#define FILESIZE (12)
```

To znaci da program ocekuje da ulazni fajl ima 12 bajtova/karaktera.

Ako se program pokrene sa 3 procesa:

- svaki proces cita `12 / 3 = 4` karaktera,
- zatim se tih 4 karaktera rasporedjuje u izlazni fajl na posebno definisane pozicije.

Sa trenutnim primerom:

- `input.txt` sadrzi: `123412341234`
- `output.txt` nakon pokretanja sadrzi: `111222333444`

Drugim recima, program preuredjuje podatke iz ulaznog fajla i grupise iste pozicije iz procitanih blokova zajedno.

## Kako kod radi korak po korak

### 1. Inicijalizacija MPI okruzenja

Program poziva `MPI_Init`, a zatim uzima:

- `rank` - redni broj procesa
- `size` - ukupan broj procesa

To omogucava svakom procesu da zna koji deo posla treba da radi.

### 2. Otvaranje ulaznog fajla

Ulazni fajl se otvara pozivom:

```cpp
MPI_File_open(MPI_COMM_WORLD, "input.txt", MPI_MODE_RDONLY, MPI_INFO_NULL, &fin);
```

Svi procesi pristupaju istom fajlu.

### 3. Racunanje velicine bloka

Svaki proces cita isti broj karaktera:

```cpp
int blockSize = FILESIZE / size;
int quarter = blockSize / 4;
```

- `blockSize` je broj bajtova koji cita jedan proces
- `quarter` predstavlja cetvrtinu tog bloka i koristi se pri definisanju rasporeda upisa

### 4. Citanje iz ulaznog fajla

Pomocu:

```cpp
MPI_Offset readOffset = (MPI_Offset)(size - 1 - rank) * blockSize;
```

svaki proces cita blok iz fajla, ali **obrnutim redosledom po procesima**.

Na primer za 3 procesa:

- proces 0 cita poslednji blok,
- proces 1 cita srednji blok,
- proces 2 cita prvi blok.

### 5. Kreiranje MPI tipa za upis

Najvazniji deo programa je:

```cpp
MPI_Type_vector(
    4,
    quarter,
    size * quarter,
    MPI_CHAR,
    &filetype);
```

Ovim se definise nacin na koji ce se podaci jednog procesa rasporediti u izlaznom fajlu:

- upis se vrsi u 4 koraka,
- u svakom koraku se upisuje `quarter` karaktera,
- izmedju dva upisa postoji razmak od `size * quarter`.

To prakticno znaci da se podaci razmecu kroz fajl, umesto da se upisuju uzastopno.

### 6. Postavljanje file view i kolektivni upis

Svaki proces dobija svoj pomeraj:

```cpp
MPI_Offset disp = rank * quarter;
```

Zatim se postavlja pogled na fajl:

```cpp
MPI_File_set_view(fout, disp, MPI_CHAR, filetype, "native", MPI_INFO_NULL);
```

Na kraju svi procesi zajedno upisuju:

```cpp
MPI_File_write_all(fout, buffer, blockSize, MPI_CHAR, MPI_STATUS_IGNORE);
```

`MPI_File_write_all` je kolektivna operacija, sto znaci da svi procesi ucestvuju u upisu istovremeno.

## Struktura projekta

- `ConsoleApplication1/ConsoleApplication1.cpp` - glavni izvorni kod
- `ConsoleApplication1/ConsoleApplication1.vcxproj` - Visual Studio projekat
- `x64/Debug/input.txt` - primer ulaznog fajla
- `x64/Debug/output.txt` - rezultat izvrsavanja
- `x64/Debug/POKRETANJE.txt` - komanda za pokretanje

## Kako se pokrece

U fajlu `x64/Debug/POKRETANJE.txt` stoji sledeca komanda:

```powershell
"C:\Program Files\Microsoft MPI\Bin\mpiexec.exe" -n 3 ConsoleApplication1.exe
```

To znaci da se program pokrece sa 3 MPI procesa.

## Kompletan kod projekta

Ispod je kompletan izvorni kod glavnog programa:

```cpp
// ConsoleApplication1.cpp : This file contains the 'main' function. Program execution begins and ends there.
//

#include <mpi.h>
#include <stdio.h>
#include <stdlib.h>

#define FILESIZE (12)

int main(int argc, char** argv)
{
    MPI_Init(&argc, &argv);

    int rank, size;

    MPI_Comm_rank(MPI_COMM_WORLD, &rank);
    MPI_Comm_size(MPI_COMM_WORLD, &size);

    MPI_File fin, fout;

    MPI_File_open(MPI_COMM_WORLD,
        "input.txt",
        MPI_MODE_RDONLY,
        MPI_INFO_NULL,
        &fin);

    int blockSize = FILESIZE / size;
    int quarter = blockSize / 4;

     char* buffer = ( char*) malloc(blockSize);

    MPI_Offset readOffset =
        (MPI_Offset)(size - 1 - rank) * blockSize;

    MPI_File_seek(fin,
        readOffset,
        MPI_SEEK_SET);

    MPI_File_read(fin,
        buffer,
        blockSize,
        MPI_BYTE,
        MPI_STATUS_IGNORE);

    MPI_File_close(&fin);

    MPI_File_open(MPI_COMM_WORLD,
        "output.txt",
        MPI_MODE_CREATE | MPI_MODE_WRONLY,
        MPI_INFO_NULL,
        &fout);

    MPI_Datatype vector, filetype;

    MPI_Type_vector(
        4,
        quarter,
        size * quarter,
        MPI_CHAR,
        &filetype);

    /*MPI_Type_create_resized(
        vector,
        0,
        quarter,
        &filetype);*/

    MPI_Type_commit(&filetype);

    MPI_Offset disp = rank * quarter;

    MPI_File_set_view(
        fout,
        disp,
        MPI_CHAR,
        filetype,
        "native",
        MPI_INFO_NULL);

    MPI_File_write_all(
        fout,
        buffer,
        blockSize,
        MPI_CHAR,
        MPI_STATUS_IGNORE);

    MPI_File_close(&fout);

    //MPI_Type_free(&vector);
    MPI_Type_free(&filetype);

    free(buffer);

    MPI_Finalize();
    return 0;
}

// Run program: Ctrl + F5 or Debug > Start Without Debugging menu
// Debug program: F5 or Debug > Start Debugging menu

// Tips for Getting Started:
//   1. Use the Solution Explorer window to add/manage files
//   2. Use the Team Explorer window to connect to source control
//   3. Use the Output window to see build output and other messages
//   4. Use the Error List window to view errors
//   5. Go to Project > Add New Item to create new code files, or Project > Add Existing Item to add existing code files to the project
//   6. In the future, to open this project again, go to File > Open > Project and select the .sln file
```

## Vazne napomene

- Program je pisan za Microsoft MPI i Visual Studio C++ okruzenje.
- Vrednost `FILESIZE` je fiksno postavljena na 12.
- Program najbolje radi kada je `FILESIZE` deljiv brojem procesa.
- Izracunavanje `quarter = blockSize / 4` podrazumeva da `blockSize` moze smisleno da se podeli na 4 dela.
- Kod nema dodatnu proveru gresaka pri otvaranju fajlova i MPI pozivima.

## Zakljucak

Ovaj projekat je vezba iz MPI programiranja koja pokazuje:

- raspodelu posla izmedju vise procesa,
- paralelno citanje iz fajla,
- kolektivno pisanje u fajl,
- upotrebu izvedenih MPI tipova za kontrolu rasporeda podataka u izlaznom fajlu.

Ukratko, projekat sluzi kao primer kako se podaci mogu reorganizovati pomocu MPI paralelnog ulaza/izlaza.
