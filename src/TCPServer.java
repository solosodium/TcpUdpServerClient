import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.LinkedList;
import java.util.Random;

import javax.swing.DefaultListModel;
import javax.swing.JList;

public class TCPServer extends Thread
{
	public int mPort;										// server port
	public String mAddr;									// server address
	private ServerSocket mListenSocket;						// listening socket
	private LinkedList <MyActiveThread> mActiveSockets;		// threads
	private int mNoConnection;								// thread counter
	
	private DefaultListModel mLogData;						// GUI log data
	
	private boolean listen_alive;
	
	// class constructor
	TCPServer (int port, DefaultListModel log_data)
	{
		mPort = port;
		mActiveSockets = new LinkedList <MyActiveThread> ();
		mNoConnection = 0;		// no thread
		mLogData = log_data;
		listen_alive = true;
	}
	
	// function to open server listen socket
	public boolean openListenSocket ()
	{
		try {
			mListenSocket = new ServerSocket(mPort);
			mAddr = mListenSocket.getLocalSocketAddress().toString();
			System.out.println("[TCP Server]: Server online.");
			System.out.println("[TCP Server]: Server info = " + mAddr + ".");
			mLogData.addElement("[Log]: Server online.");
			mLogData.addElement("[Log]: Server info = " + mAddr + ".");
			return true;
		}
		catch (Exception e) {
			System.out.println("[TCP Server]: Port " + mPort + " is used, choose another one.");
			mLogData.addElement("[Log]: Port " + mPort + " occupied, trying random one.");
			Random generator = new Random(new Date().getTime());
			mPort = 10000 + generator.nextInt(30000);		// generate a port number between 10000 - 40000
			return false;
		}
	}
	
	// function to close socket
	public boolean closeSocket ()
	{
		try {
			listen_alive = false;
			for (int k=0; k<mNoConnection; k++)
			{
				mActiveSockets.get(k).closeSocket();
			}
			mListenSocket.close();
			System.out.println("[TCP Server]: Server shutdown.");
			mLogData.addElement("[Log]: Server shutdown.");
			return true;
		}
		catch (Exception e) {
			e.printStackTrace();
			System.out.println("[TCP Server]: Server shutdown error.");
			mLogData.addElement("[Error]: Server shutdown error.");
			return false;
		}
	}
	
	// function to handle new incoming connection
	public boolean newConnection ()
	{
		try
		{
			Socket temp_socket = mListenSocket.accept();				// get incoming client(s)
			if (temp_socket != null)
			{
				mNoConnection += 1;
				mActiveSockets.add(new MyActiveThread(temp_socket, mNoConnection, mLogData));
				mActiveSockets.getLast().start();						// start the added thread
				System.out.println("[TCP Server]: New thread created, " + "sequence = " + mNoConnection + ".");
				mLogData.addElement("[Log]: Connection thread " + mNoConnection + " created.");
				return true;
			}
			else
			{
				return false;
			}
		}
		catch (Exception e)
		{
			return false;
		}
	}
	
	// function for the thread
	@Override
	public void run ()
	{
		while (listen_alive)
		{
			newConnection();
		}
	}		
	
	/////////////////////////////////////////////////////
	// public holder class for each TCP session thread //
	/////////////////////////////////////////////////////
	public class MyActiveThread extends Thread
	{
		// private parameter
		private int mID;					// thread ID
		
		private Socket mSocket;				// socket
		private BufferedInputStream bis;	// input holder
		private BufferedOutputStream bos;	// output holder
		
		private final int MAXLENGTH = 1024; // maximum packet length
		
		public JList mLog;					// GUI log object
		public DefaultListModel mLogData;	// GUI log data
		
		private String remoteAddr;
		
		private long start;					// start time
		final private long ttl = 1000;	// socket live time
		private boolean alive;				// thread indicator
		
		MyActiveThread (Socket s, int id, DefaultListModel log_data) {
			mID = id;
			mSocket = s;
			try  {
				bis = new BufferedInputStream(mSocket.getInputStream());	// input
				bos = new BufferedOutputStream(mSocket.getOutputStream());	// output
				remoteAddr = mSocket.getRemoteSocketAddress().toString();
			}
			catch (Exception e) {
				e.printStackTrace();
				System.out.println("[Thread " + mID + "]: Socket streaming failed.");
				mLogData.addElement("[Error]: Thread " + mID + " Get socket stream error.");
			}
			
			mLogData = log_data;
			alive = true;
		}
		
		// function to receive data
		public String receive ()
		{
			String data = "";
			try
			{
				if (!(bis.available()>0))
				{
					byte [] input_buffer = new byte [MAXLENGTH];
					int incomingLength = bis.read(input_buffer);
					byte [] input = new byte [incomingLength];
					System.arraycopy(input_buffer, 0, input, 0, incomingLength);
					data = new String(input);
					System.out.println("[Thread " + mID + "]: Received /" + data + "/ from " + remoteAddr + ".");
					mLogData.addElement("[Log]: Thread " + mID + " received |" + data + "| from " + remoteAddr + ".");
				}
				return data;
			}
			catch (Exception e)
			{
				return "";
			}
		}
		
		// function to send data
		public boolean send (String data)
		{
			try
			{
				byte [] output = data.getBytes();
				bos.write(output);
				bos.flush();
				System.out.println("[Thread " + mID + "]: Send /" + data + "/ to " + remoteAddr + ".");
				mLogData.addElement("[Log]: Thread " + mID + " send |" + data + "| to " + remoteAddr + ".");
				return true;
			}
			catch (Exception e)
			{
				e.printStackTrace();
				System.out.println("[Thread " + mID + "]: Send data error.");
				mLogData.addElement("[Error]: Thread " + mID + " Send data error.");
				return false;
			}
		}
		
		// function to close the socket
		public void closeSocket ()
		{
			try{
				alive = false;
				mSocket.close();
				System.out.println("[Thread " + mID + "]: Thread is closed.");
				mLogData.addElement("[Log]: Thread " + mID + " closed.");
			}
			catch (Exception e)
			{
				e.printStackTrace();
				System.out.println("[Thread " + mID + "]: Close socekt error.");
				mLogData.addElement("[Error]: Thread " + mID + " Close socket error.");
			}
		}
		
		// function to register start time
		public void setStart ()
		{
			start = new Date().getTime();
		}
		
		// function to check expire
		public boolean isExpired ()
		{
			long now =  new Date().getTime();
			return (now - start > ttl) ? true : false;
		}
		
		// function for the thread
		@Override
		public void run ()
		{
			while (alive)
			{
				String data = receive();
				if (data.equals("@close")) {
					setStart();
					closeSocket();
					break;
				}
				else if (!data.equals("")) {
					setStart();
					send(reverseOrder(data));
				}
			}
		}
		
		// function to reverse order 
		public String reverseOrder (String input)
		{
			String out = "";
			int length = input.length();
			for (int k=1; k<=length; k++) {
				out = out + input.charAt(length-k);
			}
			return out;
		}
	}
}