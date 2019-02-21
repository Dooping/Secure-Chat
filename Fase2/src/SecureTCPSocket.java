import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.security.Key;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

public class SecureTCPSocket extends Socket {

	public static final short PROTOCOL_VERSION = 1;
	public static final short PHASE = 1;

	protected DataInputStream dataIn;
	protected DataOutputStream dataOut;
	Config config;
	String username = "";
	private Map<String, Integer> nonce;

	/**
	 * Creates a secure socket from an address and a cipher suite
	 * @param host - hostname
	 * @param port - port
	 * @param config - configuration
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	SecureTCPSocket(String host, int port, Config config) throws UnknownHostException, IOException {
		super(host, port);
		this.config = config;
		dataIn = new DataInputStream(new BufferedInputStream(this.getInputStream()));
		dataOut = new DataOutputStream(new BufferedOutputStream(this.getOutputStream()));
		nonce = new HashMap<String, Integer>();
		nonce.put(username, 0);
	}

	/**
	 * Creates a secure socket from an accepted connection
	 * @param socket - connection
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	SecureTCPSocket(Socket socket) throws UnknownHostException, IOException{
		dataIn = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
		dataOut = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
		nonce = new HashMap<String, Integer>();
		nonce.put(username, 0);
	}
	
	/**
	 * Sets the current cipher suite
	 * @param config - cipher suite
	 */
	public void setConfig(Config config){
		this.config = config;
	}

	/**
	 * Applies encryption and Adds headers to a message before sending it
	 * @param message - message to send
	 * @param type - type of message
	 * @throws IOException
	 * @throws Exception
	 */
	public void write(String message, byte type) throws IOException, Exception {
		dataOut.writeUTF(Base64.encode(addHeader(encrypt(addDataHeaders(message.getBytes())), type)));
		dataOut.flush();
	}
	
	/**
	 * Applies encryption and Adds headers to a message before sending it
	 * @param message - message to send
	 * @param type - type of message
	 * @throws IOException
	 * @throws Exception
	 */
	public void write(byte[] message, byte type) throws IOException, Exception {
		dataOut.writeUTF(Base64.encode(addHeader(encrypt(message), type)));
		dataOut.flush();
	}
	
	/**
	 * Applies encryption and Adds headers to a message before sending it
	 * @param message - message to send
	 * @param type - type of message
	 * @throws IOException
	 * @throws Exception
	 */
	public void writeNotEncrypted(byte[] message, byte type) throws IOException, Exception {
		dataOut.writeUTF(Base64.encode(addHeader(message, type)));
		dataOut.flush();
	}

	/**
	 * Reads a message and decrypt it
	 * @return decrypted message
	 * @throws IOException
	 * @throws Exception
	 */
	public String read() throws IOException, Exception {
		byte[] message = Base64.decode(dataIn.readUTF());
		byte type = getMessageType(message);
		if (type == 20) {
			byte[] data = decrypt(getMessage(message));
			return new String(removeDataHeaders(data));
		} else
			return new String(getMessage(message));
	}
	
	/**
	 * Reads a message without encryption
	 * @return message
	 * @throws IOException
	 * @throws Exception
	 */
	public byte[] readNotEncrypt() throws IOException, Exception {
		return Base64.decode(dataIn.readUTF());
	}
	
	/**
	 * Reads a message and decrypt it
	 * @return byte array
	 * @throws IOException
	 * @throws Exception
	 */
	public byte[] readNotString() throws IOException, Exception {
		byte[] message = Base64.decode(dataIn.readUTF());
		return decrypt(getMessage(message));
	}
	
	/**
	 * Adds plain text headers
	 * @param message
	 * @param type - type of message
	 * @return
	 */
	private byte[] addHeader(byte[] message, byte type) {
		ByteBuffer b = ByteBuffer.allocate(message.length + 9);
		b.putShort(PHASE);
		b.putShort(PROTOCOL_VERSION);
		b.putInt(message.length);
		b.put(type);
		b.put(message, 0, message.length);
		return b.array();
	}

