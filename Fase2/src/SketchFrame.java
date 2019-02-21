
// ---------------------------------
// SRSC Course, 15/16, 2nd Semester
// TP1 - Material
// Group Chat/Messaging System
// 1/Mar/2016
// ---------------------------------

import javax.swing.*;
import java.io.*;
import java.io.Writer.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.util.*;
import java.lang.*;

public class SketchFrame extends JFrame implements ActionListener {
	ChatClient chatObj;
	String logins = "My nick";
	// protected TextArea output;
	String chat;

	// private TextArea theChat;
	// Constructor
	public SketchFrame(String title) {

		setTitle(title);
		// setDefaultCloseOperation(EXIT_ON_CLOSE);
		setJMenuBar(menuBar);

		// Main menu:
		JMenu fileMenu = new JMenu("File");
		JMenu colorMenu = new JMenu("Color");
		JMenu helpMenu = new JMenu("Help");

		// Main menu Shortcuts:
		fileMenu.setMnemonic('F');
		colorMenu.setMnemonic('C');
		helpMenu.setMnemonic('H');

		newAction = new FileAction("New", KeyStroke.getKeyStroke('N', Event.CTRL_MASK));
		saveAction = new FileAction("Save", KeyStroke.getKeyStroke('S', Event.CTRL_MASK));
		exitAction = new FileAction("Exit", KeyStroke.getKeyStroke('E', Event.CTRL_MASK));
		loginAction = new FileAction("Login As", KeyStroke.getKeyStroke('L', Event.CTRL_MASK));

		// File pull down action menu:
		addMenuItem(fileMenu, loginAction);
		fileMenu.addSeparator();
		addMenuItem(fileMenu, newAction);
		fileMenu.addSeparator();
		addMenuItem(fileMenu, saveAction);
		fileMenu.addSeparator();
		addMenuItem(fileMenu, exitAction);
		fileMenu.addSeparator();

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
		aboutItem.addActionListener(this);
		helpMenu.add(aboutItem);

		// Initialize menu items:
		menuBar.add(fileMenu);
		menuBar.add(colorMenu);
		menuBar.add(helpMenu);

		enableEvents(AWTEvent.WINDOW_EVENT_MASK);
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
		KeyStroke keyStroke = (KeyStroke) action.getValue("juh");// action.ACCELERATOR_KEY);
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

		public void actionPerformed(ActionEvent e) // throws IOException
		{

			String name = (String) getValue(NAME);

			if (name.equals(newAction.getValue(NAME))) {

				SketchFrame window2;
				window2 = new SketchFrame("ChitChat Broadcast Messenger");
				Toolkit theKit = window2.getToolkit();
				Dimension wndSize = theKit.getScreenSize();

				window2.setBounds(wndSize.width / 4, wndSize.height / 4, wndSize.width / 2, wndSize.height / 2);
				window2.setVisible(true);
				String newHost = JOptionPane.showInputDialog("Enter HostName:");
				String newPort = JOptionPane.showInputDialog("Enter PortName:");

				/*
				 * Sketcher newObj = new Sketcher();
				 * newObj.fun(newHost,Integer.parseInt(newPort),window2);
				 */

			}

			else if (name.equals(saveAction.getValue(NAME))) {
				// saveOperation();
				// chat=theChat.getText();
				// theChat.setText(chat);
				/*
				 * Frame f =new SampleFrame("File Save Dialog");
				 * f.setVisible(false); FileDialog fd = new FileDialog(f,
				 * "File 	Dialog",FileDialog.SAVE); fd.setVisible(true);
				 */
				String file = JOptionPane.showInputDialog("Enter FileName:");
				file = file + ".txt";
				// FileWriter Fwriter=new FileWriter(file);
				// TextArea chat1=chatObj.func();
				// chat=chat1.getText();
				// System.out.println(chat);
				// Writer wObj=new Writer();
				// Fwriter.write(chat);

			} else if (name.equals(exitAction.getValue(NAME))) {
				dispose();
				System.exit(0);
			} else if (name.equals(loginAction.getValue(NAME))) {
				String logins = JOptionPane.showInputDialog("Enter Login name:");
				ChatHandler handle = new ChatHandler(logins);
				// handle.start();
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

	// Font color Action
	class FontColorAction extends AbstractAction {
		public FontColorAction(String name, Color color) {
			super(name);
			this.color = color;
		}

		public void actionPerformed(ActionEvent e) {
			elementColor = color;
		}

		private Color color;
	}

	// About menu event:
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == aboutItem) {
			JOptionPane.showMessageDialog(this, "ChitChat Broadcast Messenger\nCopyright Fatima Ahmed 2004",
					"About ChitChat Broadcast Messenger", JOptionPane.INFORMATION_MESSAGE);
		}
	}

	// Color Action object
	private FileAction newAction, saveAction, exitAction, loginAction;
	private Color elementColor;
	private Font font;
	private ColorAction redAction, yellowAction, greenAction, blueAction, magentaAction, cyanAction, blackAction,
			grayAction, darkGrayAction, pinkAction, orangeAction, whiteAction;

	void FontFunc(TextArea output) {
		// FontAction FontArialAction = new FontAction("SansSerif",output);
		Font f = new Font("SansSerif", Font.BOLD, 18);
		output.setFont(f);
		// output.setForeground(Color.red);
	}
	/*
	 * public class FWriter extends Writer { FWriter name; public FWriter(File
	 * file) throws IOException { this.name=file; } }
	 */

}
// End of class SketchFrame

class SampleFrame extends Frame {
	SampleFrame(String title) {
		super(title);
		MyWindowAdapter adapter = new MyWindowAdapter(this);
		addWindowListener(adapter);
	}
}

class MyWindowAdapter extends WindowAdapter {
	SampleFrame sampleFrame;

	public MyWindowAdapter(SampleFrame sampleFrame) {
		this.sampleFrame = sampleFrame;
	}

	public void WindowClosing(WindowEvent we) {
		sampleFrame.setVisible(false);
	}
}