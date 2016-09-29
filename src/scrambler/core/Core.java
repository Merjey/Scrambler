package scrambler.core;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Properties;
import java.util.Random;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;


public class Core {
	private static int key[] = new int[5];
	private static int inclusions[] = new int[2];
	private static int maxpice;
	
	private static void keyExtraction() throws IOException {
		Properties keys = new Properties();
		keys.load(new BufferedReader(new FileReader(new File("keys.properties"))));
		for(int i = 0; i < key.length; i++){
			key[i] = Integer.parseInt(keys.getProperty("key." + i));
		}
		for(int i = 0; i < inclusions.length; i++){
			inclusions[i] = Integer.parseInt(keys.getProperty("inclusions." + i));
		}
		maxpice = Integer.parseInt(keys.getProperty("maxpice"));
	}
	
	private static ArrayList<Integer> openFile(File file) throws IOException {
		ArrayList<Integer> data = new ArrayList<>();
		try (BufferedInputStream inBuff = new BufferedInputStream (new FileInputStream(file))){
			while (true) {
				int byteValue = inBuff.read();
				if (byteValue == -1) break;
				data.add(byteValue);
			}
		} finally {}
		return data;
	}
	
	private static void saveFile(File file, ArrayList<Integer> data) throws IOException {
		Iterator<Integer> it = data.iterator();
		try (BufferedOutputStream outBuff = new BufferedOutputStream (new FileOutputStream(file))) {
			while (it.hasNext()) {
				int byteValue = it.next();
				outBuff.write(byteValue);
			}
		} finally {}
	}
	
	public static boolean encryptFile(File file) {
		try {
			keyExtraction();
			ArrayList<Integer> data = openFile(file);
			data=doShuffle(data, key[0]);
			data=doStuffing(data, key[1], inclusions[0]);
			data=doShuffle(data, key[2]);
			data=putToZip(data);
			data=doStuffing(data, key[3], inclusions[1]);
			data=doShuffle(data, key[4]);
			data=addSign(data);
			saveFile(file, data);
		} catch (Exception e){
			return false;
		}
		return true;
	}
	
	public static boolean decryptFile(File file) {
		try {
			keyExtraction();
			ArrayList<Integer> data = openFile(file);
			int datasize = data.size();
			for (int i=1; i<=5; i++){
				if (data.get(datasize-i)!=(36-i)){
					throw new Exception("This file was not encrypted");
				}
			}
			data=removeSign(data);
			data=undoShuffle(data, key[4]);
			data=undoStuffing(data, key[3], inclusions[1]);
			data=getFromZip(data);
			data=undoShuffle(data, key[2]);
			data=undoStuffing(data, key[1], inclusions[0]);
			data=undoShuffle(data, key[0]);
			saveFile(file, data);;
		} catch (Exception e){
			return false;
		}
		return true;
	}
	
	private static ArrayList<Integer> addSign(ArrayList<Integer> data) {
		ArrayList<Integer> temp = new ArrayList<>();
		temp.addAll(data);
		for (int i = 1; i <= 5; i++) temp.add(30+i);
		return temp;
	}
	
	private static ArrayList<Integer> removeSign(ArrayList<Integer> data) {
		ArrayList<Integer> temp = new ArrayList<>();
		temp.addAll(data);
		int datasize=data.size();
		for (int i = 1; i <= 5; i++) temp.remove(datasize-i);
		return temp;
	}
	
	private static ArrayList<Integer> doStuffing(ArrayList<Integer> data, int key, int items) {
		ArrayList<Integer> temp = new ArrayList<>();
		ArrayList<Integer> randArray = new ArrayList<>();
		Random rnd = new Random(key);
		Random sr= new Random();
		int datasize = data.size();
		temp.addAll(data);
		int pos;
		for (int i = 0; i<items; i++) {
			randArray.add(rnd.nextInt(datasize));
		}
		randArray.sort(null);
		ListIterator<Integer> itr = randArray.listIterator(items);
		while (itr.hasPrevious()) {
			pos = itr.previous();
			temp.add(pos,(sr.nextInt(210)+40));
		}
		return temp;
	}
	
	private static ArrayList<Integer> undoStuffing(ArrayList<Integer> data, int key, int items) {
		ArrayList<Integer> temp = new ArrayList<>();
		ArrayList<Integer> randArray = new ArrayList<>();
		Random rnd = new Random(key);
		int datasize=data.size()-items;
		temp.addAll(data);
		for (int i = 0; i<items; i++) {
			randArray.add(rnd.nextInt(datasize));
		}
		randArray.sort(null);
		int pos;
		Iterator<Integer> itr = randArray.iterator();
		while (itr.hasNext()) {
			pos=itr.next();
			temp.remove(pos);
		}
		return temp;
	}
	
	private static ArrayList<Integer> putToZip(ArrayList<Integer> data) throws IOException {
		ArrayList<Integer> temp = new ArrayList<>();
		File tempFile = File.createTempFile("temp", ".dat");
        tempFile.deleteOnExit();
        BufferedOutputStream zipper = new BufferedOutputStream(new GZIPOutputStream (new FileOutputStream(tempFile)));
		Iterator<Integer> it = data.iterator();
		while (it.hasNext()) {
			int byteValue = it.next();
			zipper.write(byteValue);
		}
		zipper.flush();
		zipper.close();
		BufferedInputStream inBuff = new BufferedInputStream (new FileInputStream(tempFile));
		while (true) {
			int byteValue = inBuff.read();
			if (byteValue == -1) break;
			temp.add(byteValue);
		}
		inBuff.close();
		tempFile.delete();
		return temp;
	}
	
	private static ArrayList<Integer> getFromZip(ArrayList<Integer> data) throws IOException {
		ArrayList<Integer> temp = new ArrayList<>();
		File tempFile = File.createTempFile("temp", ".dat");
        tempFile.deleteOnExit();
        BufferedOutputStream outBuff = new BufferedOutputStream (new FileOutputStream(tempFile));
        Iterator<Integer> it = data.iterator();
		while (it.hasNext()) {
			int byteValue = it.next();
			outBuff.write(byteValue);
		}
		outBuff.flush();
		outBuff.close();
		BufferedInputStream unzipper = new BufferedInputStream(new GZIPInputStream (new FileInputStream(tempFile)));
		while (true) {
			int byteValue = unzipper.read();
			if (byteValue == -1) break;
			temp.add(byteValue);
		}
		unzipper.close();
		tempFile.delete();
		return temp;
	}
	
	private static ArrayList<Integer> doShuffle(ArrayList<Integer> data, int key) throws InterruptedException {
		ParallelOperations task = new ParallelOperations(data, 0, data.size(), key, maxpice, true);
		return task.invoke();	
	}
	
	private static ArrayList<Integer> undoShuffle(ArrayList<Integer> data, int key) throws InterruptedException {
		ParallelOperations task = new ParallelOperations(data, 0, data.size(), key, maxpice, false);
		return task.invoke();	
	}

}
