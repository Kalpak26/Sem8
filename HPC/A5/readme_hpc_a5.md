# HPC Lab — Assignment 5: Passwordless Cluster Setup

> **Goal:** Connect two machines (`node1` = you, `node2` = neighbour) into a passwordless SSH cluster.
> **Rule:** Username must be the same on both machines.

---

## Setup Steps

### 1. Set Hostname
```bash
sudo hostname node1   # your machine
sudo hostname node2   # neighbour's machine
```

---

### 2. Get IP Address
```bash
ifconfig
# If not found:
sudo apt-get install net-tools
```

---

### 3. Map IP → Hostname (`/etc/hosts`)
```bash
sudo gedit /etc/hosts
```
Add these entries on **both** machines:
```
127.0.0.1        localhost
127.0.1.1        node1
172.16.181.120   node1    # your IP
172.16.181.130   node2    # neighbour's IP
```

---

### 4. Install OpenSSH Server
```bash
sudo apt-get install openssh-server
```

---

### 5. Generate SSH Key Pair
```bash
ssh-keygen
# Press Enter for all prompts. Enter Y where asked.
```

---

### 6. Copy Public Key to Other Node
```bash
ssh-copy-id node2   # you run this
ssh-copy-id node1   # neighbour runs this
```

---

### 7. SSH into Other Node
```bash
ssh node2   # you access neighbour
ssh node1   # neighbour accesses you
```
> First login will ask for a password. All subsequent logins are **passwordless**.

---

## Troubleshooting: SSH Service Down

If `ssh-copy-id` fails, the SSH service may be inactive.

**Check status:**
```bash
sudo systemctl status ssh
# or
sudo systemctl status sshd
```

**Fix & restart:**
```bash
sudo apt update
sudo apt upgrade openssh-server
sudo systemctl start ssh
```

---

## Quick Reference

| Step | Command |
|------|---------|
| Set hostname | `sudo hostname node1` |
| Get IP | `ifconfig` |
| Edit hosts file | `sudo gedit /etc/hosts` |
| Install SSH | `sudo apt-get install openssh-server` |
| Generate keys | `ssh-keygen` |
| Share key | `ssh-copy-id node2` |
| Connect to node | `ssh node2` |
| Check SSH status | `sudo systemctl status ssh` |
| Start SSH service | `sudo systemctl start ssh` |
