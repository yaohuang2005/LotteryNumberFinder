import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
   Using DP to list lottery numbers from a text file
 
  Usage: java LotteryNumberFinder test.txt
 
 samples:
 [ "569815571556",
 “4938532894754”,
 “1234567”,
 “472844278465445”]

 Your function should return:

 4938532894754 -> 49 38 53 28 9 47 54
 1234567 -> 1 2 3 4 5 6 7

 */

public class LotteryNumberFinder
{
    // for memorization in DP
    private static HashMap<String, List<String>> PROCESSED_TEXT_NUMBER_MAP = new HashMap<>();

/*
        A DP approach to find all segments, but ensure segment values is [1, 59]
*/
    private List<String> segmentDigits(String text){
        // DP core idea:  if already computed the current substring text, then return from map
        if (PROCESSED_TEXT_NUMBER_MAP.containsKey(text)) {
            return PROCESSED_TEXT_NUMBER_MAP.get(text);
        }

        List<String>  result = new ArrayList<>();

        if (!text.isEmpty() && text.length() <= 2) {
            int value = Integer.valueOf(text);
            if (value > 0 && value < 60) {
                result.add(text);
            }
        }

        // try each prefix (1, or 2 integer char) and extend
        // use count 2 to ensure every prefix has maximum 2 digits [1, 59]
        int count = 2;
        for (int i = 0, j = 0; i< text.length() && j < count; i++, j++) {
            String prefix = text.substring(0, i + 1);
            int value = Integer.valueOf(prefix);
            if (value > 0 && value < 60) {
                //extend to suffix
                String suffix = text.substring(i + 1);
                List<String> subRes = segmentDigits(suffix);

                for (String word : subRes) {
                    result.add(prefix + " " + word);
                }
            }
        }

        PROCESSED_TEXT_NUMBER_MAP.put(text, result);
        return result;
    }

    private List<String> processLine(String text) {
        List<String> lotteryNumbers = new ArrayList<>();

        // only need to process valid string
        if (text.length() >= 7 && text.length() <= 13) {
            // step 1: break digits (like word segement) by DP
            List<String> allSegmentedStrings = segmentDigits(text);

            // step 2: post process the numbers to validate the segements line
            for (String str : allSegmentedStrings) {
                String[] arr = str.split(" ");
                // check duplicate item: 56 9 8 15 57 15 56  (this is not valid, because two "56" in the string
                if (arr.length == 7) {
                    // ensure all 7 items are unique
                    boolean duplicated = false;
                    String[] clonedArr = arr.clone();
                    Arrays.sort(clonedArr);
                    for (int i = 0; i < 6; i++) {
                        if (clonedArr[i].equalsIgnoreCase(clonedArr[i+1])) {
                            duplicated = true;
                            break;
                        }
                    }

                    if (!duplicated) {
                        lotteryNumbers.add(str);
                    }
                }
            }
        }

        return lotteryNumbers;
    }

    private List<String> readLines(String fileName) {
        List<String> allLines = new ArrayList<>();

        try {
            allLines = Files.readAllLines(Paths.get(fileName));
        } catch (IOException e) {
            System.out.println("cannot open the file " + fileName);
        }

        return allLines;
    }

    private void findLotteryNumber(String fileName) {

        List<String> allLines = readLines(fileName);

        for (String line : allLines) {

            List<String> lotteryNumberRows = processLine(line.trim());
            System.out.println("input: " + line);
            if (lotteryNumberRows.isEmpty()) {
                System.out.println("output: no valid lottery number from the input");
            } else {
                for (String lotteryNumber : lotteryNumberRows) {
                    System.out.println("output: " + lotteryNumber);
                }
            }
            System.out.println();
        }
    }

    public static void main (String args[])
    {
        LotteryNumberFinder finder = new LotteryNumberFinder();
        if (args.length < 1) {
            System.out.println("Usage: java LotteryNumberFinder [fileName]");
        }

        finder.findLotteryNumber(args[0]);
    }
}
