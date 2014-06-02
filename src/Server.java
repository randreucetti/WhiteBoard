/*Ross Andreucetti
 * Threaded WhiteBoard Server
 * Using code extracts from Derek Molloy
 */

import java.net.*;
import java.awt.*;
import java.io.*;

import javax.swing.*;

@SuppressWarnings("serial")
public class Server extends JFrame
{	
	private static JLabel numClients;
	private static JLabel numConversations;
	
	public void createGUI()	//creates simple gui for displaying server status
	{
		JFrame frame = new JFrame("ServerGUI");
		frame.setVisible(true);
		frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
		frame.setBounds(100,500,250,70);
		numClients = new JLabel("Number of clients connected: "+ Handle.numClients);
		numConversations = new JLabel("Number of conversations: "+Handle.numConversations);
		frame.setLayout(new GridLayout(0,1));
		frame.add(numClients);
		frame.add(numConversations);
	}
	public static void updateGUI()	//called when GUI needs to be updated
	{
		numClients.setText("Number of clients connected: "+ Handle.numClients);
		numConversations.setText("Number of conversations: "+Handle.numConversations);
	}
	
    public static void main(String args[]) 	//main method for accepting connections
    {
    	new Server().createGUI();
    	
        ServerSocket serverSocket = null;
        
        try 
        {
            serverSocket = new ServerSocket(5050);	
            System.out.println("Server has started listening on port 5050");
        } 
        catch (IOException e) 
        {
            System.err.println("Error: Cannot listen on port 5050: " + e);
            System.exit(1);
        }
        while (true) // infinite loop - loops once for each client
        {
            Socket clientSocket = null;
            try 
            {
                clientSocket = serverSocket.accept(); //waits here (forever) until a client connects
                System.out.println("Server has just accepted socket connection from a client");
            } 
            catch (IOException e) 
            {
                System.err.println("Accept failed: 5050 " + e);
                break;
            }	
            // Create the Handle Connection object - our new thread object - only create it
            Handle con = new Handle(clientSocket);
            con.start();// start this thread now

        }
        try  // do not get here at the moment 
        {
            System.out.println("Closing server socket. ");
            serverSocket.close();
        } 
        catch (IOException e) 
        {
            System.err.println("Could not close server socket. " + e.getMessage());
        }
    }
}