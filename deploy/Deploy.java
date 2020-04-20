import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Deploy {

    public static void main(String args[]) throws IOException, InterruptedException {
        List<MySsh> l = new ArrayList<MySsh>();
        String [] Slaves = new String[3];
        Slaves[0] = "lame11";
        Slaves[1] = "lame9";
        Slaves[2] = "lame8";
        /*
        File file = new File("/Users/sunzengjin/Ip");
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line = null;
        while ((line = br.readLine()) != null)
        {
            String[] temp = line.split(" ");
            int times = Integer.parseInt(temp[1]);
            for(int i = 1; i <= times; i ++) {
                String host;
                if(i < 10)
                    host = "tp-" + temp[0] + "-" + '0' + i;
                else
                    host = "tp-" + temp[0] + "-" +  i;
        */
        for(String host: Slaves){
                MySsh mySsh = new MySsh(host);
                l.add(mySsh);
                mySsh.start();
            }
        //}
        for(MySsh mySsh: l)
            mySsh.join();
    }
}
