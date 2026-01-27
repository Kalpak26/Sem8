#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>
#include <string.h>
#include <omp.h> // Required for OpenMP and Timing

// Helper function to swap two integers
void swap(int *a, int *b) {
    int temp = *a;
    *a = *b;
    *b = temp;
}

// 1. Normal Bubble Sort
void bubbleSortNormal(int *arr, int n) {
    for (int i = 0; i < n - 1; i++) {
        for (int j = 0; j < n - i - 1; j++) {
            if (arr[j] > arr[j + 1]) {
                swap(&arr[j], &arr[j + 1]);
            }
        }
    }
}

// 2. Optimized Bubble Sort with "Stop" Flag
void bubbleSortOptimized(int *arr, int n) {
    bool stop;
    for (int i = 0; i < n - 1; i++) {
        stop = true; // Assume sorted
        for (int j = 0; j < n - i - 1; j++) {
            if (arr[j] > arr[j + 1]) {
                swap(&arr[j], &arr[j + 1]);
                stop = false; // Swap happened
            }
        }
        if (stop) break; // Stop if array is already sorted
    }
}

// 3. Parallel Odd-Even Sort (OpenMP)
void bubbleSortParallel(int *arr, int n) {
    int i, j;
    for (i = 0; i < n; i++) {
        int first = i % 2;
        
        // Parallel region based on your algorithm
        // Note: added 'n' to shared variables
        #pragma omp parallel for default(none) shared(arr, n, first) private(j)
        for (j = first; j < n - 1; j += 2) {
            if (arr[j] > arr[j + 1]) {
                swap(&arr[j], &arr[j + 1]);
            }
        }
    }
}

// Helper to generate random data
void generateRandomData(int *arr, int n) {
    for (int i = 0; i < n; i++) {
        arr[i] = rand() % 10000;
    }
}

int main() {
    // Array sizes to test
    int sizes[] = {1000, 2500, 5000, 7500, 10000, 15000, 20000, 25000, 50000, 100000, 150000};
    int num_sizes = sizeof(sizes) / sizeof(sizes[0]);

    // Open file for writing
    FILE *fp = fopen("benchmark_results.csv", "w");
    if (fp == NULL) {
        printf("Error opening file!\n");
        return 1;
    }
    
    // Write CSV Header
    fprintf(fp, "Size,Normal,Optimized,Parallel\n");
    printf("Starting Benchmark...\n");

    for (int s = 0; s < num_sizes; s++) {
        int N = sizes[s];
        printf("Testing size: %d...\n", N);

        // Allocate memory
        int *masterData = (int*)malloc(N * sizeof(int));
        int *tempData = (int*)malloc(N * sizeof(int));

        // Generate random data once
        generateRandomData(masterData, N);

        double start, end;

        // --- Benchmark 1: Normal ---
        // Copy master data to temp to ensure we sort the exact same dataset
        memcpy(tempData, masterData, N * sizeof(int));
        
        start = omp_get_wtime();
        bubbleSortNormal(tempData, N);
        end = omp_get_wtime();
        double timeNormal = end - start;

        // --- Benchmark 2: Optimized ---
        memcpy(tempData, masterData, N * sizeof(int)); // Reset data
        
        start = omp_get_wtime();
        bubbleSortOptimized(tempData, N);
        end = omp_get_wtime();
        double timeOpt = end - start;

        // --- Benchmark 3: Parallel ---
        memcpy(tempData, masterData, N * sizeof(int)); // Reset data
        
        start = omp_get_wtime();
        bubbleSortParallel(tempData, N);
        end = omp_get_wtime();
        double timeParallel = end - start;

        // Write results to file
        fprintf(fp, "%d,%f,%f,%f\n", N, timeNormal, timeOpt, timeParallel);

        // Free memory for this iteration
        free(masterData);
        free(tempData);
    }

    fclose(fp);
    printf("Benchmarking complete. Data saved to 'benchmark_results.csv'.\n");

    return 0;
}