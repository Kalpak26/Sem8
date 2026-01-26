# OpenMP Merge Sort Benchmark

This project benchmarks the performance of **Merge Sort** by comparing four different execution scenarios. It measures the time taken to sort arrays of increasing sizes (up to 1 million elements) to demonstrate the speedup achieved by parallel computing.

## What It Tests
The benchmark runs 3 trials for every array size in these four modes:
1.  **Recursive Serial:** Standard merge sort (1 thread).
2.  **Recursive Parallel:** Uses OpenMP Tasks to divide work across all cores.
3.  **Iterative Serial:** Bottom-up merge sort (1 thread).
4.  **Iterative Parallel:** Uses OpenMP Parallel For loops to merge chunks simultaneously.

## Prerequisites
* **GCC Compiler:** Must support OpenMP (usually included by default).
* **Python 3:** For plotting results.
* **Python Libraries:** `pandas`, `matplotlib`.

## How to Run

### 1. Compile the C Program
Use `gcc` to compile the benchmark code.
```bash
gcc merge_benchmark.c -fopenmp -O2 -o benchmark

```

**Why use these flags?**

* `-fopenmp`: Enables the parallel processing directives in the code.
* `-O2`: Turns on "Level 2" compiler optimizations. We use this to ensure the Serial version runs efficiently. This guarantees that any speedup we see is due to **parallelism**, not just because the serial code was unoptimized or slow.

### 2. Run the Benchmark

Execute the compiled binary. This will generate a data file named `results.csv`.

```bash
./benchmark

```

*Note: This may take a few moments as it runs multiple sorting tests.*

### 3. Generate the Plot

Run the Python script to visualize the performance data from the CSV file.

```bash
python plot.py

```

This generates an image named **`mergesort_performance.png`**, showing a graph of Time vs. Array Size.

## 📂 File Structure

* `merge_benchmark.c`: The main C program containing the sorting algorithms and OpenMP logic.
* `plot.py`: Python script to read `results.csv` and generate the performance graph.
* `results.csv`: Raw timing data generated after running the benchmark.

```

```
