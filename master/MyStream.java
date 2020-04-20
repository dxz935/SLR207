import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class MyStream extends Thread {
    public void initial(Process process)
    {
        this.process = process;
    }
    private Process process;
    StringBuilder output = new StringBuilder();
    public StringBuilder getOutput(){
        return  output;
    }
    public void run(){
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()));
        String line = null;
        while (true) {
            try {
                if (!(reader!=null && (line = reader.readLine()) != null)) break;
            } catch (IOException e) {
                e.printStackTrace();
            }
            output.append(line + "\n");
        }
        int exitVal = 0;
        try {
            exitVal = process.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (exitVal == 0) {
            //System.out.println(output);
        }
        else
        {
            //System.out.println(output);
        }
    }
}
