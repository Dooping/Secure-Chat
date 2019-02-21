
// ---------------------------------
// SRSC Course, 15/16, 2nd Semester
// TP1 - Material
// Group Chat/Messaging System
// 1/Mar/2016
// ---------------------------------

import java.io.*;
import java.nio.ByteBuffer;
import java.util.Random;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;


// ChatClient code:
public class ChatClient implements Runnable, WindowListener, ActionListener, ListSelectionListener {
	protected String host;
	protected int port;

	public TextArea output;
	public Config config;
	protected TextField input;
	protected TextArea temp;
	SketchFrame window;
	String yourname;
	// static ChatHandler hobj;

	GraphicsEnvironment e = GraphicsEnvironment.getLocalGraphicsEnvironment();
	String[] fontNames = e.getAvailableFontFamilyNames();
	private JList fontList = new JList(fontNames);

	String[] colorNames = { "Red", "Blue", "Green", "Black", "Orange", "Yellow", "Pink", "Gray", "DarkGray", "Cyan",
			"White" };
	Color[] fontcolor = { Color.red, Color.blue, Color.green, Color.black, Color.orange, Color.yellow, Color.pink,
			Color.gray, Color.darkGray, Color.cyan, Color.white };
	// private JList fontcolorList=new JList (fontcolor);
	private JList fontcolorList = new JList(colorNames);

	Font f = new Font("SansSerif", Font.PLAIN, 16);
	Color c = new Color(0);

	public ChatClient() {

	}

	public ChatClient(String host, int port, SketchFrame window) {
		this.host = host;
		this.port = port;
		this.yourname = JOptionPane.showInputDialog("Enter Login name:");

		window.setSize(100, 100);
		window.setBackground(Color.cyan);
		output = new TextArea(5, 30);
		output.setEditable(false);
		temp = new TextArea(5, 30);
		temp.setVisible(false);
		input = new TextField(30);
		input.setFont(f);
		input.setForeground(Color.blue);
		output.setFont(f);
		output.setForeground(Color.blue);
		input.addActionListener(this);

		GridBagConstraints constraints = new GridBagConstraints();
		window.getContentPane().setLayout(new FlowLayout(FlowLayout.CENTER));
		window.getContentPane().setLayout(new BorderLayout(40, 50));

		window.getContentPane().add(temp, BorderLayout.NORTH);
		constraints.insets = new Insets(10, 10, 10, 10);
		window.getContentPane().add(output, BorderLayout.CENTER);
		window.getContentPane().add(input, BorderLayout.SOUTH);

		fontList.setValueIsAdjusting(true);
		fontList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		fontList.setSelectedValue(f.getFamily(), true);
		fontList.addListSelectionListener(this);
		JScrollPane chooseFont = new JScrollPane(fontList);
		chooseFont.setMinimumSize(new Dimension(15, 20));
		chooseFont.setVisible(true);
		window.getContentPane().add(chooseFont, BorderLayout.WEST);

		fontcolorList.setValueIsAdjusting(true);
		fontcolorList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		fontcolorList.setSelectedValue(Color.blue, true);
		fontcolorList.addListSelectionListener(this);
		JScrollPane chooseFontColor = new JScrollPane(fontcolorList);
		chooseFontColor.setMinimumSize(new Dimension(15, 20));
		chooseFontColor.setVisible(true);
		window.getContentPane().add(chooseFontColor, BorderLayout.EAST);

		window.pack();
	}

	public void valueChanged(ListSelectionEvent e) {
		Object st;
		if (!e.getValueIsAdjusting()) {
			f = new Font((String) fontList.getSelectedValue(), Font.PLAIN, 16);
			input.setFont(f);
			output.setFont(f);
			st = fontcolorList.getSelectedValue();
			if (st == "Red") {
				input.setForeground(Color.red);
				output.setForeground(Color.red);
				// input.setForeground((Color)fontcolorList.getSelectedValue());
				// output.setForeground((Color)fontcolorList.getSelectedValue());
			}
			if (st == "Blue") {
				input.setForeground(Color.blue);
				output.setForeground(Color.blue);
			}
			if (st == "Green") {
				input.setForeground(Color.green);
				output.setForeground(Color.green);
			}
			if (st == "Black") {
				input.setForeground(Color.black);
				output.setForeground(Color.black);
			}
			if (st == "Orange") {
				input.setForeground(Color.orange);
				output.setForeground(Color.orange);
			}
			if (st == "Yellow") {
				input.setForeground(Color.yellow);
				output.setForeground(Color.yellow);
			}
			if (st == "Pink") {
				input.setForeground(Color.pink);
				output.setForeground(Color.pink);
			}
			if (st == "Gray") {
				input.setForeground(Color.gray);
				output.setForeground(Color.gray);
			}
			if (st == "DarkGray") {
				input.setForeground(Color.darkGray);
				output.setForeground(Color.darkGray);
			}
			if (st == "Cyan") {
				input.setForeground(Color.cyan);
				output.setForeground(Color.cyan);
			}
			if (st == "White") {
				input.setForeground(Color.white);
				output.setForeground(Color.white);
			}

		}
	}
	/*
	 * public String Fun() { return (yourname); }
	 */

