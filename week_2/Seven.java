import javafx.util.Pair;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Seven {
    public static void main(String[] args) throws IOException {
        HashMap<String, Integer> map = new HashMap<>();
        HashMap<String, Integer> set = new HashMap<>();
        buildFreqs(map, new String(Files.readAllBytes(Paths.get(args[0]))).toLowerCase(Locale.ROOT));
        buildFreqs(set, new String(Files.readAllBytes(Paths.get("../stop_words.txt"))).toLowerCase(Locale.ROOT));
        for (String s:set.keySet())if(map.containsKey(s))map.remove(s);
        ArrayList<Pair<String, Integer>> list = new ArrayList<>();
        for (String str:map.keySet())list.add(new Pair<>(str, map.get(str)));
        list.sort(Comparator.comparingInt(Pair::getValue));
        for (int i=0;i<25;i++) System.out.println(list.get(list.size()-i-1).getKey()+" - "+list.get(list.size()-i-1).getValue());
    }

    public static void buildFreqs(HashMap<String, Integer> map, String data){
        Matcher match = Pattern.compile("[a-z]{2,}").matcher(data);
        while (match.find()){
            String cur = match.group();
            map.put(cur, map.getOrDefault(cur, 0)+1);
        }
    }
}
