import java.io.*;
import java.util.*;

public class a1 {

    // 1. Data Structures
    static HashMap<String, String> OPTAB = new HashMap<>();
    
    // We will store the file lines here to avoid reading the file twice
    static ArrayList<String> sourceCode = new ArrayList<>();

    public static void main(String[] args) {
        
        // Step 1: Initialize Tables
        initOPTAB();

        // Step 2: Read and Display the File
        System.out.println("----- 1. SOURCE CODE -----");
        readAndDisplayFile("input.asm");

        // Step 3: Tokenize and Display Formatted Output
        System.out.println("\n----- 2. FORMATTED TOKENS -----");
        System.out.printf("%-8s %-8s %-8s %-8s%n", "LABEL", "OPCODE", "OP1", "OP2");
        System.out.println("----------------------------------------");
        
        for (String line : sourceCode) {
            tokenizeAndDisplay(line);
        }

        // Step 4: Display Tables (Extensible for SYMTAB later)
        printTables();
    }

    // --- Helper Methods ---

    static void readAndDisplayFile(String fileName) {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Add to our list so we can use it later
                sourceCode.add(line);
                // Print immediately
                System.out.println(line);
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
    }

    static void tokenizeAndDisplay(String line) {
        // Skip empty lines
        if (line.trim().isEmpty()) return;

        String[] tokens = line.trim().split("\\s+");
        String label = "_", opcode = "_", op1 = "_", op2 = "_";
        int index = 0;

        // Logic: If 1st word is NOT in OPTAB, it is a Label
        if (!OPTAB.containsKey(tokens[0])) {
            label = tokens[0];
            index++;
        }

        // The next token is the Opcode
        if (index < tokens.length) {
            opcode = tokens[index];
            index++;
        }

        // The next is Operand 1
        if (index < tokens.length) {
            op1 = tokens[index];
            index++;
        }

        // The next is Operand 2
        if (index < tokens.length) {
            op2 = tokens[index];
        }

        // Display fixed-width columns
        System.out.printf("%-8s %-8s %-8s %-8s%n", label, opcode, op1, op2);
    }

    static void printTables() {
        System.out.println("\n----- 3. TABLES -----");
        
        // Display OPTAB
        System.out.println(">> OPTAB");
        System.out.println("MNEMONIC  CLASS   CODE");
        System.out.println("----------------------");
        for (String key : OPTAB.keySet()) {
            String val = OPTAB.get(key);
            String[] parts = val.split(","); // Split "IS,01" into "IS" and "01"
            System.out.printf("%-10s %-6s %-6s%n", key, parts[0], parts[1]);
        }

        // FUTURE: Add Logic to display SYMTAB here
        // System.out.println("\n>> SYMTAB");
        // ...
    }

    static void initOPTAB() {
        // Imperative Statements (IS)
        OPTAB.put("STOP",  "IS,00");
        OPTAB.put("ADD",   "IS,01");
        OPTAB.put("SUB",   "IS,02");
        OPTAB.put("MULT",  "IS,03");
        OPTAB.put("MOVER", "IS,04");
        OPTAB.put("MOVEM", "IS,05");
        OPTAB.put("PRINT", "IS,10");
        
        // Assembler Directives (AD)
        OPTAB.put("START", "AD,01");
        OPTAB.put("END",   "AD,02");
        
        // Declarative Statements (DL)
        OPTAB.put("DC",    "DL,01");
        OPTAB.put("DS",    "DL,02");
    }
}
