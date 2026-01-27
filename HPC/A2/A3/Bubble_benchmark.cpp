#include <iostream>
#include <vector>
#include <algorithm>
#include <chrono>
#include <fstream>
#include <random>
#include <omp.h> // Required for OpenMP

using namespace std;

// 1. Normal Bubble Sort
void bubbleSortNormal(vector<int> arr) {
    int n = arr.size();
    for (int i = 0; i < n - 1; i++) {
        for (int j = 0; j < n - i - 1; j++) {
            if (arr[j] > arr[j + 1]) {
                swap(arr[j], arr[j + 1]);
            }
        }
    }
}

// 2. Optimized Bubble Sort with "Stop" Flag
void bubbleSortOptimized(vector<int> arr) {
    int n = arr.size();
    bool stop;
    for (int i = 0; i < n - 1; i++) {
        stop = true; // Assume sorted
        for (int j = 0; j < n - i - 1; j++) {
            if (arr[j] > arr[j + 1]) {
                swap(arr[j], arr[j + 1]);
                stop = false; // Swap happened, not sorted yet
            }
        }
        if (stop) break; // Break if no swaps occurred
    }
}

// 3. Parallel Odd-Even Sort (OpenMP)
void bubbleSortParallel(vector<int> arr) {
    int n = arr.size();
    for (int i = 0; i < n; i++) {
        int first = i % 2;
        
        // Parallelizing the inner loop
        #pragma omp parallel for default(none) shared(arr, n, first)
        for (int j = first; j < n - 1; j += 2) {
            if (arr[j] > arr[j + 1]) {
                swap(arr[j], arr[j + 1]);
            }
        }
    }
}

// Helper to generate random data
vector<int> generateRandomData(int size) {
    vector<int> data(size);
    random_device rd;
    mt19937 gen(rd());
    uniform_int_distribution<> dis(1, 10000);
    for (int i = 0; i < size; i++) data[i] = dis(gen);
    return data;
}

int main() {
    // Define input sizes for benchmarking
    vector<int> sizes = {1000, 2500, 5000, 7500, 10000, 12500, 15000};
    
    ofstream file("benchmark_results.csv");
    file << "Size,Normal,Optimized,Parallel\n";

    cout << "Starting Benchmark..." << endl;

    for (int n : sizes) {
        cout << "Testing size: " << n << "..." << endl;
        vector<int> baseData = generateRandomData(n);

        // Measure Normal
        auto start = chrono::high_resolution_clock::now();
        bubbleSortNormal(baseData);
        auto end = chrono::high_resolution_clock::now();
        double timeNormal = chrono::duration<double>(end - start).count();

        // Measure Optimized
        start = chrono::high_resolution_clock::now();
        bubbleSortOptimized(baseData);
        end = chrono::high_resolution_clock::now();
        double timeOpt = chrono::duration<double>(end - start).count();

        // Measure Parallel
        start = chrono::high_resolution_clock::now();
        bubbleSortParallel(baseData);
        end = chrono::high_resolution_clock::now();
        double timeParallel = chrono::duration<double>(end - start).count();

        // Write to CSV
        file << n << "," << timeNormal << "," << timeOpt << "," << timeParallel << "\n";
    }

    file.close();
    cout << "Benchmarking complete. Data saved to 'benchmark_results.csv'." << endl;
    return 0;
}