import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Pair<K, V>{
    public K key;
    public V val;

    public Pair() {
        this.key = null;
        this.val = null;
    }

    public Pair(K key, V value) {
        this.key = key;
        this.val = value;
    }

    public void setKey(K key) {
        this.key = key;
    }

    public void setVal(V val) {
        this.val = val;
    }
}

@SuppressWarnings("unchecked")
public class TwentySeven{

    public static void main(String[] args) throws IOException {
        Pair<List<String>, Object> allWords = new Pair(new ArrayList<>(), null);
        Pair<HashSet<String>, Object> stopSets = new Pair(new HashSet<>(), null);
        Pair<Map<String, Integer>, Object> wordFreq = new Pair<>(new HashMap<>(),
                (Supplier) () ->{
                        Map<String, Integer>  map = new HashMap<>();
                        for(String str:allWords.key){
                            if(!stopSets.key.contains(str)){
                                map.put(str, map.getOrDefault(str, 0)+1);
                            }
                        }
                        return map;
                });

        Pair<List<Pair<String, Integer>>, Object> sorted = new Pair<>(
                new ArrayList<>(), (Supplier) () -> {
                ArrayList<Pair<String, Integer>> list = new ArrayList<>();
                PriorityQueue<Pair<String, Integer>> pq = new PriorityQueue<>((e1, e2)->e2.val-e1.val);
                for (String str:wordFreq.key.keySet()){
                    int val = wordFreq.key.get(str);
                    pq.offer(new Pair<>(str, val));
                }

                while (!pq.isEmpty()){
                    list.add(pq.poll());
                }
                return list;
        });

        List<Pair> allColumns = new ArrayList<>();
        allColumns.add(allWords);
        allColumns.add(stopSets);
        allColumns.add(wordFreq);
        allColumns.add(sorted);

        String stopData = new String(Files.readAllBytes(Paths.get("../stop_words.txt"))).toLowerCase(Locale.ROOT);
        Matcher match = Pattern.compile("[a-z]{2,}").matcher(stopData);
        while (match.find()){
            String cur = match.group();
            stopSets.key.add(cur);
        }

        String data = new String(Files.readAllBytes(Paths.get(args[0]))).toLowerCase(Locale.ROOT);
        match = Pattern.compile("[a-z]{2,}").matcher(data);
        while (match.find()){
            String cur = match.group();
            allWords.key.add(cur);
        }

        update(allColumns);

        for (int i=0;i<25;i++){
            Pair<String, Integer> p = sorted.key.get(i);
            System.out.println(p.key+"   "+p.val);
        }
    }

    public static void update(List<Pair> list){
        for(Pair entry: list){
            if(entry.val!=null) entry.setKey(((Supplier)entry.val).get());
        }
    }
}

