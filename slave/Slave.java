import java.io.*;
import java.net.UnknownHostException;
import java.util.*;
import java.util.regex.Pattern;

import static java.lang.Math.abs;
import static java.net.InetAddress.getLocalHost;

public class Slave {
    public Slave(){
        words = new LinkedList<String>();
        hashCode = new LinkedList<Integer>();
        slaveList = new LinkedList<String>();
        wordsCount = new HashMap<String, Integer>();
        //startTime = 0;
        //endTime = 0;
    }
    /*
    public void startTiming(){
        startTime = System.currentTimeMillis();
    }
    public void endTiming(String s){
        endTime = System.currentTimeMillis();
        System.out.println("In " + s + ". " + "It costs " + (endTime - startTime) + " milliseconds");
    }
    */
    //To make a class to store the string and its counters
    private List<String> words;
    private List<Integer> hashCode;
    private List<String> slaveList;
    private String host;
    private Map<String, Integer> wordsCount;
    //private long startTime;
    //private long endTime;
    //limit the number of the words to display.
    private static final int Max_Num = 50;
    //to add the words in the hash map
    public void addWords(String s){
        if(s.equals("")){
            return;
        }
        words.add(s);
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void handleLines(String s){
        //using the pattern in unicode type to split the sentence with the special characters.
        Pattern p = Pattern.compile("\\W", Pattern.UNICODE_CHARACTER_CLASS);
        String[] l = p.split(s);
        for(int i = 0; i < l.length; i++){
            //to convert all the word in lower cases.
            addWords(l[i].toLowerCase());
        }
    }
    public void GetHostFromTxt(){
        try {
            BufferedReader bf = new BufferedReader(new FileReader("/tmp/zsun/machines.txt"));
            String line;
            while ((line = bf.readLine()) != null) {
                slaveList.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void DoShuffle(){
        GetHostFromTxt();
        for(int hash : hashCode){
            String line = slaveList.get(abs(hash % 3));
            ProcessBuilder processBuilder = new ProcessBuilder("scp",
                    "-o", "StrictHostKeyChecking=no", "-i", "/tmp/zsun/id_rsa",
                    "/tmp/zsun/shuffles/" + hash + "-" + host + ".txt",
                    "zsun@" + line + ":" + "/tmp/zsun/shufflesReceived");
            Process p = null;
            try {
                p = processBuilder.start();
                p.waitFor();
                //System.out.println(hash + " " + line);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    public void WriteToShuffle(String s){
        int hash = s.split(" ")[0].hashCode();
        File file = new File("/tmp/zsun/shuffles/" + hash + "-" + host + ".txt");
        try {
            if(file.exists() || file.createNewFile()){
                BufferedWriter bf;
                if(hashCode.contains(hash))
                bf = new BufferedWriter(new FileWriter("/tmp/zsun/shuffles/" + hash + "-" + host + ".txt", true));
                else{
                    bf = new BufferedWriter(new FileWriter("/tmp/zsun/shuffles/" + hash + "-" + host + ".txt"));
                    hashCode.add(hash);
                }
                bf.append(s).append("\n");
                bf.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void WriteToMap(String file){
        File umx = new File("/tmp/zsun/maps/" + file);
        try {
            if(umx.exists() || umx.createNewFile()){
                BufferedWriter bf = new BufferedWriter(new FileWriter("/tmp/zsun/maps/" + file));
                for(int i = 0; i < words.size(); i++){
                    String s = words.get(i);
                    if(i != 0)
                        bf.append(s).append(" ").append(String.valueOf(1)).append("\n");
                    else
                        bf.write(s + " " + 1 + '\n');
                }
                bf.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void clearCount(){
        words.clear();
    }
    public void handleReduceFile(File file){
        BufferedReader in = null;
        String line = null;
        String item = null;
        int count = 0;
        try {
            in = new BufferedReader(new FileReader(file));
            while ((line = in.readLine()) != null) {
                if(count == 0){
                    item = line.split(" ")[0];
                    count ++;
                }
                else
                    count ++;
            }
            if(wordsCount.containsKey(item)){
                wordsCount.replace(item, wordsCount.get(item) + count);
            }
            else{
                wordsCount.put(item, count);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void writeReduceFile() {
        for(Map.Entry<String, Integer> entry : wordsCount.entrySet()){
            File umx = new File("/tmp/zsun/reduces/" + entry.getKey().hashCode() + ".txt");
            try {
                if(umx.exists() || umx.createNewFile()) {
                    BufferedWriter bf = new BufferedWriter(new FileWriter("/tmp/zsun/reduces/" + entry.getKey().hashCode() + ".txt"));
                    bf.write(entry.getKey() + " " + entry.getValue() + "\n");
                    bf.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public static void main(String[] args) {
        File[] pathList;
        if (args[0].equals("0")) {
            pathList = new File[1];
            pathList[0] = new File(args[1]);

        /*
        else {
            File folder = new File("/tmp/zsun/splits");
            pathList = folder.listFiles();
        }
        */
            Slave counter = new Slave();
            for (File list : pathList) {
                if (list.isFile())
                    try {
                        BufferedReader in = new BufferedReader(new FileReader(list));
                        String line;
                        //counter.startTiming();
                        while ((line = in.readLine()) != null) {
                            counter.handleLines(line);
                        }
                        //counter.endTiming("Counting");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                String umName = "UM" + list.getName().substring(1);
                counter.WriteToMap(umName);
                counter.clearCount();
            }
            //counter.startTiming();
            //counter.showWords();
            //counter.endTiming("Sorting");
        }
        else if(args[0].equals("1")){
            String host ="";
            try {
                host = getLocalHost().getHostName();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
            pathList = new File[1];
            pathList[0] = new File(args[1]);
            Slave shuffler = new Slave();
            shuffler.setHost(host);
            for(File list : pathList){
                if(list.isFile()){
                    try {
                        BufferedReader in = new BufferedReader(new FileReader(list));
                        String line;
                        while((line = in.readLine()) != null){
                            shuffler.WriteToShuffle(line);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            shuffler.DoShuffle();
        }
        else if(args[0].equals("2")){
            File folder = new File("/tmp/zsun/shufflesReceived");
            Slave reduce = new Slave();
            pathList = folder.listFiles();
            for(File list : pathList){
                if(list.isFile()){
                    reduce.handleReduceFile(list);
                }
            }
            reduce.writeReduceFile();
        }
    }
}

