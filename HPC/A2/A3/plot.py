import pandas as pd
import matplotlib.pyplot as plt

# 1. Load Data
try:
    df = pd.read_csv('benchmark_results.csv')
except FileNotFoundError:
    print("Error: 'benchmark_results.csv' not found. Run the C++ program first.")
    exit()

# 2. Setup Plot
plt.figure(figsize=(10, 6))

# 3. Plot Lines
plt.plot(df['Size'], df['Normal'], marker='o', label='Normal Bubble Sort', linestyle='--')
plt.plot(df['Size'], df['Optimized'], marker='s', label='Optimized (Flag) Sort')
plt.plot(df['Size'], df['Parallel'], marker='^', label='Parallel Odd-Even Sort', linewidth=2)

# 4. Styling
plt.title('Bubble Sort Performance Benchmark', fontsize=14)
plt.xlabel('Array Size (N)', fontsize=12)
plt.ylabel('Time (Seconds)', fontsize=12)
plt.grid(True, linestyle='--', alpha=0.7)
plt.legend()

# 5. Show/Save
plt.tight_layout()
plt.savefig('sort_performance_analysis.png') # Saves the image
plt.show()