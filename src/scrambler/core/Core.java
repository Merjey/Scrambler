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
import java.util.List;
import java.util.ListIterator;
import java.util.Properties;
import java.util.Random;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;


public class Core {
	private static int key[] = new int[5];
	private static int inclusions[] = new int[2];
	private static int maxpiece;
	
	private static void keyExtraction() throws IOException {
		Properties keys = new Properties();
		keys.load(new BufferedReader(new FileReader(new File("keys.properties"))));
		for(int i = 0; i < key.length; i++){
			key[i] = Integer.parseInt(keys.getProperty("key." + i));
		}
		for(int i = 0; i < inclusions.length; i++){
			inclusions[i] = Integer.parseInt(keys.getProperty("inclusions." + i));
		}
		maxpiece = Integer.parseInt(keys.getProperty("maxpiece"));
	}
	
	private static List<Integer> openFile(File file) throws IOException {
		List<Integer> data = new ArrayList<>();
		try (BufferedInputStream inBuff = new BufferedInputStream (new FileInputStream(file))){
			while (true) {
				int byteValue = inBuff.read();
				if (byteValue == -1) break;
				data.add(byteValue);
			}
		} finally {}
		return data;
	}
	
	private static void saveFile(File file, List<Integer> data) throws IOException {
		Iterator<Integer> it = data.iterator();
		try (BufferedOutputStream outBuff = new BufferedOutputStream (new FileOutputStream(file))) {
			while (it.hasNext()) {
				int byteValue = it.next();
				outBuff.write(byteValue);
			}
		} finally {}
	}
	
	/**
	 * @return True if encryption was successfully. False if an exception has occurred.
	 */
	public static boolean encryptFile(File file) {
		try {
			keyExtraction();
			List<Integer> data = openFile(file);
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
	
	/**
	 * @return True if decryption was successfully. False if an exception has occurred.
	 */
	public static boolean decryptFile(File file) {
		try {
			keyExtraction();
			List<Integer> data = openFile(file);
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
	
	private static List<Integer> addSign(List<Integer> data) {
		List<Integer> temp = new ArrayList<>();
		temp.addAll(data);
		for (int i = 1; i <= 5; i++) temp.add(30+i);
		return temp;
	}
	
	private static List<Integer> removeSign(List<Integer> data) {
		List<Integer> temp = new ArrayList<>();
		temp.addAll(data);
		int datasize=data.size();
		for (int i = 1; i <= 5; i++) temp.remove(datasize-i);
		return temp;
	}
	
	private static List<Integer> doStuffing(List<Integer> data, int key, int items) {
		List<Integer> temp = new ArrayList<>();
		List<Integer> randArray = new ArrayList<>();
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
	
	private static List<Integer> undoStuffing(List<Integer> data, int key, int items) {
		List<Integer> temp = new ArrayList<>();
		List<Integer> randArray = new ArrayList<>();
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
	
	private static List<Integer> putToZip(List<Integer> data) throws IOException {
		List<Integer> temp = new ArrayList<>();
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
	
	private static List<Integer> getFromZip(List<Integer> data) throws IOException {
		List<Integer> temp = new ArrayList<>();
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
	
	private static List<Integer> doShuffle(List<Integer> data, int key) throws InterruptedException {
		ParallelOperations task = new ParallelOperations(data, 0, data.size(), key, maxpiece, true);
		return task.invoke();	
	}
	
	private static List<Integer> undoShuffle(List<Integer> data, int key) throws InterruptedException {
		ParallelOperations task = new ParallelOperations(data, 0, data.size(), key, maxpiece, false);
		return task.invoke();	
	}

}
