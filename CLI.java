class CLI{
  String command;
  String[] memory = new String[1];
  CPU cpu = new CPU(memory);

  public void interpret(String cmd){
    command = cmd.toLowerCase();

    if(command.equals("exit")){
      System.exit(0);
    } else if (command.equals("help")){
      System.out.println("Help hasn't really been integrated yet. Whoops, sorry.");
    } else {
        cpu.run(cmd, false, memory);
    }
  }
}
