package algorithms;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map.Entry;

public class StreamReduction {
	private int maxTableSize = 0;
	private double error;
	private double totalLength = 0;
	private double aveLength;
	private int numOfTrans = 0;
	private int cmin = 0;
	private HashMap<Integer, Integer> table = new HashMap<Integer, Integer>();

	public StreamReduction(int maxTableSize, double error) {
		this.maxTableSize = maxTableSize;
		this.error = error;
	}

	/** the core of algorithm **/
	public int[] reduction(int[] original, int delta) {
		int[] reducedTrans;
		ArrayList<Integer> list;
		totalLength += original.length;
		aveLength = totalLength / ++numOfTrans;
		if (maxTableSize < aveLength / error) {
			maxTableSize = (int) Math.ceil((double) aveLength / error);
		}
		
		/* process each item in transaction */
		for (int item : original) {
			if (table.keySet().contains(item)) { // item exist
				table.replace(item, table.get(item).intValue() + 1); // item++
			} else if (table.size() < maxTableSize) { // item doesn't exist and table is not full
				table.put(item, 1);
			} else { // table is full, find cmin entry to replace
				replacement(item);
			}
			udpateCmin();
		}
		
		/* all item store in map */
//		for(int item : original){
//			if (table.keySet().contains(item)) { // item exist
//				table.replace(item, table.get(item).intValue() + 1); // item++
//			} else {
//				table.put(item, 1);
//			}
//		}

		/* remain the significant item from transaction */
		list = new ArrayList<Integer>(); // reset a List
		for (int item : original) {
			if (table.get(item) > delta) {
				list.add(item);
			}
		}
		reducedTrans = transferListToArray(list);

		return reducedTrans;
	}

	/** insert a new item into table by replacement **/
	public void replacement(int item) {
		int key = -1;
		for (Entry<Integer, Integer> es : table.entrySet()) {
			if (es.getValue() == cmin) {
				key = es.getKey();
				break;
			}
		}
		table.remove(key); // remove the cmin entry
		table.put(item, cmin + 1); // insert a new entry
	}

	/** udpate cmin and mn **/
	public void udpateCmin() {
		boolean first_data = true;
		int temp = -1;

		// if table is not full, cmin = 0
		if (table.size() < maxTableSize) {
			cmin = 0;
			// mn = maxTableSize - table.size();
		} else {
			// else, table is full
			/* find cmin */
			for (int i : table.values()) {
				if (first_data) {
					temp = i;
					first_data = false;
				} else {
					temp = Math.min(temp, i); // which is small
				}
			}
			cmin = temp;

			// /* count cmin number */
			// mn = 0; // reset mn
			// for (int i : table.values()) {
			// if (cmin == i) {
			// mn++;
			// }
			// }
		}
	}

	/** print table's content **/
	public void printItemTable() {
		System.out.println("=========== table's content ========");
		for (Entry<Integer, Integer> es : table.entrySet()) {
			System.out.println("item = " + es.getKey() + ", value = " + es.getValue());
		}
		System.out.println();
	}

	/** transfer ArrayList<Integer> to int[] **/
	public int[] transferListToArray(ArrayList<Integer> list) {
		int[] array = new int[list.size()];
		// read Integer and store in array
		for (int i = 0; i < list.size(); i++) {
			array[i] = (int) list.get(i);
		}
		return array;
	}
	
	/** transfer the String array to int array **/
	public int[] transferLineToArray(String line) {
		String[] lineSplited = line.split(" ");
		int[] tran = new int[lineSplited.length];

		for (int i = 0; i < lineSplited.length; i++) {
			tran[i] = Integer.parseInt(lineSplited[i]);
		}
		return tran;
	}
	
	public void printArray(int[] arr){
		for(int i : arr){
			System.out.print(i + " ");
		}
		System.out.println();
	}

	
	/*********** mini test 
	 * @throws IOException *************/
	public void testReduction(String input, int delta) throws IOException{
		FileReader fr = new FileReader(input);
		BufferedReader br = new BufferedReader(fr);
		String line;
		int[] tran;
		int[] reducedTran;
		
				
		while ((line = br.readLine()) != null) {
			tran = transferLineToArray(line);
			reducedTran = reduction(tran, delta);
			System.out.print("reduced tran : ");
			printArray(reducedTran);
			this.printItemTable();
		}
	}
	
	/***************getter and setter *************/
	public int getmaxTableSize() {
		return maxTableSize;
	}

	public void setmaxTableSize(int maxTableSize) {
		this.maxTableSize = maxTableSize;
	}

	public double getError() {
		return error;
	}

	public void setError(double error) {
		this.error = error;
	}

	public double getTotalLength() {
		return totalLength;
	}

	public void setTotalLength(double totalLength) {
		this.totalLength = totalLength;
	}

	public double getAveLength() {
		return aveLength;
	}

	public void setAveLength(double aveLength) {
		this.aveLength = aveLength;
	}

	public HashMap getTable() {
		return table;
	}

	public void setTable(HashMap table) {
		this.table = table;
	}

}
