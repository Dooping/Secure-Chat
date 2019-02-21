import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Key;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import com.sun.org.apache.xml.internal.security.utils.Base64;

public class SSOserver {

	public static final short PROTOCOL_VERSION = 1;
	public static final short PHASE = 2;

	static final int PORT = 8080;

	static Config config;

	private static byte getMessageType(byte[] message) {
		ByteBuffer b = ByteBuffer.wrap(message);
		b.getShort();
		b.getShort();
		b.getInt();
		return b.get();
	}

	private static byte[] getMessage(byte[] message) {
		ByteBuffer b = ByteBuffer.wrap(message);
		b.getShort();
		b.getShort();
		int size = b.getInt();
		b.get();
		byte[] data = new byte[size];
		b.get(data);
		return data;
	}

	private static byte[] decrypt(byte[] finalMessageBytes) throws Exception {

		byte[] cipherText = doMacDec(finalMessageBytes);

		String ciphersuite = config.getAlgorithm() + "/" + config.getMode() + "/" + config.getPadding();
		String provider = "BC";

		SecretKey key = new SecretKeySpec(config.getSessionKey().getBytes(), 0,
				config.getSessionKey().getBytes().length, config.getAlgorithm());

		byte[] ivBytes = config.getIv().getBytes();
		IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);

		Cipher cipher = Cipher.getInstance(ciphersuite, provider);
		cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);

		byte[] plainText = cipher.doFinal(cipherText);
		return plainText;
	}

	private static byte[] doMacDec(byte[] finalMessageBytes) throws Exception {

		Mac mac = Mac.getInstance(config.getMacAlgorithm(), "BC");

		if (config.getHmacKey().getBytes().length * 8 == config.getHmacKeySize()) {

			Key macKey = new SecretKeySpec(config.getHmacKey().getBytes(), config.getMacAlgorithm());

			int macLength = mac.getMacLength();

			byte[] macByte = new byte[macLength];

			byte[] cipherText = new byte[finalMessageBytes.length - macLength];

			System.arraycopy(finalMessageBytes, 0, cipherText, 0, finalMessageBytes.length - macLength);
			System.arraycopy(finalMessageBytes, finalMessageBytes.length - macLength, macByte, 0, macLength);

			mac.init(macKey);
			mac.update(cipherText);

			byte[] createdMac = mac.doFinal();

			if (Arrays.equals(macByte, createdMac))
				return cipherText;

			throw new InvalidMacException("MAC is different");
		}
		throw new InvalidKeySizeException("MAC Key size is different");

	}

	private static byte[] removeDataHeaders(byte[] message) {
		ByteBuffer b = ByteBuffer.wrap(message);
		int len = b.getInt();
		byte[] username = new byte[100];
		b.get(username, 0, len);
		b.getInt();
		b.getInt();
		byte[] data = new byte[message.length - len - 12];
		b.get(data, 0, data.length);
		return data;
	}
	
	

	public static void main(String args[]) throws IOException {
		
		int port = PORT;
		
		if (args.length == 1) {
			try {
				port = Integer.parseInt(args[0]);
				
			} catch (NumberFormatException e) {
				System.err.println("No Port... I will use port 8080");
			}
		}
		else
			System.err.println("No Port... I will use port 8080");
		
		File configs = new File("serverConfigs.txt");
		config = new Config(configs);

		File folder = new File("session");
		String[] entries = folder.list();
		if (entries != null) {
			for (String s : entries) {
				File currentFile = new File(folder.getPath(), s);
				currentFile.delete();
			}
		}
		folder.delete();
		folder.mkdir();

		ServerSocket server = new ServerSocket(port);

		while (true) {
			SecureTCPSocket client = new SecureTCPSocket(server.accept());

			try {
				byte[] request = client.readNotEncrypt();
				byte type = getMessageType(request);

				if (type == 10) {

					String requestString = new String(getMessage(request));
					String[] requestSplited = requestString.split(",");
					String user = requestSplited[0];
					String session = requestSplited[1];
					int nonce = Integer.parseInt(requestSplited[2]);
					File userConfigs = new File("user",user + ".txt");
					Config config = new Config(userConfigs);
					client.setConfig(config);

					File sessionConfigs = new File("session/" + session + ".txt");

					byte[] ks = Files.readAllBytes(Paths.get("randomConfigs.txt"));
					byte[] b = session.getBytes();
					
					ByteBuffer buffer = ByteBuffer.allocate(ks.length + b.length + 12);
					buffer.putInt(nonce+1);
					buffer.putInt(ks.length);
					buffer.put(ks);
					buffer.putInt(b.length);
					buffer.put(b);
					
					client.write(buffer.array(),(byte)11);

				} else if (type == 12) {
					byte[] message = decrypt(getMessage(request));
					byte[] finalMessage = removeDataHeaders(message);
					FileOutputStream fileOut = new FileOutputStream(new File("session", new String(finalMessage)));
					fileOut.write(Files.readAllBytes(Paths.get("randomConfigs.txt")));
					fileOut.close();
				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}
}
