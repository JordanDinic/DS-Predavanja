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
