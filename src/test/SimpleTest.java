package test;

import java.util.ArrayList;

import algorithms.StreamReduction;

public class SimpleTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int[] a = { 1, 2, 3 };
		int[] b = { 1, 2};

		StreamReduction sr = new StreamReduction(10,10);
		
		System.out.println("i = " + sr.getTotalLength());
		
		System.out.println(Math.floor((double)7/3));
//		ArrayList<Integer> list1 = new ArrayList<Integer>();
//
//		list1.add(1);
//		list1.add(2);
//		list1.add(3);
//		list1.add(4);
//		list1.add(5);
//		// System.out.println(list1.toArray() == list2.toArray()
//
//		for (Integer i : list1) {
//			System.out.println(list1.indexOf(i));
//		}
//		System.out.println("===========");
//		list1.remove(3);
//		for (Integer i : list1) {
//			System.out.println(list1.indexOf(i));
//		}
//		System.out.println(Math.pow(2, 30));

	}
}
