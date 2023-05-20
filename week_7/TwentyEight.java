import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TwentyEight {
    public static HashMap<String, Integer> wordFreqPairs = new HashMap<>();
    public static int count = 0;

    //28.2: yields an entire line and return the words in it as a stream
    public static Stream<String> allWordsStream(String path) throws IOException {
        //return all words in the book as a stream
        return Files.lines(Paths.get(path)).flatMap(o1 -> Arrays.stream(o1.toLowerCase().split("\\P{Alnum}"))).filter(o2 -> o2.length() >= 2);
    }

    public static Stream<String> nonStopWordsStream(String path) throws IOException {
        HashSet<String> stopSets = new HashSet<>();
        String data  = new String(Files.readAllBytes(Paths.get("../stop_words.txt"))).toLowerCase(Locale.ROOT);
        Matcher match = Pattern.compile("[a-z]{2,}").matcher(data);
        while (match.find()){
            String cur = match.group();
            stopSets.add(cur);
        }
        //return all non-stopwords in the book as a stream
        return allWordsStream(path).filter(o1 -> !stopSets.contains(o1));
    }

    //count and sort the non-stopwords in the stream and return the top25 every time we count 5000 words
    public static Stream<List<Map.Entry<String, Integer>>> countAndSortStream(String path) throws IOException {
        //return sorted word-frequency pairs in a list as a stream
        return nonStopWordsStream(path).map(o1 -> {
            wordFreqPairs.put(o1, wordFreqPairs.getOrDefault(o1, 0) + 1);
            count++;
            if (count % 5000 == 0) {
                return wordFreqPairs.entrySet().stream().sorted((o2, o3) -> o3.getValue() - o2.getValue()).collect(
                        Collectors.toList());
            } else {
                return null;
            }
        }).filter(Objects::nonNull);
    }

    public static void main(String[] args) {
        wordFreqPairs = new HashMap<>();
        try {

            countAndSortStream(args[0]).forEach(o1 -> {
                System.out.println("-----------------------------");
                o1.stream().limit(25).forEach(o2 -> {
                    System.out.println(o2.getKey() + "  -  " + o2.getValue());
                });
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}