import javafx.util.Pair;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;

public class Five {
    static HashSet<String> stopSet = new HashSet<>();
    public static void main(String[] args) throws IOException {
        buildStopSets();
        BufferedReader br = readFile(args[0]);
        ArrayList<Pair<String, Integer>> freqs = new ArrayList<>();
        buildFrequencies(br, freqs);

        for(int i=0;i<25;i++){
            System.out.println(freqs.get(i).getKey()+" - "+freqs.get(i).getValue());
        }
    }

    private static void buildFrequencies(BufferedReader br, ArrayList<Pair<String, Integer>> freqs) throws IOException {
        String content = null;
        int cnt = 0;
        while ((content = br.readLine()) != null) {
            cnt++;
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

    private static void addWord(ArrayList<Pair<String, Integer>> freqs, String content, int start, int i) {
        String word = content.substring(start, i).toLowerCase();
        if (!stopSet.contains(word)) {
            int index = 0;
            for (; index < freqs.size(); index++) {
                Pair<String, Integer> pair = freqs.get(index);
                if (pair.getKey().equals(word)) {
                    freqs.set(index, new Pair<>(word, pair.getValue() + 1));
                    break;
                }
            }
            if (index >= freqs.size()) {
                freqs.add(new Pair<>(word, 1));
            } else if (freqs.size() > 1) {
                for (int j = index - 1; j >= 0; j--) {
                    if (freqs.get(index).getValue() > freqs.get(j).getValue()) {
                        Pair<String, Integer> pair = freqs.get(j);
                        freqs.set(j, freqs.get(index));
                        freqs.set(index, pair);
                        index = j;
                    }
                }
            }
        }
    }

    private static BufferedReader readFile(String fileName) throws FileNotFoundException {
        FileInputStream fis = new FileInputStream(fileName);
        return new BufferedReader(new InputStreamReader(fis));
    }

    private static void buildStopSets() throws IOException {
        InputStream is = new FileInputStream("../stop_words.txt");
        int len = is.available();
        byte[] bytes = new byte[len];
        is.read(bytes);
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
