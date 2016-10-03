package scrambler.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Random;
import java.util.concurrent.RecursiveTask;

@SuppressWarnings("serial")
public class ParallelOperations extends RecursiveTask<ArrayList<Integer>> {
	private int maxL;
	private ArrayList<Integer> data;
	private int start, end;
	private int key;
	private boolean encryption;
	
	/**
	  * @param data An array of data to shuffling in the encryption or recovery in the decryption. 
	  * @param s Data processing in subtask starts at this index.
	  * @param e Data processing end upon reaching this index.
	  * @param key Entry point of random number generator.
	  * @param ml If the file size is larger than {@code ml}, the file is divided in half, 
	  * then each part is divided again in half, until the size of each part becomes less than {@code ml}.
	  * @param enc True if encryption of file is in progress. False if decryption is in progress.
	  */
	public ParallelOperations(ArrayList<Integer> data, int s, int e, int key, int ml, boolean enc) {
		maxL = ml;
		this.data = data;
		start = s;
		end = e;
		this.key = key;
		encryption = enc;
	}
	
	/**
	 * This method does all the work for shuffling data and restore them.
	 */
	protected ArrayList<Integer> compute() {
		ArrayList<Integer> result = new ArrayList<>();
		if ((end - start) < maxL) {
			Random rnd = new Random(key);
			ArrayList<Integer> dividedData = new ArrayList<>();
			for (int i=start; i<end; i++) dividedData.add(data.get(i));
			ArrayList<Integer> randArray = new ArrayList<>();
			int divDataSize = dividedData.size();
			for (int i=0; i<divDataSize; i++) randArray.add(rnd.nextInt(divDataSize-i));
			if (encryption) {
				Iterator<Integer> it = randArray.iterator();
				while (it.hasNext()) {
					int pos=it.next();
					result.add(dividedData.get(pos));
					dividedData.remove(pos);
				}
			} else {
				ListIterator<Integer> itr = randArray.listIterator(randArray.size());
				ListIterator<Integer> itd = dividedData.listIterator(divDataSize);
				while (itr.hasPrevious()) {
					int pos=itr.previous();
					result.add(pos, itd.previous());
				}
			}
		} else {
			int middle = (start+end)/2;
			ParallelOperations subTaskA = new ParallelOperations(data, start, middle, key, maxL, encryption);
			ParallelOperations subTaskB = new ParallelOperations(data, middle, end, key, maxL, encryption);
			subTaskA.fork();
			subTaskB.fork();
			result = subTaskA.join();
			result.addAll(subTaskB.join());
		}
		return result;
	}
}
