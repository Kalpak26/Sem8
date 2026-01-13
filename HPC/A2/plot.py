import pandas as pd
import matplotlib.pyplot as plt

# Load data
df = pd.read_csv("results.csv")

# Average multiple runs
avg_df = df.groupby(["n", "method"])["time"].mean().reset_index()

# Separate methods
recursive = avg_df[avg_df["method"] == "recursive"]
iterative = avg_df[avg_df["method"] == "iterative"]

plt.figure(figsize=(10, 6))

plt.plot(recursive["n"], recursive["time"], marker="o", label="Recursive")
plt.plot(iterative["n"], iterative["time"], marker="s", label="Iterative")

plt.xscale("log")
plt.yscale("log")

plt.xlabel("Number of elements (n)")
plt.ylabel("Time (seconds)")
plt.title("Merge Sort Time Complexity (Sequential)")

plt.grid(True, which="both", linestyle="--", alpha=0.6)
plt.legend()

plt.tight_layout()
plt.savefig("mergesort_performance.png")
plt.show()
