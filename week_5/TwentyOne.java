import java.io.*;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashSet;

public class TwentyOne {
    static HashSet<String> stopSet = new HashSet<>();
    public static void main(String[] args) {
        buildStopSets();
        BufferedReader br = null;
        try {
            br = readFile(args[0]);
        } catch (FileNotFoundException e){
            e.printStackTrace();
        }

        ArrayList<AbstractMap.SimpleEntry<String, Integer>> freqs = new ArrayList<>();
        buildFrequencies(br, freqs);

        for(int i=0;i<25;i++){
            System.out.println(freqs.get(i).getKey()+" - "+freqs.get(i).getValue());
        }
    }

    private static void buildFrequencies(BufferedReader br, ArrayList<AbstractMap.SimpleEntry<String, Integer>> freqs) {
        String content = null;
        int cnt = 0;
        while (true) {
            try {
                if (!((content = br.readLine()) != null)) break;
            } catch (IOException e) {
                e.printStackTrace();
            }
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

    private static BufferedReader readFile(String fileName) throws FileNotFoundException {
        FileInputStream fis = new FileInputStream(fileName);
        return new BufferedReader(new InputStreamReader(fis));
    }

    private static void buildStopSets() {
        InputStream is = null;
        byte[] bytes = null;
        try {
            is = new FileInputStream("../stop_words.txt");
            int len = is.available();
            bytes = new byte[len];
            is.read(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(is==null)return;
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
