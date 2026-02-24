#include  <mpi.h>   /* PROVIDES THE BASIC MPI DEFINITION AND TYPES */
#include <stdio.h>
#include <stdlib.h>

int main(int argc, char **argv) {
  
  int my_rank; 
  int size;
  MPI_Init(&argc, &argv); /*START MPI */
  MPI_Comm_rank(MPI_COMM_WORLD, &my_rank); /*DETERMINE RANK OF THIS PROCESSOR*/
  MPI_Comm_size(MPI_COMM_WORLD, &size); /*DETERMINE TOTAL NUMBER OF PROCESSORS*/


  printf("Hello world! I'm rank (processor number) %d of size %d\n", my_rank, size);

  if (my_rank == 0) printf("That is all for now!\n");
  MPI_Finalize();  /* EXIT MPI */
    
  exit(0);
  
}