	public TextArea func() {
		return (output);
	}

	public Insets getInsets() {
		return new Insets(10, 10, 10, 10);
	}

	//protected DataInputStream dataIn;
	//protected DataOutputStream dataOut;
	protected Thread listener;
	protected SecureTCPSocket socket;

	public synchronized void start() throws IOException {
		if (listener == null) {
			// output.append(yourname + " has logged on\n");
			// dataOut.writeUTF (yourname+" has logged off ");
			// dataOut.flush ();
			File configs = new File("user",yourname+".txt");
			config = new Config(configs);
			SecureTCPSocket ssoSocket = new SecureTCPSocket("localhost",8080,config);
			
			Random r = new Random();
			int nonce = r.nextInt(1000);
			
			String message = yourname +"," + host + "_" + port+ "," + nonce;
			try {
				ssoSocket.writeNotEncrypted(message.getBytes(), (byte)10);
				byte[] ssoAnswer = ssoSocket.readNotString();
				ByteBuffer buffer = ByteBuffer.wrap(ssoAnswer);
				int nonceAnswer = buffer.getInt();
				if(nonceAnswer == nonce + 1){
					int configLength = buffer.getInt();
					byte[] sessionsConfigsBytes = new byte[configLength];
					buffer.get(sessionsConfigsBytes);
					int sessionLength = buffer.getInt();
					byte[] sessionsByte= new byte[sessionLength];
					buffer.get(sessionsByte);
					String session = new String(sessionsByte);

					File folder = new File("sessionUserConfigs");
					String[] entries = folder.list();
					if (entries != null) {
						for (String s : entries) {
							File currentFile = new File(folder.getPath(), s);
							currentFile.delete();
						}
					}
					folder.delete();
					folder.mkdir();
					FileOutputStream out =  new FileOutputStream(new File("sessionUserConfigs","sessionConfigs"));
					out.write(sessionsConfigsBytes);
					out.close();
					
					File sessionConfigsFile = new File("sessionUserConfigs","sessionConfigs");
					Config sessionConfigs = new Config(sessionConfigsFile);
					
					String[] sessionSplited = session.split("_");
					String host =  sessionSplited[0];
					int port =  Integer.parseInt(sessionSplited[1]);

					socket = new SecureTCPSocket(host,port,sessionConfigs);
					socket.setUsername(yourname);
				}
				else throw new InvalidNonceException("invalid answer");
				
				

			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}			
			
			//socket = new SecureTCPSocket(host, port, config);
			try {
				//dataIn = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
				//dataOut = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
				socket.write(yourname + " has logged on\n ",(byte)20);
				// dataOut.flush ();

			} catch (IOException ex) {
				socket.close();

				throw ex;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			listener = new Thread(this);
			listener.start();
			// window.setVisible (true);
		}
	}

	public synchronized void stop() throws IOException {
		// window.setVisible (false);
		if (listener != null) {
			listener.interrupt();
			listener = null;
			socket.close();
		}
	}

	public void run() {
		try {
			while (!Thread.interrupted()) {
				try{
					String line = socket.read();

					output.append(line + "\n");
				} catch (InvalidNonceException e){
					System.err.println("Invalid r1");
				}
				
				// String message=yourname +" says:\n ";

				// output.append ("\n"+message + line + "\n");
			}
		} catch (IOException ex) {
			handleIOException(ex);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected synchronized void handleIOException(IOException ex) {
		if (listener != null) {
			output.append(ex + "\n");
			input.setVisible(false);
			window.validate();
			if (listener != Thread.currentThread())
				listener.interrupt();
			listener = null;
			try {
				socket.close();

			} catch (IOException ignored) {
			}
		}
	}

	public void windowOpened(WindowEvent event) {
		input.requestFocus();
	}

	public void windowClosing(WindowEvent event) {
		try {
			stop();

		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public void windowClosed(WindowEvent event) {
	}

	public void windowIconified(WindowEvent event) {
	}

	public void windowDeiconified(WindowEvent event) {
	}

	public void windowActivated(WindowEvent event) {
	}

	public void windowDeactivated(WindowEvent event) {
	}

	public void actionPerformed(ActionEvent event) {
		try {
			input.selectAll();
			String message = yourname + " says:\n " + event.getActionCommand();
			socket.write(message,(byte)20);
		} catch (IOException ex) {
			handleIOException(ex);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}// End of chatClient code
