#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <omp.h>
#include <time.h>

#define MAX_N 1000000
#define RUNS 3
#define PARALLEL_THRESHOLD 10000 // Cutoff to stop creating tasks

/* ---------------- Utility ---------------- */

void fill_random(int *arr, int n) {
    for (int i = 0; i < n; i++)
        arr[i] = rand();
}

void copy_array(int *src, int *dst, int n) {
    memcpy(dst, src, n * sizeof(int));
}

/* ---------------- Merge Function ---------------- */
// Standard merge logic (unchanged)
void merge(int arr[], int l, int m, int r) {
    int n1 = m - l + 1;
    int n2 = r - m;

    int *L = (int *)malloc(n1 * sizeof(int));
    int *R = (int *)malloc(n2 * sizeof(int));

    for (int i = 0; i < n1; i++) L[i] = arr[l + i];
    for (int j = 0; j < n2; j++) R[j] = arr[m + 1 + j];

    int i = 0, j = 0, k = l;
    while (i < n1 && j < n2)
        arr[k++] = (L[i] <= R[j]) ? L[i++] : R[j++];

    while (i < n1) arr[k++] = L[i++];
    while (j < n2) arr[k++] = R[j++];

    free(L);
    free(R);
}

/* ---------------- Recursive Merge Sort ---------------- */

void mergeSort_recursive_serial(int arr[], int l, int r) {
    if (l < r) {
        int m = l + (r - l) / 2;
        mergeSort_recursive_serial(arr, l, m);
        mergeSort_recursive_serial(arr, m + 1, r);
        merge(arr, l, m, r);
    }
}

void mergeSort_recursive_parallel(int arr[], int l, int r) {
    if (l < r) {
        // OPTIMIZATION: If size is small, switch to serial to avoid task overhead
        if ((r - l) < PARALLEL_THRESHOLD) {
            mergeSort_recursive_serial(arr, l, r);
            return;
        }

        int m = l + (r - l) / 2;

        #pragma omp task shared(arr) if((r-l) >= PARALLEL_THRESHOLD)
        mergeSort_recursive_parallel(arr, l, m);

        #pragma omp task shared(arr) if((r-l) >= PARALLEL_THRESHOLD)
        mergeSort_recursive_parallel(arr, m + 1, r);

        #pragma omp taskwait
        merge(arr, l, m, r);
    }
}

/* ---------------- Iterative Merge Sort ---------------- */

void mergeSort_iterative(int arr[], int n) {
    for (int curr_size = 1; curr_size <= n - 1; curr_size *= 2) {
        
        // PARALLELISM ADDED HERE
        // We parallelize the merging of independent blocks
        #pragma omp parallel for schedule(static)
        for (int left = 0; left < n - 1; left += 2 * curr_size) {
            int mid = left + curr_size - 1;
            int right = (left + 2 * curr_size - 1 < n - 1)
                            ? left + 2 * curr_size - 1
                            : n - 1;

            if (mid < right)
                merge(arr, left, mid, right);
        }
    }
}

/* ---------------- Benchmark Wrappers ---------------- */

double benchmark_recursive(int *arr, int n, int threads) {
    omp_set_num_threads(threads); // Force thread count
    double start = omp_get_wtime();

    if (threads > 1) {
        #pragma omp parallel
        {
            #pragma omp single
            mergeSort_recursive_parallel(arr, 0, n - 1);
        }
    } else {
        mergeSort_recursive_serial(arr, 0, n - 1);
    }

    double end = omp_get_wtime();
    return end - start;
}

double benchmark_iterative(int *arr, int n, int threads) {
    omp_set_num_threads(threads); // Force thread count
    double start = omp_get_wtime();
    mergeSort_iterative(arr, n);
    double end = omp_get_wtime();
    return end - start;
}

/* ---------------- Main Experiment ---------------- */

int main() {
    srand(time(NULL));

    int sizes[] = {100, 500, 1000, 5000, 10000, 50000, 100000, 200000, 500000, 1000000};
    int num_sizes = sizeof(sizes) / sizeof(sizes[0]);
    
    // Determine max threads available on hardware
    int max_threads = omp_get_max_threads();
    printf("Benchmarking with Max Threads: %d\n", max_threads);

    FILE *fp = fopen("results.csv", "w");
    fprintf(fp, "n,method,threads,time\n");

    int *original = (int *)malloc(MAX_N * sizeof(int));
    int *working  = (int *)malloc(MAX_N * sizeof(int));

    for (int s = 0; s < num_sizes; s++) {
        int n = sizes[s];
        printf("Testing n = %d\n", n);
        fill_random(original, n);

        /* 1. Recursive Serial */
        for (int r = 0; r < RUNS; r++) {
            copy_array(original, working, n);
            double t = benchmark_recursive(working, n, 1);
            fprintf(fp, "%d,recursive,1,%f\n", n, t);
        }

        /* 2. Recursive Parallel */
        for (int r = 0; r < RUNS; r++) {
            copy_array(original, working, n);
            double t = benchmark_recursive(working, n, max_threads);
            fprintf(fp, "%d,recursive,%d,%f\n", n, max_threads, t);
        }

        /* 3. Iterative Serial */
        for (int r = 0; r < RUNS; r++) {
            copy_array(original, working, n);
            double t = benchmark_iterative(working, n, 1);
            fprintf(fp, "%d,iterative,1,%f\n", n, t);
        }

        /* 4. Iterative Parallel */
        for (int r = 0; r < RUNS; r++) {
            copy_array(original, working, n);
            double t = benchmark_iterative(working, n, max_threads);
            fprintf(fp, "%d,iterative,%d,%f\n", n, max_threads, t);
        }
    }

    fclose(fp);
    free(original);
    free(working);

    printf("\nBenchmark completed.\nResults saved to results.csv\n");

    return 0;
}
