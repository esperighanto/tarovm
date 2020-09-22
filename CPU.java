// register types:
//  -special purpose:
//   -program counter spc. This holds the location of the next instruction to be fetched from memory.
//   -accumulator sac. This holds the last performed result of an operation.
//   -instruction register sir. This holds the instruction currently being executed.
//  -general purpose:
//   -gpa, gpb, gpc, gpd, gpe, gpf, gpg, gph. These are general purpose registers a-h, you've got 8 of them.
//  -constant registers:
//   -zero register. crz.
//   -pi register. crp.
//   -one register. cro.
//   -negative one register. crn.

//Still to make: jump, move, call, jumpindirect, and making a block by going :whatever.

// :nameofmyblock                    This makes a block of code.
// call                              This calls to a block of code expecting to be returned.

import java.util.*;

public class CPU{
  public int spc = 0; // These are the special purpose registers.
  public int sac = 0; // This is the accumulator register.
  public int sir = 0; // This is the instruction register.
  public int scb = 0; // This is the conditional branch result register.
  public int sjb = 0; // This is the jump back register, so that after a direct (a.k.a standard) jump, you know how to return upon hitting a "jump back" command.
  public int gpa = 0; // These are the general purpose registers, from here on down.
  public int gpb = 0;
  public int gpc = 0;
  public int gpd = 0;
  public int gpe = 0;
  public int gpf = 0;
  public int gpg = 0;
  public int gph = 0;
  public int setItTo = 0; // Using this for juggling data around, especially in the case of registers and memory -> memory data transfers.

  public boolean hasBeenInitialized = false; // For when running files.
  public int initLocation = 0; // Default starting location for the sir register.

  public boolean isInBlock = false; // To tell if it's in a block or not.
  public int blockStartBuffer = 0;
  public int blockEndBuffer = 0;
  public int blockCount = 0;
  public String blockNameBuffer;
  public int startToGoTo; // These are specifically for the call command.
  public int endToGoTo;
  public String[] memory;

  String[] commands; // This is for the later parsing of the commands.
  List<int[]> blockList = new ArrayList<int[]>(); // Use this to keep track of all the blocks. Each entry should have the starting and ending points of each block.
  List<String> blockNames = new ArrayList<String>(); // This is the array list that holds the name of each block. The index of this matches the index of the block's starting and ending points as stored in the list of int arrays blockList.
  
  CPU(String[] memoryToSet){
	  memory = memoryToSet;
  }

  public String[] getInputs(String[] commands, String[] memory){
//	  for(int i = 0; i < commands.length; i++) {
//		  System.out.println("Commands[" + i + "]" + " is " + commands[i]);
//	  }
	  
	  String[] returns = {"NULL", "NULL"};
	  for(int i=1; i < 3; i++){
		  if(isInteger(commands[i])) { // If the number is an integer.
			  returns[i-1] = Integer.toString(Integer.parseInt(commands[i]));
//			  System.out.println("TYPE A: SETTING RETURN VALUE " + (i-1) + " TO " + returns[i-1]);
		  } else if(isInteger(commands[i]) == false && commands[i].charAt(0) == 120) { // If the argument is a memory address, which is the line number prefaced by an x.
			  returns[i-1] = memory[Integer.parseInt(commands[i].substring(1))-1];
//			  System.out.println("TYPE B: SETTING RETURN VALUE " + (i-1) + " TO " + returns[i-1]);
		  } else if(isInteger(commands[i]) == false) { // If the argument is a register name.
			  returns[i-1] = Integer.toString(this.getRegister(commands[i]));
//			  System.out.println("TYPE C: SETTING RETURN VALUE " + (i-1) + " TO " + returns[i-1]);
		  }
	  }
	  return returns;
  }
  
  public void setMemory(int index, int value) {
	  
  }
  
