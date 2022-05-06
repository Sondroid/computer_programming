public class DecreasingString {
    public static void printLongestDecreasingSubstringLength(String inputString) {
		// DO NOT change the skeleton code.
		// You can add codes anywhere you want.

        int count = 1;

        int ptr = 1;
        int maxCount = 1;

        while(ptr < inputString.length()){
            if(inputString.charAt(ptr-1) > inputString.charAt(ptr)){
                count += 1;
                if(count > maxCount) maxCount = count;
            } else count = 1;

            ptr += 1;
        }

        System.out.println(maxCount);
    }
}
