import java.io.*;
import java.util.*;

public class a1 {
    static int LC = 0;
    static Map<String, String> OPTAB = new HashMap<>();
    static Map<String, String> REGTAB = new HashMap<>();
    static Map<String, SymbolEntry> SYMTAB = new LinkedHashMap<>();
    static List<String> intermediateCode = new ArrayList<>();

    // Helper class for SYMTAB format
    static class SymbolEntry {
        int id, address, length;
        String name;
        SymbolEntry(int id, String name, int address, int length) {
            this.id = id; this.name = name; this.address = address; this.length = length;
        }
    }

    public static void main(String[] args) {
        setupTables();
        processPassOne("input.asm");
        displayOutput();
    }

    static void setupTables() {
        // Opcode Table
        OPTAB.put("STOP", "IS,00");  OPTAB.put("ADD", "IS,01");
        OPTAB.put("SUB", "IS,02");   OPTAB.put("MULT", "IS,03");
        OPTAB.put("MOVER", "IS,04"); OPTAB.put("MOVEM", "IS,05");
        OPTAB.put("START", "AD,01"); OPTAB.put("END", "AD,02");
        OPTAB.put("DC", "DL,01");    OPTAB.put("DS", "DL,02");

        // Register Table
        REGTAB.put("AREG", "1"); REGTAB.put("BREG", "2");
        REGTAB.put("CREG", "3"); REGTAB.put("DREG", "4");
    }

    static void processPassOne(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            int symId = 1;
            while ((line = br.readLine()) != null) {
                String[] tokens = line.trim().split("\\s+");
                if (tokens.length == 0 || tokens[0].isEmpty()) continue;

                int ptr = 0;
                String label = null;

                // Label Detection
                if (!OPTAB.containsKey(tokens[0])) {
                    label = tokens[0];
                    if (!SYMTAB.containsKey(label)) {
                        SYMTAB.put(label, new SymbolEntry(symId++, label, LC, 1));
                    } else {
                        SYMTAB.get(label).address = LC;
                    }
                    ptr++;
                }

                String mnemonic = (ptr < tokens.length) ? tokens[ptr++] : "";
                String op1 = (ptr < tokens.length) ? tokens[ptr++] : "";
                String op2 = (ptr < tokens.length) ? tokens[ptr++] : "";

                // Forward Reference Handling - add undefined symbols to SYMTAB
                if (!op2.isEmpty() && !op2.equals("-") && !REGTAB.containsKey(op2) && !op2.matches("\\d+")) {
                    if (!SYMTAB.containsKey(op2)) {
                        SYMTAB.put(op2, new SymbolEntry(symId++, op2, -1, 1)); // -1 = undefined
                    }
                }

                // Address Management
                if (mnemonic.equals("START")) {
                    LC = Integer.parseInt(op1);
                    generateIC(null, mnemonic, op1, "");
                } else {
                    generateIC(LC, mnemonic, op1, op2);
                    if (mnemonic.equals("DS")) {
                        LC += Integer.parseInt(op1);
                        if (label != null) SYMTAB.get(label).length = Integer.parseInt(op1);
                    } else {
                        LC += 1;
                    }
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    static void generateIC(Integer currentLC, String mnemonic, String op1, String op2) {
        String lc = (currentLC == null) ? "---" : String.valueOf(currentLC);
        String opcode = "(" + OPTAB.get(mnemonic) + ")";
        
        // Operand 1 (Register or Constant)
        String operand1;
        if (REGTAB.containsKey(op1)) operand1 = REGTAB.get(op1);
        else if (op1.equals("-")) operand1 = "-";
        else if (!op1.isEmpty()) operand1 = "(C," + op1 + ")";
        else operand1 = "";

        // Operand 2 (Symbol or Constant)
        String operand2;
        if (op2.equals("-")) {
            operand2 = "-";
        } else if (!op2.isEmpty()) {
            if (SYMTAB.containsKey(op2)) operand2 = "(S," + SYMTAB.get(op2).id + ")";
            else operand2 = "(C," + op2 + ")";
        } else {
            operand2 = "";
        }
        
        intermediateCode.add(String.format("%-5s | %-8s | %-8s | %-8s", lc, opcode, operand1, operand2));
    }

    static void displayOutput() {
        System.out.println("=== PASS 1 OUTPUT ===\n");

        System.out.println("--- 1. OPTAB ---");
        OPTAB.forEach((k, v) -> System.out.printf("%-8s | %-8s%n", k, v));

        System.out.println("\n--- 2. REGTAB ---");
        REGTAB.forEach((k, v) -> System.out.printf("%-8s | %-8s%n", k, v));

        System.out.println("\n--- 3. SYMTAB ---");
        System.out.printf("%-5s | %-8s | %-8s | %-8s%n", "ID", "NAME", "ADDRESS", "LENGTH");
        System.out.println("-----------------------------------");
        SYMTAB.values().forEach(s -> System.out.printf("%-5d | %-8s | %-8d | %-8d%n", s.id, s.name, s.address, s.length));

        System.out.println("\n--- 4. INTERMEDIATE CODE ---");
        System.out.printf("%-5s | %-8s | %-8s | %-8s%n", "LC", "OPCODE", "OP1", "OP2");
        System.out.println("-----------------------------------");
        intermediateCode.forEach(System.out::println);
    }
}