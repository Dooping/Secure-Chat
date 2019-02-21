import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class Config {
	
	String sessionKey;
	int keysize;
	String algorithm;
	String iv;
	String mode;
	String padding;
	String macAlgorithm;
	String hmacKey;
	int hmacKeySize;
	
	public Config(File file) throws IOException {
		readDef(file);
	}
	
	/**
	 * 
	 * @param def
	 * @throws IOException
	 */
	private void readDef(File def) throws IOException {
		Scanner read = new Scanner(def);
		read.next("SESSIONKEY:");
		read.skip(" ");
		sessionKey = read.nextLine();
		read.next("KEYSIZE:");
		keysize = read.nextInt();
		read.next("ALG:");
		read.skip(" ");
		algorithm = read.nextLine();
		read.next("IV:");
		read.skip(" ");
		iv = read.nextLine();
		read.next("MODE:");
		read.skip(" ");
		mode = read.nextLine();
		read.next("PAD:");
		read.skip(" ");
		padding = read.nextLine();
		read.next("MAC:");
		read.skip(" ");
		macAlgorithm = read.nextLine();
		read.next("HMACKEY:");
		read.skip(" ");
		hmacKey = read.nextLine();
		read.next("HMACKEYSIZE:");
		read.skip(" ");
		hmacKeySize = read.nextInt();
		read.close();
				
	}

	public String getSessionKey() {
		return sessionKey;
	}

	public void setSessionKey(String sessionKey) {
		this.sessionKey = sessionKey;
	}

	public int getKeysize() {
		return keysize;
	}

	public void setKeysize(int keysize) {
		this.keysize = keysize;
	}

	public String getAlgorithm() {
		return algorithm;
	}

	public void setAlgorithm(String algorithm) {
		this.algorithm = algorithm;
	}

	public String getIv() {
		return iv;
	}

	public void setIv(String iv) {
		this.iv = iv;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public String getPadding() {
		return padding;
	}

	public void setPadding(String padding) {
		this.padding = padding;
	}

	public String getMacAlgorithm() {
		return macAlgorithm;
	}

	public void setMacAlgorithm(String macAlgorithm) {
		this.macAlgorithm = macAlgorithm;
	}

	public String getHmacKey() {
		return hmacKey;
	}

	public void setHmacKey(String hmacKey) {
		this.hmacKey = hmacKey;
	}

	public int getHmacKeySize() {
		return hmacKeySize;
	}

	public void setHmacKeySize(int hmacKeySize) {
		this.hmacKeySize = hmacKeySize;
	}

}
