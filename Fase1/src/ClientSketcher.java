
// ---------------------------------
// SRSC Course, 15/16, 2nd Semester
// TP1 - Material
// Group Chat/Messaging System
// 1/Mar/2016
// ---------------------------------

import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;

public class ClientSketcher {
	static SketchFrame window;

	public static void main(String[] args) throws IOException {
		// SketchFrame code:
		window = new SketchFrame("ChitChat Broadcast Messenger");
		Toolkit theKit = window.getToolkit();
		Dimension wndSize = theKit.getScreenSize();

		window.setBounds(wndSize.width / 4, wndSize.height / 4, wndSize.width / 2, wndSize.height / 2);
		window.setVisible(true);

		if ((args.length != 1) || (args[0].indexOf(':') < 0))
			throw new IllegalArgumentException("Syntax: ChatClient <host>:<port>");

		int idx = args[0].indexOf(':');
		String host = args[0].substring(0, idx);
		int port = Integer.parseInt(args[0].substring(idx + 1));
		ClientSketcher sObj = new ClientSketcher();
		sObj.fun(host, port, window);

	}

	void fun(String host, int port, SketchFrame window) throws IOException {
		ChatClient client = new ChatClient(host, port, window);
		client.start();
	}
}
