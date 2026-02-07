import java.util.*;

class ICLine {
    String address;
    String type;
    String opcode;
    String op1;
    String op2;

    ICLine(String a, String t, String o, String o1, String o2) {
        address = a;
        type = t;
        opcode = o;
        op1 = o1;
        op2 = o2;
    }
}

public class AssemblerPass2 {

    static Map<Integer, Integer> symtab = new HashMap<>();
    static Map<Integer, Integer> littab = new HashMap<>();
    static Map<String, String> optab = new HashMap<>();
    static List<ICLine> ic = new ArrayList<>();

    public static void main(String[] args) {

        loadTables();
        loadIC();

        System.out.println("LC\tMachine Code");
        System.out.println("----------------------------");

        for (ICLine line : ic) {
            processLine(line);
        }
    }

    static void loadTables() {

        // Symbol Table
        symtab.put(1, 500);
        symtab.put(2, 504);
        symtab.put(3, 600);
        symtab.put(4, 603);
        symtab.put(5, 604);

        // Literal Table
        littab.put(1, 800);
        littab.put(2, 804);

        // Opcode Table
        optab.put("01", "58"); // L
        optab.put("02", "5A"); // A
        optab.put("03", "50"); // ST
        optab.put("04", "47"); // BNE
        optab.put("05", "07"); // BR
    }

    static void loadIC() {

        ic.add(new ICLine("-", "AD", "01", "-", "C,500"));
        ic.add(new ICLine("500", "IS", "01", "1", "L,1"));
        ic.add(new ICLine("501", "IS", "02", "3", "S,4"));
        ic.add(new ICLine("502", "IS", "03", "3", "L,1"));
        ic.add(new ICLine("503", "AD", "02", "-", "01"));
        ic.add(new ICLine("504", "IS", "01", "-", "L,2"));
        ic.add(new ICLine("505", "IS", "04", "1", "S,3"));
        ic.add(new ICLine("-", "AD", "03", "-", "C,600"));
        ic.add(new ICLine("600", "IS", "01", "2", "S,5"));
        ic.add(new ICLine("601", "IS", "05", "-", "S,4"));
        ic.add(new ICLine("602", "IS", "03", "1", "1"));
        ic.add(new ICLine("603", "DL", "01", "-", "C,5"));
        ic.add(new ICLine("-", "DL", "-", "-", "C,4"));
    }

    static void processLine(ICLine line) {

        if (line.type.equals("AD"))
            return;

        if (line.type.equals("DL")) {
            if (line.opcode.equals("01")) { // DC
                System.out.println(line.address + "\t" + String.format("%06d", getConstant(line.op2)));
            }
            return;
        }

        if (line.type.equals("IS")) {

            String machineOpcode = optab.get(line.opcode);
            String reg = resolveRegister(line.op1);
            String mem = resolveOperand(line.op2);

            System.out.println(line.address + "\t" + machineOpcode + reg + mem);
        }
    }

    static String resolveRegister(String r) {
        if (r.equals("-")) return "0";
        return r;
    }

    static String resolveOperand(String op) {

        if (op == null || op.equals("-"))
            return "000";

        if (op.startsWith("S")) {
            int idx = Integer.parseInt(op.split(",")[1]);
            return String.format("%03d", symtab.get(idx));
        }

        if (op.startsWith("L")) {
            int idx = Integer.parseInt(op.split(",")[1]);
            return String.format("%03d", littab.get(idx));
        }

        if (op.startsWith("C")) {
            return String.format("%03d", getConstant(op));
        }

        return String.format("%03d", Integer.parseInt(op));
    }

    static int getConstant(String c) {
        return Integer.parseInt(c.split(",")[1]);
    }
}
