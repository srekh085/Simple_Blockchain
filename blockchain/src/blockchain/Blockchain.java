package blockchain;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Scanner;
import java.util.Set;

public class Blockchain{
	private static ArrayList<Block> blockList;
	public Blockchain(ArrayList<Block> blockList){		
		Blockchain.blockList = blockList;
	}
	public ArrayList<Block> getBlocklist() {
		return blockList;
	}
	public void toFile(String fileName) {		
		try {
			PrintWriter writer = new PrintWriter(fileName);
			for(int i = 0; i < getBlocklist().size(); i++) {
				writer.println(getBlocklist().get(i).getIndex());				
				writer.println(Long.toString(getBlocklist().get(i).getTimestamp().getTime()));
				writer.println(getBlocklist().get(i).getTransaction().getSender());
				writer.println(getBlocklist().get(i).getTransaction().getReceiver());
				writer.println(getBlocklist().get(i).getTransaction().getAmount());
				writer.println(getBlocklist().get(i).getNonce());
				writer.println(getBlocklist().get(i).getHash());
			}	
			writer.close();			
		} 
		catch (IOException e) {
			System.out.println("Error Writing File");
		}
	}
	public static Blockchain fromfile(String filename) {
		ArrayList<String> strlist = new ArrayList<String>();
		ArrayList<Block> readmethodblocklist = new ArrayList<Block>();
		try {
			FileReader fr = new FileReader(filename+".txt");
			BufferedReader br = new BufferedReader (fr);
			String str;
			while((str = br.readLine()) != null) {
				strlist.add(str);
			}
		} catch (FileNotFoundException e) {
			System.out.println("File Not Found");
		} catch (IOException e) {
			System.out.println("File Not Read");
		}
		for (int i=0; i<(strlist.size() - 6); i+=7) {
			Block tempBlock = null;
			Transaction temptransaction = new Transaction(strlist.get(i+2), strlist.get(i+3), Integer.parseInt(strlist.get(i+4)));
			if(Integer.parseInt(strlist.get(i)) == 0) {
				tempBlock = new Block(Integer.parseInt(strlist.get(i)), "00000", strlist.get(i+6), strlist.get(i+5), Long.parseLong(strlist.get(i+1)), temptransaction);
			}
			else {
				tempBlock = new Block(Integer.parseInt(strlist.get(i)),strlist.get(i-1) , strlist.get(i+6), strlist.get(i+5), Long.parseLong(strlist.get(i+1)), temptransaction);
			}
			readmethodblocklist.add(tempBlock);
		}
		Blockchain a = new Blockchain(readmethodblocklist);
		return a;
	}
public boolean validateBlockchain() {
		Sha1 checkHash = new Sha1();
		for(int i = 0; i < getBlocklist().size(); i++) {	
			if(getBlocklist().get(i).getIndex() != i){		
				return false;
			}
		}
		for(int i = 0; i < getBlocklist().size()-1; i++) {
			try {
				if (!checkHash.hash(getBlocklist().get(i).toString()).equals(getBlocklist().get(i+1).getPreviousHash())){
					return false;
				}
			} 
			catch (UnsupportedEncodingException e) {
				System.out.println("Block Cannot Be Encoded");
			}		
		}		
		ArrayList<String> listOfPeople = new ArrayList<String>();
		for(int i = 0; i < getBlocklist().size(); i++) {
			listOfPeople.add(getBlocklist().get(i).getTransaction().getSender());
			listOfPeople.add(getBlocklist().get(i).getTransaction().getReceiver());
		}
		//Removing duplicate names to decrease runtime for large chains, using LinkedHashSet. 
		Set<String> withoutDuplicates = new LinkedHashSet<String>(listOfPeople);
		listOfPeople.clear();
		listOfPeople.addAll(withoutDuplicates);
		for(int i = 0; i < listOfPeople.size(); i++) {
			if (getBalance(listOfPeople.get(i)) < 0) {		
				return false;
			}
		}	
		return true;
	}		
	public int getBalance(String username) {
		int returnBalance = 0;
		if (username.equals("bitcoin")){
			//since bitcoin is the bank, it can have bitcoins to generate and give, for this model.
			return 10000;
		}
		else {
			for(int i = 0; i < getBlocklist().size(); i++) {
				if (getBlocklist().get(i).getTransaction().getSender().equals(username)){
					returnBalance -= getBlocklist().get(i).getTransaction().getAmount();
				}
				if (getBlocklist().get(i).getTransaction().getReceiver().equals(username)){			
					returnBalance += getBlocklist().get(i).getTransaction().getAmount();
				}					
			}
			return returnBalance;
		}		
	}	
	public void add(Block block) {	
		blockList.add(block);
	}
	@SuppressWarnings("resource")
	public static void main(String[] args) {
		String fileIn, fileOut, sender, receiver, amount, transact;
		boolean flag = true;
		Scanner scan = new Scanner(System.in);
		System.out.println("Enter File Name to Read From (Do not add .txt):  ");
		fileIn = scan.next();
		Blockchain blockListCollection = fromfile(fileIn);
		if(!blockListCollection.validateBlockchain()){
			System.out.println("Invalid Block Chain");
		}
		else {
			System.out.println("\nMaking a new Transaction");
			while(flag) {
				System.out.println("Enter the sender for Transaction: ");
				sender = scan.next();
				System.out.println("Enter the receiver for Transaction: ");
				receiver = scan.next();
				System.out.println("Enter the amount for Transaction: ");
				amount = scan.next();				
				if (blockListCollection.getBalance(sender)<Integer.parseInt(amount)) {
					System.out.println("Sender Balance is less than amount specified. Please try again");
					continue;
				}
				else{
					Transaction newTransaction = new Transaction(sender, receiver, Integer.parseInt(amount));
					Block newBlockCreated = new Block(blockListCollection.getBlocklist().size(), newTransaction, blockListCollection.getBlocklist().get(blockListCollection.getBlocklist().size()-1).getHash());
					blockListCollection.add(newBlockCreated);
					System.out.println("Do you want to make another transaction? Type yes or no (lowercase only)");
					transact = scan.next();
					if(transact.equals("no")) {
						flag = false;
						break;
					}
					else if(!transact.equals("yes")){
						while(true) {
							System.out.println("Please try again. Type yes or no (lowercase only)");
							transact = scan.next();
							if(transact.equals("no")) {
								flag = false;
								break;	
							}
							else if(transact.equals("yes")) {			
								break;
							}							
						}					
					}
				}
			}		
			System.out.println("Writing To File");
			fileOut = fileIn + "_srekh085";
			blockListCollection.toFile(fileOut + ".txt");
			System.out.println("Succesful!");
		}	
	}
	
}