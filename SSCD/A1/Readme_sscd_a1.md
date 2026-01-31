# Two-Pass Assembler - Pass 1

A Java implementation of Pass 1 of a two-pass assembler that processes assembly language instructions and generates intermediate code.

## Features

- **OPTAB**: Opcode table with instruction set (IS), assembler directives (AD), and declarative statements (DL)
- **REGTAB**: Register table mapping register names to codes
- **SYMTAB**: Symbol table tracking labels with addresses and lengths
- **Intermediate Code**: Location counter, opcodes, and operands in structured format
- **Forward Reference Handling**: Resolves symbols used before declaration

## How to Run

### Compile
```bash
javac a1.java
```

### Execute
```bash
java a1
```

The program reads from `input.asm` and displays all tables and intermediate code.

## Input File Format

Each line in `input.asm` follows the pattern:
```
[LABEL] MNEMONIC OPERAND1 OPERAND2
```

- Use `-` as placeholder for missing operands
- **START**: `START <address> -`
- **Instructions**: `LABEL MNEMONIC REGISTER SYMBOL/CONSTANT`
- **DS/DC**: `LABEL DS/DC <value> -`
- **END**: `END - -`

### Example (input.asm)
```
START 100 -
L1 ADD AREG 10
MOVER BREG A
A DS 05 -
END - -
```

## Output

Displays four sections:
1. **OPTAB** - Opcode table
2. **REGTAB** - Register table  
3. **SYMTAB** - Symbol table with IDs, addresses, and lengths
4. **Intermediate Code** - LC, opcode, and operands in (type,value) format
