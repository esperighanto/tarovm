import java.util.*;
import java.lang.*;
import java.io.*;
import java.nio.*;


//Ops are going to be short ints.

class main{
  public static void main(String args[]){
    /*Argument structures:
        -eduvm filename.vmasm -[tags]             To run vmasm assembly.
        -eduvm filename.vmbc -[tags]              To run vmasm bytecode.
        -eduvm live                               To run the vmasm live console.
    */
    if(args.length == 0){
      System.out.println("Welcome to EduVM.");
      System.out.println("The syntax of this program is:");
      System.out.println("eduvm filename.eduvm -[tags]  | Use the eduvm interpreter.");
      System.out.println("eduvm live                    | Use the eduvm live console.");
    } else if (args.length > 0){
      if(args[0].equals("live")){ // Begin running the live console.
        CLI cmd = new CLI();
        boolean programState = true;
        Scanner input = new Scanner(System.in);
        String cmdToRun;

        System.out.println("Enter your commands here. To exit, type 'exit'. and for help type 'help'.");

        while(programState){
          cmdToRun = input.nextLine();
          cmd.interpret(cmdToRun);
        }
      } else{
        if(args[0].equals("run")){ // If it was called as "eduvm run ____" then run the file that is detailed on the underscore.

          File codeToRun = new File(args[1]);
          BufferedReader reader = null;
          List<String> lines = new ArrayList<String>();
          String lineRead;

          try{
            reader = new BufferedReader(new FileReader(args[1]));

            while((lineRead = reader.readLine()) != null){
              lines.add(lineRead);
            }
          } catch(FileNotFoundException e){
            e.printStackTrace();
          } catch(IOException e){
            e.printStackTrace();
          } finally{
            try{
              if(reader != null){
                reader.close();
              }
            } catch(IOException e){
              e.printStackTrace();
            }
          }
          String[] memory = new String[1023]; // Give the user a kilo"byte" of memory. More like a kiloint if you want the truth.
          CPU cpu = new CPU(memory);

          System.out.println("----------MEMORY UPLOAD------------");

          for(int i=0; i < lines.size(); i++){ // Add each command to the memory array to be passed into the CPU.
            memory[i] = lines.get(i);
            System.out.println(lines.get(i) + " was written to memory location " + i);
          }

          System.out.println("----------PROGRAM LOG------------");

          cpu.initialize(memory.length);
          cpu.start();
        }
        // I'll have to have it check for the file here, and if it exists, then run it.
      }
    }
  }
}
