import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

/**
 * In the Counter class, there are starTiming and endTiming to handle the precess time,
 * There is structure to store the string , counters and implements the comparable method.
 */
public class Counter {
    public Counter(){
        words = new HashMap<String, Integer>();
        startTime = 0;
        endTime = 0;
    }
    public void startTiming(){
        startTime = System.currentTimeMillis();
    }
    public void endTiming(String s){
        endTime = System.currentTimeMillis();
        System.out.println("In " + s + ". " + "It costs " + (endTime - startTime) + " milliseconds");
    }
    //To make a class to store the string and its counters
    public static class WordsAndNum implements Comparable<WordsAndNum>{
        private String s;
        private Integer n;
        public WordsAndNum(String s, Integer n){
            this.s = s;
            this.n = n;
        }
        @Override
        //to set the compare rules, first compare the counters, then compare the string.
        public int compareTo(WordsAndNum o) {
            if(!o.n.equals(this.n))
                return o.n.compareTo(this.n);
            else
                return this.s.compareTo(o.s);
        }

        public Integer getN() {
            return n;
        }

        public String getS() {
            return s;
        }
    }
    private HashMap<String, Integer> words;
    private long startTime;
    private long endTime;
    //limit the number of the words to display.
    private static final int Max_Num = 50;
    //to add the words in the hash map
    public void addWords(String s){
        if(s.equals("")){
            return;
        }
        if(words.containsKey(s)){
            int oldNum = words.get(s);
            words.replace(s,oldNum, oldNum + 1);
        }
        else{
            words.put(s, 1);
        }
    }
    public void showWords(){
        //To put the entry of the map in a list using the class WordsAndNum
        List<WordsAndNum> wordsAndNums = new ArrayList<>();
        for(Map.Entry<String, Integer> entry : words.entrySet()){
            wordsAndNums.add(new WordsAndNum(entry.getKey(), entry.getValue()));
        }
        //Sort the list
        Collections.sort(wordsAndNums);
        //display the string and the number
        for(int i = 0; i < wordsAndNums.size() && i < Max_Num; i ++){
            System.out.println(wordsAndNums.get(i).getS() + ": " + wordsAndNums.get(i).getN());
        }
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


    public static void main(String[] args) {
        String path = "forestier_mayotte.txt";
        Counter counter = new Counter();
        try {
            BufferedReader in = new BufferedReader(new FileReader(path));
            String line;
            counter.startTiming();
            while((line = in.readLine()) != null){
                counter.handleLines(line);
            }
            counter.endTiming("Counting");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        counter.startTiming();
        counter.showWords();
        counter.endTiming("Sorting");
    }
}
