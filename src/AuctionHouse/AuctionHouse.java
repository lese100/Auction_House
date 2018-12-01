package AuctionHouse;

import Utility.CommunicationService;
import Utility.IDRecord;

import java.io.IOException;

public class AuctionHouse {

    private IDRecord IDRecord;
    private CommunicationService cs = null;
    private AuctionDisplay display;

    public AuctionHouse(AuctionDisplay display, String name, String hostName,
                        int port, String bankHostName, int bankPort)
            throws IOException {

        IDRecord = new IDRecord(Utility.IDRecord.RecordType.AUCTION_HOUSE,
                name, 0.0, hostName, port);

        //cs = new CommunicationService(bankHostName, bankPort);
        BankProxy bankProxy = new BankProxy();

        this.display = display;

        System.out.println("AH Created");
        System.out.println("NAME:\t" + name);
        System.out.println("HOST:\t" + hostName);
        System.out.println("PORT:\t" + port);
        System.out.println("BHOST:\t" + bankHostName);
        System.out.println("BPORT:\t" + bankPort);



    }
}
