import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Ten {
    static int num = 25;
    public static void main(String[] args) {
        new bindObj(args[0])
                .bind(new readFile())
                .bind(new addToFrequencies())
                .bind(new removeStopWords())
                .bind(new printPairs());
    }

    public static class bindObj {
        Object val;
        public bindObj(Object val){
            this.val = val;
        }

        public bindObj bind(Function func){
            this.val = func.apply(this.val);
            return this;
        }
    }

    public static class readFile implements Function<String, String>{

        @Override
        public String apply(String path) {
            String words = null;
            try {
                InputStream is = new FileInputStream(path);
                int len = is.available();
                byte[] bytes = new byte[len];
                is.read(bytes);
                words = new String(bytes).toLowerCase(Locale.ROOT);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return words;
        }
    }

    public static class addToFrequencies implements Function<String, HashMap<String, Integer>>{
        @Override
        public HashMap<String, Integer> apply(String data) {
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
    }

    public static class removeStopWords implements Function<HashMap<String, Integer>, HashMap<String, Integer>>{

        @Override
        public HashMap<String, Integer> apply(HashMap<String, Integer> map) {
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
            } catch (IOException e){
                e.printStackTrace();
            }

            return map;
        }
    }

    public static class printPairs implements Function<HashMap<String, Integer>, Object> {

        @Override
        public Object apply(HashMap<String, Integer> map) {
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
            return null;
        }
    }
}
