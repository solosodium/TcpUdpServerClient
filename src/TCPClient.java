import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.net.Socket;
import java.util.Date;

import javax.swing.DefaultListModel;

public class TCPClient
{
	public String mRemoteAddr;				// server address
	public int mPort;						// server port
	
	private Socket mSocket;					// client socket
	private BufferedOutputStream bos;		// buffered output
	private BufferedInputStream bis;		// buffered input
	
	private final int MAXLENGTH = 1024;		// maximum packet length
	
	private long start;						// start time
	private long ttl = 1000;				// time to live
	
	private DefaultListModel mLogData;		// GUI log data
	
	TCPClient (String remoteAddr, int r_port, DefaultListModel log)
	{
		mRemoteAddr = remoteAddr;
		mPort = r_port;
		mLogData = log;
	}	
	
	// function to open socket
	public boolean openSocket ()
	{
		try {
			mSocket = new Socket(mRemoteAddr, mPort);
			bos = new BufferedOutputStream(mSocket.getOutputStream());
			bis = new BufferedInputStream(mSocket.getInputStream());
			System.out.println("[TCP Client]: Connected to server.");
			System.out.println("[TCP Client]: Remote IP address = " + mRemoteAddr + ", port = " + mPort + ".");
			mLogData.addElement("[Log]: Connected to server.");
			mLogData.addElement("[Log]: Remote IP address = " + mRemoteAddr + ", port = " + mPort + ".");
			return true;
		}
		catch (Exception e) {
			System.out.println("[TCP Client]: Failed to connect to the server, please try again.");
			mLogData.addElement("[Error]: Failed to connect to the server, please try again.");
			return false;
		}
	}
	
	// function to close the socket
	public boolean closeSocket ()
	{
		try{
			mSocket.close();
			System.out.println("[TCP Client]: Socket closed.");
			mLogData.addElement("[Log]: Socket cloesed.");
			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			System.out.println("[TCP Client]: Close socekt error.");
			mLogData.addElement("[Error]: Close socket error.");
			return false;
		}
	}
	
	// function to receive data
	public String receive ()
	{
		String data = "";
		try
		{
			if (bis.available() > 0)
			{
				byte [] input_buffer = new byte [MAXLENGTH];
				int incomingLength = bis.read(input_buffer);
				byte [] input = new byte [incomingLength];
				System.arraycopy(input_buffer, 0, input, 0, incomingLength);
				data = new String(input);
				System.out.println("[TCP Client]: Received /" + data + "/.");
				mLogData.addElement("[Log]: Received |" + data + "|.");
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
		try{
			byte [] output = data.getBytes();
			bos.write(output);
			bos.flush();
			System.out.println("[TCP Client]: Sending /" + data + "/.");
			mLogData.addElement("[Log]: Sending |" + data + "|.");
			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			System.out.println("[TCP Client]: Send data error.");
			mLogData.addElement("[Error]: Send data error.");
			return false;
		}
	}	
	
	public void setStart ()
	{
		start = new Date().getTime();
	}
	
	
	public boolean isExpired ()
	{
		long now =  new Date().getTime();
		return (now - start > ttl) ? true : false;
	}
}