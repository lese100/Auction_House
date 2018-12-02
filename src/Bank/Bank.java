package Bank;

import Utility.AccountLink;
import Utility.BankAccount;
import Utility.IDRecord;
import Utility.NotificationServer;
import javafx.application.Platform;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Set;

/**
 * Provides the structure and functionality of a simulated Bank accessible to
 * Agents, Auction Houses, and other entities. Keeps track of client accounts
 * and provides some typical account functionality, such as opening and
 * closing accounts, funding an account, transferring funds from one account
 * to another, etc.
 * created: 11/28/18 by wdc
 * last modified: 12/02/18 by wdc (coord w/GUI)
 * previously modified: 12/01/18 by wdc
 * previously modified: 11/28/18 by wdc (creation)
 * @author Liam Brady
 * @author Warren D. Craft (wdc)
 * @author Tyler Fenske (thf)
 */
public class Bank {

    private String bankName;
    private String location;  // machine location
    private int portNumber;   // port used for clients
    private HashMap<Integer, BankAccount> hashMapOfAllAccts;
    private HashMap<Integer, AccountLink> secretKeys;
    private ArrayList<IDRecord> listOfAuctionHouseIDRecords;
    private ArrayList<IDRecord> listOfAgentIDRecords;
    private String summaryInfoString;
    private BankProtocol bankProtocol;
    private NotificationServer notificationServer;

    private BankDisplay bankDisplay;

    private Random rng = new Random();

    // ****************************** //
    //   Constructor(s)               //
    // ****************************** //

    /**
     * Public constructor for a Bank object, requiring no specific params.
     * This then produces a Bank object with default location = localhost
     * and default port number 1234.
     */
    public Bank(BankDisplay bankDisplay) {
        this("bank", "localhost", 1234, bankDisplay);
    }

    /**
     * Public constructor for a Bank object, allowing the initial specification
     * of the Bank's (machine) location (e.g. localhost or network machine name)
     * and the Bank's port number used for communications.
     * @param location   String hostname
     * @param portNumber int communication port
     */
    public Bank(String bankName,
                String location,
                int portNumber,
                BankDisplay bankDisplay) {
        this.bankName = bankName;
        this.location = location;
        this.portNumber = portNumber;
        this.bankDisplay = bankDisplay;
        hashMapOfAllAccts = new HashMap<>();
        secretKeys = new HashMap<>();
        listOfAuctionHouseIDRecords = new ArrayList<>();
        listOfAgentIDRecords = new ArrayList<>();
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

    /**
     * Creates an account with the Bank, using information in the given
     * IDRecord to create a BankAccount object of type AGENT or AUCTION_HOUSE.
     * @param theIDRecord An IDRecord
     * @return IDRecord An updated IDRecord with new bank account number
     */
    public IDRecord createAccount(IDRecord theIDRecord) {
        System.out.println("Bank.createAccount() begins");
        IDRecord updatedIDRecord = theIDRecord;

        // pull out or generate info for BankAccount
        String userName = updatedIDRecord.getName();
        System.out.println("Bank.createAccount(): userName = " + userName);
        // generate an account number
        int acctNum = getUniqueAccountNumber();
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

            case BANK:
                baType = BankAccount.AccountType.BANK;
                break;

            default:
                baType = BankAccount.AccountType.OTHER;
                break;
        }
        BankAccount newBankAccount =
            new BankAccount(baType, userName, acctNum, initBalance);
        System.out.println("Bank.createAccount(): just after creation, " +
            " the newBankAccount has balance: $" +
            newBankAccount.getTotalBalance());

        // update Bank's list(s) of accounts
        hashMapOfAllAccts.put(acctNum, newBankAccount);
        if (baType == BankAccount.AccountType.AUCTION_HOUSE) {
            listOfAuctionHouseIDRecords.add(updatedIDRecord);
            System.out.println("Bank: createAccount(): updated " +
                "listOfAuctionHouseIDRecords: ");
            for (IDRecord rec : listOfAuctionHouseIDRecords) {
                System.out.println("Acct #: " + rec.getNumericalID());
            }
        } else if (baType == BankAccount.AccountType.AGENT) {
            listOfAgentIDRecords.add(updatedIDRecord);
            System.out.println("Bank: createAccount(): updated " +
                "listOfAgentIDRecords: ");
            for (IDRecord rec : listOfAgentIDRecords) {
                System.out.println("Acct #: " + rec.getNumericalID());
            }
        }

        updateBankDisplay();

        return updatedIDRecord;
    }

    /**
     * Gets account balance information for the account whose
     * account number appears in the given IDRecord.
     * @param idRecord An IDRecord
     * @return BankAccount object with account balance information
     */
    public BankAccount getBalance (IDRecord idRecord) {
        System.out.println("Entering Bank: getBalance()");
        BankAccount currentBankAccount;
        int theAcctNum = idRecord.getNumericalID();
        System.out.println("Bank.getBalance(): for acct # " + theAcctNum);

        // find BankAccount from account -> BankAccount HashMap
        currentBankAccount = hashMapOfAllAccts.get(theAcctNum);

        if ( currentBankAccount == null) {
            // if no account was found, generate a generic empty account
            System.out.println("Bank: getBalance(): no account found!");
            currentBankAccount = new BankAccount();
        } else {
            System.out.println("Bank: getBalance(): account found!");
        }

        return currentBankAccount;
    }

