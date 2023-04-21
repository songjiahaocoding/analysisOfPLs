import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Eight{
    public static void main(String[] args) throws IOException {
        HashMap<String, Integer> map = new HashMap<>();
        HashMap<String, Integer> set = new HashMap<>();
        buildFreqs(map, new String(Files.readAllBytes(Paths.get(args[0]))).toLowerCase(Locale.ROOT));
        buildFreqs(set, new String(Files.readAllBytes(Paths.get("../stop_words.txt"))).toLowerCase(Locale.ROOT));
        for (String s:set.keySet())if(map.containsKey(s))map.remove(s);
        ArrayList<AbstractMap.SimpleEntry<String, Integer>> list = new ArrayList<>();
        for (String str:map.keySet())list.add(new AbstractMap.SimpleEntry<>(str, map.get(str)));
        sort(list);
        for (int i=0;i<25;i++) System.out.println(list.get(list.size()-i-1).getKey()+" - "+list.get(list.size()-i-1).getValue());
    }

    public static void buildFreqs(HashMap<String, Integer> map, String data){
        Matcher match = Pattern.compile("[a-z]{2,}").matcher(data);
        while (match.find()){
            String cur = match.group();
            map.put(cur, map.getOrDefault(cur, 0)+1);
        }
    }

    public static void sort(List<AbstractMap.SimpleEntry<String, Integer>> list){
        if(list.size()<=1)return;
        ArrayList<AbstractMap.SimpleEntry<String, Integer>> left = new ArrayList<>();
        ArrayList<AbstractMap.SimpleEntry<String, Integer>> right = new ArrayList<>();
        int mid = list.size()/2;
        for(int i=0;i<mid;i++)left.add(list.get(i));
        for(int i=mid;i<list.size();i++)right.add(list.get(i));
        sort(left);
        sort(right);
        merge(list, left, right);
    }

    private static void merge(List<AbstractMap.SimpleEntry<String, Integer>> list, ArrayList<AbstractMap.SimpleEntry<String, Integer>> left, ArrayList<AbstractMap.SimpleEntry<String, Integer>> right) {
        int i = 0;
        int j = 0;
        int k = 0;

        // Compare elements from left and right halves and merge them in sorted order
        while (i < left.size() && j < right.size()) {
            if (left.get(i).getValue() <= right.get(j).getValue()) {
                list.set(k++, left.get(i++));
            } else {
                list.set(k++, right.get(j++));
            }
        }

        // Add remaining elements from left half
        while (i < left.size()) {
            list.set(k++, left.get(i++));
        }

        // Add remaining elements from right half
        while (j < right.size()) {
            list.set(k++, right.get(j++));
        }
    }
}
