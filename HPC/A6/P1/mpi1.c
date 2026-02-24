#include <mpi.h>   /* PROVIDES THE BASIC MPI DEFINITION AND TYPES */
#include <stdio.h>

int main(int argc, char **argv) {
  
  MPI_Init(&argc, &argv); /*START MPI */
  printf("Hello world\n");
  MPI_Finalize();  /* EXIT MPI */
}