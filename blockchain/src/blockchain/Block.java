package blockchain;

import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.util.Random;

public class Block {
	private int index;
	private java.sql.Timestamp timestamp;
	private Transaction transaction;
	private String nonce;
	private String previousHash;
	private String hash;
	//Method called when adding a new block from scratch
	public Block(int indx, Transaction tr, String previoushash){	
		index = indx;
		transaction = tr;
		if(indx == 0){	
			previousHash = "00000";
		}
		else{	
			previousHash = previoushash;	
		}
		timestamp = new Timestamp(System.currentTimeMillis());
		blockHashmaker();
	}
	//Method called when reading from file and saving as a BlockChain
	public Block(int indx, String previoushash, String thishash, String readnonce, long time, Transaction tr) {
		if (indx == 0) {
			previousHash = "00000";
		}
		else {
			previousHash = previoushash;
		}
		transaction = tr;
		hash = thishash;
		index = indx;
		nonce = readnonce;
		timestamp = new Timestamp(time);
	}
	public String toString() {
		return timestamp.toString() + ":" + transaction.toString()
		+ "." + nonce+ previousHash;
	}
	private String noncegen() {
		Random rand = new Random();
		Random rand1 = new Random();
		String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz`1234567890-=~!@#$%^&*()_+[];\',./{}|:<>?";
		int length = rand1.nextInt(5)+5;
		String tempnonce = "";
		for (int  i1 = 0; i1<length; i1++) {
			tempnonce = tempnonce + characters.charAt(rand.nextInt(characters.length()));
		}
		nonce = tempnonce;
		return tempnonce;
	}
	public void blockHashmaker() {
		int tries = 0;
		while (true) {
			tries++;
			noncegen();
			String temp;
			try {
				temp = Sha1.hash(toString());
				if (temp.startsWith("00000")) {
					System.out.println(tries+" Attempts; Hashcode generated: "+temp);
					hash = temp;
					break;
				}
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public Timestamp getTimestamp() {	
		return timestamp;
	}
	public Transaction getTransaction() {	
		return transaction;
	}
	public String getPreviousHash() {	
		return previousHash;
	}
	public String getHash() {
		return hash;
	}
	public int getIndex() {	
		return index;
	}
	public String getNonce() {	
		return nonce;
	}
}
