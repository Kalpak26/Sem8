# Vector Addition Benchmark

This project benchmarks three approaches to vector addition (`z = x + y`) to demonstrate the impact of **False Sharing** and **Memory Access Patterns** in parallel computing.

## The Approaches
1. **API Demo:** Before benchmarking, the program explicitly calls `omp_set_num_threads(4)` and uses `omp_get_thread_num()` to print thread IDs, verifying the parallel environment.
2. **Sequential:** Standard single-threaded loop.
3. **Parallel (Chunked):** The efficient OpenMP approach. The array is split into `N` large contiguous blocks. Each thread works on its own block, minimizing cache conflicts.
4. **Parallel (Interleaved):** The "False Sharing" approach. Threads access indices cyclically (0, 1, 2, 3...) causing them to fight over the same cache lines when writing to `z`.

## How to Run

### 1. Compile (C)
```bash
gcc -fopenmp vector_benchmark.c -o vector_benchmark
```

### 2. Run Benchmark
```bash
./vector_benchmark
```
3. Visualize
```bash
python plot_vector.py
```

---

### Expected Results
- Chunked should be the fastest (for very large N).
- Interleaved should be slower than Chunked (and sometimes slower than Sequential!) because the CPU cores spend time invalidating each other's cache lines rather than calculating.

### Quick Explanation on False Sharing 
When you run the **Interleaved** code:
1.  Thread 0 writes to `z[0]`.
2.  `z[0]` sits in a "Cache Line" (usually 64 bytes). `z[1]` through `z[7]` are likely in that **same** cache line.
3.  Thread 1 tries to write to `z[1]`.
4.  The CPU hardware sees that Thread 0 modified that cache line. It forces Thread 1 to wait while it invalidates the line and re-fetches it.
5.  This "Ping Pong" effect destroys performance.

The **Chunked** version avoids this because Thread 0 works on indices `0` to `1,000,000`. It owns those cache lines exclusively.
