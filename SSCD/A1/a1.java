import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

class Opcode {
    String type;   // IS, AD, DL
    int code;

    Opcode(String type, int code) {
        this.type = type;
        this.code = code;
    }
}

public class a1 {

    static HashMap<String, Opcode> OPTAB = new HashMap<>();

    public static void main(String[] args) {

        // Load OPTAB
        loadOPTAB();

        // Read & Display Source File
        String fileName = "input.asm";

        try {
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            String line;

            System.out.println("----- Source Program -----");
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }

            br.close();
            br = new BufferedReader(new FileReader(fileName));
            
            System.out.println("LABEL  OPCODE OP1    OP2");
            while ((line = br.readLine()) != null) {
                tokenizeAndDisplay(line);
            }            

            br.close();
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }

        // Display OPTAB
        System.out.println("\n----- OPTAB -----");
        for (String key : OPTAB.keySet()) {
            Opcode op = OPTAB.get(key);
            System.out.println(key + " -> (" + op.type + ", " + op.code + ")");
        }
        
    }

    static void loadOPTAB() {
        OPTAB.put("ADD",   new Opcode("IS", 1));
        OPTAB.put("SUB",   new Opcode("IS", 2));
        OPTAB.put("MOVER", new Opcode("IS", 3));
        OPTAB.put("MOVEM", new Opcode("IS", 4));
        OPTAB.put("PRINT", new Opcode("IS", 5));
        OPTAB.put("READ",  new Opcode("IS", 6));
        OPTAB.put("START",  new Opcode("AD", 1));
        OPTAB.put("END",    new Opcode("AD", 2));
        OPTAB.put("DC",   new Opcode("DL", 1));
        OPTAB.put("DS",  new Opcode("DL", 02));
    }

    static void tokenizeAndDisplay(String line) {

        String label = "_";
        String opcode = "_";
        String op1 = "_";
        String op2 = "_";
    
        String[] tokens = line.trim().split("\\s+");
    
        int index = 0;
    
        // If first token is NOT in OPTAB, it is a LABEL
        if (!OPTAB.containsKey(tokens[index])) {
            label = tokens[index];
            index++;
        }
    
        // OPCODE
        if (index < tokens.length) {
            opcode = tokens[index];
            index++;
        }
    
        // Operand 1
        if (index < tokens.length) {
            op1 = tokens[index];
            index++;
        }
    
        // Operand 2
        if (index < tokens.length) {
            op2 = tokens[index];
        }
    
        System.out.printf("%-6s %-6s %-6s %-6s%n", label, opcode, op1, op2);
    }
    
}

