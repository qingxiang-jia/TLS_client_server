package a2.logic;

import a2.handlers.ClientHandler;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Scanner;

/**
 * Handles all user interactions of the client.
 */
public class ClientLogic
{
    InputStream netIn; // data from server comes out
    OutputStream netOut; // data sent to server

    public ClientLogic(InputStream netIn, OutputStream netOut)
    {
        this.netIn = netIn;
        this.netOut = netOut;
    }

    public void perform() // should ask Debra if command is case sensitive
    {
        Scanner keyboard = new Scanner(System.in);
        while (true)
        {
            String userInput = keyboard.nextLine();
            if (userInput.equalsIgnoreCase("stop"))
                break;
            else
            {
                String[] cmd = userInput.split("\\s+");
                if (cmd.length < 3)
                    System.out.println("Error: too few arguments.");
                else if (cmd.length > 4)
                    System.out.println("Error: too many arguments.");
                else // 3 or 4 arguments
                {
                    if (cmd[0].equalsIgnoreCase("get")) // get
                    {
                        if (cmd.length == 3) // get path N
                        {
                            if (cmd[2].equalsIgnoreCase("E"))
                                System.out.println("Error: Missing parameters, \"E\" requires a password");
                            else if (!cmd[2].equalsIgnoreCase("N"))
                                System.out.println("Error: Invalid parameter \"" + cmd[2] + "\"");
                            else // legal [get path N]
                                ClientHandler.handGet(cmd[1], netIn, netOut);
                        } else // get path E pwd
                        {
                            if (cmd[2].equalsIgnoreCase("N"))
                                System.out.println("Error: Parameter conflict, N means no password, but password present");
                            else if (!cmd[2].equalsIgnoreCase("E"))
                                System.out.println("Error: Invalid parameter \"" + cmd[2] + "\"");
                            else // legal [get path E pwd]
                                ClientHandler.handGet(cmd[1], cmd[3], netIn, netOut);
                        }
                    } else if (cmd[0].equalsIgnoreCase("put")) // put
                    {
                        if (cmd.length == 3) // put path N
                        {
                            if (cmd[2].equalsIgnoreCase("E"))
                                System.out.println("Error: Missing parameters, \"E\" requires a password");
                            else if (!cmd[2].equalsIgnoreCase("N"))
                                System.out.println("Error: Invalid parameter \"" + cmd[2] + "\"");
                            else // legal [put path N]
                                ClientHandler.handPut(cmd[1], netIn, netOut);
                        } else // put path E pwd
                        {
                            if (cmd[2].equalsIgnoreCase("N"))
                                System.out.println("Error: Parameter conflict, N means no password, but password present");
                            else if (!cmd[2].equalsIgnoreCase("E"))
                                System.out.println("Error: Invalid parameter \"" + cmd[2] + "\"");
                            else // legal [put path E pwd]
                                ClientHandler.handPut(cmd[1], cmd[3], netIn, netOut);
                        }
                    } else
                        System.out.println("Error: Invalid commands, options are \"get\" \"put\" \"stop\"");
                }
            }
        }
    }
}