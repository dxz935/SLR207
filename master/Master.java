import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Master {
    private long startTime;
    private long endTime;
    public void startTiming(){
        startTime = System.currentTimeMillis();
    }
    public void endTiming(String s){
        endTime = System.currentTimeMillis();
        System.out.println("In " + s + ". " + "It costs " + (endTime - startTime) + " milliseconds");
    }
    public static void main(String args[]) throws IOException, InterruptedException {
        StringBuilder output = new StringBuilder();
        Master master = new Master();
        List<Process> ps = new LinkedList<Process>();
        List<MyStream> myStreams = new LinkedList<MyStream>();
        MyStream myStream = new MyStream();
        MyStream myStream1 = new MyStream();
        //String masterComputer = "lame10";
        String [] slaveComputers = new String[3];
        slaveComputers[0] = "lame11";
        slaveComputers[1] = "lame9";
        slaveComputers[2] = "lame8";
        //ProcessBuilder pb = new ProcessBuilder("java", "-jar", "slave.jar");
        ProcessBuilder pbGetLines = new ProcessBuilder("wc", "-l", "/Users/sunzengjin/mapReduce/input.txt");
        pbGetLines.redirectErrorStream(true);
        Process pGL = pbGetLines.start();
        myStream1.initial(pGL);
        myStream1.start();
        pGL.waitFor(); // wait for the process to terminate
        myStream1.join();
        String [] tmp = myStream1.getOutput().toString().split("\\W");
        int linesNum = Integer.parseInt(myStream1.getOutput().toString().split(" ")[6]);
        linesNum = linesNum / 3 + 1;
        Process deployP = Runtime.getRuntime().exec("java Deploy",null,
                new File("/Users/sunzengjin/deploy/out/production/deploy/"));
        deployP.waitFor();
        Process splitP = Runtime.getRuntime().exec("split -l "+ linesNum + " input.txt",
                null, new File("/Users/sunzengjin/mapReduce/"));
        splitP.waitFor();
        for(int i = 0; i < 3; i ++) {
            Process mvP = Runtime.getRuntime().exec("mv xa" + (char)(i + 'a') + " S" + i + ".txt",
                    null, new File("/Users/sunzengjin/mapReduce/"));
            mvP.waitFor();
        }
            /*
        ProcessBuilder pb = new ProcessBuilder(
                "mv", "/Users/sunzengjin/mapReduce/xaa", "/Users/sunzengjin/mapReduce/0.txt",
                "mv", "-f", "/Users/sunzengjin/mapReduce/xab", "/Users/sunzengjin/mapReduce/S1.txt",
                "mv", "-f", "/Users/sunzengjin/mapReduce/xac", "/Users/sunzengjin/mapReduce/S2.txt"
                );
        /*ProcessBuilder pb = new ProcessBuilder("ssh",
                "-o", "StrictHostKeyChecking=no",
                "-i", "~/Desktop/id_rsa", "zsun@" + masterComputer, "ls /tmp/zsun/splits",
                "exit"
        );

        pb.redirectErrorStream(true);
        Process process = pb.start();
        myStream.initial(process);
        myStream.start();
        process.waitFor(); // wait for the process to terminate
        myStream.join();
        System.out.println(myStream.getOutput().toString());
        */
        //String [] slaves = ls.split("\\n+");
        String [] slaves = new String[3];
        slaves[0] = "S0.txt";
        slaves[1] = "S1.txt";
        slaves[2] = "S2.txt";
        for(int i = 0; i < slaves.length; i ++){
            ProcessBuilder pbi = new ProcessBuilder("scp",
                    "-o", "StrictHostKeyChecking=no","-i", "~/Desktop/id_rsa", "/Users/sunzengjin/mapReduce/" + slaves[i],
                    "zsun@" + slaveComputers[i % 3] + ":" + "/tmp/zsun/splits");
            ProcessBuilder pbi2 = new ProcessBuilder("scp", "-3",
                    "-o", "StrictHostKeyChecking=no","-i", "~/Desktop/id_rsa", "/Users/sunzengjin/mapReduce/machines.txt",
                    "zsun@" + slaveComputers[i % 3] + ":" + "/tmp/zsun/");
            /*
            ProcessBuilder pbi = new ProcessBuilder("scp", "-3",
                    "-o", "StrictHostKeyChecking=no","-i", "~/Desktop/id_rsa", "zsun@" + masterComputer + ":" +
                    "/tmp/zsun/splits/" + slaves[i],
                    "zsun@" + slaveComputers[i % 3] + ":" + "/tmp/zsun/splits");
            ProcessBuilder pbi2 = new ProcessBuilder("scp", "-3",
                    "-o", "StrictHostKeyChecking=no","-i", "~/Desktop/id_rsa", "zsun@" + masterComputer + ":" +
                    "/tmp/zsun/machines.txt",
                    "zsun@" + slaveComputers[i % 3] + ":" + "/tmp/zsun/");
             */
            Process pba = pbi.start();
            MyStream m = new MyStream();
            m.initial(pba);
            m.start();
            ps.add(pba);

            Process pbb = pbi2.start();
            MyStream m2 = new MyStream();
            m2.initial(pbb);
            m2.start();
            ps.add(pbb);
        }
        for(Process p : ps)
            p.waitFor();
        ps.clear();

        master.startTiming();
        for(int i = 0; i < slaves.length; i ++){
            ProcessBuilder pbi = new ProcessBuilder("ssh",
                    "-o", "StrictHostKeyChecking=no",
                    "-i", "~/Desktop/id_rsa", "zsun@" + slaveComputers[i % 3], "java -jar /tmp/zsun/slave.jar","0",
                    "/tmp/zsun/splits/" + slaves[i],
                    "exit"
            );
            Process pba = pbi.start();
            MyStream m = new MyStream();
            m.initial(pba);
            m.start();
            ps.add(pba);
        }
        for(Process p : ps)
            p.waitFor();
        ps.clear();
        System.out.println("MAP FINISHED");
        master.endTiming("MAP");

        master.startTiming();
        for(int i = 0; i < slaves.length; i ++){
            ProcessBuilder pbi = new ProcessBuilder("ssh",
                    "-o", "StrictHostKeyChecking=no",
                    "-i", "~/Desktop/id_rsa", "zsun@" + slaveComputers[i % 3], "java -jar /tmp/zsun/slave.jar","1",
                    "/tmp/zsun/maps/" + "UM" + slaves[i].charAt(1) + ".txt",
                    "exit"
            );
            Process pba = pbi.start();
            MyStream m = new MyStream();
            m.initial(pba);
            m.start();
            ps.add(pba);
        }
        for(Process p : ps)
            p.waitFor();
        ps.clear();
        System.out.println("SHUFFLE FINISHED");
        master.endTiming("SHUFFLE");

        master.startTiming();
        for(int i = 0; i < slaves.length; i ++){
            ProcessBuilder pbi = new ProcessBuilder("ssh",
                    "-o", "StrictHostKeyChecking=no",
                    "-i", "~/Desktop/id_rsa", "zsun@" + slaveComputers[i % 3], "java -jar /tmp/zsun/slave.jar","2",
                    "exit"
            );
            Process pba = pbi.start();
            MyStream m = new MyStream();
            m.initial(pba);
            m.start();
            ps.add(pba);
        }
        for(Process p : ps)
            p.waitFor();
        ps.clear();
        System.out.println("REDUCE FINISHED");
        master.endTiming("REDUCE");

        master.startTiming();
        for(int i = 0; i < slaves.length; i ++){
            ProcessBuilder pbi = new ProcessBuilder("ssh",
                    "-o", "StrictHostKeyChecking=no",
                    "-i", "~/Desktop/id_rsa", "zsun@" + slaveComputers[i % 3], "cat /tmp/zsun/reduces/*.txt",
                    "exit"
            );
            Process pba = pbi.start();
            MyStream m = new MyStream();
            m.initial(pba);
            m.start();
            ps.add(pba);
            myStreams.add(m);
        }
        for(Process p : ps)
            p.waitFor();
        for(MyStream m : myStreams)
            m.join();
        ps.clear();
        for(MyStream m: myStreams)
            output.append(m.getOutput());
        myStreams.clear();
        System.out.println(output);
        System.out.println("OUTPUT FINISHED");
        master.endTiming("OUTPUT");
    }
}
