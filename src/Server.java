import java.applet.Applet;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

public class Server extends Applet implements ActionListener 
{
	private static final long serialVersionUID = 1L;
	
	////////////////////////////////////////////////
	
	private TCPServer tcps = null;
	private UDPServer udps = null;
	
	////////////////////////////////////////////////
	
	JButton TCP_START;
	JButton TCP_STOP;
    JButton UDP_START;
    JButton UDP_STOP;
    
    JLabel TCP_Banner;
    JLabel UDP_Banner;
    
    JTextField TCP_IP;
    JTextField TCP_PORT;
    JTextField UDP_IP;
    JTextField UDP_PORT;
    
    DefaultListModel TCP_Log_data;
    JScrollPane TCP_Log_holder;
    JList TCP_Log;
    DefaultListModel UDP_Log_data;
    JScrollPane UDP_Log_holder;
    JList UDP_Log;
    
    JPanel TCP;
    JPanel UDP;

    public void init()  
    {
    	// initialize panels
      	 TCP = new JPanel();
      	 UDP = new JPanel();
      	 
      	 // banners
      	 TCP_Banner = new JLabel("TCP Server Status");
      	 UDP_Banner = new JLabel("UDP Server Status");
      	 
      	 // buttons
         TCP_START = new JButton("Start");
         TCP_START.setAlignmentX(CENTER_ALIGNMENT);
         TCP_START.addActionListener(this);
         TCP_STOP = new JButton("Stop");
         TCP_STOP.setAlignmentX(CENTER_ALIGNMENT);
         TCP_STOP.addActionListener(this);
         UDP_START = new JButton("Start");
         UDP_START.setAlignmentX(CENTER_ALIGNMENT);
         UDP_START.addActionListener(this);
         UDP_STOP = new JButton("Stop");
         UDP_STOP.setAlignmentX(CENTER_ALIGNMENT);
         UDP_STOP.addActionListener(this);
         
         // ip and ports
         TCP_IP = new JTextField();
         TCP_IP.setText("IP field");
         TCP_PORT = new JTextField();
         TCP_PORT.setText("[Input] port number here");
         UDP_IP = new JTextField();
         UDP_IP.setText("IP field");
         UDP_PORT = new JTextField();
         UDP_PORT.setText("[Input] port number here");
         
         // log list
         TCP_Log_data = new DefaultListModel();
         UDP_Log_data = new DefaultListModel();
         TCP_Log = new JList(TCP_Log_data);
         UDP_Log = new JList(UDP_Log_data);
         TCP_Log_holder = new JScrollPane(TCP_Log);
         TCP_Log_holder.setPreferredSize(new Dimension(380,230));
         UDP_Log_holder = new JScrollPane(UDP_Log);
         UDP_Log_holder.setPreferredSize(new Dimension(380,230));
         
         // add components to the first pane
         TCP.add(TCP_Banner);
         TCP.add(TCP_IP);
         TCP.add(TCP_PORT);
         TCP.add(TCP_START);
         TCP.add(TCP_STOP);
         TCP.add(TCP_Log_holder);
         
         // add components to the second pane
         UDP.add(UDP_Banner);
         UDP.add(UDP_IP);
         UDP.add(UDP_PORT);
         UDP.add(UDP_START);
         UDP.add(UDP_STOP);
         UDP.add(UDP_Log_holder);

         // final set up
         TCP.setLayout(new BoxLayout(TCP, BoxLayout.Y_AXIS));
         TCP.setPreferredSize(new Dimension(380,380));
         
    	 UDP.setLayout(new BoxLayout(UDP, BoxLayout.Y_AXIS));
    	 UDP.setPreferredSize(new Dimension(380,380));
         
    	 this.setLayout(new FlowLayout());
    	 this.setSize(new Dimension(800,400));
    	 this.add(TCP);
    	 this.add(UDP);
    }

    public void actionPerformed(ActionEvent evt)
    {
    	if (evt.getSource() == TCP_START)
    	{
    		if (tcps != null) {
    			TCP_Log_data.addElement("[Error]: Server already started.");
    			return;
    		}
    		int port;
    		try {
    			port = Integer.parseInt(TCP_PORT.getText().toString());
    			if (port < 0 || port > 65535) {
    				TCP_Log_data.addElement("[Error]: Please input valid port number.");
            		return;
    			}
    		}
    		catch (NumberFormatException e) {
    			TCP_Log_data.addElement("[Error]: Please input valid port number.");
        		return;
    		}
    		tcps = new TCPServer(port, TCP_Log_data);
    		while (!tcps.openListenSocket()){}
    		TCP_IP.setText(tcps.mAddr);
    		TCP_PORT.setText(Integer.toString(tcps.mPort));
    		tcps.start();
    	}
    	else if (evt.getSource() == TCP_STOP)
    	{
    		if (tcps == null) {
    			TCP_Log_data.addElement("[Error]: Server not started.");
    			return;
    		}
    		tcps.closeSocket();
    		tcps = null;
    	}
    	else if (evt.getSource() == UDP_START)
    	{
    		if (udps != null) {
    			UDP_Log_data.addElement("[Error]: Server already started.");
    			return;
    		}
    		int port;
    		try {
    			port = Integer.parseInt(UDP_PORT.getText().toString());
    			if (port < 0 || port > 65535) {
    				UDP_Log_data.addElement("[Error]: Please input valid port number.");
            		return;
    			}
    		}
    		catch (NumberFormatException e) {
    			UDP_Log_data.addElement("[Error]: Please input valid port number.");
        		return;
    		}
    		udps = new UDPServer(port, UDP_Log_data);
    		while(!udps.openSocket()){}
    		UDP_IP.setText(udps.mAddr);
    		UDP_PORT.setText(Integer.toString(udps.mPort));
    		udps.start();
    	}
    	else if (evt.getSource() == UDP_STOP)
    	{
    		if (udps == null) {
    			UDP_Log_data.addElement("[Error]: Server not started.");
    			return;
    		}
    		udps.closeSocket();
    		udps = null;
    	}
    }
} 
 