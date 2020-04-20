import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Clean {
    public static void main(String args[]) throws IOException, InterruptedException {
        List<Myclean> l = new ArrayList<Myclean>();
        String [] Slaves = new String[3];
        Slaves[0] = "lame11";
        Slaves[1] = "lame9";
        Slaves[2] = "lame8";

        for(String host: Slaves){
            Myclean myclean = new Myclean(host);
            l.add(myclean);
            myclean.start();
        }
        for(Myclean mySsh: l)
            mySsh.join();
    }
}
