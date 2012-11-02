import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Date;

import javax.swing.DefaultListModel;

class UDPClient
{
	// private parameters
	private DatagramSocket mSocket;
	public InetAddress remoteAddr;
	public int remotePort;
	
	final int MAXLENGTH = 1024;
	
	private long start;
	final long ttl = 1000;
	
	private DefaultListModel mLogData;
	
	// class constructor
	UDPClient (String addr, int port, DefaultListModel log)
	{
		try {
			remoteAddr = InetAddress.getByName(addr);
		}
		catch (Exception e) {
			System.out.println("[UDP Client]: Fail to get address.");
		}
		remotePort = port;
	    
	    mLogData = log;
	}
	
	// function to open socket
	public boolean openSocket () {
		try {
			mSocket = new DatagramSocket(); // no need port
			System.out.println("[UDP Client]: Connected to server.");
			System.out.println("[UDP Client]: Remote IP address = " + remoteAddr.toString() + ", port = " + remotePort + ".");
			mLogData.addElement("[Log]: Connected to server.");
			mLogData.addElement("[Log]: Remote IP address = " + remoteAddr + ", port = " + remotePort + ".");
			return true;
		}
		catch (Exception e) {
			System.out.println("[UDP Client]:  Failed to connect to the server, try again.");
			mLogData.addElement("[Error]: Failed to connect to the server, please try again.");
		}
		return false;
	}
	
	// function to close socket
	public boolean closeSocket ()
	{
		try{
			mSocket.close();
			System.out.println("[UDP Client]: Socket closed.");
			mLogData.addElement("[Log]: Socket cloesed.");
			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			System.out.println("[UDP Client]: Close socekt error.");
			mLogData.addElement("[Error]: Close socket error.");
			return false;
		}
	}
	
	// function to receive data
	public String receive ()
	{
		String data = "";
		byte[] receiveData = new byte[MAXLENGTH];
		DatagramPacket mReceive = new DatagramPacket(receiveData, receiveData.length);
		try {
			mSocket.receive(mReceive);
			remoteAddr = mReceive.getAddress();
			remotePort = mReceive.getPort();
			data = new String(mReceive.getData());
			System.out.println("[UDP Client]: Receive from " + remoteAddr.toString() + ", data = " + data + ".");
			mLogData.addElement("[Log]: Received |" + data + "|.");
		}
		catch (Exception e) {
			return "";
		}
		return data;
	}
	
	// function to send data
	public boolean send (String data)
	{
	    byte[] sendData = data.getBytes();
		DatagramPacket mSend = new DatagramPacket(sendData, sendData.length, remoteAddr, remotePort);
		try {
			mSocket.send(mSend);
			System.out.println("[UDP Client]: Send to " + remoteAddr.toString() + ", data = " + data + ".");
			mLogData.addElement("[Log]: Sending |" + data + "|.");
		}
		catch (Exception e) {
			System.out.println("[UDP Client]: fail to send data.");
			mLogData.addElement("[Log]: Send data error");
			return false;
		}
		return true;
	}
	
	// function to get start time
	public void setStart () {
		start = new Date().getTime();
	}
	
	// function to check whether expire
	public boolean isExpired () {
		long now =  new Date().getTime();
		return (now-start>ttl)? true : false;
	}
}