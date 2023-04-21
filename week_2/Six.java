import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Six {
    public static void main(String[] args) throws IOException {
        printPairs(removeStopWords(addToFrequencies(readFile(args[0]))), 25);
    }

    public static String readFile(String fileName) throws IOException {
        InputStream is = new FileInputStream(fileName);
        int len = is.available();
        byte[] bytes = new byte[len];
        is.read(bytes);
        String words = new String(bytes).toLowerCase(Locale.ROOT);
        return words;
    }

    public static HashMap<String, Integer> addToFrequencies(String data){
        String pattern = "[a-z]{2,}";

        Pattern p = Pattern.compile(pattern);

        Matcher match = p.matcher(data);
        HashMap<String, Integer> map = new HashMap<>();
        while (match.find()){
            String cur = match.group();
            map.put(cur, map.getOrDefault(cur, 0)+1);
        }

        return map;
    }

    public static HashMap<String, Integer> removeStopWords(HashMap<String, Integer> map) throws IOException {
        InputStream is = new FileInputStream("../stop_words.txt");
        int len = is.available();
        byte[] bytes = new byte[len];
        is.read(bytes);
        String stopWords = new String(bytes);

        HashSet<String> stopSet = new HashSet<>();
        for (String s:stopWords.split(",")){
            stopSet.add(s);
        }

        for (String s:stopSet){
            if(map.containsKey(s))map.remove(s);
        }
        return map;
    }

    public static void printPairs(HashMap<String, Integer> map ,int num){
        PriorityQueue<AbstractMap.SimpleEntry<String, Integer>> pq = new PriorityQueue<>(Comparator.comparingInt(AbstractMap.SimpleEntry::getValue));

        for (String str:map.keySet()){
            if (pq.size()<num){
                pq.add(new AbstractMap.SimpleEntry<>(str, map.get(str)));
            } else {
                if(map.get(str)>pq.peek().getValue()){
                    pq.poll();
                    pq.add(new AbstractMap.SimpleEntry<>(str, map.get(str)));
                }
            }
        }

        ArrayList<AbstractMap.SimpleEntry<String, Integer>> list = new ArrayList<>();
        while(!pq.isEmpty()){
            AbstractMap.SimpleEntry<String, Integer> pair = pq.poll();
            list.add(pair);
        }

        for (int i=list.size()-1;i>=0;i--){
            AbstractMap.SimpleEntry<String, Integer> pair = list.get(i);
            System.out.println(pair.getKey()+" - "+pair.getValue());
        }
    }
}
