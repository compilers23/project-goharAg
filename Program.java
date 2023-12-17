import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.File;
import java.io.InputStreamReader;

public class Program {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java Program <inputFileName>");
            System.exit(1);
        }

        String inputFileName = args[0];
        String outputFileName = "test.s";
        String objectFileName = "test.o";
        String executableFileName = "output_executable";

        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(outputFileName)))) {
            
            BufferedReader reader = new BufferedReader(new FileReader(inputFileName));

           
            createOutputFile(outputFileName);

    
            out.println(".data");
            out.println("fmt: .asciz \"%d\"");
            out.println("newline: .ascii \"\\n\"");
            out.println( ".text");
            out.println(".global _main");
            out.println("_main:");

           
            String line;
            while ((line = reader.readLine()) != null) {

                String exp = line.trim();

                if(isNumber(exp)){
                    out.println("  pushq $" + exp);
                }
                else if(exp.equals(".s")) {
                    out.println("movq (%rsp), %rsi"); 
                    out.println("movq $0, %rax");
                    out.println("pushq %rax");
                    out.println("lea fmt(%rip), %rdi"); 
                    out.println("call printf");
                    out.println("mov $0, %edi"); 
                    out.println("call fflush"); 
                }
                else if(exp.equals("+")){
                    out.println("  popq %rbx");
                    out.println("  popq %rax");
                    out.println("  addq %rax, %rbx");
                    out.println("  pushq %rbx");

                }else if(exp.equals("-")){
                    out.println("  popq %rbx");
                    out.println("  popq %rax");
                    out.println("  subq %rax, %rbx");
                    out.println("  pushq %rax");
                }else if(exp.equals("*")){
                    out.println("  popq %rbx");
                    out.println("  popq %rax");
                    out.println("  imulq %rax, %rbx");
                    out.println("  pushq %rax");
                }else if(exp.equals("swap")){
                    out.println("  popq %rbx");
                    out.println("  popq %rax");
                    out.println("  pushq %rax");
                    out.println("  pushq %rbx");
                }else if(exp.equals("nip")){
                    out.println("  popq %rbx");
                    out.println("  popq %rax");
                    out.println("  pushq %rbx");
                }else if(exp.equals("dup")){
                    out.println("  popq %rbx");
                    out.println("  pushq %rbx");
                    out.println("  pushq %rbx");
                }
                //more operations
             
               
            }

            out.println("movq %rax, %rdi");
            out.println("movq $60, %rax");
            out.println( "syscall");
        
            reader.close();
            out.close();

            System.out.println("File copied successfully!");

            assembleCode("as -o " + objectFileName + " " + outputFileName);

            linkObjectFile("ld -o " + executableFileName + " " + objectFileName);

            executeShellCommand("./" + executableFileName);


        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void createOutputFile(String fileName) throws IOException {
        Path path = Paths.get(fileName);

        if (!Files.exists(path)) {
            Files.createFile(path);
        }
    }

     private static boolean isNumber(String str) {
        return str.matches("-?\\d+(\\.\\d+)?");
    }


    private static void assembleCode(String command) throws IOException, InterruptedException {
        executeShellCommand(command);
    }

    private static void linkObjectFile(String command) throws IOException, InterruptedException {
        executeShellCommand(command);
    }

    private static void executeShellCommand(String command) throws IOException, InterruptedException {
        Process process = new ProcessBuilder("bash", "-c", command)
                .directory(new File("."))
                .redirectErrorStream(true)
                .start();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        }

        int exitCode = process.waitFor();

        if (exitCode != 0) {
            throw new RuntimeException("Command execution failed. Exit code: " + exitCode);
        }
    }
}

