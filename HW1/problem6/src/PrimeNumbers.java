public class PrimeNumbers {
    public static void printPrimeNumbers(int m, int n) {
		// DO NOT change the skeleton code.
		// You can add codes anywhere you want.

        String out = "";

        for(int i=m; i<=n; i++){
            if(isPrime(i)){
                out = out + i + " ";
            }
        }
        out.trim();
        System.out.println(out);
    }

    public static boolean isPrime(int num){
        double threshold = Math.sqrt(num);

        if(num == 1) return false;

        for(int n=2; n <= threshold; n++){
            if(num % n == 0){
                return false;
            }
        }
        return true;
    }
}
