import java.io.*;
import java.util.Scanner;

/**
 * Handles all user interactions of the client.
 * The idea is to separate the actual client operation from user interactions.
 * This class handles all user interactions.
 */
public class ClientLogic
{
    ObjectOutputStream objOut = null; // data from server comes out
    ObjectInputStream objIn = null; // data sent to server

    /**
     * Constructor, takes an InputStream object and OutputStream object.
     * @param netIn Data sent to server
     * @param netOut Data from server comes out
     */
    public ClientLogic(InputStream netIn, OutputStream netOut)
    {
        try {
            objOut = new ObjectOutputStream(netOut);
            objIn = new ObjectInputStream(netIn);
        } catch (IOException e) {
            System.out.println("Cannot create object streams from streams");
        }
    }

    /**
     * Applies the client logic, interacts with the user.
     * Notice: all commands are NOT case sensitive.
     */
    public void perform()
    {
        Scanner keyboard = new Scanner(System.in); // take user keyboard input
        while (true) {
            System.out.println("waiting for user input");
            String userInput = keyboard.nextLine();
            if (userInput.equalsIgnoreCase("stop")) // if types stop, quit program
                break;
            else {
                String[] cmd = userInput.split("\\s+"); // since there is no space in path, always splits user input by space
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
                                    Path filePath = new Path(cmd[1], false); // don't check path, it's server's job
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
                                    Path filePath = new Path(cmd[1], false); // don't check path, it's server's job
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
                                    Path filePath = new Path(cmd[1], true); // check path in case of garbage input
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
                                System.out.println("Error: Password must be 8 characters");
                            else { // legal [put path E pwd]
                                try {
                                    Path filePath = new Path(cmd[1], true); // check path in case of garbage input
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
