package Agent;

import Utility.CommunicationService;
import java.io.IOException;

public class AuctionHouseProxy {
    private CommunicationService coms;
    public AuctionHouseProxy(String hostName, int port){
        try {
            coms = new CommunicationService(hostName, port);
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
