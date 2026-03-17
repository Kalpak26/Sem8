#include <stdio.h>
#include <mpi.h>

int main(int argc, char **argv) {
    int rank, size, name_len;
    // The buffer size should be at least MPI_MAX_PROCESSOR_NAME characters long.
    char processor_name[MPI_MAX_PROCESSOR_NAME];

    // Initialize the MPI environment.
    MPI_Init(&argc, &argv);

    // Get the number of processes.
    MPI_Comm_size(MPI_COMM_WORLD, &size);

    // Get the rank (ID) of the current process.
    MPI_Comm_rank(MPI_COMM_WORLD, &rank);

    // Get the name of the processor the process is running on.
    MPI_Get_processor_name(processor_name, &name_len);

    // Print a message with the rank and processor name
    printf("Hello world! I am process number: rank %d of %d on processor %s\n", rank, size, processor_name);

    // Finalize the MPI environment.
    MPI_Finalize();

    return 0;
}
