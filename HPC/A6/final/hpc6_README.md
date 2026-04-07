# Parallel Reduction (MAX) over an MPI Cluster

This repository demonstrates how to set up a passwordless SSH cluster across multiple Ubuntu nodes and execute a Message Passing Interface (MPI) program. The included C program uses `MPI_Reduce` to find the maximum value across distributed sets of random data.

## Environment Setup
This guide assumes you are operating with at least two Ubuntu machines on the same network:
* **Master Node:** `kalpak`
* **Worker Node:** `kalpak2`

---

## Part 1: Creating the Passwordless Cluster

To allow the master node to securely dispatch tasks to worker nodes without manual intervention, we must establish a passwordless SSH connection using cryptographic keys.

### 1. Install SSH Services
Ensure the SSH server is installed on both machines.
```bash
sudo apt update
sudo apt install openssh-server -y
```

### 2. Map Hostnames (Optional but Recommended)
To avoid typing IP addresses, map the nodes in your `hosts` file on both machines. *(Note: Use `nano` instead of a GUI editor like `gedit` to avoid dbus errors).*
```bash
sudo nano /etc/hosts
```
*Add the IPs and hostnames of your cluster nodes to this file.*

### 3. Generate SSH Keys
On the master node (`kalpak`), generate an Ed25519 key pair. Press `Enter` through all prompts to leave the passphrase empty.
```bash
ssh-keygen -t ed25519
```

### 4. Distribute the Public Key
Copy your newly generated public key to the worker node (`kalpak2`). You will be asked for `kalpak2`'s password one final time.
```bash
ssh-copy-id kalpak2
```

### 5. Verify the Connection
Test the connection. You should log directly into the worker node without a password prompt.
```bash
ssh kalpak2

#if that worked u can exit by this command
logout
```

---

## Part 2: MPI Implementation and Execution

With the cluster connected, we can now configure the MPI environment, compile our parallel reduction code, and deploy it.

### 1. Install OpenMPI Libraries
Run this command on **both** `kalpak` and `kalpak2` to install the MPI compiler and runtime.
```bash
sudo apt install openmpi-bin libopenmpi-dev -y
```

### 2. Create the Resource Configuration (Hostfile)
On the master node, create a text file that tells MPI how many processor slots are available on each machine.
```bash
nano resource_config.txt
```
**Contents of `resource_config.txt`:**
```text
kalpak slots=4
kalpak2 slots=2
```

### 3. Write the MPI Program
Create the C source file on the master node.
```bash
nano max_reduce.c
```
*Insert your MPI C code here. The code should initialize MPI, generate random data for each rank, find the local maximum, and then use `MPI_Reduce` with the `MPI_MAX` operator to find the global maximum at the root node (rank 0).*

### 4. Compile the Code
Use the MPI C compiler wrapper to build the executable.
```bash
mpicc max_reduce.c -o max_reduce
```

### 5. Distribute the Executable
Since MPI executes the same file across all nodes, `kalpak2` needs a copy of the compiled program in the exact same directory path.
```bash
scp max_reduce kalpak2:~/
```

### 6. Execute the Cluster Job
Run the parallel job across the cluster using the slots defined in your configuration file. We route standard errors to `/dev/null` to keep the terminal output clean from non-critical network warnings.
```bash
mpirun -np 6 --hostfile resource_config.txt ./max_reduce 2>/dev/null
```
```
