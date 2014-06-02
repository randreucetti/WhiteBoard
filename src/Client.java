/*Ross Andreucetti
 *WhiteBoard Client
 * Using code extracts from Derek Molloy
 */

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

import javax.swing.*;


@SuppressWarnings("serial")
public class Client extends JFrame implements Runnable
{
	private Socket socket = null;		//for server comm
    private ObjectOutputStream os = null;
    private ObjectInputStream is = null;
    
    
    private int x1;	//for drawing shapes
    private int y1;
    private WhiteBoardShape s;
    Color c;
    
	JPanel connectionPanel;	//different areas of frame
	JPanel westPanel;
	JPanel toolsPanel;
	JPanel colorPanel;
	WhiteBoardCanvas canvas;	//canvas for drawing shapes
	
	JButton newConvoButton;	//connection buttons
	JButton disconnectButton;
	
	JRadioButton lineButton;	//tool buttons
	JRadioButton rectangleButton;
	JRadioButton filledRectButton;
	JRadioButton ovalButton;
	JRadioButton filledOvalButton;
	JRadioButton textButton;
	
	JRadioButton redButton;	//color buttons
	JRadioButton blueButton;
	JRadioButton greenButton;
	JRadioButton yellowButton;
	JRadioButton blackButton;
	
	ButtonGroup toolGroup;	//our button groups
	ButtonGroup colorGroup;
	
	
	public Client(String ip) 	//constructor
	{
		super("WhiteBoard Client ZX9000");
		if(!connectToServer(ip))	//attempts to connect to server
		{
			System.err.println("Cannot open socket connection..."); 
		}
		else	//mostly GUI stuff
		{
			this.setBounds(100, 100, 600, 400);
			this.setResizable(false);
			this.setDefaultCloseOperation(EXIT_ON_CLOSE);
			this.setVisible(true);
			
			this.setLayout(new BorderLayout());	//chooses layout
			
			connectionPanel = new JPanel(new FlowLayout());	//sets our areas
			westPanel = new JPanel(new GridLayout(2,1));
			toolsPanel = new JPanel(new GridLayout(0,2));
			colorPanel = new JPanel(new GridLayout(0,2));
			canvas = new WhiteBoardCanvas();
			
			connectionPanel.setBorder(BorderFactory.createBevelBorder(0));
			toolsPanel.setBorder(BorderFactory.createBevelBorder(0));
			colorPanel.setBorder(BorderFactory.createBevelBorder(0));
			
			westPanel.add(toolsPanel);
			westPanel.add(colorPanel);
			
			this.add("North",connectionPanel);	//adds panels
			this.add("West",westPanel);
			this.add("Center",canvas);
			
			newConvoButton = new JButton("Create or Join Conversation");	//inits buttons
			disconnectButton = new JButton("Disconnect");
			disconnectButton.setEnabled(false);
			
			lineButton = new JRadioButton("");
			rectangleButton = new JRadioButton("");
			filledRectButton = new JRadioButton("");
			ovalButton = new JRadioButton("");
			filledOvalButton  = new JRadioButton("");
			textButton = new JRadioButton("");
			
			lineButton.setEnabled(false);
			rectangleButton.setEnabled(false);
			filledRectButton.setEnabled(false);
			ovalButton.setEnabled(false);
			filledOvalButton.setEnabled(false);
			textButton.setEnabled(false);
			
			lineButton.setActionCommand("line");
			rectangleButton.setActionCommand("rect");
			filledRectButton.setActionCommand("filledRect");
			ovalButton.setActionCommand("oval");
			filledOvalButton.setActionCommand("filledOval");
			textButton.setActionCommand("text");
			
			
			redButton = new JRadioButton("");
			blueButton = new JRadioButton("");
			greenButton = new JRadioButton("");
			yellowButton = new JRadioButton("");
			blackButton = new JRadioButton("");
			
			redButton.setEnabled(false);
			blueButton.setEnabled(false);
			greenButton.setEnabled(false);
			yellowButton.setEnabled(false);
			blackButton.setEnabled(false);
			
			redButton.setActionCommand("red");
			blueButton.setActionCommand("blue");
			greenButton.setActionCommand("green");
			yellowButton.setActionCommand("yellow");
			blackButton.setActionCommand("black");
			
			toolGroup = new ButtonGroup();
			colorGroup = new ButtonGroup();
			
			
			connectionPanel.add(newConvoButton);	//adds buttons
			connectionPanel.add(disconnectButton);
			
			toolGroup.add(lineButton);	//adds to button group
			toolGroup.add(rectangleButton);
			toolGroup.add(filledRectButton);
			toolGroup.add(ovalButton);
			toolGroup.add(filledOvalButton);
			toolGroup.add(textButton);
			
			colorGroup.add(redButton);
			colorGroup.add(blueButton);
			colorGroup.add(greenButton);
			colorGroup.add(yellowButton);
			colorGroup.add(blackButton);
			
			
			lineButton.setSelected(true);
			
			toolsPanel.add(lineButton);
			toolsPanel.add(new JLabel(new ImageIcon("images/line.gif")));
			toolsPanel.add(rectangleButton);
			toolsPanel.add(new JLabel(new ImageIcon("images/rectangle.gif")));
			toolsPanel.add(filledRectButton);
			toolsPanel.add(new JLabel(new ImageIcon("images/filledRect.gif")));
			toolsPanel.add(ovalButton);
			toolsPanel.add(new JLabel(new ImageIcon("images/circle.gif")));
			toolsPanel.add(filledOvalButton);
			toolsPanel.add(new JLabel(new ImageIcon("images/filledCirc.gif")));
			toolsPanel.add(textButton);
			toolsPanel.add(new JLabel(new ImageIcon("images/text.gif")));
			
			redButton.setSelected(true);
			
			colorPanel.add(redButton);
			colorPanel.add(new JLabel(new ImageIcon("images/red.gif")));
			colorPanel.add(blueButton);
			colorPanel.add(new JLabel(new ImageIcon("images/blue.gif")));
			colorPanel.add(greenButton);
			colorPanel.add(new JLabel(new ImageIcon("images/green.gif")));
			colorPanel.add(yellowButton);
			colorPanel.add(new JLabel(new ImageIcon("images/yellow.gif")));
			colorPanel.add(blackButton);
			colorPanel.add(new JLabel(new ImageIcon("images/black.gif")));
			
			
			canvas.setBackground(Color.WHITE);
			
			canvas.setEnabled(false);
			
			this.pack();
			
			MouseAdapter mouseAdapter = (new MouseAdapter()	//for painting shapes on screen
			{
				public void mousePressed ( MouseEvent e )
	            {
					if(colorGroup.getSelection().getActionCommand()=="red")
						c = Color.red;
					if(colorGroup.getSelection().getActionCommand()=="blue")
						c = Color.blue;
					if(colorGroup.getSelection().getActionCommand()=="green")
						c = Color.green;
					if(colorGroup.getSelection().getActionCommand()=="yellow")
						c = Color.yellow;
					if(colorGroup.getSelection().getActionCommand()=="black")
						c = Color.black;
					x1 = e.getX();
					y1 = e.getY();
					
					if(toolGroup.getSelection().getActionCommand()=="line")
						canvas.addTemp(new Line(e.getX(),e.getY(),e.getX(),e.getY(),c));
					if(toolGroup.getSelection().getActionCommand()=="rect")
						canvas.addTemp(new Rectangle(e.getX(),e.getY(),e.getX(),e.getY(),c));
					if(toolGroup.getSelection().getActionCommand()=="filledRect")
						canvas.addTemp(new FilledRectangle(e.getX(),e.getY(),e.getX(),e.getY(),c));
					if(toolGroup.getSelection().getActionCommand()=="oval")
						canvas.addTemp(new Oval(e.getX(),e.getY(),e.getX(),e.getY(),c));
					if(toolGroup.getSelection().getActionCommand()=="filledOval")
						canvas.addTemp(new FilledOval(e.getX(),e.getY(),e.getX(),e.getY(),c));
					if(toolGroup.getSelection().getActionCommand()=="text")
						writeText();
	            }

	            public void mouseDragged ( MouseEvent e )	//drag effect while shape is being drawn
	            {
	            	if(toolGroup.getSelection().getActionCommand()=="line")
						canvas.addTemp(new Line(x1,y1,e.getX(),e.getY(),c));
					if(toolGroup.getSelection().getActionCommand()=="rect")
						canvas.addTemp(new Rectangle(x1,y1,e.getX(),e.getY(),c));
					if(toolGroup.getSelection().getActionCommand()=="filledRect")
						canvas.addTemp(new FilledRectangle(x1,y1,e.getX(),e.getY(),c));
					if(toolGroup.getSelection().getActionCommand()=="oval")
						canvas.addTemp(new Oval(x1,y1,e.getX(),e.getY(),c));
					if(toolGroup.getSelection().getActionCommand()=="filledOval")
						canvas.addTemp(new FilledOval(x1,y1,e.getX(),e.getY(),c));

	            }

	            public void mouseReleased ( MouseEvent e )	//commit to drawing the shape
	            {
	            	if(toolGroup.getSelection().getActionCommand()=="line")
	            	{
	            		Line l = new Line(x1,y1,e.getX(),e.getY(),c);
						canvas.add(l);
						try {
							os.writeObject(l);
							os.flush();
						} catch (IOException e1) {
							System.err.println("Error writing object");
						}
	            	}
	            	
					if(toolGroup.getSelection().getActionCommand()=="rect")
					{
	            		Rectangle r = new Rectangle(x1,y1,e.getX(),e.getY(),c);
						canvas.add(r);
						try {
							os.writeObject(r);
							os.flush();
						} catch (IOException e1) {
							System.err.println("Error writing object");
						}
	            	}
					if(toolGroup.getSelection().getActionCommand()=="filledRect")
					{
	            		FilledRectangle r = new FilledRectangle(x1,y1,e.getX(),e.getY(),c);
						canvas.add(r);
						try {
							os.writeObject(r);
							os.flush();
						} catch (IOException e1) {
							System.err.println("Error writing object");
						}
	            	}
					if(toolGroup.getSelection().getActionCommand()=="oval")
					{
	            		Oval o = new Oval(x1,y1,e.getX(),e.getY(),c);
						canvas.add(o);
						try {
							os.writeObject(o);
							os.flush();
						} catch (IOException e1) {
							System.err.println("Error writing object");
						}
	            	}
					if(toolGroup.getSelection().getActionCommand()=="filledOval")
					{
	            		FilledOval o = new FilledOval(x1,y1,e.getX(),e.getY(),c);
						canvas.add(o);
						try {
							os.writeObject(o);
							os.flush();
						} catch (IOException e1) {
							System.err.println("Error writing object");
						}
	            	}
	            }
			});
			
			newConvoButton.addActionListener(new ActionListener()	//connection button
			{
				public void actionPerformed(ActionEvent e) {
					createConvo();
				}
			});
			
			disconnectButton.addActionListener(new ActionListener() //disconnect button
			{
				public void actionPerformed(ActionEvent e) {
					disconnect();
				}
			});
			
			canvas.addMouseListener(mouseAdapter);
			canvas.addMouseMotionListener(mouseAdapter);
			this.run();	//starts thread which listens for input
		}

	}
	private boolean connectToServer(String ip)	 //attempts to connect to server with ip
	{
		try  // open a new socket to port: 5050 and create streams
        {
			this.socket = new Socket(ip,5050);
			this.os = new ObjectOutputStream(this.socket.getOutputStream());
			this.is = new ObjectInputStream(this.socket.getInputStream());
			System.out.print("Connected to Server\n");
        } 
        catch (Exception ex) 
        {
        	System.err.print("Failed to Connect to Server\n" + ex.toString());	
        	System.err.println(ex.toString());
        	return false;
        }
		return true;
	}
	public void writeText()	//special method for writing text
	{
		String s = JOptionPane.showInputDialog(null,"Enter Text: ","",1);	//dialog box for input
		if(s!=null)
		{
			Text t = new Text(x1,y1,0,0,c,s);
			canvas.add(t);
			try {
				os.writeObject(t);
				os.flush();
			} catch (IOException e1) {
				System.err.println("Error writing object");
			}
		}
	}
	public void createConvo()	//for joining or creating whiteboard convos
	{
		try {
			os.writeObject("new");	//writes a string so handle knows what to do
			os.flush();
		} catch (IOException e) {
			System.err.println("Error creating conversation");
		}
		String s = JOptionPane.showInputDialog(null,"Create name for new conversation or enter existing name: ","",1);
		if(s!=null)
		{
			if(s.compareTo("")==0)
				JOptionPane.showMessageDialog(this, "Please enter valid string.");
			else
				try {
					os.writeObject(s);
					os.flush();
				} catch (IOException e) {
					System.err.println("Error writing ID");
				}
		}
		lineButton.setEnabled(true);	//update gui appropriately
		rectangleButton.setEnabled(true);
		filledRectButton.setEnabled(true);
		ovalButton.setEnabled(true);
		filledOvalButton.setEnabled(true);
		textButton.setEnabled(true);
		
		redButton.setEnabled(true);
		blueButton.setEnabled(true);
		greenButton.setEnabled(true);
		yellowButton.setEnabled(true);
		blackButton.setEnabled(true);
		
		canvas.setEnabled(true);
		newConvoButton.setEnabled(false);
		disconnectButton.setEnabled(true);
			
	}
	public void disconnect()	//for leaving a convo
	{
		try {
			os.writeObject("disconnect");
			os.flush();
		} catch (IOException e) {
			System.err.println("Error creating conversation");
		}
		lineButton.setEnabled(false);
		rectangleButton.setEnabled(false);
		filledRectButton.setEnabled(false);
		ovalButton.setEnabled(false);
		filledOvalButton.setEnabled(false);
		textButton.setEnabled(false);
		
		redButton.setEnabled(false);
		blueButton.setEnabled(false);
		greenButton.setEnabled(false);
		yellowButton.setEnabled(false);
		blackButton.setEnabled(false);
		
		canvas.setEnabled(false);
		newConvoButton.setEnabled(true);
		disconnectButton.setEnabled(false);
		canvas.clear();
	}
	public static void main(String [] args)	//main method, takes ip argument
	{
		if(args.length>0)
		{
		   new Client(args[0]);
		}
		else
		{
			new Client("127.0.0.1");
		}
	}
	@Override
	public void run() 	//listens for input from server (Shapes)
	{
		boolean isRunning = true;	//while server is running
		while(isRunning)
		{
		try {
			Object o = is.readObject();
			s = (WhiteBoardShape) o;
		} catch (Exception e){
			isRunning = false;
			System.err.println("Server has stopped running, please exit");
		}
		if(s!=null)
			 canvas.add(s);
		}
		
	}
}
