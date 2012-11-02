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

public class Client extends Applet implements ActionListener 
{
	private static final long serialVersionUID = 1L;

////////////////////////////////////////////////
	
	private TCPClient tcpc = null;
	private UDPClient udpc = null;
	
////////////////////////////////////////////////
	
	JButton TCP_START;
	JButton TCP_STOP;
    JButton UDP_START;
    JButton UDP_STOP;
    JButton TCP_SEND;
    JButton UDP_SEND;
    
    JLabel TCP_Banner;
    JLabel UDP_Banner;
    
    JTextField TCP_IP;
    JTextField TCP_PORT;
    JTextField TCP_MESSAGE;
    JTextField UDP_IP;
    JTextField UDP_PORT;
    JTextField UDP_MESSAGE;
    
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
      	 TCP_Banner = new JLabel("TCP Client Status");
      	 UDP_Banner = new JLabel("UDP Client Status");
      	 
      	 // buttons
         TCP_START = new JButton("Start");
         TCP_START.setAlignmentX(CENTER_ALIGNMENT);
         TCP_START.addActionListener(this);
         TCP_STOP = new JButton("Stop");
         TCP_STOP.setAlignmentX(CENTER_ALIGNMENT);
         TCP_STOP.addActionListener(this);
         TCP_SEND = new JButton("Send");
         TCP_SEND.setAlignmentX(CENTER_ALIGNMENT);
         TCP_SEND.addActionListener(this);
         
         UDP_START = new JButton("Start");
         UDP_START.setAlignmentX(CENTER_ALIGNMENT);
         UDP_START.addActionListener(this);
         UDP_STOP = new JButton("Stop");
         UDP_STOP.setAlignmentX(CENTER_ALIGNMENT);
         UDP_STOP.addActionListener(this);
         UDP_SEND = new JButton("Send");
         UDP_SEND.setAlignmentX(CENTER_ALIGNMENT);
         UDP_SEND.addActionListener(this);
         
         // ip and ports
         TCP_IP = new JTextField();
         TCP_IP.setText("[Input] IP address here");
         TCP_PORT = new JTextField();
         TCP_PORT.setText("[Input] port number here");
         UDP_IP = new JTextField();
         UDP_IP.setText("[Input] IP address here");
         UDP_PORT = new JTextField();
         UDP_PORT.setText("[Input] port number here");
         TCP_MESSAGE = new JTextField();
         TCP_MESSAGE.setText("[Input] message here");
         UDP_MESSAGE = new JTextField();
         UDP_MESSAGE.setText("[Input] message here");
         
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
         TCP.add(TCP_MESSAGE);
         TCP.add(TCP_SEND);
         TCP.add(TCP_Log_holder);
         
         // add components to the second pane
         UDP.add(UDP_Banner);
         UDP.add(UDP_IP);
         UDP.add(UDP_PORT);
         UDP.add(UDP_START);
         UDP.add(UDP_STOP);
         UDP.add(UDP_MESSAGE);
         UDP.add(UDP_SEND);
         UDP.add(UDP_Log_holder);

         // final set up
         TCP.setLayout(new BoxLayout(TCP, BoxLayout.Y_AXIS));
         TCP.setPreferredSize(new Dimension(380,480));
         
    	 UDP.setLayout(new BoxLayout(UDP, BoxLayout.Y_AXIS));
    	 UDP.setPreferredSize(new Dimension(380,480));
         
    	 this.setLayout(new FlowLayout());
    	 this.setSize(new Dimension(800,500));
    	 this.add(TCP);
    	 this.add(UDP);
    }
	
	@Override
	public void actionPerformed(ActionEvent evt) {
		// TODO Auto-generated method stub
		if (evt.getSource() == TCP_START)
		{
			if (tcpc != null) {
				TCP_Log_data.addElement("[Error]: Client already started.");
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
    		String ip = TCP_IP.getText().toString();
    		tcpc = new TCPClient(ip, port, TCP_Log_data);
    		if (!tcpc.openSocket()) {
    			tcpc = null;
    			return;
    		}
		}
		else if (evt.getSource() == TCP_STOP)
		{
			if (tcpc == null) {
				TCP_Log_data.addElement("[Error]: Client not started.");
				return;
			}
			else
			{
				tcpc.send("@close");
				tcpc.closeSocket();
				tcpc = null;
			}
		}
		else if (evt.getSource() == TCP_SEND)
		{
			if (tcpc == null) {
				TCP_Log_data.addElement("[Error]: Client not started.");
				return;
			}
			else
			{
				String data = TCP_MESSAGE.getText().toString();
				if (data.equals("")) {
					return;
				}
				else if (data.equals("@close")) {
					if (tcpc.send(data)) {
						tcpc.closeSocket();
						tcpc = null;
					}
				}
				else {
					tcpc.send(data);
					tcpc.setStart();
					while (!tcpc.isExpired()) {
						String receive = tcpc.receive();
						if (receive != "") {
							return;
						}
					}
					TCP_Log_data.addElement("[Error]: Receive expired.");
					tcpc.closeSocket();
					tcpc = null;
				}
			}
		}
		else if (evt.getSource() == UDP_START)
		{
			if (udpc != null) {
				UDP_Log_data.addElement("[Error]: Client already started.");
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
    		String ip = UDP_IP.getText().toString();
    		udpc = new UDPClient(ip, port, UDP_Log_data);
    		if (!udpc.openSocket()) {
    			udpc = null;
    			return;
    		}
		}
		else if (evt.getSource() == UDP_STOP)
		{
			if (udpc == null) {
				UDP_Log_data.addElement("[Error]: Client not started.");
				return;
			}
			else
			{
				udpc.closeSocket();
				udpc = null;
			}
		}
		else if (evt.getSource() == UDP_SEND)
		{
			if (udpc == null) {
				UDP_Log_data.addElement("[Error]: Client not started.");
				return;
			}
			else
			{
				String data = UDP_MESSAGE.getText().toString();
				if (data.equals("")) {
					return;
				}
				else if (data.equals("@close")) {
					udpc.closeSocket();
					udpc = null;
				}
				else {
					udpc.send(data);
					udpc.setStart();
					while (!udpc.isExpired()) {
						String receive = udpc.receive();
						if (receive != "") {
							return;
						}
					}
					UDP_Log_data.addElement("[Error]: Receive expired.");
					udpc.closeSocket();
					udpc = null;
				}
			}
		}
	}
}