import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class a1 {

    public static void main(String[] args) {

        String fileName = "input.asm";   // Assembly source file

        try {
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            String line;

            System.out.println("----- Source Program -----");

            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }

            br.close();
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
    }
}

