import java.io.*;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;

public class Four {
    static HashSet<String> stopSet = new HashSet<>();

    public static void main(String[]args) throws IOException {
        // Read stop words
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

        // Read the content of the novel
        FileInputStream fis = new FileInputStream(args[0]);
        BufferedReader br = new BufferedReader(new InputStreamReader(fis));
        ArrayList<Map.Entry<String, Integer>> freqs = new ArrayList<>();
        String content = null;
        int cnt = 0;
        while((content = br.readLine())!=null){
            cnt++;
            int start = -1;
            int i = 0;
            for(char c : content.toCharArray()){
                if (start==-1){
                    if (Character.isLetter(c) || Character.isDigit(c)){
                        start = i;
                    }
                } else {
                    if (!Character.isLetter(c) && !Character.isDigit(c)){
                        String word = content.substring(start, i).toLowerCase();
                        if(!stopSet.contains(word)){
                            int index = 0;
                            for (;index<freqs.size();index++){
                                Map.Entry<String, Integer> pair = freqs.get(index);
                                if (pair.getKey().equals(word)){
                                    freqs.set(index, new AbstractMap.SimpleEntry<>(word, pair.getValue()+1));
                                    break;
                                }
                            }
                            if (index>=freqs.size()){
                                freqs.add(new AbstractMap.SimpleEntry<>(word, 1));
                            } else if (freqs.size()>1) {
                                for (int j=index-1;j>=0;j--){
                                    if (freqs.get(index).getValue() > freqs.get(j).getValue()){
                                        Map.Entry<String, Integer> pair = freqs.get(j);
                                        freqs.set(j, freqs.get(index));
                                        freqs.set(index, pair);
                                        index = j;
                                    }
                                }
                            }
                        }
                        // Reset the word
                        start = -1;
                    }
                }
                i++;
            }
            if (start!=-1){
                String word = content.substring(start, i).toLowerCase();
                if(!stopSet.contains(word)){
                    int index = 0;
                    for (;index<freqs.size();index++){
                        Map.Entry<String, Integer> pair = freqs.get(index);
                        if (pair.getKey().equals(word)){
                            freqs.set(index, new AbstractMap.SimpleEntry<>(word, pair.getValue()+1));
                            break;
                        }
                    }
                    if (index>=freqs.size()){
                        freqs.add(new AbstractMap.SimpleEntry<>(word, 1));
                    } else if (freqs.size()>1) {
                        for (int j=index-1;j>=0;j--){
                            if (freqs.get(index).getValue() > freqs.get(j).getValue()){
                                Map.Entry<String, Integer> pair = freqs.get(j);
                                freqs.set(j, freqs.get(index));
                                freqs.set(index, pair);
                                index = j;
                            }
                        }
                    }
                }
            }
        }

        for(int i=0;i<25;i++){
            System.out.println(freqs.get(i).getKey()+" - "+freqs.get(i).getValue());
        }
    }
}
