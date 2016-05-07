package test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import tools.MemoryLogger;
import algorithms.Rskip;
import algorithms.Skip_LCSS;
import algorithms.T2andRskip;

public class TestSkipLCSS {
	private static String input_path = "D:\\Data\\FPdataset\\";
	private static String output_path = "D:\\Data\\FPdataset\\output\\";
	private static String verify_path = "D:\\Data\\FPdataset\\verify\\";
//	static double[] mst = {0.02, 0.01, 0.009, 0.008, 0.007, 0.006, 0.005, 0.004, 0.003, 0.002};
//	static double[] mst = {0.02, 0.01, 0.009, 0.008, 0.007, 0.006, 0.005, 0.004, 0.003, 0.002, 0.001}; 
	static double[] mst = {0.05, 0.04, 0.03, 0.02, 0.01};  
	static int tableSize = 5000000;  // 0.5M=500000  1M=1000000  1.5M=1500000 2M=2000000 5M=5000000  10M=10000000
	static String dataset = "retail";  // retail  retail_1000  kosarak_1000  BMS1  BMS2  T10I4D100K
	static String inputFile = input_path + dataset + ".txt";
	static String verifyFile = verify_path + dataset + "_mst=";// + mst + ".txt";
	static double error = 0.05;
	static boolean isReduce = true; // choose whether reduction or not   ... true  false
	static String algorithm = "rskip"; // skipLCSS  rskip  t2andRskip
	static String outputFile = output_path + "[ArrayList] " + algorithm + "_" +  dataset + "_" + (double)tableSize/1000000 + "M_";
	
	public static void main(String[] arg) throws IOException{
		switch(algorithm){
			case "skipLCSS":
				Skip_LCSS skipLCSS = new Skip_LCSS(inputFile, outputFile, verifyFile, mst, tableSize,tableSize,error,isReduce);
				skipLCSS.runSkipLCSS(inputFile, outputFile, verifyFile, mst, tableSize);
				break;
				
			case "rskip":
				Rskip rskip = new Rskip(inputFile, outputFile, verifyFile, mst, tableSize,tableSize,error,isReduce);
				rskip.runSkipLCSS(inputFile, outputFile, verifyFile, mst, tableSize);
				break;
				
			case "t2andRskip":
				T2andRskip t2andRskip = new T2andRskip(inputFile, outputFile, verifyFile, mst, tableSize,tableSize,error,isReduce);
				t2andRskip.runSkipLCSS(inputFile, outputFile, verifyFile, mst, tableSize);
				break;
		}
		
	}
	
	public static void printSystemInfo() {
		System.out.println("******** program information ***********");
		System.out.print("algorithm : " + algorithm);
		System.out.println( (isReduce == true) ? " + reduction " : "" );
		System.out.println("data : " + dataset);
		System.out.println("table size : " + (double)tableSize/1000000 + "M");
		System.out.println("data structure : ArrayList ");
		System.out.println();
	}
}
