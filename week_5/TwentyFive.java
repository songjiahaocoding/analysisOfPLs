import java.io.*;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashSet;

public class TwentyFive {
    static HashSet<String> stopSet = new HashSet<>();
    public static void main(String[] args) {
        buildStopSets();
        BufferedReader br = readFile(args[0]);
        ArrayList<AbstractMap.SimpleEntry<String, Integer>> freqs = new ArrayList<>();
        ArrayList<String> list = readContent(br);
        buildFrequencies(list, freqs);

        for(int i=0;i<25;i++){
            System.out.println(freqs.get(i).getKey()+" - "+freqs.get(i).getValue());
        }
    }

    private static ArrayList<String> readContent(BufferedReader br) {
        ArrayList<String> list = new ArrayList<>();
        String content = null;
        while (true){
            try {
                content = br.readLine();
            } catch (IOException e){
                e.printStackTrace();
            }
            if (content==null)break;
            list.add(content);
            content = null;
        }
        return list;
    }

    private static void buildFrequencies(ArrayList<String> list, ArrayList<AbstractMap.SimpleEntry<String, Integer>> freqs) {
        for (String content:list) {
            int start = -1;
            int i = 0;
            for (char c : content.toCharArray()) {
                if (start == -1) {
                    if (Character.isLetter(c) || Character.isDigit(c)) {
                        start = i;
                    }
                } else {
                    if (!Character.isLetter(c) && !Character.isDigit(c)) {
                        addWord(freqs, content, start, i);
                        // Reset word
                        start = -1;
                    }
                }
                i++;
            }
            if(start!=-1){
                addWord(freqs, content, start, i);
            }
        }
    }

    private static void addWord(ArrayList<AbstractMap.SimpleEntry<String, Integer>> freqs, String content, int start, int i) {
        String word = content.substring(start, i).toLowerCase();
        if (!stopSet.contains(word)) {
            int index = 0;
            for (; index < freqs.size(); index++) {
                AbstractMap.SimpleEntry<String, Integer> pair = freqs.get(index);
                if (pair.getKey().equals(word)) {
                    freqs.set(index, new AbstractMap.SimpleEntry<>(word, pair.getValue() + 1));
                    break;
                }
            }
            if (index >= freqs.size()) {
                freqs.add(new AbstractMap.SimpleEntry<>(word, 1));
            } else if (freqs.size() > 1) {
                for (int j = index - 1; j >= 0; j--) {
                    if (freqs.get(index).getValue() > freqs.get(j).getValue()) {
                        AbstractMap.SimpleEntry<String, Integer> pair = freqs.get(j);
                        freqs.set(j, freqs.get(index));
                        freqs.set(index, pair);
                        index = j;
                    }
                }
            }
        }
    }

    private static BufferedReader readFile(String fileName) {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(fileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return new BufferedReader(new InputStreamReader(fis));
    }

    private static void buildStopSets() {
        byte[] bytes = null;
        try{
            InputStream is = new FileInputStream("../stop_words.txt");
            int len = is.available();
            bytes = new byte[len];
            is.read(bytes);
        } catch (IOException e){
            e.printStackTrace();
        }
        String stopWords = new String(bytes);
        for (String s:stopWords.split(",")){
            stopSet.add(s);
        }
        for (int i=0;i<26;i++){
            char c = (char) ('a'+i);
            stopSet.add(String.valueOf(c));
        }
    }
}
