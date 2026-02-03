import pandas as pd
import matplotlib.pyplot as plt

# Load Data
try:
    df = pd.read_csv('vector_results.csv')
except FileNotFoundError:
    print("Error: vector_results.csv not found. Run the C program first.")
    exit()

# Get Thread Count from the first row of the data
thread_count = df['Threads'].iloc[0]

# Setup Plot
plt.figure(figsize=(10, 6))

# Plot Lines
plt.plot(df['Size'], df['Sequential'], marker='o', label='Sequential', linewidth=2)
plt.plot(df['Size'], df['Parallel_Chunked'], marker='s', label='Parallel (Chunked/Good)', linewidth=2)
plt.plot(df['Size'], df['Parallel_Interleaved'], marker='^', linestyle='--', color='red', label='Parallel (Interleaved/False Sharing)')

# Styling with Dynamic Title
plt.title(f'Vector Addition Performance ({thread_count} Threads)', fontsize=14, fontweight='bold')
plt.xlabel('Vector Size (Elements)', fontsize=12)
plt.ylabel('Time (Seconds)', fontsize=12)
plt.legend()
plt.grid(True, linestyle='--', alpha=0.7)

# Format X-axis labels to be more readable (e.g., 10M instead of 10000000)
plt.xticks(df['Size'], labels=[f'{x/1000000:.0f}M' for x in df['Size']])

# Save
plt.tight_layout()
plt.savefig('vector_performance.png')
print(f"Plot saved as vector_performance.png (Threads detected: {thread_count})")
plt.show()