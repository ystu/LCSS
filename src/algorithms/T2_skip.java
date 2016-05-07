package algorithms;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import itemset_array_integers_with_count.Itemset;
import summary.EntryTable;
import tools.MemoryLogger;

public class T2_skip {
	private int currentTime;
	private EntryTable summary;
	private FileReader fr;
	private BufferedReader br;
	private int r;
	private int cs;
	private int cmin;
	private int mn;
	private int delta;
	private String inputFile;
	private String verifyFile;
	private double mst;
	private int tableSize;
	private String line;
	private int[] tran;
	ArrayList<int[]> candidates = new ArrayList<int[]>();
	ArrayList<int[]> hasAppearedInTable = null;
	HashMap<Integer, Integer> appearedItems = new HashMap<Integer, Integer>();
	EntryTable entryTable = new EntryTable("summary");
	char whatCase;
	ArrayList<int[]> miningResult = new ArrayList<int[]>();
	ArrayList<int[]> accList;
	int fp = 0, tp = 0;
	int numOfCaseC = 0;
	long startTime;
	long endTime;

	/* constructor */
	public T2_skip(String inputFile, String verifyFile, double mst, int tableSize) {
		this.inputFile = inputFile;
		this.verifyFile = verifyFile;
		this.mst = mst;
		this.tableSize = tableSize;
		this.mn = tableSize;
		InitialEntryTable(tableSize);
	}

	/* initial a fix size table */
	public void InitialEntryTable(int tableSize) {
		Itemset itemset;
		for (int i = 0; i < tableSize; i++) {
			itemset = new Itemset(null);
			itemset.setAbsoluteSupport(0);
			entryTable.addItemset(itemset);
		}
	}

	/* the core of algorithm */
	public void runSkipLCSS(String inputFile, String verifyFile, double mst, int tableSize) throws IOException {
		fr = new FileReader(inputFile);
		br = new BufferedReader(fr);
		int num = 0;
		startTime = System.currentTimeMillis();
		double t2_threshold = (Math.log(tableSize) / Math.log(2)) + 1;
		MemoryLogger.getInstance().reset();
		
		
		// start read each line
		while ((line = br.readLine()) != null) {			
			tran = transferLineToArray(line); // get the transaction int[]
			Arrays.sort(tran); // sort by lexicographic order
			System.out.println("Line: " + ++num);
			updateSummary(tran, hasAppearedInTable, t2_threshold );
//			entryTable.printItemsets(1);
		}

		// print info
		System.out.println("t2_threshold : " + t2_threshold);
		this.printSystemInfo();
		this.printSummaryInfo();
		// mining
		miningResult = miningSummary(entryTable, mst * num);
		// verify
		verify(miningResult, verifyFile);
		endTime = System.currentTimeMillis();

		// we check the memory usage
		MemoryLogger.getInstance().checkMemory();

		// evalute recall, precision by fp, tp and totalAccurate
		printResult();
		br.close();
		fr.close();
	}

	/* update the summary by candidate pattern */
	public void updateSummary(int[] transaction, ArrayList<int[]> hasAppearedInTable, double t2_threshold) {
		hasAppearedInTable = new ArrayList<int[]>();
		
		// the key point of t2-skip
		if((double)transaction.length > t2_threshold ){ // it is case C
			System.out.println("t2 skip");
			allSupportPlusOne();
			return; // skip udpate and replace
		}
		
		supportIncreaseIfExist(transaction, hasAppearedInTable); // if candidate exist in summary, support++
		updateInfoBeforeReplacement(); // update cmin and mn
		cs = (int) (Math.pow(2, transaction.length) - 1 - r); // number of candidate need inserted
		// find candidate pattern in order to replace in
		long start = System.currentTimeMillis();
		// System.out.println("case : " + whichCase(cs, mn));
		if (cs <= mn) { // case A or B
			candidates = getCandidateSetCaseAB(transaction, hasAppearedInTable); //
		} else { // case C
			candidates = getCandidateSetCaseC(transaction, hasAppearedInTable, mn);
		}
		long endGetingCand = System.currentTimeMillis();
		// System.out.println("case : " + whichCase(cs,mn));
		replaceByCandidate(candidates); // use candidate to replace entry in table
		long endReplace = System.currentTimeMillis();
		updateTableInfo(); // update r, delta and cmin

	}

