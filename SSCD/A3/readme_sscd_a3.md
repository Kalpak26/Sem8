# MacroPass1 — Java Macro Processor (Pass 1)

## Overview

`MacroPass1` is a simple Java implementation of **Pass 1 of a macro processor**, typically studied in system programming or compiler design.
It scans source code, identifies macro definitions, and builds the core tables required for macro expansion in later stages.

This program demonstrates how macro definitions are parsed and stored using structured tables rather than expanded immediately.

---

## What the Program Does

During Pass 1, the program:

* Detects macro definitions (`MACRO` … `MEND`)
* Extracts the **macro prototype**
* Builds and displays three key tables:

### 1️⃣ Macro Definition Table (MDT)

Stores the processed macro body with arguments replaced by positional references (`#1`, `#2`, …).

### 2️⃣ Macro Name Table (MNT)

Stores metadata about each macro:

* Serial index
* Macro name
* Pointer to its definition in MDT

Implemented using a `LinkedHashMap` for fast lookup and ordered iteration.

### 3️⃣ Argument List Array (ALA)

Tracks dummy arguments from the macro prototype and assigns them positional indices used in substitution.

---

## Approach / Design

The program uses a single-pass scan of structured input instructions:

1. **State Flags**

   * `isMacroDefinition` → identifies macro scope
   * `isPrototype` → detects first line after `MACRO`

2. **Prototype Handling**

   * Creates `MNTEntry` with macro metadata
   * Stores dummy parameters in ALA
   * Writes prototype into MDT

3. **Body Processing**

   * Replaces parameters with positional indices using ALA lookup
   * Appends transformed instructions to MDT

4. **Termination**

   * On encountering `MEND`, macro mode exits and MDT records closure

This design prioritizes:

* Fast lookup (Map-based MNT)
* Clear table separation
* Readable substitution logic

---

## Requirements

* Java JDK **8 or later**
* Command line or IDE (IntelliJ, Eclipse, VS Code, etc.)

---

## How to Compile and Run

### Using Terminal

```bash
javac MacroPass1.java
java MacroPass1
```

### Using an IDE

1. Create a Java project
2. Add `MacroPass1.java`
3. Run the `main()` method

---

## Sample Output

The program prints formatted tables:

* MDT entries with index and instructions
* MNT entries showing macro metadata
* ALA entries showing argument mapping

(This verifies correct macro parsing and substitution.)

---

## Notes / Limitations

* Input is currently hardcoded for demonstration
* Supports positional argument substitution only
* Does not perform Pass 2 (macro expansion)
* Not designed for nested or complex macros

---
