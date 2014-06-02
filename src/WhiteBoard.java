import java.util.LinkedList;


public class WhiteBoard	//whiteboard convo class
{
	public LinkedList<WhiteBoardShape> shapes;	//shapes in board
	private LinkedList<Handle> clients;	//clients connected
	public String ID;
	
	public WhiteBoard(String ID)	//new whiteboard
	{
		shapes = new LinkedList<WhiteBoardShape>();
		clients = new LinkedList<Handle>();
		this.ID = ID;
	}

	public synchronized void add(WhiteBoardShape s,Handle handle)	//add a shape to board
	{
		shapes.add(s);
		for(Handle h:clients)	//tells all the clients connected
		{
			if(h!=handle)
				h.sendShape(s);
		}
	}
	public void addClient(Handle handle)	//new client
	{
		clients.add(handle);

	}
	public void removeClient(Handle handle)	//clients leaves
	{
		clients.remove(handle);
	}
	
}
