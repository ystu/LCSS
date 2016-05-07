package algorithms;

import java.io.BufferedReader;
//import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import itemset_array_integers_with_count.Itemset;
import summary.EntryTable;
import tools.MemoryLogger;

public class Rskip extends Skip_LCSS{

	public Rskip(String inputFile, String outputFile, String verifyFile, double[] mst, int tableSize, int maxItemTableSize, double error, boolean isReduce) {
		super(inputFile, outputFile, verifyFile, mst, tableSize,tableSize,error,isReduce);
	}
//	private int currentTime;
//	private EntryTable summary;
//	private FileReader fr;
//	private BufferedReader br;
//	private int r;
//	private int cs;
//	private int cmin;
//	private int mn;
//	private int delta;
//	private String inputFile;
//	private String verifyFile;
//	private double[] mst;
//	private int tableSize;
//	private String line;
//	private int[] tran;
//	ArrayList<int[]> candidates = new ArrayList<int[]>();
//	ArrayList<int[]> hasAppearedInTable = null;
//	EntryTable entryTable = new EntryTable("summary");
//	char whatCase;
//	ArrayList<int[]> miningResult = new ArrayList<int[]>();
//	ArrayList<int[]> accList;
//	int fp = 0, tp = 0;
//	int numOfCaseC = 0;
//	long startTime;
//	long endTime;
//	int maxItemTableSize;
//	double error;
//	boolean isReduce;
//
//	/* constructor */
//	public Rskip(String inputFile, String verifyFile, double[] mst, int tableSize, int maxItemTableSize, double error, boolean isReduce) {
//		this.inputFile = inputFile;
//		this.verifyFile = verifyFile;
//		this.mst = mst;
//		this.tableSize = tableSize;
//		this.mn = tableSize;
//		this.maxItemTableSize = maxItemTableSize;
//		this.error = error;
//		this.isReduce = isReduce;
//		InitialEntryTable(tableSize);
//	}
//
//	/* initial a fix size table */
//	public void InitialEntryTable(int tableSize) {
//		Itemset itemset;
//		for (int i = 0; i < tableSize; i++) {
//			itemset = new Itemset(null);
//			itemset.setAbsoluteSupport(0);
//			entryTable.addItemset(itemset);
//		}
//	}
//
//	/** the core of algorithm **/
//	@Override
//	public void runSkipLCSS(String inputFile, String verifyFile, double[] mst, int tableSize) throws IOException {
//		fr = new FileReader(inputFile);
//		br = new BufferedReader(fr);
//		int num = 0;
//		startTime = System.currentTimeMillis();
//		StreamReduction sr = new StreamReduction(maxItemTableSize,error);
//		
//		MemoryLogger.getInstance().reset();
//		// start read each line
//		while ((line = br.readLine()) != null) {
//			tran = transferLineToArray(line); // get the transaction int[]
//			System.out.println("Line: " + ++num);
//			/* stream reduction */
//			if(isReduce){
//				tran = sr.reduction(tran, delta);
//			}
//			Arrays.sort(tran); // sort by lexicographic order
//			updateSummary(tran, hasAppearedInTable);
//		}
//		// end time 
//		endTime = System.currentTimeMillis();
//		// we check the memory usage
//		MemoryLogger.getInstance().checkMemory();
//		// print info
//		this.printSystemInfo();
//		this.printSummaryInfo();
//		
//		// print performance
//		printPerformance();
//		
//		// mining several in 
//		for(int i=0; i<mst.length; i++){
//			miningResult = miningSummary(entryTable, mst[i] * num);
//			verify(miningResult, verifyFile + mst[i] + ".txt");// verify by correct answer
//			printAccuracy(mst[i]);// evalute recall, precision by fp, tp and totalAccurate. Print it.
//		}
//
//		br.close();
//		fr.close();
//	}

	/* update the summary by candidate pattern */
	@Override
	public void updateSummary(int[] transaction, ArrayList<int[]> hasAppearedInTable) {
		hasAppearedInTable = new ArrayList<int[]>();

		supportIncreaseIfExist(transaction, hasAppearedInTable); // if candidate exist in summary, support++
		updateInfoBeforeReplacement(); // update cmin and mn
		cs = (int) (Math.pow(2, transaction.length) - 1 - r); // number of candidate need inserted
		// find candidate pattern in order to replace in
		if (cs <= mn) { // case A or B
			candidates = getCandidateSetCaseAB(transaction, hasAppearedInTable); //
			replaceByCandidate(candidates); // use candidate to replace entry in table
		}
		updateTableInfo(); // update r, delta and cmin
	}

