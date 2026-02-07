import java.util.*;

public class MacroPass1 {

    // --- Data Structures ---
    static List<String> mdt = new ArrayList<>();
    static List<MNTEntry> mnt = new ArrayList<>();
    static List<String> ala = new ArrayList<>(); 
    // --- Helper Classes for Table Formatting ---
    static class MNTEntry {
        int index;
        String name;
        int mdtIndex;

        MNTEntry(int index, String name, int mdtIndex) {
            this.index = index;
            this.name = name;
            this.mdtIndex = mdtIndex;
        }
    }

    public static void main(String[] args) {
        // Hardcoded Input Format: {Label, Opcode, Op1, Op2}
        String[][] input = {
            {null, "MACRO", null, null},
            {null, "INCR", "&ARG1", null},
            {null, "ADD", "AREG", "&ARG1"},
            {null, "SUB", "BREG", "&ARG1"},
            {null, "MOVER", "AREG", "B"},
            {null, "MEND", null, null},
            {null, "START", "100", null},
            {null, "INCR", "DATA1", null},
            {null, "END", null, null}
        };

        pass1(input);
        printTables();
    }

    private static void pass1(String[][] source) {
        boolean isMacroDefinition = false;
        boolean isPrototype = false;

        for (String[] line : source) {
            String label = line[0];
            String opcode = line[1];
            String op1 = line[2];
            String op2 = line[3];

            // 1. Check for MACRO start
            if (opcode.equals("MACRO")) {
                isMacroDefinition = true;
                isPrototype = true; // Next line is the prototype
                ala.clear(); // Clear ALA for the new macro
                continue;
            }

            // 2. Process Macro Definition
            if (isMacroDefinition) {
                if (opcode.equals("MEND")) {
                    mdt.add("MEND");
                    isMacroDefinition = false;
                } 
                else if (isPrototype) {
                    // --- Handle Prototype Line ---
                    // Add to MNT
                    // MNT points to current MDT index
                    mnt.add(new MNTEntry(mnt.size() + 1, opcode, mdt.size()));

                    // Add Args to ALA
                    if (op1 != null && op1.startsWith("&")) ala.add(op1);
                    if (op2 != null && op2.startsWith("&")) ala.add(op2);

                    // Add Prototype to MDT (Store as is)
                    mdt.add(opcode + " " + formatOperands(op1, op2));
                    
                    isPrototype = false; // Next lines are body
                } 
                else {
                    // --- Handle Body Line (Substitution) ---
                    // Check if operands exist in ALA, replace with #Index
                    String subOp1 = substituteIndex(op1);
                    String subOp2 = substituteIndex(op2);

                    // Construct instruction string
                    String instruction = opcode + " " + formatOperands(subOp1, subOp2);
                    mdt.add(instruction);
                }
            }
            // 3. Ignore normal program lines for Pass 1 (START, END, Calls)
        }
    }

    // --- Helper: Replaces &ARG with #Index if found in ALA ---
    private static String substituteIndex(String arg) {
        if (arg == null) return null;
        
        // Find index of argument in ALA
        int index = ala.indexOf(arg);
        
        // If found, return #index (1-based index)
        if (index != -1) {
            return "#" + (index + 1);
        }
        
        // If not found (regular register/literal), return as is
        return arg;
    }

    // --- Helper: Formats Op1 and Op2 into a string ---
    private static String formatOperands(String op1, String op2) {
        if (op1 == null && op2 == null) return "";
        if (op1 != null && op2 == null) return op1;
        return op1 + ", " + op2;
    }

    private static void printTables() {
        System.out.println("OUTPUT TABLES (PASS 1)");
        System.out.println("----------------------");

        System.out.println("\nMDT (Macro Definition Table)");
        System.out.println("-----------------------------");
        System.out.printf("| %-10s | %-25s |\n", "MDT Index", "Instruction");
        System.out.println("-----------------------------");
        for (int i = 0; i < mdt.size(); i++) {
            System.out.printf("| %-10d | %-25s |\n", i, mdt.get(i));
        }
        System.out.println("-----------------------------");

        System.out.println("\nMNT (Macro Name Table)");
        System.out.println("----------------------------------------");
        System.out.printf("| %-10s | %-10s | %-10s |\n", "MNT Index", "Name", "MDT Index");
        System.out.println("----------------------------------------");
        for (MNTEntry entry : mnt) {
            System.out.printf("| %-10d | %-10s | %-10d |\n", entry.index, entry.name, entry.mdtIndex);
        }
        System.out.println("----------------------------------------");

        System.out.println("\nALA (Argument List Array)");
        System.out.println("-----------------------------");
        System.out.printf("| %-10s | %-15s |\n", "ALA Index", "Dummy Argument");
        System.out.println("-----------------------------");
        for (int i = 0; i < ala.size(); i++) {
            // Displaying 1-based index
            System.out.printf("| %-10d | %-15s |\n", (i + 1), ala.get(i));
        }
        System.out.println("-----------------------------");
    }
}
