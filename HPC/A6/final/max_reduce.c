#include <mpi.h>
#include <stdio.h>
#include <stdlib.h>
#include <time.h>

#define DATA_SIZE 50

int main(int argc, char** argv) {
    MPI_Init(&argc, &argv);

    int world_rank;
    MPI_Comm_rank(MPI_COMM_WORLD, &world_rank);

    int world_size;
    MPI_Comm_size(MPI_COMM_WORLD, &world_size);

    // Seed random uniquely for each node
    srand(time(NULL) + world_rank);

    int local_data[DATA_SIZE];
    int local_max = -1;

    // Generate random data and find the local max for this node
    for (int i = 0; i < DATA_SIZE; i++) {
        local_data[i] = rand() % 10000;
        if (local_data[i] > local_max) {
            local_max = local_data[i];
        }
    }

    printf("Node %d finished searching. Local Max: %d\n", world_rank, local_max);

    // MPI_Reduce acts as a funnel to find the absolute highest number
    int global_max;
    MPI_Reduce(&local_max, &global_max, 1, MPI_INT, MPI_MAX, 0, MPI_COMM_WORLD);

    // Only Node 0 (the root) prints the final answer
    if (world_rank == 0) {
        printf("\n==================================================\n");
        printf("SUCCESS: MPI_Reduce complete across %d processes.\n", world_size);
        printf("The Global Maximum number found is: %d\n", global_max);
        printf("==================================================\n");
    }

    MPI_Finalize();
    return 0;
}