  public int run(String command, boolean isLive, String[] memory){
    commands = command.split(" ");
    String[] returnedArguments;

    System.out.println("Running command " + Arrays.toString(commands) + " at sir " + getRegister("sir") + " while inBlock: " + Boolean.toString(isInBlock));

    if(commands[0].equals("add") && isInBlock == false){ // Add command.
    	returnedArguments = getInputs(commands, memory);
    	this.setRegister("sac", Integer.toString(Integer.parseInt(returnedArguments[0]) + Integer.parseInt(returnedArguments[1])));
      return 1;
    } else if(commands[0].equals("sub") && isInBlock == false){ // Subtract command.
    	returnedArguments = getInputs(commands, memory);
    	this.setRegister("sac", Integer.toString(Integer.parseInt(returnedArguments[0]) - Integer.parseInt(returnedArguments[1])));
      return 1;
    } else if(commands[0].equals("mul") && isInBlock == false){ // Multiply command.
    	returnedArguments = getInputs(commands, memory);
    	this.setRegister("sac", Integer.toString(Integer.parseInt(returnedArguments[0]) * Integer.parseInt(returnedArguments[1])));
      return 1;
    } else if(commands[0].equals("div") && isInBlock == false){ // Divide command.
    	returnedArguments = getInputs(commands, memory);
    	this.setRegister("sac", Integer.toString(Integer.parseInt(returnedArguments[0]) / Integer.parseInt(returnedArguments[1])));
      return 1;
    } else if(commands[0].equals("mod") && isInBlock == false){ // Modulo command.
    	returnedArguments = getInputs(commands, memory);
    	this.setRegister("sac", Integer.toString(Integer.parseInt(returnedArguments[0]) % Integer.parseInt(returnedArguments[1])));
      return 1;
    } else if(commands[0].equals("set") && isInBlock == false){ // Set register to value.
        if(isInteger(commands[2])){ // If this register is being set to a numerical value.
          this.setRegister(commands[1], commands[2]);
        } else if(isInteger(commands[2]) == false && commands[2].charAt(0) == 120) { // If this register is being set to the value of a memory address.
//        	System.out.println("Just testing this " + memory[Integer.parseInt(commands[2].substring(1))]);
        	this.setRegister(commands[1], memory[(Integer.parseInt(commands[2].substring(1))) - 1]);
        } else if(isInteger(commands[2]) == false){ // If this register is being set to the value of another register.
          setItTo = this.getRegister(commands[2]);
          this.setRegister(commands[1], Integer.toString(setItTo));
        }
        return 1;
    } else if(commands[0].equals("dump") && commands[1].equals("registers") && isInBlock == false){ // Dump every register.
      System.out.println("---------------------");
      System.out.println("Register spc: " + spc);
      System.out.println("Register sac: " + sac);
      System.out.println("Register sir: " + sir);
      System.out.println("Register scb: " + scb);
      System.out.println("Register gpa: " + gpa);
      System.out.println("Register gpb: " + gpb);
      System.out.println("Register gpc: " + gpc);
      System.out.println("Register gpd: " + gpd);
      System.out.println("Register gpe: " + gpe);
      System.out.println("Register gpf: " + gpf);
      System.out.println("Register gpg: " + gpg);
      System.out.println("Register gph: " + gph);
      System.out.println("---------------------");
      return 1;
    } else if(commands[0].equals("dump") && commands[1].equals("memory") && isInBlock == false) { // Dumps commands[2] lines of memory.
		System.out.println("\nMEMORY DUMP COMMENCING: (Dumping " + commands[2] + " commands)");
    	for(int i = 0; i < Integer.parseInt(commands[2]); i++){
    		System.out.println("MEMORY LINE " + (i + 1) + ": " + memory[i]);
    	}
    } else if(commands[0].equals("ifless") && isInBlock == false){
    	returnedArguments = getInputs(commands, memory);
        if(Integer.parseInt(returnedArguments[0]) < Integer.parseInt(returnedArguments[1])){
        	this.setRegister("scb", "1");
	    } else{
	    	this.setRegister("scb", "0");
	    }
      return 1;
    } else if(commands[0].equals("ifequal") && isInBlock == false){
    	returnedArguments = getInputs(commands, memory);
        if(Integer.parseInt(returnedArguments[0]) == Integer.parseInt(returnedArguments[1])){
        	this.setRegister("scb", "1");
	    } else{
	    	this.setRegister("scb", "0");
	    }
      return 1;
    } else if(commands[0].equals("ifunequal") && isInBlock == false){
    	returnedArguments = getInputs(commands, memory);
        if(Integer.parseInt(returnedArguments[0]) != Integer.parseInt(returnedArguments[1])){
        	this.setRegister("scb", "1");
	    } else{
	    	this.setRegister("scb", "0");
	    }
      return 1;
    } else if(commands[0].equals("ifgreater") && isInBlock == false){ // If both arguments are integers.
    	returnedArguments = getInputs(commands, memory);
        if(Integer.parseInt(returnedArguments[0]) > Integer.parseInt(returnedArguments[1])){
        	this.setRegister("scb", "1");
	    } else{
	    	this.setRegister("scb", "0");
	    }
      return 1;
    } else if(commands[0].equals("and") && isInBlock == false){ // And operation.
    	returnedArguments = getInputs(commands, memory);
    	this.setRegister("sac", Integer.toString(Integer.parseInt(returnedArguments[0]) & Integer.parseInt(returnedArguments[1])));
      return 1;
    } else if(commands[0].equals("or") && isInBlock == false){ // Or operation.
    	returnedArguments = getInputs(commands, memory);
    	this.setRegister("sac", Integer.toString(Integer.parseInt(returnedArguments[0]) | Integer.parseInt(returnedArguments[1])));
      return 1;
    } else if(commands[0].equals("not") && isInBlock == false){ // Inversion / not / complement operation.
      if(isInteger(commands[1])){ // If the arguments is an integer.
        this.setRegister("sac", Integer.toString((~Integer.parseInt(commands[1]))));
      } else if(isInteger(commands[1]) == false && commands[1].charAt(0) == 120) {
    	this.setRegister("sac", commands[1].substring(1));
      } else{ // If the argument is not an integer.
        this.setRegister("sac", Integer.toString((~this.getRegister(commands[1]))));
      }
      return 1;
    } else if(commands[0].equals("xor") && isInBlock == false){ // Xor operation.
      if(isInteger(commands[1]) && isInteger(commands[2])){ // If both arguments are integers.
        this.setRegister("sac", Integer.toString((Integer.parseInt(commands[1]) ^ Integer.parseInt(commands[2]))));
      } else if(isInteger(commands[1]) == false && isInteger(commands[2]) == false){ // If neither argument is an integer.
        this.setRegister("sac", Integer.toString((this.getRegister(commands[1]) ^ this.getRegister(commands[2]))));
      } else if(isInteger(commands[1]) == false && isInteger(commands[2]) == true ){ // If only the second argument is an integer.
        this.setRegister("sac", Integer.toString((this.getRegister(commands[1]) ^ Integer.parseInt(commands[2]))));
      } else if(isInteger(commands[1]) == true && isInteger(commands[2]) == false){ // If only the first argument is an integer.
        this.setRegister("sac", Integer.toString((Integer.parseInt(commands[1]) ^ this.getRegister(commands[2]))));
      this.setRegister("sac", Integer.toString(~(this.getRegister(commands[1]) & this.getRegister(commands[2]))));
    }
      return 1;
    } else if(commands[0].equals("nand") && isInBlock == false){ // Nand operation (And + Not/Inversion/Complement).
      if(isInteger(commands[1]) && isInteger(commands[2])){ // If both arguments are integers.
        this.setRegister("sac", Integer.toString(~(Integer.parseInt(commands[1]) & Integer.parseInt(commands[2]))));
      } else if(isInteger(commands[1]) == false && isInteger(commands[2]) == false){ // If neither argument is an integer.
      } else if(isInteger(commands[1]) == false && isInteger(commands[2]) == true ){ // If only the second argument is an integer.
        this.setRegister("sac", Integer.toString(~(this.getRegister(commands[1]) & Integer.parseInt(commands[2]))));
      } else if(isInteger(commands[1]) == true && isInteger(commands[2]) == false){ // If only the first argument is an integer.
        this.setRegister("sac", Integer.toString(~(Integer.parseInt(commands[1]) & this.getRegister(commands[2]))));
      }
      return 1;
    } else if(commands[0].equals("nor") && isInBlock == false){ // Nor operation (Or + Not/Inversion/Complement).
    	returnedArguments = getInputs(commands, memory);
    	this.setRegister("sac", Integer.toString(~(Integer.parseInt(returnedArguments[0]) | Integer.parseInt(returnedArguments[1]))));
      return 1;
//      if(isInteger(commands[1]) && isInteger(commands[2])){ // If both arguments are integers.
//        this.setRegister("sac", Integer.toString(~(Integer.parseInt(commands[1]) | Integer.parseInt(commands[2]))));
//      } else if(isInteger(commands[1]) == false && isInteger(commands[2]) == false){ // If neither argument is an integer.
//        this.setRegister("sac", Integer.toString(~(this.getRegister(commands[1]) | this.getRegister(commands[2]))));
//      } else if(isInteger(commands[1]) == false && isInteger(commands[2]) == true ){ // If only the second argument is an integer.
//        this.setRegister("sac", Integer.toString(~(this.getRegister(commands[1]) | Integer.parseInt(commands[2]))));
//      } else if(isInteger(commands[1]) == true && isInteger(commands[2]) == false){ // If only the first argument is an integer.
//        this.setRegister("sac", Integer.toString(~(Integer.parseInt(commands[1]) | this.getRegister(commands[2]))));
//      }
//      return 1;
    } else if(commands[0].equals("xnor") && isInBlock == false){ // Xnor operation (Xor + Not/Inversion/Complement).
    	returnedArguments = getInputs(commands, memory);
    	this.setRegister("sac", Integer.toString(~(Integer.parseInt(returnedArguments[0]) ^ Integer.parseInt(returnedArguments[1]))));
      return 1;
//      if(isInteger(commands[1]) && isInteger(commands[2])){ // If both arguments are integers.
//        this.setRegister("sac", Integer.toString(~(Integer.parseInt(commands[1]) ^ Integer.parseInt(commands[2]))));
//      } else if(isInteger(commands[1]) == false && isInteger(commands[2]) == false){ // If neither argument is an integer.
//        this.setRegister("sac", Integer.toString(~(this.getRegister(commands[1]) ^ this.getRegister(commands[2]))));
//      } else if(isInteger(commands[1]) == false && isInteger(commands[2]) == true ){ // If only the second argument is an integer.
//        this.setRegister("sac", Integer.toString(~(this.getRegister(commands[1]) ^ Integer.parseInt(commands[2]))));
//      } else if(isInteger(commands[1]) == true && isInteger(commands[2]) == false){ // If only the first argument is an integer.
//        this.setRegister("sac", Integer.toString(~(Integer.parseInt(commands[1]) ^ this.getRegister(commands[2]))));
//      }
//      return 1;
    } else if(commands[0].equals("read") && isInBlock == false){ //Read operator, you "read data_from_one_address address_to_where_its_going".
       setRegister(commands[2], memory[Integer.parseInt(commands[1])]);
       return 1;
    } else if(commands[0].equals("write") && isInBlock == false){ // Write operator, you "write data address_getting_written_to".
      memory[Integer.parseInt(commands[2])] = commands[1];
      return 1;
    } else if(commands[0].equals("jump") && isLive == false && isInBlock == false){
      // TODO: Make this able to interpret the value of a register as well.
      if(isInteger(commands[1])){ // If it's jumping to a direct location in memory represented by a number.
        System.out.println("Jumping to an int.");
        setRegister("sir", commands[1]);
        setRegister("sjb", Integer.toString(getRegister("sjb")));
        return 0;
      } else if(commands[1].equals("back")){
        setRegister("sir", Integer.toString(getRegister("sjb"))); // This is for jumping back.
      } else{ // If it's jumping to a location stored in a register.
      System.out.println("Jumping to a register.");
        setRegister("sir", Integer.toString(getRegister(commands[1])));
        return 0;
      }
    } else if(commands[0].equals("shutdown") && isInBlock == false){
      return -1;
    } else if(commands[0].equals("move") && isInBlock == false){ // I need to make this work with numerical memory representations as well as register names.
      returnedArguments = getInputs(commands, memory);
      System.out.println("MOVE DETECTED. MOVING x" + returnedArguments[0] + " to x" + returnedArguments[1]);
      memory[Integer.parseInt(commands[2].substring(1)) - 1] = memory[Integer.parseInt(commands[1].substring(1)) - 1];
    	
//    	if(isInteger(commands[1]) && isInteger(commands[2])){ // Move address address.
//        memory[Integer.parseInt(commands[1])] = memory[Integer.parseInt(commands[2])];
//      } else if(isInteger(commands[1]) == false && isInteger(commands[2]) == true){ // Move register address.
//        memory[Integer.parseInt(commands[2])] = Integer.toString(getRegister(commands[1]));
//      } else if(isInteger(commands[1]) == true && isInteger(commands[2]) == false){ // Move address register.
//        setRegister(commands[2], memory[Integer.parseInt(commands[1])]);
//      } else if(isInteger(commands[1]) == false && isInteger(commands[2]) == false){ // Move register register.
//        setRegister(commands[2], Integer.toString(getRegister(commands[1])));
//      }
    } else if(commands[0].charAt(0) == 58 || commands[0].charAt(0) == 59){ // If a new block is being declared, by using either a : or a ;.
      // System.out.println("New block found, called " + commands[0].substring(1, commands[0].length()));
      if(isInBlock == true){ // If you are already in a block. This means that the next found point should be a semicolon.
        if(commands[0].charAt(0) == 59){ // Checking if the next found character is a semicolon.
          blockEndBuffer = getRegister("sir"); // TODO: Check this for weaknesses against OBO errors.

          blockList.add(blockCount, new int[] {blockStartBuffer, blockEndBuffer}); // This adds the proper data to blockList.
          blockNames.add(blockCount, blockNameBuffer); // And also appropriately adds it to blockNames.

          blockCount += 1; // Increment the amount of blocks that are present, so that we can keep a steady index on the amount of blocks that are stored in their respective data structures.
          blockEndBuffer = 0;
          blockStartBuffer = 0;
          blockNameBuffer = "";
          isInBlock = false;
        }
      } else{ // If you are not already in a block. This means that the next found point should be a normal colon.
        if(commands[0].charAt(0) == 58){ // This just gets the whole thing going, so not too much going on here, but this is how a block is declared.
          blockStartBuffer = (getRegister("sir")); // TOOD: Also check this one through for OBO errors.
          blockNameBuffer = commands[0].substring(1, commands[0].length());
          isInBlock = true;
        }
      }
    } else if(commands[0].equals("call")){
      //Set wherever the semicolon was to a "Jump ________" with the ________ being the memory that it originally jumped from, so the current sir register value.
      System.out.println("CALL SET ALERT");

      for(int i=0; i<blockNames.size(); i++){ // Iterate through blockNames.
        if(commands[1].equals(blockNames.get(i))){ // If a match is found, then.
          System.out.println("Calling block " + blockNames.get(i) + " at sir " + blockList.get(i)[0]); // Log to the file what's going on.
          startToGoTo = blockList.get(i)[0]; // The first entry of the 2-dimensional int array blockList is the starting point.
          endToGoTo = blockList.get(i)[1]; // And the second entry is the end point.
        }
      }

      memory[endToGoTo] = "jump " + (getRegister("sir") + 1); // Set the end of that function to jump back to where you are right now.
      setRegister("sir", Integer.toString(startToGoTo + 1)); // Set the sir register to go to where it needs to be.
      return 0;
    } else if(commands[0].equals("scbcall")){ // This checks if scb == 1. If true, then it jumps to the tag mentioned, and resets scb to 0.
      //Set wherever the semicolon was to a "Jump ________" with the ________ being the memory that it originally jumped from, so the current sir register value.
      System.out.println("CONDITIONAL CALL SET ALERT, SCB: " + scb);
    	if(scb == 1) {
	
	      for(int i=0; i<blockNames.size(); i++){ // Iterate through blockNames.
	        if(commands[1].equals(blockNames.get(i))){ // If a match is found, then.
	          System.out.println("Calling block " + blockNames.get(i) + " at sir " + blockList.get(i)[0]); // Log to the file what's going on.
	          startToGoTo = blockList.get(i)[0]; // The first entry of the 2-dimensional int array blockList is the starting point.
	          endToGoTo = blockList.get(i)[1]; // And the second entry is the end point.
	        }
	      }
	
	      scb = 0;
	      memory[endToGoTo] = "jump " + (getRegister("sir") + 1); // Set the end of that function to jump back to where you are right now.
	      setRegister("sir", Integer.toString(startToGoTo + 1)); // Set the sir register to go to where it needs to be.
	      return 0;
	    }
    } else{
      return 1; // Return 1 so that only 1 is added to sir and the next instruction gets read.
    }
    return 1;
  }

