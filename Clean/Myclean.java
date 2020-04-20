import java.io.IOException;

public class Myclean extends Thread{
    String host;
    Myclean(String host) {
        this.host = host;
    }
    public void run(){
        ProcessBuilder processBuilder = new ProcessBuilder("ssh",
                "-o", "StrictHostKeyChecking=no",
                "-i", "~/Desktop/id_rsa", "zsun@" + host,
                "rm", "-rf", "/tmp/zsun",
                "exit"
        );
        try {
            Process process = processBuilder.start();
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

    }
}
