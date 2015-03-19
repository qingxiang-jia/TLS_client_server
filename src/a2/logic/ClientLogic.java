package a2.logic;

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

    public void perform()
    {
        Scanner keyboard = new Scanner(System.in);
        while (true)
        {
            String userInput = keyboard.nextLine();
            if (userInput.equalsIgnoreCase("q"))
                break;
            System.out.println(userInput);
        }
    }
}
