import pandas as pd
import matplotlib.pyplot as plt

# Load data
try:
    df = pd.read_csv("results.csv")
except FileNotFoundError:
    print("Error: 'results.csv' not found. Run the benchmark C program first.")
    exit()

# Average the runs (groupby n, method, AND threads)
avg_df = df.groupby(["n", "method", "threads"])["time"].mean().reset_index()

# Separate the four distinct testing scenarios
# 1. Recursive Serial (threads == 1)
rec_serial = avg_df[(avg_df["method"] == "recursive") & (avg_df["threads"] == 1)]

# 2. Recursive Parallel (threads > 1)
rec_parallel = avg_df[(avg_df["method"] == "recursive") & (avg_df["threads"] > 1)]

# 3. Iterative Serial (threads == 1)
iter_serial = avg_df[(avg_df["method"] == "iterative") & (avg_df["threads"] == 1)]

# 4. Iterative Parallel (threads > 1)
iter_parallel = avg_df[(avg_df["method"] == "iterative") & (avg_df["threads"] > 1)]

# Get the max thread count for labeling (assuming it's consistent in the CSV)
max_threads = df["threads"].max()

# --- Plotting ---
plt.figure(figsize=(10, 6))

# Plot Serial lines (Solid)
plt.plot(rec_serial["n"], rec_serial["time"], 
         marker="o", linestyle="-", color="blue", label="Recursive Serial (1 Thread)")

plt.plot(iter_serial["n"], iter_serial["time"], 
         marker="s", linestyle="-", color="red", label="Iterative Serial (1 Thread)")

# Plot Parallel lines (Dashed)
plt.plot(rec_parallel["n"], rec_parallel["time"], 
         marker="o", linestyle="--", color="cyan", label=f"Recursive Parallel ({max_threads} Threads)")

plt.plot(iter_parallel["n"], iter_parallel["time"], 
         marker="s", linestyle="--", color="orange", label=f"Iterative Parallel ({max_threads} Threads)")

# Formatting
plt.xscale("log")
plt.yscale("log")

plt.xlabel("Number of elements (n)", fontsize=12)
plt.ylabel("Time (seconds)", fontsize=12)
plt.title("Merge Sort Performance: Serial vs Parallel", fontsize=14, fontweight='bold')

plt.grid(True, which="both", linestyle="--", alpha=0.6)
plt.legend()

plt.tight_layout()
plt.savefig("mergesort_performance2.png")
print(f"Plot saved as 'mergesort_performance2.png'. Max threads detected: {max_threads}")
plt.show()