	/* mining frequent pattern in Summary */
	public ArrayList<int[]> miningSummary(EntryTable table, double threshold) {
		ArrayList<int[]> list = new ArrayList<int[]>();
		// check each entry
		for (Itemset itemset : table.getItemsets()) {
			if (itemset.getAbsoluteSupport() >= threshold) {
				list.add(itemset.getItems());
			}
		}
		return list;
	}

	public void verify(ArrayList<int[]> miningResult, String accurateFile) throws IOException {
		// step1. read accurateFile and store in Itemsets
		accList = getAccuratePattern(accurateFile);
		// step2. read each pattern in appro and compare accurate, count the total patterns, true positive and false positive
		for (int[] appro : miningResult) {
			// check if int[] is accurate
			if (indexOfItemset(accList, appro) == -1)
				fp++; // pattern is wrong
			else
				tp++; // pattern is right
		}

	}

	public void printResult() {
		NumberFormat nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits(3); // 小數後3位

		System.out.println("******** run time & memory usage ***********");
		System.out.println("run time : " + (endTime - startTime) / 1000.0 + " s");
		System.out.println("Maximum memory usage : " + MemoryLogger.getInstance().getMaxMemory() + " MB");

		System.out.println("******verify**********");
		System.out.println("true positive = " + tp);
		System.out.println("false positive = " + fp);
		System.out.println("total accurate = " + accList.size());
		System.out.println("");

		double recall = (double) tp / accList.size();
		double precision = (double) tp / (fp + tp);
		double f_score = (2 * recall * precision) / (recall + precision);
		System.out.println("********accuracy**********");
		System.out.println("recall = " + nf.format(recall));
		System.out.println("precision = " + nf.format(precision));
		System.out.println("f_score = " + nf.format(f_score));
	}

	/***************************** minor function ******************************/
	
	// when t-skip happened
	public void allSupportPlusOne(){
		int support;
		for (Itemset itemset : entryTable.getItemsets()) {
			support = itemset.getAbsoluteSupport();
			if(support != 0){ // in the beginning, support = 0
				itemset.setAbsoluteSupport(++support); // support + 1
			}
		}
		if(cmin != 0){
			cmin++; // because all entry ++
			//delta++;
		}
	}
	
	/* if pattern exist in table, support++ */
	public void supportIncreaseIfExist(int[] transaction, ArrayList<int[]> hasAppearedInTable) {
		int support;
		r = 0;

		for (Itemset itemset : entryTable.getItemsets()) {
			// check if the itemset is the subset of transaction. If yes, support++
			if (isSubsetOfTran(transaction, itemset.getItems())) {
				support = itemset.getAbsoluteSupport();
				itemset.setAbsoluteSupport(++support); // support + 1
				r++;
				hasAppearedInTable.add(itemset.getItems()); // store the itemset had appeared
			}
		}
	}

	/* It need to update Summary's information in order to realize the cmin(minimum support in table) mn (number of minimum entry) */
	public void updateInfoBeforeReplacement() {
		// step1. udpate cmin (minimum support)
		int tmp = entryTable.getItemsets().get(0).getAbsoluteSupport();
		for (Itemset itemset : entryTable.getItemsets()) {
			if (itemset.getAbsoluteSupport() < tmp) {
				tmp = itemset.getAbsoluteSupport(); // found the smallest support

			}
		}
		cmin = tmp;

		int number = 0;
		for (Itemset itemset : entryTable.getItemsets()) {
			if (itemset.getAbsoluteSupport() == cmin) {
				number++;
			}
		}
		mn = number;
	}

	public ArrayList<int[]> getCandidateSetCaseAB(int[] trans, ArrayList<int[]> hasAppearedInTable) {
		ArrayList<int[]> candidates = new ArrayList<int[]>();
		ArrayList<Integer> itemset = null;
		int length = trans.length;
		int total = 1 << length; // 1 left shit all, means there are 2^length patterns
		int[] pattern;

		// generate all combination
		for (int i = 1; i < total; i++) {
			itemset = new ArrayList<Integer>(); // create a new List
			// use 1010.... to select number
			for (int j = 0; j < length; j++) {
				if ((i & (1 << j)) != 0) {
					itemset.add(trans[j]);
				}
			}
			pattern = transferListToArray(itemset);
			// if pattern didn't exist in table, add it
			if (!isPatternAppeared(pattern, null, hasAppearedInTable)) {
				candidates.add(pattern); // insert a itemset
			}
		}
		return candidates;
	}

