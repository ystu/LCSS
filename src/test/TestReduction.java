package test;

import java.io.IOException;

import algorithms.StreamReduction;

public class TestReduction {
	static int maxTableSize = 0;
	static double error = 0.01;
	static String dataset = "simple";
	static String input_path = "D:\\Data\\FPdataset\\";
	static String inputFile = input_path + dataset + ".txt";
	static int delta;  
	 
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		StreamReduction sr = new StreamReduction(maxTableSize,error);
		sr.testReduction(inputFile, delta);
		
	}

}
