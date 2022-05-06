public class NumberCounter {
    public static void countNumbers(String str0, String str1, String str2) {
		// DO NOT change the skeleton code.
		// You can add codes anywhere you want.

        int int0 = Integer.parseInt(str0);
        int int1 = Integer.parseInt(str1);
        int int2 = Integer.parseInt(str2);
        int num = int0 * int1 * int2;

        int[] bucket = new int[10];

        while(num > 0){
            bucket[num % 10] += 1;
            num = num / 10;
        }

        int length = 0;

        for(int i=0; i < 10; i++){
            if(bucket[i] != 0){
                printNumberCount(i, bucket[i]);
                length += bucket[i];
            }
        }
        System.out.println("length: " + length);
    }

    private static void printNumberCount(int number, int count) {
        System.out.printf("%d: %d times\n", number, count);
    }
}