  public void initialize(int startingSir){
    hasBeenInitialized = true;
    initLocation = startingSir;
  }

  public void start(){
    boolean programState = true;
    while(programState){
      int nextSir = this.run(memory[this.sir], false, memory);

      if(nextSir != -2){
        setRegister("sir", Integer.toString(this.sir + nextSir));
      } else{
        continue;
      }

      if(nextSir == -1){
        break;
      }
    }
  }
  
  public void setRegister(String register, String valueToConvert){
    int value = Integer.parseInt(valueToConvert);
    if(register.equals("spc")){
      this.spc = value;
    } else if(register.equals("sac")){
      this.sac = value;
    } else if(register.equals("sir")){
      this.sir = value;
    } else if(register.equals("gpa")){
      this.gpa = value;
    } else if(register.equals("gpb")){
      this.gpb = value;
    } else if(register.equals("gpc")){
      this.gpc = value;
    } else if(register.equals("gpd")){
      this.gpd = value;
    } else if(register.equals("gpe")){
      this.gpe = value;
    } else if(register.equals("gpf")){
      this.gpf = value;
    } else if(register.equals("gpg")){
      this.gpg = value;
    } else if(register.equals("gph")){
      this.gph = value;
    } else if (register.equals("scb")){
      this.scb = value;
    } else if (register.equals("sjb")){
      this.sjb = value;
    } else {
      throw new Error("Attempt to set register not detected by name of " + register + " to " + value);
    }
  }

  public int getRegister(String register){
    if(register.equals("spc")){
      return this.spc;
    } else if(register.equals("sac")){
      return this.sac;
    } else if(register.equals("sir")){
      return this.sir;
    } else if(register.equals("gpa")){
      return this.gpa;
    } else if(register.equals("gpb")){
      return this.gpb;
    } else if(register.equals("gpc")){
      return this.gpc;
    } else if(register.equals("gpd")){
      return this.gpd;
    } else if(register.equals("gpe")){
      return this.gpe;
    } else if(register.equals("gpf")){
      return this.gpf;
    } else if(register.equals("gpg")){
      return this.gpg;
    } else if(register.equals("gph")){
      return this.gph;
    } else if(register.equals("sjb")){
      return this.sjb;
    } else {
      throw new Error("Attempt to get register not detected by name of " + register);
    }
  }

  public static boolean isInteger(String s){
      try{
          Integer.parseInt(s);
      } catch(NumberFormatException e){
          return false;
      } catch(NullPointerException e){
          return false;
      }

      return true; // Should only have gotten to this point if you've not returned false by now.
  }
}
