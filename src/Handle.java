/*Ross Andreucetti
 * WhiteBoard Handle Connection
 * Using code extracts from Derek Molloy
 */

import java.net.*;
import java.util.LinkedList;
import java.io.*;

public class Handle extends Thread
{
	private static LinkedList<WhiteBoard> whiteBoards = new LinkedList<WhiteBoard>();
	public static int numClients=0;	//keeps track of clients and convo for server GUI
	public static int numConversations=0;
    private Socket clientSocket;			// Client socket object
    private ObjectInputStream is;			// Input stream
    private ObjectOutputStream os;			// Output stream
    private WhiteBoard w;		//local whiteboard pointing to l.list
    private Object o;	//for reading commands
    
    // The constructor for the connection handler

    public Handle(Socket clientSocket) 	
    {
        this.clientSocket = clientSocket;
    }

    // The main thread execution method 

    public void run() 
    {
    	
        try 
        {
            this.is = new ObjectInputStream(clientSocket.getInputStream());
            this.os = new ObjectOutputStream(clientSocket.getOutputStream());
            while (this.readCommand()) { }

         } 
         catch (IOException e) 
         {
                e.printStackTrace();
         }
    }

    // Receive and process incoming command from client socket 

    private boolean readCommand() //reads input from client
    {

        try 
        {
            o = is.readObject();
        } 
        catch (Exception e) 
        {
            o = null;
        }
        if (o == null) 
        {
            this.closeSocket();
            return false;
        }
        
        if(o.getClass() == String.class)	// if its a string, deal with in this way
        {
        	String s = (String) o;
        	if(s.compareTo("new")==0)
        		newConvo();
        	if(s.compareTo("disconnect")==0)
        			disconnect();
        }
        else	//otherwise shape is added to appropriate whiteboard
        {
        	WhiteBoardShape w = (WhiteBoardShape) o;
        	addShape(w);
        }
        
        return true;
    }

    public synchronized void addShape(WhiteBoardShape shape)
    {
    	w.add(shape,this);
    }
    public void sendShape(WhiteBoardShape shape)	//shape is sent to client
    {
    	try {
			os.writeObject(shape);
			os.flush();
		} catch (IOException e) {
			System.err.println("Error writing shape to client");
		}
    	
    }
    
    // Close the client socket 
    public void closeSocket()		//close the socket connection
    {
        try 
        {
        	if(w!=null)
        		w.removeClient(this);
            this.os.close();
            this.is.close();
            this.clientSocket.close();
        } 
        catch (Exception ex) 
        {
            System.err.println(ex.toString());
        }
    }
    public void updateClient()	//used when client joins new convo to display shapes already present
    {
    	for(WhiteBoardShape s: w.shapes)
    	{
    		try {
    			os.writeObject(s);
    			os.flush();
    		} catch (IOException e) {
    			System.err.println("Error writing initial shapes to client");
    		}
    	}
    }
    public void newConvo()	//when a client connects to server
    {
    	numClients++;
    	Server.updateGUI();
    	boolean isMatch = false;	//used to see if there is already a convo with that ID
    	String s = null;
		try {
			s = (String) is.readObject();	//reads the ID
		} catch (Exception e) {
			e.printStackTrace();
		}
    	for(WhiteBoard board: whiteBoards)
    	{
    		if(s.compareTo(board.ID)==0)	//checks for match
    		{
    			try{    			
    				w = board;	//client joins existing convo
    				w.addClient(this);
    				updateClient();
    				isMatch=true;
    				break;
    			}
    			catch(Exception e1)
    			{
    				e1.printStackTrace();
    			}
    		}
    	}
    	if(!isMatch)	//no match
    	{
    		w = new WhiteBoard(s);	//new convo is created
    		w.addClient(this);
    		whiteBoards.add(w);
    		numConversations++;
    		Server.updateGUI();
    	}
    }
    public void disconnect()	//when client leaves a convo
    {
    	w.removeClient(this);
    	numClients--;
    	Server.updateGUI();
    }
}