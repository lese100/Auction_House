package Agent;
import Utility.*;

import java.io.IOException;

public class BankProxy {
    private CommunicationService coms;
    public BankProxy(String hostName, int port){
        try {
            coms = new CommunicationService(hostName, port);
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    public void sendMessage(){

    }
}
