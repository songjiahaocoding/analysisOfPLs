import javafx.util.Pair;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;

public class four {
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

        for (String s: args) System.out.println(s);
        // Read the content of the novel
        FileInputStream fis = new FileInputStream(args[0]);
        BufferedReader br = new BufferedReader(new InputStreamReader(fis));
        System.out.println("Buffer");
        ArrayList<Pair<String, Integer>> freqs = new ArrayList<>();
        String content = null;
        int cnt = 0;
        while((content = br.readLine())!=null){
            System.out.println(cnt);
            int start = -1;
            int i = 0;
            for(char c : content.toCharArray()){
                if (start==-1){
                    if (Character.isLowerCase(c)||Character.isUpperCase(c)){
                        start = i;
                    }
                } else {
                    if (!Character.isLowerCase(c) && !Character.isUpperCase(c)){
                        String word = content.substring(start, i).toLowerCase();
                        if(!stopSet.contains(word)){
                            int index = 0;
                            for (;index<freqs.size();i++){
                                if (freqs.get(index).getKey().equals(word)){
                                    break;
                                }
                            }
                            if (index>=freqs.size()){
                                freqs.add(new Pair<>(word, 1));
                            } else if (freqs.size()>1) {
                                for (int j=index;j>=0;j--){
                                    if (freqs.get(index).getValue() > freqs.get(j).getValue()){
                                        Pair<String, Integer> pair = freqs.get(j);
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
            cnt++;
        }

        for(int i=0;i<25;i++){
            System.out.println(freqs.get(i).getKey()+" - "+freqs.get(i).getValue());
        }
    }
}
