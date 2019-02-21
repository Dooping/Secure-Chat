
// ---------------------------------
// SRSC Course, 15/16, 2nd Semester
// TP1 - Material
// Group Chat/Messaging System
// 1/Mar/2016
// ---------------------------------

import javax.swing.*;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.*;

public class ChatHandler implements Runnable {
	
	public static final short PROTOCOL_VERSION = 1;
	public static final short PHASE = 1;
	
	protected Socket socket;
	public String logins;
	MCRServer obj;

	public ChatHandler(String login) {
		this.logins = login;
	}

	public ChatHandler(Socket socket, String logins) {
		this.socket = socket;
		this.logins = logins;
	}

	protected DataInputStream dataIn;
	protected DataOutputStream dataOut;
	protected Thread listener;

	public synchronized void start() {
		if (listener == null) {
			try {
				dataIn = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
				dataOut = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));

				// String message=yourname+" has logged on\n";
				// broadcastNew(message);

				listener = new Thread(this);
				listener.start();

			} catch (IOException ignored) {
			}

		}
	}

	public synchronized void stop() {
		if (listener != null) {
			try {
				if (listener != Thread.currentThread())
					listener.interrupt();
				listener = null;
				dataOut.close();
				String msg = "\n" + InetAddress.getLocalHost() + "has logged off.\n";
				broadcast(Base64.encode(addHeader(msg.getBytes(), (byte)21)));
				obj.output.append("\n" + InetAddress.getLocalHost() + " has logged off.\n");
			} catch (IOException ignored) {
			}
		}
	}

	protected static Vector handlers = new Vector();

	public void run() {
		try {
			handlers.addElement(this);
			while (!Thread.interrupted()) {
				String message1 = dataIn.readUTF();
				String message = message1;
				broadcast(message);
			}
		} catch (EOFException ignored) {
		} catch (IOException ex) {
			if (listener == Thread.currentThread())
				ex.printStackTrace();
		} finally {
			handlers.removeElement(this);
		}
		stop();
	}

	protected void broadcast(String message) {
		synchronized (handlers) {
			Enumeration enume = handlers.elements();
			while (enume.hasMoreElements()) {
				ChatHandler handler = (ChatHandler) enume.nextElement();
				try {
					handler.dataOut.writeUTF(message);
					handler.dataOut.flush();
				} catch (IOException ex) {
					handler.stop();
				}
			}
		}
	}

	protected void broadcastNew(String message) {
		synchronized (handlers) {
			Enumeration enume = handlers.elements();
			while (enume.hasMoreElements()) {
				ChatHandler handler = (ChatHandler) enume.nextElement();
				try {
					handler.dataOut.writeUTF(message);
					handler.dataOut.flush();
				} catch (IOException ex) {
					handler.stop();
				}
			}
		}
	}
	
	protected byte[] getMessage(byte[] message){
		ByteBuffer b = ByteBuffer.wrap(message);
		b.getShort();
		b.getShort();
		int size = b.getInt();
		b.get();
		byte[] data = new byte[size];
		b.get(data);
		return data;
	}
	
	protected byte[] addHeader(byte[] message, byte type){
		ByteBuffer b = ByteBuffer.allocate(message.length + 9);
		b.putShort(PHASE);
		b.putShort(PROTOCOL_VERSION);
		b.putInt(message.length);
		b.put(type);
		b.put(message, 0, message.length);
		return b.array();
	}
}
