import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TwentyNine {
    static class ThreadObj extends Thread {
        String name;
        Queue<Message> queue;
        boolean stop;

        public ThreadObj() {
            this.queue = new LinkedBlockingDeque<>();
            this.stop = false;

        }


    }

    static class DataStorageManager {
        List<String> list;
        String data;

        public List<String> dispatch(String[] message){
            if (message[0].equals("init")){
                this._init(message[1]);
            } else if (message[0].equals("words")){
                this._words();
                return list;
            }
            return null;
        }

        private void _init (String filePath){
            Path p = Paths.get(filePath);
            list = new ArrayList<>();
            try {
                data = new String(Files.readAllBytes(p)).toLowerCase(Locale.ROOT);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void _words(){
            Matcher match = Pattern.compile("[a-z]{2,}").matcher(data);
            while (match.find()){
                String cur = match.group();
                list.add(cur);
            }
        }
    }

    static class StopWordManager {
        HashSet<String> set;

        public boolean dispatch(String[] args){
            if (args[0].equals("init")){
                this._init();
            } else if (args[0].equals("isStopWord")){
                return this.isStopWord(args[1]);
            }
            return true;
        }

        private boolean isStopWord(String word) {
            return set.contains(word);
        }

        private void _init() {
            set = new HashSet<>();
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
    }

    static class FreqManager {
        private Map<String, Integer> map = new HashMap<>();

        public List<AbstractMap.SimpleEntry<String, Integer>> dispatch(String[] args){
            if(args[0].equals("increment_count")){
                this.increment_count(args[1]);
            } else if(args[0].equals("sorted")){
                return this.sorted();
            }
            return null;
        }

        private List<AbstractMap.SimpleEntry<String, Integer>> sorted() {
            PriorityQueue<AbstractMap.SimpleEntry<String, Integer>> pq = new PriorityQueue<>(Comparator.comparingInt(AbstractMap.SimpleEntry::getValue));

            for (String str:map.keySet()){
                if (pq.size()<25){
                    pq.add(new AbstractMap.SimpleEntry<>(str, map.get(str)));
                } else {
                    if(map.get(str)>pq.peek().getValue()){
                        pq.poll();
                        pq.add(new AbstractMap.SimpleEntry<>(str, map.get(str)));
                    }
                }
            }

            ArrayList<AbstractMap.SimpleEntry<String, Integer>> list = new ArrayList<>();
            while(!pq.isEmpty()){
                AbstractMap.SimpleEntry<String, Integer> pair = pq.poll();
                list.add(pair);
            }
            return list;
        }

        private void increment_count(String str){
            this.map.put(str, map.getOrDefault(str, 0)+1);
        }
    }

    static class WordFreqController {
        private DataStorageManager storageManager;
        private StopWordManager stopManager;
        private FreqManager freqManager;

        public void dispatch(String[] args){
            if(args[0].equals("init")){
                this._init(args[1]);
            } else if(args[0].equals("run")){
                this._run();
            }
        }

        private void _init(String arg) {
            this.storageManager = new DataStorageManager();
            this.stopManager = new StopWordManager();
            this.freqManager = new FreqManager();
            this.storageManager.dispatch(new String[]{"init", arg});
            this.stopManager.dispatch(new String[]{"init"});
        }

        private void _run() {
            for(String s:this.storageManager.dispatch(new String[]{"words"})){
                if(!this.stopManager.dispatch(new String[]{"isStopWord", s})){
                    this.freqManager.dispatch(new String[]{"increment_count", s});
                }
            }

            List<AbstractMap.SimpleEntry<String, Integer>> list = this.freqManager.dispatch(new String[]{"sorted"});
            for (int i=list.size()-1;i>=0;i--){
                AbstractMap.SimpleEntry<String, Integer> pair = list.get(i);
                System.out.println(pair.getKey()+" - "+pair.getValue());
            }
        }
    }

    public static void main(String[] args) {
        WordFreqController controller = new WordFreqController();
        controller.dispatch(new String[]{"init", args[0]});
        controller.dispatch(new String[]{"run"});
    }

    class Message {
        String command;
        Object value;

        public Message(String command, Object value) {
            this.command = command;
            this.value = value;
        }
    }


    class Sender {
        public static void send(ThreadObj receiver, Message message) {
            receiver.queue.offer(message);
        }
    }
}


