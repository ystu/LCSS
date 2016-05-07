package summary;

/* This file is copyright (c) 2008-2012 Philippe Fournier-Viger
 * 
 * This file is part of the SPMF DATA MINING SOFTWARE
 * (http://www.philippe-fournier-viger.com/spmf).
 * 
 * SPMF is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * SPMF is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with
 * SPMF. If not, see <http://www.gnu.org/licenses/>.
 */

import itemset_array_integers_with_count.Itemset;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class represents a set of itemsets, where an itemset is an array of integers with an associated support count. Itemsets are ordered by size. For example, level 1 means itemsets of size 1 (that
 * contains 1 item).
 * 
 * @author Philippe Fournier-Viger
 */
public class EntryTable {
	/** We store the itemsets in a list named "levels". **/
	private List<Itemset> Itemsets = new ArrayList<Itemset>();
	/** the total number of itemsets **/
	private int itemsetsCount = 0; // current size
	/** a name that we give to these itemsets (e.g. "frequent itemsets") */
	private String name;

	/**
	 * Constructor
	 * 
	 * @param name
	 *            the name of these itemsets
	 */
	public EntryTable(String name) {
		this.name = name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ca.pfv.spmf.patterns.itemset_array_integers_with_count.AbstractItemsets #printItemsets(int)
	 */
	public void printItemsets(int nbObject) {
		System.out.println(" ------- " + name + " -------");
		int patternCount = 0;

		// for each itemset
		for (Itemset itemset : Itemsets) {
			if (itemset.getItems() == null) {
				System.out.print("  pattern " + patternCount + ":  ");
				System.out.print("null ");
				
			} else {
				Arrays.sort(itemset.getItems());
				// print the itemset
				System.out.print("  pattern " + patternCount + ":  ");
				itemset.print();
			}
			// print the support of this itemset
			System.out.println("support :  " + itemset.getRelativeSupportAsString(nbObject));
			patternCount++;
			System.out.println("");
		}
		System.out.println(" --------------------------------");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ca.pfv.spmf.patterns.itemset_array_integers_with_count.AbstractItemsets #addItemset (ca.pfv.spmf.patterns.itemset_array_integers_with_count.Itemset, int)
	 */
	public void addItemset(Itemset itemset) {
		Itemsets.add(itemset);
		itemsetsCount++;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ca.pfv.spmf.patterns.itemset_array_integers_with_count.AbstractItemsets #getItemsetsCount()
	 */
	public int getItemsetsCount() {
		return itemsetsCount;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ca.pfv.spmf.patterns.itemset_array_integers_with_count.AbstractItemsets #setName(java.lang.String)
	 */
	public void setName(String newName) {
		name = newName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ca.pfv.spmf.patterns.itemset_array_integers_with_count.AbstractItemsets #decreaseItemsetCount()
	 */
	public void decreaseItemsetCount() {
		itemsetsCount--;
	}

	public List<Itemset> getItemsets() {
		return Itemsets;
	}

	public void setItemsets(List<Itemset> itemsets) {
		Itemsets = itemsets;
	}

}
