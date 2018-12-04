package Bank;

import Utility.AccountLink;
import Utility.BankAccount;
import Utility.IDRecord;
import Utility.NotificationServer;
import javafx.application.Platform;

import java.io.IOException;
import java.text.DecimalFormat;
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
    private HashMap<Integer, AccountLink> hashMapOfSecretKeys;
    private ArrayList<IDRecord> listOfAuctionHouseIDRecords;
    private ArrayList<IDRecord> listOfAgentIDRecords;
    private String summaryInfoString;
    private BankProtocol bankProtocol;
    private NotificationServer notificationServer;

    private BankDisplay bankDisplay;

    private Random rng = new Random();
    private DecimalFormat df = new DecimalFormat("####0.00");

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
        hashMapOfSecretKeys = new HashMap<>();
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

    public int createSecretKey (AccountLink theAccountLink) {
        // should verify that the accountlink contains valid account #s
        // generate a unique secret key
        int aSecretKey = getUniqueSecretKey();
        // store away that secret key in HashMap with theAccountLink
        hashMapOfSecretKeys.put(aSecretKey, theAccountLink);
        System.out.println("Bank.createSecretKey(): aSecretKey = " +
            aSecretKey);
        return aSecretKey;
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

        updateBankDisplay();
        return currentBankAccount;

    }

    public boolean checkAndFreezeFunds (int secretKey, double proposedFreeze) {

        int theBankAccountNumber;
        // use AccountLink and secret key to get actual Bank Account number
        AccountLink theAccountLink = hashMapOfSecretKeys.get(secretKey);
        if ( theAccountLink != null ) {
            theBankAccountNumber = theAccountLink.getAGENT_ACCOUNT_NUMBER();
        } else {
            return false;
        }
        // use account number to get full BankAccount
        BankAccount theBankAccount =
            hashMapOfAllAccts.get(theBankAccountNumber);

        // ask BankAccount to check and (if possible) freeze the amount
        boolean fundsFrozen = theBankAccount.checkAndFreeze(proposedFreeze);

        updateBankDisplay();

        return fundsFrozen;
    }

    public boolean unfreezeFunds (int secretKey, double amtToUnfreeze) {

        int theBankAccountNumber;
        // use AccountLink and secret key to get actual Bank Account number
        AccountLink theAccountLink = hashMapOfSecretKeys.get(secretKey);
        if ( theAccountLink != null ) {
            theBankAccountNumber = theAccountLink.getAGENT_ACCOUNT_NUMBER();
        } else {
            return false;
        }
        // use account number to get full BankAccount
        BankAccount theBankAccount =
            hashMapOfAllAccts.get(theBankAccountNumber);

        // ask BankAccount to check and (if possible) un-freeze the amount
        boolean fundsUnfrozen = theBankAccount.decreaseFreeze(amtToUnfreeze);

        if ( fundsUnfrozen ) {
            updateBankDisplay();
        }

        return fundsUnfrozen;
    }

    public boolean transferFunds (int secretKey, double amtToTransfer) {

        int sourceBankAccountNumber;   // account FROM which to transfer
        int targetBankAccountNumber;   // account TO which to transfer

        // use secretKey to obtain AccountLink, which will contains the
        // source and target BankAccount numbers
        AccountLink theAccountLink = hashMapOfSecretKeys.get(secretKey);

        if ( theAccountLink != null ) { // i.e. secretKey was valid

            sourceBankAccountNumber = theAccountLink.getAGENT_ACCOUNT_NUMBER();
            targetBankAccountNumber = theAccountLink.getAH_ACCOUNT_NUMBER();

        } else { // secretKey appears invalid; no transfer possible
            return false;
        }

        // use account numbers to get full BankAccounts
        BankAccount sourceBankAccount =
            hashMapOfAllAccts.get(sourceBankAccountNumber);
        BankAccount targetBankAccount =
            hashMapOfAllAccts.get(targetBankAccountNumber);

        // ask source BankAccount to delete amtToTransfer (if possible)
        boolean fundsTakenFromSource =
            sourceBankAccount.decreaseFrozenAndBalance(amtToTransfer);

        // if funds were able to be taken from source, then add amt to target
        if ( fundsTakenFromSource ) {
            targetBankAccount.increaseTotalBalance(amtToTransfer);
            updateBankDisplay();
        } else {
            return false; // b/c funds could not be taken from source
        }

        return true;

    }

    public BankAccount closeAccount ( IDRecord theIDRecord ) {

        // Basically, if request is legitimate, then remove associated
        // item(s) from the:
        // (1) hashMapOfAllAccts
        // (2) listOfAuctionHouseIDRecords;
        // (3) listOfAgentIDRecords
        // (4) hashMapOfSecretKeys

        // For closure requests from an Auction House, simply comply
        // with request -- any un-realized gains from
        // frozen-but-not-transferred funds from agent accounts is ignored

        // For closure requests from an Agent, comply with request only
        // if agent account has no frozen funds. Frozen funds indicate a
        // transfer that is pending.

        // (0) extract the Bank Account Number
        int theBankAccountNumber = theIDRecord.getNumericalID();

        // (1) Get the actual BankAccount
        BankAccount theBankAccount =
            hashMapOfAllAccts.get(theBankAccountNumber);

        // (2) If such a BankAccount actually exists ...
        if (theBankAccount != null) {

            // (3) Check if we're dealing with an Auction House
            if ( theBankAccount.getAccountType() ==
                 BankAccount.AccountType.AUCTION_HOUSE) {

                // 3(a) Remove account from hashMapOfAllAccts
                hashMapOfAllAccts.remove(theBankAccountNumber);

                // 3(b) Remove account from listOfAuctionHouseIDRecords
                // will this work? Does it know how to compare IDRecords?
                listOfAuctionHouseIDRecords.remove(theIDRecord);

                // 3(c) More difficult: remove from the HashMap of secretKeys
                Set<Integer> setOfSecretKeys = hashMapOfSecretKeys.keySet();
                for (int i : setOfSecretKeys) {
                    int tempAcctNum =
                        (hashMapOfSecretKeys.get(i)).getAH_ACCOUNT_NUMBER();
                    if ( tempAcctNum == theBankAccountNumber ) {
                        hashMapOfSecretKeys.remove(i);
                        // we don't break, because AH might be associated
                        // with multiple secret keys; so keep searching
                    }
                }

                // 3(d) then return BankAccount
                return theBankAccount;


            }
            // (4) if account type is AGENT and Agent has no frozen funds
            else if ( theBankAccount.getAccountType() ==
                       BankAccount.AccountType.AGENT &&
                       theBankAccount.getTotalFrozen() == 0.0) {

                // 4(a) Remove account from hashMapOfAllAccts
                hashMapOfAllAccts.remove(theBankAccountNumber);

                // 4(b) Remove account from listOfAgentIDRecords
                // will this work? Does it know how to compare IDRecords?
                listOfAgentIDRecords.remove(theIDRecord);

                // 4(c) More difficult: remove from the HashMap of secretKeys
                Set<Integer> setOfSecretKeys = hashMapOfSecretKeys.keySet();
                for (int i : setOfSecretKeys) {
                    int tempAcctNum =
                        (hashMapOfSecretKeys.get(i)).getAGENT_ACCOUNT_NUMBER();
                    if ( tempAcctNum == theBankAccountNumber ) {
                        hashMapOfSecretKeys.remove(i);
                        // we don't break, because AH might be associated
                        // with multiple secret keys; so keep searching
                    }
                }

                // 4(d) then return BankAccount
                return theBankAccount;

            } else {
                // valid BankAccount but:
                // not an auction house
                // not an agent with 0 frozen funds
                return new BankAccount();
            }

        } else {
            // null BankAccount -- cannot close
            return new BankAccount();
        }

    }

    public BankAccount getBankAccount (int secretKey) {
        // using a secretKey to obtain a BankAccount means/assumes that
        // the BankAccount belongs to an Agent that has been involved in
        // an AuctionHouse-linked transaction

        int theBankAccountNumber;
        // use the secretKey to get associated AccountLink
        AccountLink theAccountLink = hashMapOfSecretKeys.get(secretKey);

        if (theAccountLink != null) { // i.e. secret key was valid
            theBankAccountNumber = theAccountLink.getAGENT_ACCOUNT_NUMBER();
        } else {
            // secretKey appears to be invalid, return generic BankAccount
            return new BankAccount();
        }

        // return the associated BankAccount
        return hashMapOfAllAccts.get(theBankAccountNumber);

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
     * Private Bank utility function used internally to generate unique
     * random integer values for so-called secret keys. Should probably be
     * synchronized at some point.
     * Note: at the time of this writing, this method uses the same process
     * as the one for generating unique bank account numbers, but I wanted
     * to keep it separate so we have the option for the two processes to
     * produce different types of values.
     * @return int
     */
    private int getUniqueSecretKey () {

        System.out.println("Entering Bank: getUniqueSecretKey()");
        int minInt = 100000;
        int maxInt = 999999;
        int candidateNumber = -1;
        Set<Integer> listOfCurrentSecretKeys =
            hashMapOfSecretKeys.keySet();
        boolean numberIsUnique = false;
        while ( !numberIsUnique ) {

            candidateNumber = rng.nextInt(maxInt+1);
            // assume unique for a moment
            numberIsUnique = true;
            // verify uniqueness assumption
            for ( int theNum : listOfCurrentSecretKeys) {
                if (candidateNumber == theNum) {
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
            "\n\nAcct # \tType \tBalance \t\tFrozen \t\tAvailable \tUser Name";
        for ( IDRecord rec : listOfAgentIDRecords ) {
            int tempAcctNum = rec.getNumericalID();
            BankAccount tempBA = hashMapOfAllAccts.get(tempAcctNum);
            summaryInfoString = summaryInfoString +
                "\n" +    tempAcctNum +
                "\t" +    rec.getRecordType() +
                "\t$" +   df.format(tempBA.getTotalBalance()) +
                "\t\t$" + df.format(tempBA.getTotalFrozen()) +
                "\t\t$" + df.format(tempBA.getTotalUnfrozen()) +
                "\t\t" +  tempBA.getUserName();
        }
        for ( IDRecord rec : listOfAuctionHouseIDRecords ) {
            int tempAcctNum = rec.getNumericalID();
            BankAccount tempBA = hashMapOfAllAccts.get(tempAcctNum);
            summaryInfoString = summaryInfoString +
                "\n" + tempAcctNum +
                "\tAH" +
                "\t\t$" +  df.format(tempBA.getTotalBalance()) +
                "\t\t$" +  df.format(tempBA.getTotalFrozen()) +
                "\t\t$" +  df.format(tempBA.getTotalUnfrozen()) +
                "\t\t" + tempBA.getUserName();
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
