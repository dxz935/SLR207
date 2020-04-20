import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

public class MySsh extends Thread{
    String line;
    public void setLine(String line){
        this.line = line;
    }
    MySsh(String line){
        this.line = line;
    }
    public void run() {
        ProcessBuilder processBuilder = new ProcessBuilder("ssh",
                "-o", "StrictHostKeyChecking=no",
                "-i", "~/Desktop/id_rsa", "zsun@" + line,  "mkdir -p /tmp/zsun/", "mkdir -p /tmp/zsun/splits",
                "mkdir -p /tmp/zsun/maps",
                "mkdir -p /tmp/zsun/shuffles",
                "mkdir -p /tmp/zsun/shufflesReceived",
                "mkdir -p /tmp/zsun/reduces",
                "exit"
                );
        ProcessBuilder processBuilder1 = new ProcessBuilder("scp",
                "-o", "StrictHostKeyChecking=no","-i", "~/Desktop/id_rsa",
                "/Users/sunzengjin/slave/out/artifacts/slave_jar/slave.jar",
                "zsun@" + line + ":" + "/tmp/zsun");
        ProcessBuilder processBuilder2 = new ProcessBuilder("scp",
                "-o", "StrictHostKeyChecking=no","-i", "~/Desktop/id_rsa",
                "/Users/sunzengjin/Desktop/id_rsa",
                "zsun@" + line + ":" + "/tmp/zsun");
        processBuilder.redirectErrorStream(true);
        Process process = null;
        Process process1 = null;
        Process process2 = null;
        try {
            process = processBuilder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        StringBuilder output = new StringBuilder();
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()));
        String line = null;
        while (true) {
            try {
                if (!(reader!=null && (line = reader.readLine()) != null)) break;
            } catch (IOException e) {
                e.printStackTrace();
            }
            output.append(line);
        }
        if(output.length() == 0)
            try {
                process1 = processBuilder1.start();
                process2 = processBuilder2.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        int exitVal = 0;
        try {
            exitVal = process.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (exitVal == 0) {
            System.out.println(output);
        }
        else
        {
            System.out.println(output);
        }
    }
}
