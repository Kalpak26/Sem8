# Macro Assembler – Two-Pass Implementation (Java)

> **Academic Assignment Demo** — A simplified, single-file Java program that simulates how a macro assembler processes macro definitions and expands macro calls using two passes.

---

## What Is a Macro Assembler?

In assembly language programming, a **macro** is a named block of instructions you define once and reuse many times — similar to a function, but expanded *inline* at the point of use (not called at runtime).

A **Macro Assembler** handles two jobs in one:
1. **Recognizing and storing** macro definitions during Pass 1
2. **Expanding macro calls** with actual arguments during Pass 2

### Real-World vs. This Demo

| Aspect | Real Assembler | This Demo |
|---|---|---|
| Input | Actual `.asm` source files | Hardcoded `String[][]` array |
| Argument handling | Named/positional, default values, complex scoping | Simple positional (`&ARG1`, `&ARG2`) |
| Nested macros | Supported | Not supported |
| Output | Object/machine code | Pretty-printed expanded source |
| Symbol Table | Full (labels, addresses, etc.) | Not included |
| Error handling | Comprehensive | Minimal |

This program is a **clear, minimal demonstration** of the core concept — ideal for understanding the logic before studying production assemblers.

---

## The Three Key Data Structures

### 1. MNT — Macro Name Table
Stores an entry for every macro that is defined.

| Field | Description |
|---|---|
| `name` | The macro's name (e.g., `INCR`) |
| `mdtIndex` | Index into MDT where this macro's definition starts |
| `argCount` | Number of dummy arguments |

### 2. MDT — Macro Definition Table
A flat list of instructions belonging to all defined macros. Dummy arguments (`&ARG1`) are replaced with positional placeholders (`#1`, `#2`) during Pass 1.

```
Index 0 → INCR &ARG1, &ARG2    ← Prototype line
Index 1 → ADD AREG, #1          ← Body (substituted)
Index 2 → SUB BREG, #2
Index 3 → MEND
```

### 3. ALA — Argument List Array
Maps positional placeholders to actual arguments at expansion time.

| Index | Dummy Arg | Actual Arg |
|---|---|---|
| #1 | &ARG1 | DATA1 |
| #2 | &ARG2 | DATA2 |

---

## How the Two Passes Work

### Pass 1 — Build the Tables

```
For each line in source:
  If MACRO keyword → enter macro-definition mode
    Next line = prototype → extract macro name + dummy args → write to MNT
    Subsequent lines → substitute &ARGx with #index → write to MDT
  If MEND → write MEND to MDT, exit macro-definition mode
  Otherwise → skip (non-macro lines are ignored in Pass 1)
```

### Pass 2 — Expand Macro Calls

```
For each line in source:
  If inside a MACRO definition → skip it (already processed)
  If opcode exists in MNT → it's a macro call:
      Populate ALA with actual arguments from the call
      Start reading MDT from (MNTEntry.mdtIndex + 1)   ← skip prototype
      For each MDT line until MEND:
          Replace #1, #2 … with ALA actual values
          Print expanded instruction
  Else → print line as-is
```

---

## Input / Output Example

### Input Source
```
MACRO
INCR  &ARG1, &ARG2
ADD   AREG, &ARG1
SUB   BREG, &ARG2
MEND

START  100
READ   N
MOVER  BREG, ='1'
INCR   DATA1, DATA2     ← macro call
STOP
END
```

### Expanded Output (Pass 2)
```
START 100
READ N
MOVER BREG, ='1'
.INCR EXPANSION START
ADD AREG, DATA1
SUB BREG, DATA2
STOP
END
```

---

## How to Run

**Requirements:** Java 8 or above

```bash
# Compile
javac MacroAssembler.java

# Run
java MacroAssembler
```

No external libraries. No build tools needed.

---

## File Structure

```
MacroAssembler.java
│
├── main()          → Defines input, triggers Pass 1 then Pass 2
├── pass1()         → Builds MNT, MDT, ALA
├── pass2()         → Expands macros using built tables
├── expandMacro()   → Core expansion logic
├── substituteIndex()      → &ARG → #N  (Pass 1 helper)
├── substituteActualArgs() → #N → actual (Pass 2 helper)
└── printTables()   → Displays MNT, MDT, ALA in formatted output
```

---

## Key Concepts to Remember

- **Prototype line** is stored in MDT but *skipped* during expansion (MDT pointer starts at `mdtIndex + 1`)
- **Dummy arguments** (`&ARG1`) are resolved to index placeholders (`#1`) in Pass 1 so MDT is argument-agnostic
- **ALA is rebuilt** at every macro call with the actual arguments from that specific call
- **MEND** acts as the terminator — Pass 2 stops expanding when it hits `MEND` in the MDT

---

## Limitations of This Demo

- Only one macro can be defined (ALA is global and gets cleared per definition)
- No support for nested macro calls
- No label handling inside macro bodies
- Actual arguments are positional only — no keyword arguments
- No error detection (undefined macros, wrong arg counts, etc.)

These are intentional omissions to keep the logic readable for assignment purposes.

---

*For a production-grade macro assembler, refer to system software textbooks such as "System Software" by Leland Beck or "Systems Programming" by Donovan.*
