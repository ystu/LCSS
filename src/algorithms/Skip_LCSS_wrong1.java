package algorithms;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import itemset_array_integers_with_count.Itemset;
import summary.EntryTable;

public class Skip_LCSS_wrong1 {
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
	ArrayList<ArrayList<Integer>> candidates = new ArrayList<ArrayList<Integer>>();
	ArrayList<ArrayList<Integer>> markedToRemove = null;
	EntryTable entryTable = new EntryTable("summary");
	char whatCase;

	/* constructor */
	public Skip_LCSS_wrong1(String inputFile, String verifyFile, double mst,
			int tableSize) {
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
		for (int i = 0; i < tableSize ; i++) {
			itemset = new Itemset(null);
			itemset.setAbsoluteSupport(0);
			entryTable.addItemset(itemset);
		}
//		for(Itemset it : entryTable.getItemsets()){
//			System.out.println(it.getItems() + " " + it.getAbsoluteSupport());
//		}
	}

	/* the core of algorithm */
	public void runSkipLCSS(String inputFile, String verifyFile, double mst, int tableSize) throws IOException {

		fr = new FileReader(inputFile);
		br = new BufferedReader(fr);
		
		// start read each line
		while ((line = br.readLine()) != null) {
			tran = transferLineToArray(line);
			candidates = getCandidatePattern(tran);
			// test indexOfItemset
			System.out.println("before update ... ");
			entryTable.printItemsets(1);
			updateSummary(candidates, tran.length);
			System.out.println("after update ... ");
			entryTable.printItemsets(1);
			printSummaryInfo();
		}

		br.close();
		fr.close();

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

	/* get candidate pattern from a transaction */
	@SuppressWarnings("null")
	public ArrayList<ArrayList<Integer>> getCandidatePattern(int[] data) {
		ArrayList<ArrayList<Integer>> candidates = new ArrayList<ArrayList<Integer>>();
		ArrayList<Integer> itemset = null;
		int length = data.length;
		int total = 1 << length; // 1 left shit all, means there are 2^total patterns
		
		// generate all combination
		for (int i = 1; i < total; i++) {
			itemset = new ArrayList<Integer>(); // create a new List
			// use 1010.... to select number
			for (int j = 0; j < length; j++) {
				if ((i & (1 << j)) != 0) {
					itemset.add(data[j]);
				}
			}
			candidates.add(itemset); // insert a itemset
		}
		return candidates;
	}

	/* update the summary by candidate pattern */
	public void updateSummary(ArrayList<ArrayList<Integer>> candidate, int length) {
		int[] array = null;
		Itemset itemset;	
		markedToRemove = new ArrayList<ArrayList<Integer>>(); // clear it
		
		supportIncreaseIfExist(candidate,markedToRemove); // if candidate exist in summary, support++
		udpateInfoBeforeReplacement(); // update cmin and mn 
		cs = (int) (Math.pow(2, length) - 1 - r); // number of candidate need inserted
		deleteExistItemsetInCandidate(candidate,markedToRemove); // delete exist pattern in candidate pattern
		whatCase = whichCase(cs,mn); //determine which case is
		// before replacing the entry by candidate pattern, deleting (cs-mn) number candidate pattern
		if (whatCase == 'C') { // if case C, choose mn number pattern in candidate pattern
			deleteRandomly(candidate, cs-mn); // delete (cs-mn) number of pattern
		}
		replaceByCandidate(); // use candidate to replace entry in table
		updateTableInfo(); // update r, delta and cmin
	}
	
	public EntryTable miningSummary() {
		return null;
	}
	
	public void verify(String verifyFile, EntryTable miningResult) {

	}

	public void printResult() {

	}
	
	/* use candidate to replace entry in table */
	public void replaceByCandidate(){
		Itemset itemset;
		int[] arr;
		//find cmin entry to replace it
		for(ArrayList<Integer> candidate : candidates){ // get a candidate
			for(Itemset entry : entryTable.getItemsets()){ // find cmin entry
				if(entry.getAbsoluteSupport() == cmin){
					entry.itemset = transferListToArray(candidate); // replacement
					entry.setAbsoluteSupport(delta + 1); // support = delta + 1
					break; // replace finish, check another candidate
				}
			}
		}
	}
	
	/* if pattern exist in table, support++ */
	public void supportIncreaseIfExist(ArrayList<ArrayList<Integer>> candidate, ArrayList<ArrayList<Integer>> markedToRemove){
		int index = -1;
		
		for (ArrayList<Integer> list : candidate) {
			// itemset exist in table, count++
			if ((index = indexOfItemset(list)) != -1) {
				entryTable.getItemsets().get(index).support++; // count++
				r++; // update information of summary
				markedToRemove.add(list); // This itemset should be remove later
			}
		}
	}
	
	/* delete exist pattern in candidate pattern */
	public void deleteExistItemsetInCandidate(ArrayList<ArrayList<Integer>> candidate, ArrayList<ArrayList<Integer>> markedToRemove){
		for (ArrayList<Integer> list : markedToRemove) {
			candidate.remove(list);
		}
	}
	
	/* check if the itemset exist in table, if exist, return index of itemset. otherwise, return -1. */
	public int indexOfItemset(ArrayList<Integer> candidate) {
		int index = -1; // suppose it's not exist
		// check each itemset in entry table whether it is exist in summary
		for (Itemset itemset : entryTable.getItemsets()) {
			if (itemset.getItems() != null && isItemsetEqual(candidate, itemset.getItems())) {
				return entryTable.getItemsets().indexOf(itemset); // index
			}
		}
		return index;
	}

	/* determine whether the two itemset is equal */
	public boolean isItemsetEqual(ArrayList<Integer> candidate, int[] entry) {
		if (candidate.size() == entry.length) { // if the size is equal, check the content.
			for (int i = 0; i < entry.length; i++) {
				if (candidate.get(i) != entry[i]) { // if the content is different, return false
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

	/* It need to update Summary's information in order to realize the cmin(minimum support in table) mn (number of minimum entry) */
	public void udpateInfoBeforeReplacement() {
		// step1. udpate cmin (minimum support)
		cmin++; // suppose all entry had +1
		for (Itemset itemset : entryTable.getItemsets()) {
			if (itemset.getAbsoluteSupport() < cmin) {
				cmin--; // found the smallest support
				break;
			}
		}
		// step2. update mn (number of minimum support)
		int number = 0;
		for (Itemset itemset : entryTable.getItemsets()) {
			if (itemset.getAbsoluteSupport() == cmin) {
				number++;
			}
		}
		mn = number;
	}

	/* determine which case is A B or C*/
	public char whichCase(int cs, int mn){
		char c;
		if(cs > mn)
			c = 'C';
		else if(cs == mn)
			c = 'B';
		else
			c = 'A';
		
		return c;
		
	}
	
	// unfinished
	/* delete (cs-mn) number of pattern, in order to replace */
	public void deleteRandomly(ArrayList<ArrayList<Integer>> candidate, int delNum){
		Random random = new Random();
		// delete entry
		for(int i=0 ; i<delNum ; i++){
			candidate.remove(Math.abs(random.nextInt()%(candidate.size()))); // remove randomly
		}
	}
	
	/* update r, delta and cmin */
	public void updateTableInfo(){		
		/** case A : **/
		if(whatCase == 'A'){
			// do nothing
		}
		/** case B : **/
		else if(whatCase == 'B'){
			int tmp = delta + 1;
			delta = cmin;
			cmin = tmp;
		}
		/** case C : **/
		else if(whatCase == 'C'){
			cmin = delta + 1;
			delta = delta + 1;
		}
		whatCase = ' ';
		r = 0;
	}
	
	/* print information of summary */
	public void printSummaryInfo(){
		System.out.println("r:\t" + r);
		System.out.println("cs:\t" + cs);
		System.out.println("cmin:\t" + cmin);
		System.out.println("mn:\t" + mn);
		System.out.println("delta:\t" + delta);
	}
	
	public void printCandidate(){
		for( ArrayList<Integer> list : candidates){
			for(int i : list){
				System.out.print(i + " ");
			}
			System.out.println();
		}
	}
	
	public EntryTable getEntryTable() {
		return entryTable;
	}

	public void setEntryTable(EntryTable entryTable) {
		this.entryTable = entryTable;
	}

}
