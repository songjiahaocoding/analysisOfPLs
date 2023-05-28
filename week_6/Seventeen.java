import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

public class Seventeen {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Please input the class name or input -1 to get the word count information");
        String name = sc.next();
        if(("-1").equals(name))printWC(args);
        else printInfo(name);
    }

    public static void printInfo(String className){
        try {
            Class<?> cls = Class.forName(className);
            System.out.println("Fields:");
            Field[] fields = cls.getFields();
            for (Field field:fields){
                System.out.println(field.getName()+" "+field.getType());
            }
            System.out.println("Methods name:");
            Method[] methods = cls.getMethods();
            for (Method method:methods){
                System.out.println(method.getName());
            }
            System.out.println("Super Classes");
            Class superClass = cls.getSuperclass();
            while (superClass!=null){
                System.out.println(superClass.getName());
                superClass = superClass.getSuperclass();
            }
            System.out.println("Implemented interfaces");
            Class[] interfaces = cls.getInterfaces();
            for(Class inter: interfaces){
                System.out.println(inter.getName());
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void printWC(String[] args){
        try {
            Class<?> wfController = Class.forName("WordFrequencyController");
            Constructor constructor = wfController.getConstructors()[0];
            Object obj = constructor.newInstance(args[0]);
            Method method = obj.getClass().getMethod("run");
            method.invoke(obj);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}

abstract class WCApp {
    public String getInfo() {
        return this.getClass().getName();
    }
}

/** Models the stop word filter. */
class StopWordManager extends WCApp {
    private Set<String> stopWords;

    public StopWordManager() throws IOException {
        this.stopWords = new HashSet<String>();

        Scanner f = new Scanner(new File("../stop_words.txt"), "UTF-8");
        try {
            f.useDelimiter(",");
            while (f.hasNext()) {
                this.stopWords.add(f.next());
            }
        } finally {
            f.close();
        }

        // Add single-letter words
        for (char c = 'a'; c <= 'z'; c++) {
            this.stopWords.add("" + c);
        }
    }

    public boolean isStopWord(String word) {
        return this.stopWords.contains(word);
    }

    public String getInfo() {
        return super.getInfo() + ": My major data structure is a " + this.stopWords.getClass().getName();
    }
}

/** Models the contents of the file. */
class DataStorageManager extends WCApp {
    private List<String> words;

    public DataStorageManager(String pathToFile) throws IOException {
        this.words = new ArrayList<String>();

        Scanner f = new Scanner(new File(pathToFile), "UTF-8");
        try {
            f.useDelimiter("[\\W_]+");
            while (f.hasNext()) {
                this.words.add(f.next().toLowerCase());
            }
        } finally {
            f.close();
        }
    }

    public List<String> getWords() {
        return this.words;
    }

    public String getInfo() {
        return super.getInfo() + ": My major data structure is a " + this.words.getClass().getName();
    }
}



/** Keeps the word frequency data. */
class WordFrequencyManager extends WCApp {
    private Map<String, MutableInteger> wordFreqs;

    public WordFrequencyManager() {
        this.wordFreqs = new HashMap<String, MutableInteger>();
    }

    public void incrementCount(String word) {
        MutableInteger count = this.wordFreqs.get(word);
        if (count == null) {
            this.wordFreqs.put(word, new MutableInteger(1));
        } else {
            count.setValue(count.getValue() + 1);
        }
    }

    public List<WordFrequencyPair> sorted() {
        List<WordFrequencyPair> pairs = new ArrayList<WordFrequencyPair>();
        for (Map.Entry<String, MutableInteger> entry : wordFreqs.entrySet()) {
            pairs.add(new WordFrequencyPair(entry.getKey(), entry.getValue().getValue()));
        }
        Collections.sort(pairs);
        Collections.reverse(pairs);
        return pairs;
    }

    public String getInfo() {
        return super.getInfo() + ": My major data structure is a " + this.wordFreqs.getClass().getName();
    }
}

class MutableInteger {
    private int value;

    public MutableInteger(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}

class WordFrequencyPair implements Comparable<WordFrequencyPair> {
    private String word;
    private int frequency;

    public WordFrequencyPair(String word, int frequency) {
        this.word = word;
        this.frequency = frequency;
    }

    public String getWord() {
        return word;
    }

    public int getFrequency() {
        return frequency;
    }

    public int compareTo(WordFrequencyPair other) {
        return this.frequency - other.frequency;
    }
}

class WordFrequencyController extends WCApp {
    private DataStorageManager storageManager;
    private StopWordManager stopWordManager;
    private WordFrequencyManager wordFreqManager;

    public WordFrequencyController(String pathToFile) {
        try {
            Class dsManager = Class.forName("dataStorageManager");
            Class swManager = Class.forName("StopWordManager");
            Class wfManager = Class.forName("WordFrequencyManager");

            Constructor cons = dsManager.getConstructor(String.class);
            storageManager = (DataStorageManager) cons.newInstance(pathToFile);
            stopWordManager = (StopWordManager) swManager.newInstance();
            wordFreqManager = (WordFrequencyManager) wfManager.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void run() {
        for (String word : this.storageManager.getWords()) {
            if (!this.stopWordManager.isStopWord(word)) {
                this.wordFreqManager.incrementCount(word);
            }
        }

        int numWordsPrinted = 0;
        for (WordFrequencyPair pair : this.wordFreqManager.sorted()) {
            System.out.println(pair.getWord() + " - " + pair.getFrequency());

            numWordsPrinted++;
            if (numWordsPrinted >= 25) {
                break;
            }
        }
    }
}