    /**
     * Returns an ArrayList<IDRecord> of Auction House IDRecords corresponding
     * to Auction Houses currently having accounts with the Bank. The ArrayList
     * might be empty, but should never be null.
     * @return ArrayList<IDRecord> of Auction House IDRecords
     */
    public ArrayList<IDRecord> getListOfAuctionHouses () {
        System.out.println("Bank: getListofAHs(): ");
        for (IDRecord rec : listOfAuctionHouseIDRecords) {
            System.out.println("Acct #: " + rec.getNumericalID());
        }
        return listOfAuctionHouseIDRecords;
    }

    public BankAccount addFunds(int theAcctNum, double amtToAdd) {
        System.out.println("Bank.addFunds(): " + amtToAdd);
        BankAccount currentBankAccount;
        System.out.println("Bank.addFunds(): for acct # " + theAcctNum);

        // find BankAccount from Bank's account -> BankAccount HashMap
        currentBankAccount = hashMapOfAllAccts.get(theAcctNum);

        if ( currentBankAccount == null) {
            // if no account was found, generate a generic empty account
            System.out.println("Bank.addFunds(): no account found!");
            currentBankAccount = new BankAccount();
        } else {
            System.out.println("Bank.addFunds(): account found!");
            System.out.println("Bank.addFunds(): account has balance: $" +
                currentBankAccount.getTotalBalance());
            // increase funds by amtToAdd
            currentBankAccount.increaseTotalBalance(amtToAdd);
            System.out.println("Bank.addFunds(): increased total by: $" +
                amtToAdd);
            System.out.println("Bank.addFunds(): account now has " +
                "balance: $" + currentBankAccount.getTotalBalance());
        }

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

    /**
     * Private Bank utility function used internally to generate unique
     * random integer values for account numbers. Should probably be
     * synchronized at some point.
     * @return int
     */
    private int getUniqueAccountNumber () {

        System.out.println("Entering Bank: getUniqueAccountNumber()");
        int minInt = 100000;
        int maxInt = 999999;
        int candidateNumber = -1;
        Set<Integer> listOfCurrentAccountNumbers =
            hashMapOfAllAccts.keySet();
        boolean numberIsUnique = false;
        while ( !numberIsUnique ) {

            candidateNumber = rng.nextInt(maxInt+1);
            // assume unique for a moment
            numberIsUnique = true;
            // verify uniqueness assumption
            for ( int acctNum : listOfCurrentAccountNumbers) {
                if (candidateNumber == acctNum) {
                    numberIsUnique = false;
                    break;
                }
            } // end for() loop

        } // end while() loop

        return candidateNumber;
    }

    /**
     * Utility function to update display information when Bank accounts
     * change — for example, when new accounts are created, old accounts
     * closed, or existing accounts have funds frozen or unfrozen.
     * This updates all display information -- later we might break this
     * into pieces if we regularly need to update just one or two
     * components.
     */
    private void updateBankDisplay () {

        // create string of account info for displaying bankDisplay
        // first getting Agent accts, then Auction House accts
        summaryInfoString = "Summary of Account(s): " +
            "\n\nAcct # \t Type \t Balance \t\t Available \t User Name";
        for ( IDRecord rec : listOfAgentIDRecords ) {
            int tempAcctNum = rec.getNumericalID();
            BankAccount tempBA = hashMapOfAllAccts.get(tempAcctNum);
            summaryInfoString = summaryInfoString +
                "\n" + tempAcctNum +
                "\t" + rec.getRecordType() +
                "\t$" + tempBA.getTotalBalance() +
                "\t\t$" + tempBA.getTotalUnfrozen() +
                "\t\t" + tempBA.getUserName();
        }
        for ( IDRecord rec : listOfAuctionHouseIDRecords ) {
            int tempAcctNum = rec.getNumericalID();
            BankAccount tempBA = hashMapOfAllAccts.get(tempAcctNum);
            summaryInfoString = summaryInfoString +
                "\n" + tempAcctNum +
                "\tAH" +
                "\t\t$" + tempBA.getTotalBalance() +
                "\t\tN/A" +
                "\t\t\t" + tempBA.getUserName();
        }
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                bankDisplay.updateNumberOfAccounts(hashMapOfAllAccts.size());
                bankDisplay.updateNumberOfAgentAccounts(
                    listOfAgentIDRecords.size());
                bankDisplay.updateNumberOfAHAccounts(
                    listOfAuctionHouseIDRecords.size());
                bankDisplay.updateTextAreaOutput(summaryInfoString);
            }
        });
    }

}
