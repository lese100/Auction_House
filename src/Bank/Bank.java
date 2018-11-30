package Bank;

import Utility.AccountLink;
import Utility.BankAccount;
import Utility.NotificationServer;

import java.io.IOException;
import java.util.HashMap;

public class Bank {

    private String location;  // machine location
    private int portNumber;   // port used for clients
    private HashMap<Integer, BankAccount> auctionHouseAccts;
    private HashMap<Integer, BankAccount> agentAccts;
    private HashMap<Integer, AccountLink> secretKeys;
    private BankProtocol bankProtocol;
    private NotificationServer notificationServer;

    // ****************************** //
    //   Constructor(s)               //
    // ****************************** //

    /**
     * Public constructor for a Bank object, requiring no specific params.
     * This then produces a Bank object with default location = localhost
     * and default port number 1234.
     */
    public Bank () {
        this("localhost", 1234);
    }

    /**
     * Public constructor for a Bank object, allowing the initial specification
     * of the Bank's (machine) location (e.g. localhost or network machine name)
     * and the Bank's port number used for communications.
     * @param location String hostname
     * @param portNumber int communication port
     */
    public Bank ( String location, int portNumber ) {
        this.location = location;
        this.portNumber = portNumber;
        try {
            bankSetup();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ****************************** //
    //   Getter(s) & Setter(s)        //
    // ****************************** //

    // ****************************** //
    //   Public Methods               //
    // ****************************** //

    public void testMethod () {
        System.out.println("Bank: testMethod() called.");
    }

    // ****************************** //
    //   Utility Fxns                 //
    // ****************************** //

    private void bankSetup() throws IOException {
        bankProtocol = new BankProtocol(this);
        notificationServer = new NotificationServer(portNumber, bankProtocol);
        Thread serverThread = new Thread(notificationServer);
        serverThread.start();
    }


}