	/* mining frequent pattern in Summary */
//	public ArrayList<int[]> miningSummary(EntryTable table, double threshold) {
//		ArrayList<int[]> list = new ArrayList<int[]>();
//		// check each entry
//		for (Itemset itemset : table.getItemsets()) {
//			if (itemset.getAbsoluteSupport() >= threshold) {
//				list.add(itemset.getItems());
//			}
//		}
//		return list;
//	}
//
//	public void verify(ArrayList<int[]> miningResult, String accurateFile) throws IOException {
//		// step1. read accurateFile and store in Itemsets
//		accList = getAccuratePattern(accurateFile);
//
//		// step2. read each pattern in appro and compare accurate, count the total patterns, true positive and false positive
//		tp = 0;
//		fp = 0;
//		for (int[] appro : miningResult) {
//			// check if int[] is accurate
//			if (indexOfItemset(accList, appro) == -1)
//				fp++; // pattern is wrong
//			else
//				tp++; // pattern is right
//		}
//
//	}
//	
//	public void printPerformance(){
//		System.out.println("******** run time & memory usage ***********");
//		System.out.println("run time : " + (endTime - startTime) / 1000.0 + " s");
//		System.out.println("Maximum memory usage : " + MemoryLogger.getInstance().getMaxMemory() + " MB");
//		System.out.println();
//	}
//	
//	public void printAccuracy(double thres) {
//		NumberFormat nf = NumberFormat.getInstance();
//		nf.setMaximumFractionDigits(3); // 小數後3位
//		System.out.println("********** mst = " + thres + " *************");
//		System.out.println("===== verify =====");
//		System.out.println("true positive = " + tp);
//		System.out.println("false positive = " + fp);
//		System.out.println("total accurate = " + accList.size());
//		
//
//		double recall = (double) tp / accList.size();
//		double precision = (double) tp / (fp + tp);
//		double f_score = (2 * recall * precision) / (recall + precision);
//		System.out.println("===== accuracy =====");
//		System.out.println("recall = " + nf.format(recall));
//		System.out.println("precision = " + nf.format(precision));
//		System.out.println("f_score = " + nf.format(f_score));
//		System.out.println();
//	}
//
//	/***************************** minor function ******************************/
//
//	/* if pattern exist in table, support++ */
//	public void supportIncreaseIfExist(int[] transaction, ArrayList<int[]> hasAppearedInTable) {
//		int index = -1;
//		int support;
//		r = 0;
//
//		for (Itemset itemset : entryTable.getItemsets()) {
//			// check if the itemset is the subset of transaction. If yes, support++
//			if (isSubsetOfTran(transaction, itemset.getItems())) {
//				support = itemset.getAbsoluteSupport();
//				itemset.setAbsoluteSupport(++support); // support + 1
//				r++;
//				hasAppearedInTable.add(itemset.getItems()); // store the itemset had appeared
//			}
//		}
//	}
//
//	/* It need to update Summary's information in order to realize the cmin(minimum support in table) mn (number of minimum entry) */
//	public void updateInfoBeforeReplacement() {
//		// step1. udpate cmin (minimum support)
//		int tmp = entryTable.getItemsets().get(0).getAbsoluteSupport(); // get the first count
//		for (Itemset itemset : entryTable.getItemsets()) { // finding minimal support
//			if (itemset.getAbsoluteSupport() < tmp) {
//				tmp = itemset.getAbsoluteSupport(); // found the smallest support
//
//			}
//		}
//		cmin = tmp;
//		// cmin++; // suppose all entry had +1
//		// for (Itemset itemset : entryTable.getItemsets()) {
//		// if (itemset.getAbsoluteSupport() < cmin) {
//		// cmin--; // found the smallest support
//		// break;
//		// }
//		// }
//		// step2. update mn (number of minimum support)
//		int number = 0;
//		for (Itemset itemset : entryTable.getItemsets()) {
//			if (itemset.getAbsoluteSupport() == cmin) {
//				number++;
//			}
//		}
//		mn = number;
//	}
//
//	public ArrayList<int[]> getCandidateSetCaseAB(int[] trans, ArrayList<int[]> hasAppearedInTable) {
//		ArrayList<int[]> candidates = new ArrayList<int[]>();
//		ArrayList<Integer> itemset = null;
//		int length = trans.length;
//		int total = 1 << length; // 1 left shit all, means there are 2^length patterns
//		int[] pattern;
//
//		// generate all combination
//		for (int i = 1; i < total; i++) {
//			itemset = new ArrayList<Integer>(); // create a new List
//			// use 1010.... to select number
//			for (int j = 0; j < length; j++) {
//				if ((i & (1 << j)) != 0) {
//					itemset.add(trans[j]);
//				}
//			}
//			pattern = transferListToArray(itemset);
//			// if pattern didn't exist in table, add it
//			if (!isPatternAppeared(pattern, null, hasAppearedInTable)) {
//				candidates.add(pattern); // insert a itemset
//			}
//		}
//		return candidates;
//	}
//
//
//	/* check whether pattern had appeared */
//	public boolean isPatternAppeared(int[] pattern, ArrayList<int[]> candidates, ArrayList<int[]> hasAppearedInTable) {
//		// 1. check candidates
////		if (candidates != null) {
////			for (int[] cand : candidates) {
////				if (isItemsetEqual(pattern, cand)) {
////					return true;
////				}
////			}
////		}
//		// 2. check hasAppearedInTable
//		if (hasAppearedInTable != null) {
//			for (int[] appeared : hasAppearedInTable) {
//				if (isItemsetEqual(pattern, appeared)) {
//					return true;
//				}
//			}
//		}
//
//		return false; // pattern had not appeared
//	}
//
//
//	/* get candidate pattern from a transaction */
//	public ArrayList<int[]> getAllCombination(int[] data) {
//		ArrayList<int[]> candidates = new ArrayList<int[]>();
//		ArrayList<Integer> itemset = null;
//		int length = data.length;
//		int total = 1 << length; // 1 left shit all, means there are 2^length patterns
//
//		// generate all combination
//		for (int i = 1; i < total; i++) {
//			itemset = new ArrayList<Integer>(); // create a new List
//			// use 1010.... to select number
//			for (int j = 0; j < length; j++) {
//				if ((i & (1 << j)) != 0) {
//					itemset.add(data[j]);
//				}
//			}
//			candidates.add(transferListToArray(itemset)); // insert a itemset
//		}
//		return candidates;
//	}
//
//	/* determine which case is A B or C */
//	public char whichCase(int cs, int mn) {
//		char c;
//		if (cs > mn)
//			c = 'C';
//		else if (cs == mn)
//			c = 'B';
//		else
//			c = 'A';
//
//		return c;
//
//	}
//
//	/* check if the itemset is the subset of transaction */
//	public boolean isSubsetOfTran(int[] transaction, int[] itemset) {
//		// first, check the length
//		if (itemset != null && transaction.length >= itemset.length) { // itemset should not be null in the beginning
//			// second, check subset
//			for (int i : itemset) {
//				if (!isItemContainInTran(transaction, i)) { // if a item doesn't contain, it's not subset
//					return false;
//				}
//			}
//		} else { // itemset is not subset of transaction
//			return false;
//		}
//
//		return true; // itemset is a subset
//	}
//
//	/* check if a item exist in transaction */
//	public boolean isItemContainInTran(int[] transaction, int item) {
//		for (int t : transaction) {
//			if (item == t) {
//				return true;
//			}
//		}
//		return false;
//	}
//
//	/* use candidate to replace entry in table */
//	@Override
//	public void replaceByCandidate(ArrayList<int[]> candidates) {
//		ArrayList<Itemset> list = (ArrayList<Itemset>) entryTable.getItemsets();
//		int index = -1; // recored current position in table
//		Itemset itemset;
//		int size = list.size();
//
//		// find cmin entry to replace it
//		for (int[] candidate : candidates) { // get a candidate
//			// start from index, check each entry
//			for (int i = index + 1; i < size; i++) {
//				if (list.get(i).getAbsoluteSupport() == cmin) { // check cmin
//					itemset = new Itemset(candidate);
//					itemset.setAbsoluteSupport(delta + 1); // set the support
//					list.set(i, itemset); // replace it by candidate pattern
//					index = i; // recored current position
//					break; // keep process next pattern
//				}
//			}
//		}
//	}
//
//	/* if itemset had appeared in table, return the index in candidate */
//	public int indexOfItemset(ArrayList<int[]> candidate, int[] itemset) {
//		int index = -1; // suppose it's not exist
//		// check each itemset in entry table whether it is exist in summary
//		for (int[] arr : candidate) {
//			if (isItemsetEqual(arr, itemset)) {
//				return candidate.indexOf(arr); // index
//			}
//		}
//		return index;
//	}
//
//	/* get the accurate answer from file */
//	public ArrayList<int[]> getAccuratePattern(String accurateFile) throws IOException {
//		ArrayList<int[]> accurateList = new ArrayList<int[]>();
//		BufferedReader reader = new BufferedReader(new FileReader(accurateFile));
//		String line;
//		while ((line = reader.readLine()) != null) {
//			// split the line according to spaces
//			String[] lineSplited = line.split(" ");
//			// create an array of int to store the items in this transaction
//			int acc[] = new int[lineSplited.length];
//			for (int i = 0; i < lineSplited.length; i++) {
//				// transform this item from a string to an integer
//				Integer item = Integer.parseInt(lineSplited[i]);
//				// store the item in the memory representation of the database
//				acc[i] = item;
//			}
//			// add acc into accurateList
//			accurateList.add(acc);
//		}
//		return accurateList;
//
//	}
//
//	/* determine whether the two itemset is equal */
//	public boolean isItemsetEqual(int[] cand, int[] itemset) {
//		if (cand.length == itemset.length) { // if the size is equal, check the content.
//			for (int i = 0; i < itemset.length; i++) {
//				if (cand[i] != itemset[i]) { // if the content is different, return false
//					return false;
//				}
//			}
//			return true; // size and content is equal
//		}
//		return false; // size is not equal
//	}
//
//	/* transfer ArrayList<Integer> to int[] */
//	public int[] transferListToArray(ArrayList list) {
//		int[] array = new int[list.size()];
//		// read Integer and store in array
//		for (int i = 0; i < list.size(); i++) {
//			array[i] = (int) list.get(i);
//		}
//
//		return array;
//	}
//
//	/* transfer int[] to ArrayList<Integer> */
//	public ArrayList<Integer> transferArrayToList(int[] arr) {
//		ArrayList<Integer> list = new ArrayList<Integer>();
//		for (int i : arr) {
//			list.add(i);
//		}
//		return list;
//	}
//
//	/* transfer the String array to int array */
//	public int[] transferLineToArray(String line) {
//		String[] lineSplited = line.split(" ");
//		int[] tran = new int[lineSplited.length];
//
//		for (int i = 0; i < lineSplited.length; i++) {
//			tran[i] = Integer.parseInt(lineSplited[i]);
//		}
//		return tran;
//	}
//
//	/* update r, delta and cmin */
//	public void updateTableInfo() {
//		// step1. udpate cmin by scanning table
//		int tmp = entryTable.getItemsets().get(0).getAbsoluteSupport(); // get the first count
//		for (Itemset itemset : entryTable.getItemsets()) { // finding minimal support
//			if (itemset.getAbsoluteSupport() < tmp) {
//				tmp = itemset.getAbsoluteSupport(); // found the smallest support
//
//			}
//		}
//		cmin = tmp;
//
//		// step2. update delta
//		/** case A : **/
//		if (cs < mn) {
//			delta = cmin;
//		}
//		/** case B : **/
//		else if (cs == mn) {
//			// int tmp = delta + 1;
//			delta = cmin;
//			// cmin = tmp;
//		}
//		/** case C : **/
//		else if (cs > mn) {
//			// r-skip, when case C, do nothing
//			// cmin = delta + 1;
//			delta = delta + 1;
//			numOfCaseC++;
//		}
//		whatCase = ' ';
//		r = 0;
//	}
//
//	public void printSystemInfo() {
//		System.out.println("algorithm : " + this.getClass().getName());
//		System.out.println("data : " + inputFile);
//		System.out.println("table size : " + tableSize);
//		System.out.println();
//	}
//	
//	/* print information of summary */
//	public void printSummaryInfo() {
//		System.out.println("r:\t" + r);
//		System.out.println("cs:\t" + cs);
//		System.out.println("cmin:\t" + cmin);
//		System.out.println("mn:\t" + mn);
//		System.out.println("delta:\t" + delta);
//		System.out.println("numOfCaseC:\t" + numOfCaseC);
//		System.out.println();
//	}
//
//	public void printCandidate() {
//		for (int[] list : candidates) {
//			for (int i : list) {
//				System.out.print(i + " ");
//			}
//			System.out.println();
//		}
//	}
//
//	public void printFP() {
//		for (int[] list : miningResult) {
//			for (int i : list) {
//				System.out.print(i + " ");
//			}
//			System.out.println();
//		}
//	}
//
//	public EntryTable getEntryTable() {
//		return entryTable;
//	}
//
//	public void setEntryTable(EntryTable entryTable) {
//		this.entryTable = entryTable;
//	}

}
