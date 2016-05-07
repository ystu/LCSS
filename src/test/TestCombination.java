package test;

public class TestCombination {
	public static void main(String[] args) {
		combination1();
	}

    public static void combination1() {

        String arr[] = { "a", "b", "c"};
        int all = arr.length;
        int nbit = 1 << all;  // 1 left shit all, means 2^all number
        for (int i = 0; i < nbit; i++) {
            StringBuffer sb = new StringBuffer();
            for (int j = 0; j < all; j++) {
                if ((i & (1 << j)) != 0) {
                    sb.append(arr[j]);
                }
            }
            System.out.println(sb);
        }
    }
    
	// C(m, n) = C(m-1, n-1) + C(m-1, n)
	public static String comb(char[] from, char[] to, int len, int m, int n) {
		String result = "";
		if (n == 0) {
			for (int i = 0; i < len; i++) {
				result += to[i];
			}
			result += "\n";
		} else {
			to[n - 1] = from[m - 1];

			if (m > n - 1) {
				result = comb(from, to, len, m - 1, n - 1);
			}
			if (m > n) {
				result = comb(from, to, len, m - 1, n) + result;
			}
		}
		return result;
	}
	
	public static  void Combination( ) {


        String[] str = {"a" , "b" ,"c","d"};
        int n = str.length;                                  //���葵���
        //瘙雿�蝏����葵�嚗�2^n
        int nbit = 1<<n;                                     // ��<<�� 銵函內 撌衣宏:������撌衣宏�撟脖����腺撘���‘0��:�瘙2^n=2Bit��
        System.out.println("�蝏���葵�銝綽��"+nbit);
        
        for(int i=0 ;i<nbit ; i++) {                        //蝏��bit銝芥��蝏��摮�憭扯�嚗颲0,1,2,3,....2^n��
            System.out.print("蝏���  "+i + " 撖孵���蛹嚗� ");
            for(int j=0; j<n ; j++) {                        //瘥葵�鈭���憭隞亙椰蝘裳甈∴�������������鈭���潔��
                int tmp = 1<<j ;        
                if((tmp & i)!=0) {                            //& 銵函內銝�舅銝芯�銝�1�嚗���蛹1
                    System.out.print(str[j]);
                }
            }
            System.out.println();
        }
    }
	

}