	/**
	 * Gets the type of the message
	 * @param message
	 * @return type
	 */
	private byte getMessageType(byte[] message) {
		ByteBuffer b = ByteBuffer.wrap(message);
		b.getShort();
		b.getShort();
		b.getInt();
		return b.get();
	}

	/**
	 * Filters the header of the message
	 * @param message
	 * @return data
	 */
	private byte[] getMessage(byte[] message) {
		ByteBuffer b = ByteBuffer.wrap(message);
		b.getShort();
		b.getShort();
		int size = b.getInt();
		b.get();
		byte[] data = new byte[size];
		b.get(data);
		return data;
	}

	/**
	 * Adds headers to data before encryption
	 * @param message
	 * @return byte array
	 */
	private byte[] addDataHeaders(byte[] message) {
		ByteBuffer b = ByteBuffer.allocate(message.length + username.length() + 12);
		b.putInt(username.length());
		b.put(username.getBytes());
		b.putInt(nonce.get(username));
		b.putInt(nonce.get(username)+1);
		b.put(message);
		return b.array();
	}

	/**
	 * Removes headers form data and checks if nonce is correct
	 * @param message - data + headers
	 * @return data
	 * @throws InvalidNonceException 
	 */
	private byte[] removeDataHeaders(byte[] message) throws InvalidNonceException {
		ByteBuffer b = ByteBuffer.wrap(message);
		int len = b.getInt();
		byte[] usernameArray = new byte[len];
		b.get(usernameArray, 0, len);
		String username = new String(usernameArray);
		if(nonce.containsKey(username)){
			int r1client = nonce.get(username);
			int r1 = b.getInt();
			if (nonce.containsKey(username) && r1 != r1client)
				throw new InvalidNonceException("invalid r1");
		}
		else
			b.getInt();
		nonce.put(username, b.getInt());

		byte[] data = new byte[message.length - len - 12];
		b.get(data, 0, data.length);
		return data;
	}

	/**
	 * Applies encryption to an array and adds mac
	 * @param message - byte array
	 * @return - byte array
	 * @throws Exception
	 */
	private byte[] encrypt(byte[] message) throws Exception {

		String ciphersuite = config.getAlgorithm() + "/" + config.getMode() + "/" + config.getPadding();
		String provider = "BC";

		if (config.getSessionKey().getBytes().length * 8 == config.getKeysize()) {

			SecretKey key = new SecretKeySpec(config.getSessionKey().getBytes(), 0,
					config.getSessionKey().getBytes().length, config.getAlgorithm());

			byte[] ivBytes = config.getIv().getBytes();
			IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);

			Cipher cipher = Cipher.getInstance(ciphersuite, provider);
			cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
			byte[] ciphertext = cipher.doFinal(message);

			byte[] mac = doMacEnc(ciphertext);

			byte[] finalMessage = new byte[mac.length + ciphertext.length];

			System.arraycopy(ciphertext, 0, finalMessage, 0, ciphertext.length);
			System.arraycopy(mac, 0, finalMessage, ciphertext.length, mac.length);

			return finalMessage;
		}
		throw new InvalidKeySizeException("Key size is different");

	}

	/**
	 * Checks if mac is the same and decrypts the message
	 * @param finalMessageBytes
	 * @return - byte array
	 * @throws Exception
	 */
	private byte[] decrypt(byte[] finalMessageBytes) throws Exception {

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

	/**
	 * Calculates mac
	 * @param cipherTextBytes
	 * @return - mac
	 * @throws Exception
	 */
	private byte[] doMacEnc(byte[] cipherTextBytes) throws Exception {

		Mac mac = Mac.getInstance(config.getMacAlgorithm(), "BC");
		Key macKey = new SecretKeySpec(config.getHmacKey().getBytes(), config.getMacAlgorithm());

		mac.init(macKey);
		mac.update(cipherTextBytes);

		return mac.doFinal();
	}

	/**
	 * Checks if mac is the same
	 * @param finalMessageBytes
	 * @return - message
	 * @throws Exception
	 */
	private byte[] doMacDec(byte[] finalMessageBytes) throws Exception {

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
	
	/**
	 * Sets username
	 * @param username
	 */
	public void setUsername(String username){
		this.username = username;
		nonce.put(username, 0);
	}

}