	public ArrayList<int[]> getCandidateSetCaseC(int[] trans, ArrayList<int[]> hasAppearedInTable, int mn) {
		ArrayList<int[]> candidates = new ArrayList<int[]>();
		ArrayList<Integer> itemset = null;
		int[] pattern;
		int length = trans.length;
		int total = 1 << length; // 1 left shit all, means there are 2^length patterns
		int quantity = 0;
		ArrayList<Integer> appearedList = new ArrayList<Integer>();
		ArrayList<Integer> notAppearedList = new ArrayList<Integer>();
		int[] classifiedTrans = new int[trans.length]; // this array is sorted by appeared and not appeared

		// classify appeared and not appeared
		for (int item : trans) {
			if (appearedItems.containsKey(item)) {
				appearedList.add(item);
			} else {
				notAppearedList.add(item);
			}
		}
		// System.out.println("appearedList:");
		// this.printIntegerList(appearedList);
		// System.out.println("notAppearedList:");
		// this.printIntegerList(notAppearedList);
		// System.out.println("appeared + not appeared : ");

		notAppearedList.addAll(appearedList);
		classifiedTrans = transferListToArray(notAppearedList);

		// this.printIntArray(classifiedTrans);

		// generate combination until getting mn patterns
		for (int i = 1; i < total; i++) {
			itemset = new ArrayList<Integer>(); // create a new List
			// use 1010.... to select number
			for (int j = 0; j < length; j++) {
				if ((i & (1 << j)) != 0) {
					itemset.add(classifiedTrans[j]);
				}
			}
			pattern = transferListToArray(itemset); // ArrayList<Integer> --> int[]
			Arrays.sort(pattern);
			if (!isPatternAppeared(pattern, null, hasAppearedInTable)) {
				candidates.add(pattern); // insert a itemset
				quantity++;
			}
			// complete getting mn patterns, return
			if (quantity >= mn) {
				break;
			}
		}
		return candidates;
	}

	public ArrayList<int[]> testgetCandidateSetCaseC(int[] trans, ArrayList<int[]> hasAppearedInTable, int mn) {
		ArrayList<int[]> candidates = new ArrayList<int[]>();
		ArrayList<Integer> itemset = null;
		int[] pattern;
		int length = trans.length;
		int total = 1 << length; // 1 left shit all, means there are 2^length patterns
		int quantity = 0;
		ArrayList<Integer> appearedList = new ArrayList<Integer>();
		ArrayList<Integer> notAppearedList = new ArrayList<Integer>();
		int[] classifiedTrans = new int[trans.length]; // this array is sorted by appeared and not appeared

		ArrayList<Integer> tranList = this.transferArrayToList(trans);
		Collections.shuffle(tranList);
		trans = this.transferListToArray(tranList);

//		System.out.println("shuffle list : ");
//		this.printIntArray(trans);

		// generate combination until getting mn patterns
		for (int i = 1; i < total; i++) {
			itemset = new ArrayList<Integer>(); // create a new List
			// use 1010.... to select number
			for (int j = 0; j < length; j++) {
				if ((i & (1 << j)) != 0) {
					itemset.add(trans[j]);
				}
			}
			pattern = transferListToArray(itemset); // ArrayList<Integer> --> int[]
			Arrays.sort(pattern);
			if (!isPatternAppeared(pattern, null, hasAppearedInTable)) {
				candidates.add(pattern); // insert a itemset
				quantity++;
			}
			// complete getting mn patterns, return
			if (quantity >= mn) {
				break;
			}
		}
		return candidates;
	}

	/* check whether pattern had appeared */
	public boolean isPatternAppeared(int[] pattern, ArrayList<int[]> candidates, ArrayList<int[]> hasAppearedInTable) {
		// 1. check candidates
		// if (candidates != null) {
		// for (int[] cand : candidates) {
		// if (isItemsetEqual(pattern, cand)) {
		// return true;
		// }
		// }
		// }
		// 2. check hasAppearedInTable
		if (hasAppearedInTable != null) {
			for (int[] appeared : hasAppearedInTable) {
				if (isItemsetEqual(pattern, appeared)) {
					return true;
				}
			}
		}

		return false; // pattern had not appeared
	}

