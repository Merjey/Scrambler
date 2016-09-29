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
	
	public ParallelOperations(ArrayList<Integer> data, int s, int e, int key, int ml, boolean enc) {
		maxL = ml;
		this.data = data;
		start = s;
		end = e;
		this.key = key;
		encryption = enc;
	}
	
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
