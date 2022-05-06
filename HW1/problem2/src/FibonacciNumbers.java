public class FibonacciNumbers {
    public static void printFibonacciNumbers(int n) {
		// DO NOT change the skeleton code.
		// You can add codes anywhere you want.

        int[] values = new int[n];

        values[0] = 0;
        if(n > 1) values[1] = 1;

        for(int i=2; i < n; i++){
            values[i] = values[i-2] + values[i-1];
        }

        String out = "";
        int sum = 0;
        for(int j=0; j < n; j++){
            out += values[j];
            if(j != n-1){
                out += " ";
            }
            sum += values[j];
        }

        System.out.println(out);

        if(sum >= 100000){
            String sumStr = Integer.toString(sum);
            String lastFive = sumStr.substring(sumStr.length()-5);
            System.out.println("last five digits of sum = " + lastFive);
        }
        else System.out.println("sum = " + sum);
    }
}