	/* get random number of elements in the front of the shuffled list */
	public int[] getElementsInFront(ArrayList<Integer> list, int size) {
		Random random = new Random();
		int num = random.nextInt(size - 1) + 1; // random choose how many number of elements
		int[] pattern = new int[num];

		// get the list data
		for (int i = 0; i < num; i++) {
			pattern[i] = list.get(i);
		}
		// sort it
		Arrays.sort(pattern);

		return pattern;
	}

	/* get candidate pattern from a transaction */
	public ArrayList<int[]> getAllCombination(int[] data) {
		ArrayList<int[]> candidates = new ArrayList<int[]>();
		ArrayList<Integer> itemset = null;
		int length = data.length;
		int total = 1 << length; // 1 left shit all, means there are 2^length patterns

		// generate all combination
		for (int i = 1; i < total; i++) {
			itemset = new ArrayList<Integer>(); // create a new List
			// use 1010.... to select number
			for (int j = 0; j < length; j++) {
				if ((i & (1 << j)) != 0) {
					itemset.add(data[j]);
				}
			}
			candidates.add(transferListToArray(itemset)); // insert a itemset
		}
		return candidates;
	}

	/* used in case C, get the mn candidate pattern from a transacton */

	/* check if the itemset is the subset of transaction */
	public boolean isSubsetOfTran(int[] transaction, int[] itemset) {
		// first, check the length
		if (itemset != null && transaction.length >= itemset.length) { // itemset should not be null in the beginning
			// second, check subset
			for (int i : itemset) {
				if (!isItemContainInTran(transaction, i)) { // if a item doesn't contain, it's not subset
					return false;
				}
			}
		} else { // itemset is not subset of transaction
			return false;
		}

		return true; // itemset is a subset
	}

	/* determine which case is A B or C */
	public char whichCase(int cs, int mn) {
		char c;
		if (cs > mn)
			c = 'C';
		else if (cs == mn)
			c = 'B';
		else
			c = 'A';

		return c;

	}

	/* check if a item exist in transaction */
	public boolean isItemContainInTran(int[] transaction, int item) {
		for (int t : transaction) {
			if (item == t) {
				return true;
			}
		}
		return false;
	}

	/* use candidate to replace entry in table */
	public void replaceByCandidate(ArrayList<int[]> candidates) {
		ArrayList<Itemset> list = (ArrayList<Itemset>) entryTable.getItemsets();
		int index = -1; // recored current position in table
		Itemset itemset;
		int size = list.size();

		// find cmin entry to replace it
		for (int[] candidate : candidates) { // get a candidate
			// start from index, check each entry
			for (int i = index + 1; i < size; i++) {
				if (list.get(i).getAbsoluteSupport() == cmin) { // check cmin
					removeItemsFromMap(list.get(i).getItems());
					addItemsIntoMap(candidate);
					itemset = new Itemset(candidate);
					itemset.setAbsoluteSupport(delta + 1); // set the support
					list.set(i, itemset); // replace it by candidate pattern
					index = i; // recored current position
					break; // keep process next pattern
				}
			}
		}
	}

	// remove the items in the entry, which will be replaced
	public void removeItemsFromMap(int[] entry) {
		if (entry != null) {
			for (int item : entry) {
				int value = appearedItems.get(item) - 1;
				if (value == 0) { // check whether item will become 0
					appearedItems.remove(item);// the item's value is 0, delete
				} else {
					appearedItems.replace(item, value); // decrease
				}
			}
		}
	}

	// add the items in Map, which will insert into table
	public void addItemsIntoMap(int[] cands) {
		for (int item : cands) {
			if (appearedItems.containsKey(item)) { // check if it exist
				appearedItems.replace(item, appearedItems.get(item) + 1); // increase
			} else {
				appearedItems.put(item, 1); // first appear
			}
		}
	}

	/* if itemset had appeared in table, return the index in candidate */
	public int indexOfItemset(ArrayList<int[]> candidate, int[] itemset) {
		int index = -1; // suppose it's not exist
		// check each itemset in entry table whether it is exist in summary
		for (int[] arr : candidate) {
			if (isItemsetEqual(arr, itemset)) {
				return candidate.indexOf(arr); // index
			}
		}
		return index;
	}

