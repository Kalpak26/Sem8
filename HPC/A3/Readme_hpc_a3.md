# Bubble Sort Benchmark

A performance comparison of three Bubble Sort implementations in C, analyzing the impact of optimization flags and OpenMP parallelization.

## Algorithms Tested
1. **Normal:** Standard $O(N^2)$ Bubble Sort.
2. **Optimized (Flag):** Uses a "stop" flag to exit the loop early if the array becomes sorted.
3. **Parallel (Odd-Even):** Implements Odd-Even Transposition Sort using OpenMP for parallel processing.

## Requirements
* **GCC Compiler:** Must support OpenMP (e.g., `-fopenmp`).
* **Python 3:** Required libraries: `pandas`, `matplotlib`.

## How to Run

### 1. Compile (C)
You must link the OpenMP library.
```bash
gcc bubble_benchmark.c -fopenmp -o bubble_benchmark
# OR to run it at more optimized level
gcc bubble_benchmark.c -fopenmp -O2 -o bubble_benchmark
```

### 2. Benchmark (C)

Runs the sorts on increasing array sizes and exports data to benchmark_results.csv.
```bash
./bubble_benchmark
```

### 3. Visualize (Python)

Reads the CSV and generates sort_performance_analysis.png.
```bash
python plot.py
```
