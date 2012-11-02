import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Date;
import java.util.Random;

import javax.swing.DefaultListModel;

class UDPServer extends Thread
{
	// private parameters
	public int mPort;
	public String mAddr;
	private DatagramSocket mSocket;
	private InetAddress remoteAddr;
	private int remotePort;
	
	private final int MAXLENGTH = 1024;
	
	private boolean alive;
	
	private DefaultListModel mLogData;
	
	// class constructor
	UDPServer (int port, DefaultListModel log)
	{
		mPort = port;
	    remoteAddr = null;		// initialize to something not useful
	    remotePort = -1;		// initialize to something not useful
	    mLogData = log;
	    alive = true;
	}
	
	// function to open socket
	public boolean openSocket ()
	{
		try {
			mSocket = new DatagramSocket(mPort);
			mAddr = mSocket.getLocalAddress().toString();
			System.out.println("[UDP Server]: Server online.");
			System.out.println("[UDP Server]: IP = " + mAddr + ", port = " + mPort);
			mLogData.addElement("[Log]: Server online.");
			mLogData.addElement("[Log]: Server info = " + mAddr + " : " + mPort + ".");
			return true;
		}
		catch (Exception e) {
			System.out.println("[UDP Server]: port " + mPort + " is used, choose another one.");
			mLogData.addElement("[Log]: Port " + mPort + " occupied, trying random one.");
			Random generator = new Random(new Date().getTime());
			mPort = 10000 + generator.nextInt(30000);
			return false;
		}
	}
	
	// function to close socket
	public boolean closeSocket ()
	{
		try{
			alive = false;
			mSocket.close();
			System.out.println("[Log]: Server shutdown.");
			mLogData.addElement("[Log]: Server shutdown.");
			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			System.out.println("[Error]: Server shutdown error.");
			mLogData.addElement("[Error]: Server shutdown error.");
			return false;
		}
	}
	
	// function to receive data
	public String receive ()
	{
		String data = "";
		byte[] receiveData = new byte [MAXLENGTH];
		DatagramPacket mReceive = new DatagramPacket(receiveData, receiveData.length);
		try {
			mSocket.receive(mReceive);
			remoteAddr = mReceive.getAddress();
			remotePort = mReceive.getPort();
			data = new String(mReceive.getData());
			System.out.println("[UDP Server]: receive /" + data + "/ from " + remoteAddr.toString() + " " + remotePort);
			mLogData.addElement("[Log]: Received | " + data + " | from "  + remoteAddr.toString() + " : " + remotePort);
		}
		catch (Exception e) {
			return "";
		}
		return data;
	}
	
	// function to send data
	public boolean send (String data)
	{
		if (remoteAddr == null || remotePort == -1) {
			System.out.println("[UDP Server]: fail to send data.");
			return false;
		}
		byte [] sendData = data.getBytes();
		DatagramPacket mSend = new DatagramPacket(sendData, sendData.length, remoteAddr, remotePort);
		try {
			mSocket.send(mSend);
			System.out.println("[UDP Server]: send /" + data + "/ to " + remoteAddr.toString() + " " + remotePort);
			mLogData.addElement("[Log]: Sending |" + data + "| to " + remoteAddr.toString() + " : " + remotePort);
		}
		catch (Exception e) {
			System.out.println("[UDP Server]: fail to send data.");
			mLogData.addElement("[Error]: Send data error.");
			return false;
		}
		return true;
	}
	
	// function to reverse the String
	public String reverseOrder (String input)
	{
		String out = "";
		int length = input.length();
		for (int k=1; k<=length; k++) {
			out = out + input.charAt(length-k);
		}
		return out;
	}
	
	// thread function
	@Override
	public void run ()
	{
		while (alive)
		{
			String data = receive();
			if (data != "") {
				send(reverseOrder(data));
			}
		}
	}
}