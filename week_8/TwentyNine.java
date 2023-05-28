import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TwentyNine{
    public static void send(ThreadObj obj, Message message){
        obj.queue.offer(message);
    }

    public static void main(String[] args) {
        wordFrequencyManager wordFrequencyManager = new wordFrequencyManager();
        stopWordManager stopWordManager = new stopWordManager(wordFrequencyManager);
        send(stopWordManager, new Message("init", ""));
        dataStorageManager dataManager = new dataStorageManager(stopWordManager);
        send(dataManager, new Message("init", args[0]));
        wordFrequencyController wordFrequencyController = new wordFrequencyController();
        send(wordFrequencyController, new Message("init", dataManager));

        for (Thread t : new Thread[]{wordFrequencyManager, stopWordManager, dataManager, wordFrequencyController}) {
            try {
                t.join();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    static class ThreadObj extends Thread {
        String name;
        Queue<Message> queue;
        boolean stop;

        public ThreadObj() {
            this.queue = new LinkedBlockingDeque<>();
            this.stop = false;
            this.start();
        }

        @Override
        public void run(){
            while (!this.stop){
                Message message;
                if (queue.isEmpty()){
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    message = queue.poll();
                    dispatch(message);
                    if ("die".equals(message.command))this.stop = true;
                }
            }
        }

        public void dispatch(Message message) {
        }
    }

    static class dataStorageManager extends ThreadObj {
        String content;
        stopWordManager stopWordManager;

        public dataStorageManager() {
            this.content = "";
        }

        public dataStorageManager(stopWordManager stopWordManager) {
            this.stopWordManager = stopWordManager;
            this.content = "";
        }

        @Override
        public void dispatch(Message message) {
            String command = message.command;
            if ("init".equals(command)) {
                this.init((String)message.obj);
            } else if ("send".equals(command)) {
                this._run((ThreadObj)message.obj);
            } else {
                send(this.stopWordManager, message);
            }
        }

        private void _run(ThreadObj obj) {
            Matcher match = Pattern.compile("[a-z]{2,}").matcher(content);
            while (match.find()){
                send(stopWordManager, new Message("filter", match.group()));
            }
            send(stopWordManager, new Message("top", obj));
        }

        public void init(String path) {
            try {
                content = new String(Files.readAllBytes(Paths.get(path))).toLowerCase(Locale.ROOT);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    static class stopWordManager extends ThreadObj {
        Set<String> stopWords;
        wordFrequencyManager wordFrequencyManager;

        public stopWordManager(wordFrequencyManager wordFrequencyManager) {
            this.stopWords = new HashSet<>();
            this.wordFrequencyManager = wordFrequencyManager;
        }

        @Override
        public void dispatch(Message message) {
            String command = message.command;
            if ("init".equals(command)) {
                this.init();
            } else if ("filter".equals(command)) {
                this.checkStopWords((String)message.obj);
            } else {
                send(this.wordFrequencyManager, message);
            }

        }

        public void init() {
            String path = "../stop_words.txt";
            try {
                String data = new String(Files.readAllBytes(Paths.get(path))).toLowerCase(Locale.ROOT);
                String[] strs = data.split(",");
                for(String str: strs){
                    stopWords.add(str);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void checkStopWords(String word) {
            if (!this.stopWords.contains(word) && word.length() >= 2) {
                send(this.wordFrequencyManager, new Message("word", word));
            }
        }
    }

    static class wordFrequencyManager extends ThreadObj {
        Map<String, Integer> freqs;

        public wordFrequencyManager(){
            this.freqs = new HashMap<>();
        }

        @Override
        public void dispatch(Message message) {
            String command = message.command;
            if ("word".equals(message.command)) {
                this.count((String)message.obj);
            } else if ("top".equals(message.command)) {
                this.sorted((ThreadObj)message.obj);
            }
        }

        public void count(String str) {
            this.freqs.put(str, this.freqs.getOrDefault(str, 0) + 1);
        }

        public void sorted(ThreadObj recipient) {
            List<Map.Entry<String, Integer>> list = new ArrayList<>(this.freqs.entrySet());
            list.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));
            send(recipient, new Message("top", list));
        }

    }

    static class wordFrequencyController extends ThreadObj {
        dataStorageManager dataStorageManager;

        public wordFrequencyController(){}

        @Override
        public void dispatch(Message message) {
            String command = message.command;
            if ("init".equals(command)) {
                this.init((dataStorageManager)message.obj);
            } else if ("top".equals(command)) {
                this.display((List<Map.Entry<String, Integer>>)message.obj);
            } else {
                throw new IllegalArgumentException("Message not found!!");
            }
        }

        public void init(dataStorageManager dataStorageManager) {
            this.dataStorageManager = dataStorageManager;
            send(this.dataStorageManager, new Message("send", this));
        }

        public void display(List<Map.Entry<String, Integer>> list) {
            for (int i = 0; i < 25; i++) {
                System.out.println(list.get(i).getKey() + "  -  " + list.get(i).getValue());
            }
            send(this.dataStorageManager, new Message("die", ""));
            this.stop = true;
        }
    }

    static class Message {
        String command;
        Object obj;

        public Message(String command, Object obj){
            this.command = command;
            this.obj = obj;
        }
    }
}



