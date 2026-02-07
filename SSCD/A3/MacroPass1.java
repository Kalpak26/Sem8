import java.util.*;

public class MacroPass1 {

    // --- Data Structures ---
    static List<String> mdt = new ArrayList<>();
    
    // CORRECTION: Map the Name to the MNTEntry Object.
    // This gives us O(1) lookup speed BUT keeps the detailed table data (Index, pointers).
    static Map<String, MNTEntry> mnt = new LinkedHashMap<>();
    
    static List<String> ala = new ArrayList<>(); 

    // We bring back the Helper Class because a Table usually holds multiple properties.
    static class MNTEntry {
        int index;      // The Macro's serial number (1, 2, 3...)
        String name;    // The Macro Name
        int mdtIndex;   // Pointer to MDT

        MNTEntry(int index, String name, int mdtIndex) {
            this.index = index;
            this.name = name;
            this.mdtIndex = mdtIndex;
        }
    }

    public static void main(String[] args) {
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
        int mntCounter = 1; // We need to manually track the Index now

        for (String[] line : source) {
            String opcode = line[1];
            String op1 = line[2];
            String op2 = line[3];

            if ("MACRO".equals(opcode)) {
                isMacroDefinition = true;
                isPrototype = true;
                ala.clear(); 
                continue;
            }

            if (isMacroDefinition) {
                if ("MEND".equals(opcode)) {
                    mdt.add("MEND");
                    isMacroDefinition = false;
                } 
                else if (isPrototype) {
                    // --- REFACTORED LOGIC ---
                    // 1. Create the Entry Object with all details (Index, Name, Pointer)
                    MNTEntry entry = new MNTEntry(mntCounter++, opcode, mdt.size());
                    
                    // 2. Store it in Map for O(1) Access by Name
                    mnt.put(opcode, entry);

                    // Add Args to ALA
                    if (op1 != null && op1.startsWith("&")) ala.add(op1);
                    if (op2 != null && op2.startsWith("&")) ala.add(op2);

                    // Add Prototype to MDT
                    mdt.add(opcode + " " + formatOperands(op1, op2));
                    isPrototype = false;
                } 
                else {
                    // Body Substitution
                    String subOp1 = substituteIndex(op1);
                    String subOp2 = substituteIndex(op2);
                    mdt.add(opcode + " " + formatOperands(subOp1, subOp2));
                }
            }
        }
    }

    private static String substituteIndex(String arg) {
        if (arg == null) return null;
        int index = ala.indexOf(arg);
        return (index != -1) ? "#" + (index + 1) : arg;
    }

    private static String formatOperands(String op1, String op2) {
        if (op1 == null && op2 == null) return "";
        if (op1 != null && op2 == null) return op1;
        return op1 + ", " + op2;
    }

    private static void printTables() {
        System.out.println("=== OUTPUT TABLES (PASS 1 - ROBUST) ===");

        System.out.println("\nMDT (Macro Definition Table)");
        System.out.println("-----------------------------");
        System.out.printf("| %-10s | %-25s |\n", "Index", "Instruction");
        System.out.println("-----------------------------");
        for (int i = 0; i < mdt.size(); i++) {
            System.out.printf("| %-10d | %-25s |\n", i, mdt.get(i));
        }

        System.out.println("\nMNT (Macro Name Table) - Map<String, MNTEntry>");
        System.out.println("----------------------------------------");
        System.out.printf("| %-10s | %-10s | %-10s |\n", "MNT Index", "Name", "MDT Index");
        System.out.println("----------------------------------------");
        
        // Iterating over the values of the Map gives us the Entry objects
        for (MNTEntry entry : mnt.values()) {
            System.out.printf("| %-10d | %-10s | %-10d |\n", entry.index, entry.name, entry.mdtIndex);
        }
        System.out.println("----------------------------------------");

        System.out.println("\nALA (Argument List Array)");
        System.out.println("-----------------------------");
        System.out.printf("| %-10s | %-15s |\n", "Index", "Dummy Argument");
        System.out.println("-----------------------------");
        for (int i = 0; i < ala.size(); i++) {
            System.out.printf("| %-10d | %-15s |\n", (i + 1), ala.get(i));
        }
        System.out.println("-----------------------------");
    }
}
