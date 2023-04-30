import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.io.*;
import java.util.function.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Thirteen {
    private static void getStrData(Map<String, Object> dataStorage, String path) {
        Path p = Paths.get(path);
        List<String> list = new ArrayList<>();
        String data = null;
        try {
            data = new String(Files.readAllBytes(p)).toLowerCase(Locale.ROOT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        dataStorage.put("data", data);
        dataStorage.put("list", list);
    }

    private static List<String> getWords(Map<String, Object> map) {
        String data = (String) map.get("data");
        List<String> list = (List<String>) map.get("list");
        Matcher match = Pattern.compile("[a-z]{2,}").matcher(data);
        while (match.find()){
            String cur = match.group();
            list.add(cur);
        }
        return list;
    }

    // load stop words
    private static void loadStopWords(Map<String, Object> map) {
        HashSet<String> set = new HashSet<>();
        try {
            String data = new String(Files.readAllBytes(Paths.get("../stop_words.txt")));
            String[] strs = data.split(",");
            for (String str: strs){
                set.add(str);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        map.put("set", set);
    }

    // sorted
    public static List<Map.Entry<String, Integer>> sorted(HashMap<String, Integer> map) {
        List<Map.Entry<String, Integer>> list = new ArrayList<>(map.entrySet());
        Collections.sort(list, (o1, o2) -> o2.getValue().compareTo(o1.getValue()));
        return list;
    }


    // // count frequency
    // @SuppressWarnings("unchecked")
    // private static void count(Map<String, Object> map, String word) {
    // 	Map<String, Integer> wordFreqs = (Map<String, Integer>)map.get("freqs");
    // 	wordFreqs.put(word, wordFreqs.getOrDefault(word, 0) + 1);
    // 	map.put("freqs", wordFreqs);
    // }

    public static void main(String[] args) {
        // args[0] is the file path of input file

        // data object
        Map<String, Object> dataStorage = new HashMap<>();
        dataStorage.put("init", (Consumer)(path -> getStrData(dataStorage, (String)path)));
        dataStorage.put("words", (Supplier)(() ->  getWords(dataStorage)));

        // stop words object
        Map<String, Object> stopWords = new HashMap<>();
        stopWords.put("init", (Consumer)((x) ->  loadStopWords(stopWords)));
        stopWords.put("check", (Predicate)((word) -> ((HashSet<String>)stopWords.get("set")).contains(word)));

        // word frequencies object
        Map<String, Object> wordFreqs = new HashMap<>();
        wordFreqs.put("freqs", new HashMap<String, Integer>());
        wordFreqs.put("count", (Consumer)((word) ->  ((HashMap<String, Integer>)wordFreqs.get("freqs"))
                .put((String)word, ((HashMap<String, Integer>)wordFreqs.get("freqs")).getOrDefault(word, 0) + 1)));
        wordFreqs.put("sorted", (Supplier)(() -> {
            List<Map.Entry<String, Integer>> list = new ArrayList(((HashMap<String, Integer>)wordFreqs.get("freqs")).entrySet());
            Collections.sort(list, (o1, o2) -> o2.getValue().compareTo(o1.getValue()));
            return list;
        }));
        wordFreqs.put("print", (Consumer)((x) -> {
            List<Map.Entry<String, Integer>> list = (List<Map.Entry<String, Integer>>)((Supplier)wordFreqs.get("sorted")).get();
            for (int i = 0; i < 25; i++) {
                System.out.println(list.get(i).getKey() + "  -  " + list.get(i).getValue());
            }
        }));


        ((Consumer)dataStorage.get("init")).accept(args[0]);
        ((Consumer)stopWords.get("init")).accept(0);
        for (String word : (ArrayList<String>)((Supplier)dataStorage.get("words")).get()) {
            if (!((Predicate)stopWords.get("check")).test(word) && word.length() >= 2) {
                ((Consumer)wordFreqs.get("count")).accept(word);
            }
        }

        ((Consumer)wordFreqs.get("print")).accept(0);
    }


}