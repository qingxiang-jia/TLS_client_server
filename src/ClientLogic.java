import java.io.*;
import java.util.Scanner;

/**
 * Handles all user interactions of the client.
 */
public class ClientLogic
{
    ObjectOutputStream objOut = null; // data from server comes out
    ObjectInputStream objIn = null; // data sent to server

    public ClientLogic(InputStream netIn, OutputStream netOut)
    {
        try {
            objOut = new ObjectOutputStream(netOut);
            objIn = new ObjectInputStream(netIn);
        } catch (IOException e) {
            System.out.println("Cannot create object streams from streams");
        }
    }


    public void perform() // should ask Debra if command is case sensitive
    {
        Scanner keyboard = new Scanner(System.in);
        while (true) {
            System.out.println("waiting for user input");
            String userInput = keyboard.nextLine();
            if (userInput.equalsIgnoreCase("stop"))
                break;
            else {
                String[] cmd = userInput.split("\\s+");
                if (cmd.length < 3)
                    System.out.println("Error: too few arguments.");
                else if (cmd.length > 4)
                    System.out.println("Error: too many arguments.");
                else { // 3 or 4 arguments
                    if (cmd[0].equalsIgnoreCase("get")) { // get
                        if (cmd.length == 3) { // get path N
                            if (cmd[2].equalsIgnoreCase("E"))
                                System.out.println("Error: Missing parameters, \"E\" requires a password");
                            else if (!cmd[2].equalsIgnoreCase("N"))
                                System.out.println("Error: Invalid parameter \"" + cmd[2] + "\"");
                            else { // legal [get path N]
                                try {
                                    Path filePath = new Path(cmd[1]);
                                    ClientHandler.handleGet(filePath, objIn, objOut);
                                } catch (IllegalFilePathException e) {
                                    System.out.println("Error: Invalid file path or file does not exist");
                                }
                            }
                        } else { // get path E pwd
                            if (cmd[2].equalsIgnoreCase("N"))
                                System.out.println("Error: Parameter conflict, N means no password, but password present");
                            else if (!cmd[2].equalsIgnoreCase("E"))
                                System.out.println("Error: Invalid parameter \"" + cmd[2] + "\"");
                            else if (cmd[3].length() != 8)
                                System.out.println("Error: Password must be 8 characters");
                            else { // legal [get path E pwd]
                                try {
                                    Path filePath = new Path(cmd[1]);
                                    ClientHandler.handleGet(filePath, cmd[3], objIn, objOut);
                                } catch (IllegalFilePathException e) {
                                    System.out.println("Error: Invalid file path or file does not exist");
                                }
                            }
                        }
                    } else if (cmd[0].equalsIgnoreCase("put")) { // put
                        if (cmd.length == 3) { // put path N
                            if (cmd[2].equalsIgnoreCase("E"))
                                System.out.println("Error: Missing parameters, \"E\" requires a password");
                            else if (!cmd[2].equalsIgnoreCase("N"))
                                System.out.println("Error: Invalid parameter \"" + cmd[2] + "\"");
                            else { // legal [put path N]
                                try {
                                    Path filePath = new Path(cmd[1]);
                                    ClientHandler.handlePut(filePath, objIn, objOut);
                                } catch (IllegalFilePathException e) {
                                    System.out.println("Error: Invalid file path or file does not exist");
                                }
                            }
                        } else { // put path E pwd
                            if (cmd[2].equalsIgnoreCase("N"))
                                System.out.println("Error: Parameter conflict, N means no password, but password present");
                            else if (!cmd[2].equalsIgnoreCase("E"))
                                System.out.println("Error: Invalid parameter \"" + cmd[2] + "\"");
                            else if (cmd[3].length() != 8)
                                System.out.println("Error: Password must be 8 characters" + "|" + cmd[2] + "|");
                            else { // legal [put path E pwd]
                                try {
                                    Path filePath = new Path(cmd[1]);
                                    ClientHandler.handlePut(filePath, cmd[3], objIn, objOut);
                                } catch (IllegalFilePathException e) {
                                    System.out.println("Error: Invalid file path or file does not exist");
                                }
                            }
                        }
                    } else
                        System.out.println("Error: Invalid commands, options are \"get\" \"put\" \"stop\"");
                }
            }
        }
    }
}
