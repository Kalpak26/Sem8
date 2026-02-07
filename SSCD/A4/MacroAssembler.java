import java.util.*;

public class MacroAssembler {

    // --- Data Structures ---
    static List<String> mdt = new ArrayList<>();
    static Map<String, MNTEntry> mnt = new LinkedHashMap<>();
    static List<ALAEntry> ala = new ArrayList<>(); 

    // --- Helper Classes ---
    static class MNTEntry {
        String name;
        int mdtIndex; // Pointer to MDT index
        int argCount; // Number of arguments (for safety)

        MNTEntry(String name, int mdtIndex, int argCount) {
            this.name = name;
            this.mdtIndex = mdtIndex;
            this.argCount = argCount;
        }
    }

    static class ALAEntry {
        int index;      // #1, #2...
        String dummy;   // &ARG1
        String actual;  // DATA1 (Filled in Pass 2)

        ALAEntry(int index, String dummy) {
            this.index = index;
            this.dummy = dummy;
            this.actual = "-"; // Default before Pass 2
        }
    }

    public static void main(String[] args) {
        // Input Source Code
        String[][] input = {
            // Macro Definition
            {null, "MACRO", null, null},
            {null, "INCR", "&ARG1", "&ARG2"}, // Prototype
            {null, "ADD", "AREG", "&ARG1"},
            {null, "SUB", "BREG", "&ARG2"},
            {null, "MEND", null, null},
            
            // Main Program
            {null, "START", "100", null},
            {null, "READ", "N", null},
            {null, "MOVER", "BREG", "='1'"},
            {null, "INCR", "DATA1", "DATA2"}, // Macro Call
            {null, "STOP", null, null},
            {null, "END", null, null}
        };

        System.out.println("--- PASS 1: PROCESSING DEFINITIONS ---");
        pass1(input);
        printTables(true); // Print tables after Pass 1

        System.out.println("\n--- PASS 2: EXPANDING MACROS ---");
        pass2(input);
        
        System.out.println("\n--- FINAL STATE OF TABLES ---");
        printTables(false); // Print tables again to see Actual Arguments
    }

    // ================= PASS 1 =================
    private static void pass1(String[][] source) {
        boolean isMacroDefinition = false;
        boolean isPrototype = false;

        for (String[] line : source) {
            String opcode = line[1];
            String op1 = line[2];
            String op2 = line[3];

            if ("MACRO".equals(opcode)) {
                isMacroDefinition = true;
                isPrototype = true;
                ala.clear(); // Reset ALA for new macro definition
                continue;
            }

            if (isMacroDefinition) {
                if ("MEND".equals(opcode)) {
                    mdt.add("MEND");
                    isMacroDefinition = false;
                } else if (isPrototype) {
                    // 1. Process Prototype
                    // Count args to store in MNT
                    int argCount = 0;
                    if (op1 != null) { ala.add(new ALAEntry(ala.size(), op1)); argCount++; }
                    if (op2 != null) { ala.add(new ALAEntry(ala.size(), op2)); argCount++; }

                    // Add to MNT
                    mnt.put(opcode, new MNTEntry(opcode, mdt.size(), argCount));

                    // Add to MDT (Store as string)
                    mdt.add(opcode + " " + formatOperands(op1, op2));
                    isPrototype = false;
                } else {
                    // 2. Process Body - Substitute &ARG with #Index
                    String subOp1 = substituteIndex(op1);
                    String subOp2 = substituteIndex(op2);
                    mdt.add(opcode + " " + formatOperands(subOp1, subOp2));
                }
            }
        }
    }

