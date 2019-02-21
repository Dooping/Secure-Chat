
// ---------------------------------
// SRSC Course, 15/16, 2nd Semester
// TP1 - Material
// Group Chat/Messaging System
// 1/Mar/2016
// ---------------------------------

import java.io.*;
import java.net.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

public class MCRServer extends JFrame {
	// implements ActionListener{
	// static ChatClient cObj;
	// static String yname;
	static String yourname;

	public MCRServer(String title) {

		output = new TextArea(15, 40);
		output.setEditable(false);
		output.setFont(f);
		output.setForeground(Color.blue);
		this.yourname = "Mickey Mouse";

		setTitle(title);
		setJMenuBar(menuBar);
		JMenu fileMenu = new JMenu("File");
		JMenu colorMenu = new JMenu("Color");
		JMenu helpMenu = new JMenu("Help");

		// Main menu Shortcuts:
		fileMenu.setMnemonic('F');
		colorMenu.setMnemonic('C');
		helpMenu.setMnemonic('H');

		addMenuItem(fileMenu, exitAction = new FileAction("Exit"));

		// Color pulldown menu:

		// Set Background Colors:
		addMenuItem(colorMenu, redAction = new ColorAction("Red BackGround", Color.red));
		addMenuItem(colorMenu, yellowAction = new ColorAction("Yellow BackGround", Color.yellow));
		addMenuItem(colorMenu, greenAction = new ColorAction("Green BackGround", Color.green));
		addMenuItem(colorMenu, blueAction = new ColorAction("Blue BackGround", Color.blue));
		addMenuItem(colorMenu, magentaAction = new ColorAction("Magenta BackGround", Color.magenta));
		addMenuItem(colorMenu, cyanAction = new ColorAction("Cyan BackGround", Color.cyan));
		addMenuItem(colorMenu, blackAction = new ColorAction("Black BackGround", Color.black));
		addMenuItem(colorMenu, grayAction = new ColorAction("Gray BackGround", Color.gray));
		addMenuItem(colorMenu, darkGrayAction = new ColorAction("DarkGray BackGround", Color.darkGray));
		addMenuItem(colorMenu, pinkAction = new ColorAction("Pink BackGround", Color.pink));
		addMenuItem(colorMenu, orangeAction = new ColorAction("Orange BackGround", Color.orange));
		addMenuItem(colorMenu, whiteAction = new ColorAction("White BackGround", Color.white));

		// About Dialog init:
		aboutItem = new JMenuItem("About");
		// aboutItem.addActionListener((ActionListener)this);
		helpMenu.add(aboutItem);
		addMenuItem(helpMenu, aboutAction = new AboutAction("About"));

		// Initialize menu items:
		menuBar.add(fileMenu);
		menuBar.add(colorMenu);
		menuBar.add(helpMenu);

		enableEvents(AWTEvent.WINDOW_EVENT_MASK);

	}

	class AboutAction extends AbstractAction {
		JOptionPane opt;
		String name;

		public AboutAction(String Name) {
			this.name = Name;
		}

		// About menu event:
		public void actionPerformed(ActionEvent ae) {
			// if(ae.getSource() == aboutAction)
			{
				JOptionPane.showMessageDialog(opt, "MSCServer Broadcast 1-N Messenger\nCopyright SRSC20152016",
						"About MSCServer 1-N Broadcast Messenger", JOptionPane.INFORMATION_MESSAGE);
			}
		}
	}

	protected void processWindowEvent(WindowEvent e) {
		if (e.getID() == WindowEvent.WINDOW_CLOSING) {
			dispose();
			System.exit(0);
		}
		super.processWindowEvent(e);
	}

	private JMenuItem addMenuItem(JMenu menu, Action action) {
		JMenuItem item = menu.add(action);
		KeyStroke keyStroke = (KeyStroke) action.getValue("j"); // action.ACCELERATOR_KEY
																// dont worry;
		if (keyStroke != null)
			item.setAccelerator(keyStroke);
		return item;
	}

	// Main menu init:
	private JMenuBar menuBar = new JMenuBar();
	// About menu init:
	private JMenuItem aboutItem;

	// File Menu Action:
	class FileAction extends AbstractAction {
		public FileAction(String NAME, KeyStroke keyStroke) {
			super(NAME);
		}

		String name;

		public FileAction(String name) {
			super(name);
			this.name = name;
		}

		public void actionPerformed(ActionEvent e) {
			String name = (String) getValue(NAME);
			if (name.equals(exitAction.getValue(NAME))) {
				dispose();
				System.exit(0);
			}
		}
	}

	// Background color Action
	// Inner Class definition
	class ColorAction extends AbstractAction {
		public ColorAction(String name, Color color) {
			super(name);
			this.color = color;
		}

		public void actionPerformed(ActionEvent e) {
			elementColor = color;
			getContentPane().setBackground(color);
		}

		private Color color;
	}

	private AboutAction aboutAction;
	private FileAction exitAction;
	private Color elementColor;
	private ColorAction redAction, yellowAction, greenAction, blueAction, magentaAction, cyanAction, blackAction,
			grayAction, darkGrayAction, pinkAction, orangeAction, whiteAction;

	public static TextArea output;
	Font f = new Font("SansSerif", Font.PLAIN, 16);

	public static void main(String args[]) throws IOException {

		int port = 9999;
		if (args.length == 1) {
			try {
				port = Integer.parseInt(args[0]);
			} catch (NumberFormatException e) {
				System.err.println("No Port... I will use port 9999");
			}
		}

		System.out.println("MCRServer started,ADDRESS= "+InetAddress.getLocalHost().getHostAddress()+" PORT= " + port);

		MCRServer ServerWindow = new MCRServer("MCRServer sent Messenger: Server Window");
		Toolkit theKit = ServerWindow.getToolkit();
		Dimension wndSize = theKit.getScreenSize();

		ServerWindow.setBounds(wndSize.width / 4, wndSize.height / 4, wndSize.width / 2, wndSize.height / 2);
		ServerWindow.setVisible(true);
		ServerWindow.getContentPane().add("North", output);
		ServerWindow.getContentPane().setLayout(new FlowLayout(FlowLayout.CENTER));

		ServerWindow.pack();

		String logins;
		ServerSocket server = new ServerSocket(port);
		while (true) {
			Socket client = server.accept();

			///////////////////////////////////
			// logins = JOptionPane.showInputDialog("Enter Login name:");
			// yname=cObj.Fun();
			// System.out.println ("Accepted from " + client.getInetAddress ()+"
			/////////////////////////////////// with name "+logins);

			ChatHandler handler = new ChatHandler(client, yourname);
			handler.start();
			output.append("\n Accepted from " + client.getInetAddress() + "\n");

		}
	}

}
