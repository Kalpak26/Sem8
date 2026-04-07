# Merge Sort Profiling with gprof

This project demonstrates how to use `gprof` (the GNU Profiler) to analyze the performance and task distribution of a C program. It uses a recursive Merge Sort algorithm sorting an array of 10 million randomly generated integers to generate meaningful CPU execution time and profiling data.

## Prerequisites

This project is designed for Linux environments (specifically Ubuntu/Debian). You will need the standard C build tools installed. 

If you don't have them, install them via your terminal:
```bash
sudo apt update
sudo apt install build-essential
```

## How to Execute and Profile

Follow these steps to compile the code, run the sort, and generate the profiling analysis text file.

### 1. Compile with Profiling Enabled
To use `gprof`, you must tell the `gcc` compiler to include profiling information. We do this by adding the `-pg` flag.

```bash
gcc -pg mergesort.c -o mergesort
```

### 2. Run the Executable
Execute the compiled program normally. Because it was compiled with the `-pg` flag, running it will automatically generate a raw data file named `gmon.out` in the same directory.

```bash
./mergesort
```
*Note: You should see "Sorting 10000000 elements..." followed by "Done!" once the sorting is complete.*

### 3. Generate the Analysis Report
Now, use the `gprof` tool to read the raw `gmon.out` data and translate it into a human-readable text file called `analysis.txt`.

```bash
gprof mergesort gmon.out > analysis.txt
```

### 4. Read the Results
Open `analysis.txt` in any text editor. The file is split into two main sections:
* **The Flat Profile:** Shows how much total CPU time was spent in each function (e.g., `merge` vs. `mergeSort`).
* **The Call Graph:** Shows the task distribution, displaying which functions called which, how many times they were invoked, and the hierarchical flow of the program.

---

## Optional: Visualizing the Data

If you want to go beyond the text file and generate a visual flowchart of the call graph, you can use Python and Graphviz to turn the data into an image.

**Setup:**
```bash
sudo apt install graphviz python3-pip pipx
pipx install gprof2dot
```

**Generate the Image:**
Ensure you have already generated `gmon.out` from Step 2, then run:
```bash
gprof ./mergesort | gprof2dot | dot -Tpng -o profile_map.png
```
This will generate `profile_map.png`, providing a color-coded map showing exactly where your program spent the most time!
```
