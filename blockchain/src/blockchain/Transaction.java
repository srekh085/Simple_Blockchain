package blockchain;

public class Transaction {
	private String sender, receiver;
	private int amount;
	public Transaction(String s, String r, int a) {
		sender = s;
		receiver = r;
		amount = a;
	}
	public String getSender() {
		return sender;
	}
	public String getReceiver() {
		return receiver;
	}
	public int getAmount() {
		return amount;
	}
	public String toString() {
		return sender + ":" + receiver + "=" + amount;
	}
}
