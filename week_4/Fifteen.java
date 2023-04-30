import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.*;
import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Fifteen {
    public static class WordFrequencyFramework {
        private List<Consumer<String>> loadEventHandlers;
        private List<Runnable> doworkEventHandlers;
        private List<Runnable> endEventHandlers;

        public WordFrequencyFramework() {
            loadEventHandlers = new ArrayList<>();
            doworkEventHandlers = new ArrayList<>();
            endEventHandlers = new ArrayList<>();
        }

        public void registerForLoadEvent(Consumer<String> handler) {
            this.loadEventHandlers.add(handler);
        }

        public void registerForDoworkEvent(Runnable handler) {
            this.doworkEventHandlers.add(handler);
        }

        public void registerForEndEvent(Runnable handler) {
            this.endEventHandlers.add(handler);
        }

        public void run(String path) {
            for (Consumer h : loadEventHandlers) {
                h.accept(path);
            }
            for (Runnable h : doworkEventHandlers) {
                h.run();
            }
            for (Runnable h : endEventHandlers) {
                h.run();
            }
        }
    }

    public static class DataManager {
        String data;
        List<Consumer> wordEventHandlers;
        StopWordManager stopwordsfilter;

        public DataManager(WordFrequencyFramework wfapp, StopWordManager stopwordsfilter) {
            this.data = "";
            this.wordEventHandlers = new ArrayList<>();
            this.stopwordsfilter = stopwordsfilter;
            wfapp.registerForLoadEvent(this::load);
            wfapp.registerForDoworkEvent(this::words);
        }

        // use buffered reader to read the input file(pride-and-prejudice)
        public void load(String path) {
            Path p = Paths.get(path);
            try {
                this.data = new String(Files.readAllBytes(p)).toLowerCase(Locale.ROOT);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void words() {
            Matcher match = Pattern.compile("[a-z]{2,}").matcher(data);
            while (match.find()){
                String word = match.group();
                if (!stopwordsfilter.checkStopWords(word) && word.length() >= 2) {
                    for (Consumer<String> h : wordEventHandlers) {
                        h.accept(word);
                    }
                }
            }
        }

        private void registerWordEventHandler(Consumer<String> handler) {
            wordEventHandlers.add(handler);
        }
    }

    public static class StopWordManager {
        Set<String> set;

        public StopWordManager(WordFrequencyFramework wfapp) {
            this.set = new HashSet<>();
            wfapp.registerForLoadEvent(this::load);
        }

        public void load(String path) {
            try {
                String data = new String(Files.readAllBytes(Paths.get("../stop_words.txt")));
                String[] strs = data.split(",");
                for (String str: strs){
                    set.add(str);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // return whether is stop words
        public boolean checkStopWords(String word) {
            return this.set.contains(word);
        }

    }

    public static class WordFrequencyManager {
        Map<String, Integer> wordFreqs;

        public WordFrequencyManager(WordFrequencyFramework wfapp, DataManager dataStorage) {
            this.wordFreqs = new HashMap<>();
            dataStorage.registerWordEventHandler(this::count);
            wfapp.registerForEndEvent(this::print);
        }

        // count the frequencies
        public void count(String str) {
            this.wordFreqs.put(str, this.wordFreqs.getOrDefault(str, 0) + 1);
        }

        public void print() {
            List<Map.Entry<String, Integer>> list = new ArrayList<>(this.wordFreqs.entrySet());
            Collections.sort(list, (o1, o2) -> o2.getValue().compareTo(o1.getValue()));
            for (int i = 0; i < 25; i++) {
                System.out.println(list.get(i).getKey() + "  -  " + list.get(i).getValue());
            }
        }

    }

    @SuppressWarnings("unchecked")
    public static void main(String[] args) {
        // args[0] is the file path of input FileReader
        WordFrequencyFramework wfapp = new WordFrequencyFramework();
        StopWordManager stopWordFilter = new StopWordManager(wfapp);
        DataManager dataStorage = new DataManager(wfapp, stopWordFilter);
        WordFrequencyManager wordFrequencyCounter = new WordFrequencyManager(wfapp, dataStorage);

        wfapp.run(args[0]);
    }
}
