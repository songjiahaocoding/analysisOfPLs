import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Nine {
    static int num = 25;
    public static void main(String[] args) {
        new readFile().accept(args[0], new addToFrequencies());
    }

    public static class readFile implements BiConsumer<String, BiConsumer> {

        @Override
        public void accept(String path, BiConsumer func) {
            try {
                InputStream is = new FileInputStream(path);
                int len = is.available();
                byte[] bytes = new byte[len];
                is.read(bytes);
                String words = new String(bytes).toLowerCase(Locale.ROOT);
                func.accept(words, new removeStopWords());
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public static class addToFrequencies implements BiConsumer<String, BiConsumer> {

        @Override
        public void accept(String data, BiConsumer func) {
            String pattern = "[a-z]{2,}";

            Pattern p = Pattern.compile(pattern);

            Matcher match = p.matcher(data);
            HashMap<String, Integer> map = new HashMap<>();
            while (match.find()){
                String cur = match.group();
                map.put(cur, map.getOrDefault(cur, 0)+1);
            }

            func.accept(map, new printPairs());
        }
    }

    public static class removeStopWords implements BiConsumer<HashMap<String, Integer>, BiConsumer> {

        @Override
        public void accept(HashMap<String, Integer> map, BiConsumer func) {

            try {
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
                func.accept(map, null);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static class printPairs implements BiConsumer<HashMap<String, Integer>, BiConsumer> {

        @Override
        public void accept(HashMap<String, Integer> map, BiConsumer biConsumer) {
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
}
