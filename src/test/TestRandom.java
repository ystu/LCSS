package test;

import java.util.Random;

public class TestRandom {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Random random = new Random();
		random.setSeed(System.currentTimeMillis());
		for(int i=0; i<1; i++){
			System.out.print(random.nextInt(10) + " ");
		}
	}

}