    // ================= PASS 2 =================
    private static void pass2(String[][] source) {
        boolean isMacroDefinition = false;

        System.out.println("EXPANDED SOURCE CODE:");
        System.out.println("---------------------");

        for (String[] line : source) {
            String opcode = line[1];
            String op1 = line[2];
            String op2 = line[3];

            // Ignore MACRO definitions in Pass 2
            if ("MACRO".equals(opcode)) {
                isMacroDefinition = true;
                continue;
            }
            if (isMacroDefinition) {
                if ("MEND".equals(opcode)) isMacroDefinition = false;
                continue; 
            }

            // Check if this line is a Macro Call
            if (mnt.containsKey(opcode)) {
                expandMacro(opcode, op1, op2);
            } else {
                // Not a macro? Print standard line
                System.out.println(opcode + " " + formatOperands(op1, op2));
            }
        }
    }

    // --- Expansion Logic ---
    private static void expandMacro(String name, String actualArg1, String actualArg2) {
        MNTEntry entry = mnt.get(name);
        
        // 1. Update ALA with Actual Arguments
        // In a real assembler, we would map specific ALA entries to this specific call.
        // Here, we update the global ALA for demonstration.
        if (ala.size() > 0 && actualArg1 != null) ala.get(0).actual = actualArg1;
        if (ala.size() > 1 && actualArg2 != null) ala.get(1).actual = actualArg2;

        System.out.println("." + name + " EXPANSION START"); // Comment marker

        // 2. Set MDT Pointer (Skip the prototype line)
        int pointer = entry.mdtIndex + 1;

        // 3. Loop until MEND
        while (pointer < mdt.size()) {
            String line = mdt.get(pointer);
            if ("MEND".equals(line)) break;

            // 4. Substitute #Index with Actual Value from ALA
            String expandedLine = substituteActualArgs(line);
            System.out.println(expandedLine);
            pointer++;
        }
    }

    // ================= HELPERS =================

    // Pass 1: Replace &ARG1 with #1
    private static String substituteIndex(String arg) {
        if (arg == null) return null;
        for (int i = 0; i < ala.size(); i++) {
            if (ala.get(i).dummy.equals(arg)) {
                return "#" + (i + 1);
            }
        }
        return arg;
    }

    // Pass 2: Replace #1 with DATA1
    private static String substituteActualArgs(String line) {
        String result = line;
        
        // We look for patterns like #1, #2 and replace them with ALA.actual
        for (ALAEntry entry : ala) {
            String placeholder = "#" + (entry.index + 1); // e.g., #1
            
            if (result.contains(placeholder)) {
                result = result.replace(placeholder, entry.actual);
            }
        }
        return result;
    }

    private static String formatOperands(String op1, String op2) {
        if (op1 == null && op2 == null) return "";
        if (op1 != null && op2 == null) return op1;
        return op1 + ", " + op2;
    }

    private static void printTables(boolean pass1Only) {
        System.out.println("\n--- TABLES ---");
        
        // MNT
        System.out.println("MNT (Macro Name Table)");
        System.out.println("----------------------------------------");
        System.out.printf("| %-10s | %-10s | %-10s |\n", "Name", "MDT Index", "Arg Count");
        System.out.println("----------------------------------------");
        for (MNTEntry e : mnt.values()) {
            System.out.printf("| %-10s | %-10d | %-10d |\n", e.name, e.mdtIndex, e.argCount);
        }

        // MDT
        System.out.println("\nMDT (Macro Definition Table)");
        System.out.println("-----------------------------");
        System.out.printf("| %-10s | %-25s |\n", "Index", "Instruction");
        System.out.println("-----------------------------");
        for (int i = 0; i < mdt.size(); i++) {
            System.out.printf("| %-10d | %-25s |\n", i, mdt.get(i));
        }

        // ALA
        System.out.println("\nALA (Argument List Array)");
        System.out.println("-------------------------------------------");
        System.out.printf("| %-10s | %-15s | %-15s |\n", "Index", "Dummy Arg", "Actual Arg");
        System.out.println("-------------------------------------------");
        for (int i = 0; i < ala.size(); i++) {
            ALAEntry e = ala.get(i);
            System.out.printf("| #%-9d | %-15s | %-15s |\n", (i+1), e.dummy, e.actual);
        }
        System.out.println("-------------------------------------------");
    }
}
