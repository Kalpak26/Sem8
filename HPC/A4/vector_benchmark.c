#include <stdio.h>
#include <stdlib.h>
#include <omp.h>

// --- Helper: OpenMP Function Demo ---
// This function exists solely to demonstrate the OpenMP API functions
void demoOpenMPFunctions() {
    printf("--- OpenMP API Demo ---\n");
    
    // 1. omp_set_num_threads(): Force specific number of threads (e.g., 4)
    //omp_set_num_threads(4);
    printf("Requesting 4 threads using omp_set_num_threads()...\n");

    #pragma omp parallel
    {
        // 2. omp_get_thread_num(): Get ID of current thread
        int id = omp_get_thread_num();
        
        // 3. omp_get_num_threads(): Get total threads in this team
        int total = omp_get_num_threads();
        
        // Print only from the master thread to avoid messy output, 
        // or let everyone print to show they exist.
        printf("Hello from Thread %d of %d\n", id, total);
    }
    printf("------------------------\n\n");
}

// 1. Sequential Addition
void vectorAddSequential(double *x, double *y, double *z, long n) {
    for (long i = 0; i < n; i++) {
        z[i] = x[i] + y[i];
    }
}

// 2. Parallel Addition: Contiguous Chunks (Good)
void vectorAddParallelChunked(double *x, double *y, double *z, long n) {
    #pragma omp parallel for schedule(static)
    for (long i = 0; i < n; i++) {
        z[i] = x[i] + y[i];
    }
}

// 3. Parallel Addition: Interleaved (Bad - False Sharing)
void vectorAddParallelInterleaved(double *x, double *y, double *z, long n) {
    // schedule(static, 1) forces cyclic distribution (Thread 0 -> Index 0, 4, 8...)
    // This causes false sharing on the cache lines of 'z'
    #pragma omp parallel for schedule(static, 1)
    for (long i = 0; i < n; i++) {
        z[i] = x[i] + y[i];
    }
}

int main() {
    // --- Step 1: Run the API Demo ---
    demoOpenMPFunctions();

    // --- Step 2: Setup Benchmark ---
    // Reset thread count to maximum for the actual performance test
    int max_threads = omp_get_max_threads();
    omp_set_num_threads(max_threads);
    
    long sizes[] = {100000, 250000, 500000, 750000, 1000000, 2500000, 5000000, 7500000, 10000000, 25000000, 50000000}; 
    int num_sizes = sizeof(sizes) / sizeof(sizes[0]);

    FILE *fp = fopen("vector_results.csv", "w");
    if (!fp) { perror("File open failed"); return 1; }

    fprintf(fp, "Size,Sequential,Parallel_Chunked,Parallel_Interleaved,Threads\n");
    printf("Starting Benchmark with %d threads...\n", max_threads);

    for (int s = 0; s < num_sizes; s++) {
        long N = sizes[s];
        printf("Benchmarking Size: %ld... ", N);

        double *x = (double*)malloc(N * sizeof(double));
        double *y = (double*)malloc(N * sizeof(double));
        double *z = (double*)malloc(N * sizeof(double));

        // Initialize (Parallel initialization is faster for setup)
        #pragma omp parallel for
        for (long i = 0; i < N; i++) { x[i] = 1.0; y[i] = 2.0; }

        double start, end;

        // --- Measure Sequential ---
        start = omp_get_wtime();
        vectorAddSequential(x, y, z, N);
        end = omp_get_wtime();
        double timeSeq = end - start;

        // --- Measure Chunked (Good) ---
        start = omp_get_wtime();
        vectorAddParallelChunked(x, y, z, N);
        end = omp_get_wtime();
        double timeChunk = end - start;

        // --- Measure Interleaved (Bad) ---
        start = omp_get_wtime();
        vectorAddParallelInterleaved(x, y, z, N);
        end = omp_get_wtime();
        double timeInterleaved = end - start;

        fprintf(fp, "%ld,%f,%f,%f,%d\n", N, timeSeq, timeChunk, timeInterleaved, max_threads);
        printf("Done.\n");

        free(x); free(y); free(z);
    }

    fclose(fp);
    printf("Results saved to vector_results.csv\n");
    return 0;
}