package Bank;

import Utility.AccountLink;
import Utility.BankAccount;
import Utility.IDRecord;
import Utility.NotificationServer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Random;

public class Bank {

    private String location;  // machine location
    private int portNumber;   // port used for clients
    private HashMap<Integer, BankAccount> auctionHouseAccts;
    private HashMap<Integer, BankAccount> agentAccts;
    private HashMap<Integer, AccountLink> secretKeys;
    private BankProtocol bankProtocol;
    private NotificationServer notificationServer;

    private Random rng = new Random();

    // ****************************** //
    //   Constructor(s)               //
    // ****************************** //

    /**
     * Public constructor for a Bank object, requiring no specific params.
     * This then produces a Bank object with default location = localhost
     * and default port number 1234.
     */
    public Bank() {
        this("localhost", 1234);
    }

    /**
     * Public constructor for a Bank object, allowing the initial specification
     * of the Bank's (machine) location (e.g. localhost or network machine name)
     * and the Bank's port number used for communications.
     *
     * @param location   String hostname
     * @param portNumber int communication port
     */
    public Bank(String location, int portNumber) {
        this.location = location;
        this.portNumber = portNumber;
        auctionHouseAccts = new HashMap<>();
        agentAccts = new HashMap<>();
        secretKeys = new HashMap<>();
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

    public void testMethod() {
        System.out.println("Bank: testMethod() called.");
    }

    public IDRecord createAccount(IDRecord theIDRecord) {
        System.out.println("Bank.createAccount() begins");
        IDRecord updatedIDRecord = theIDRecord;

        // pull out or generate info for BankAccount
        String userName = updatedIDRecord.getName();
        System.out.println("Bank.createAccount(): userName = " + userName);
        // generate an account number -- method to be modified
        int acctNum = rng.nextInt(99999);
        double initBalance = updatedIDRecord.getInitialBalance();
        System.out.println("Bank.createAccount(): initialBalance = " +
            initBalance);
        updatedIDRecord.setNumericalID(acctNum);
        System.out.println("Bank.createAccount(): updated acctNum");

        // formalize info into an actual BankAccount object
        BankAccount.AccountType baType;
        switch (updatedIDRecord.getRecordType()) {
            case AGENT:
                baType = BankAccount.AccountType.AGENT;
                break;

            case AUCTION_HOUSE:
                baType = BankAccount.AccountType.AUCTION_HOUSE;
                break;

            case BANK: // for future generalization
                baType = BankAccount.AccountType.BANK;
                break;

            default:
                baType = BankAccount.AccountType.AGENT;
                break;
        }
        BankAccount newBankAccount =
            new BankAccount(baType, userName, acctNum, initBalance);
        System.out.println("Bank.createAccount(): just after creation, " +
            " the newBankAccount has balance: $" +
            newBankAccount.getTotalBalance());

        // add account to appropriate list of accounts
        if ( baType == BankAccount.AccountType.AGENT ) {
            agentAccts.put(acctNum, newBankAccount);
        } else if (baType == BankAccount.AccountType.AUCTION_HOUSE ) {
            auctionHouseAccts.put(acctNum, newBankAccount);
        } // no list of Bank BankAccounts in current edition

        return updatedIDRecord;
    }

    public BankAccount getBalance (IDRecord idRecord) {
        BankAccount currentBankAccount = new BankAccount();
        int theAcctNum = idRecord.getNumericalID();
        System.out.println("Bank.getBalance(): for acct # " + theAcctNum);
        BankAccount.AccountType baType;
        switch (idRecord.getRecordType()) {
            case AGENT:
                baType = BankAccount.AccountType.AGENT;
                break;

            case AUCTION_HOUSE:
                baType = BankAccount.AccountType.AUCTION_HOUSE;
                break;

            case BANK: // for future generalization
                baType = BankAccount.AccountType.BANK;
                break;

            default: // for other generalizations
                baType = BankAccount.AccountType.OTHER;
                break;
        }
        // find account from appropriate account list
        if ( baType == BankAccount.AccountType.AGENT ) {
            System.out.println("Bank: Acct appears to be for an AGENT");
            currentBankAccount = agentAccts.get(theAcctNum);
            System.out.println("Bank: Acct appears to have balance: $" +
                 currentBankAccount.getTotalBalance());
        } else if (baType == BankAccount.AccountType.AUCTION_HOUSE ) {
            System.out.println("Acct appears to be for an AUCTION HOUSE");
            currentBankAccount = auctionHouseAccts.get(theAcctNum);
        } // no list of Bank BankAccounts in current edition

        return currentBankAccount;
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