	/* get the accurate answer from file */
	public ArrayList<int[]> getAccuratePattern(String accurateFile) throws IOException {
		ArrayList<int[]> accurateList = new ArrayList<int[]>();
		BufferedReader reader = new BufferedReader(new FileReader(accurateFile));
		String line;
		while ((line = reader.readLine()) != null) {
			// split the line according to spaces
			String[] lineSplited = line.split(" ");
			// create an array of int to store the items in this transaction
			int acc[] = new int[lineSplited.length];
			for (int i = 0; i < lineSplited.length; i++) {
				// transform this item from a string to an integer
				Integer item = Integer.parseInt(lineSplited[i]);
				// store the item in the memory representation of the database
				acc[i] = item;
			}
			// add acc into accurateList
			accurateList.add(acc);
		}
		return accurateList;

	}

	/* determine whether the two itemset is equal */
	public boolean isItemsetEqual(int[] cand, int[] itemset) {
		if (cand.length == itemset.length) { // if the size is equal, check the content.
			for (int i = 0; i < itemset.length; i++) {
				if (cand[i] != itemset[i]) { // if the content is different, return false
					return false;
				}
			}
			return true; // size and content is equal
		}
		return false; // size is not equal
	}

	/* transfer ArrayList<Integer> to int[] */
	public int[] transferListToArray(ArrayList list) {
		int[] array = new int[list.size()];
		// read Integer and store in array
		for (int i = 0; i < list.size(); i++) {
			array[i] = (int) list.get(i);
		}

		return array;
	}

	/* transfer int[] to ArrayList<Integer> */
	public ArrayList<Integer> transferArrayToList(int[] arr) {
		ArrayList<Integer> list = new ArrayList<Integer>();
		for (int i : arr) {
			list.add(i);
		}
		return list;
	}

	/* transfer the String array to int array */
	public int[] transferLineToArray(String line) {
		String[] lineSplited = line.split(" ");
		int[] tran = new int[lineSplited.length];

		for (int i = 0; i < lineSplited.length; i++) {
			tran[i] = Integer.parseInt(lineSplited[i]);
		}
		return tran;
	}

	/* update r, delta and cmin */
	public void updateTableInfo() {
		/** case A : **/
		if (cs < mn) {
			// do nothing
			delta = cmin;
		}
		/** case B : **/
		else if (cs == mn) {
			int tmp = delta + 1;
			delta = cmin;
			cmin = tmp;
		}
		/** case C : **/
		else if (cs > mn) {
			cmin = delta + 1;
			delta = delta + 1;
			numOfCaseC++;
		}
		whatCase = ' ';
		r = 0;
	}

	public void printSystemInfo() {
		System.out.println("algorithm : skip LCSS");
		System.out.println("data : " + inputFile);
		System.out.println("mst : " + mst);
		System.out.println("table size : " + tableSize);
	}

	/* print information of summary */
	public void printSummaryInfo() {
		System.out.println("r:\t" + r);
		System.out.println("cs:\t" + cs);
		System.out.println("cmin:\t" + cmin);
		System.out.println("mn:\t" + mn);
		System.out.println("delta:\t" + delta);
		System.out.println("numOfCaseC:\t" + numOfCaseC);
	}

	public void printCandidate() {
		for (int[] list : candidates) {
			for (int i : list) {
				System.out.print(i + " ");
			}
			System.out.println();
		}
	}

	public void printFP() {
		for (int[] list : miningResult) {
			for (int i : list) {
				System.out.print(i + " ");
			}
			System.out.println();
		}
	}

	public void printItems(HashMap map) {
		for (Object key : map.keySet()) {
			System.out.println("item : " + key + ", count = " + map.get(key));
		}
		System.out.println("=================");
	}

	public void printIntegerList(ArrayList<Integer> list) {
		for (int i : list) {
			System.out.println(i + " ");
		}
		System.out.println("=================");
	}

	public void printIntArray(int[] array) {
		for (int i : array) {
			System.out.println(i + " ");
		}
		System.out.println("=================");
	}

	public EntryTable getEntryTable() {
		return entryTable;
	}

	public void setEntryTable(EntryTable entryTable) {
		this.entryTable = entryTable;
	}

}
