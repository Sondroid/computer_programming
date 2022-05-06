import java.util.Arrays;
import java.util.Scanner;

public class CharacterCounter {
    public static void countCharacter(String str) {
		// DO NOT change the skeleton code.
		// You can add codes anywhere you want.

        int[] uppercaseBucket = new int[26];
        int[] lowercaseBucket = new int[26];

        for(int i=0; i < str.length(); i++){
            int ascii =(int) str.charAt(i);
            if(ascii <= 90 && ascii >= 65){
                uppercaseBucket[ascii-65] += 1;
            }
            else{
                lowercaseBucket[ascii-97] += 1;
            }
        }

        for(int j=0; j<26; j++){
            if(uppercaseBucket[j] == 0 && lowercaseBucket[j] == 0){
                ;
            }
            else if(uppercaseBucket[j] != 0 && lowercaseBucket[j] != 0){
                printCountBoth((char) (j+65), uppercaseBucket[j], (char) (j+97), lowercaseBucket[j]);
            }
            else if(uppercaseBucket[j] == 0 && lowercaseBucket[j] != 0){
                printCount((char) (j+97), lowercaseBucket[j]);
            }
            else if(uppercaseBucket[j] != 0 && lowercaseBucket[j] == 0){
                printCount((char) (j+65), uppercaseBucket[j]);
            }
        }

    }

    private static void printCount(char character, int count) {
        System.out.printf("%c: %d times\n", character, count);
    }
    private static void printCountBoth(char upper, int upcnt, char lower, int lwcnt) {
        System.out.printf("%c: %d times, %c: %d times\n", upper, upcnt, lower, lwcnt);
    }
}
