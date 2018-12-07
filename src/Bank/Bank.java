package Bank;

import Utility.AccountLink;
import Utility.BankAccount;
import Utility.IDRecord;
import Utility.NotificationServer;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.util.*;

/**
 * Provides the structure and functionality of a simulated Bank accessible to
 * Agents and Auction Houses, keeping track of client accounts
 * and providing some typical account functionality, such as opening and
 * closing accounts, funding an account, transferring funds from one account
 * to another, etc.
 * created: 11/28/18 by wdc
 * last modified: 12/06/18 by wdc (coord w/GUI)
 * previously modified: 12/01/18 by wdc
 * previously modified: 11/28/18 by wdc (creation)
 * @author Liam Brady (lb)
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
    // private String summaryInfoString;
    private BankProtocol bankProtocol;
    private NotificationServer notificationServer;

    private BankDisplay bankDisplay;

    private Random rng = new Random();
    // private DecimalFormat df = new DecimalFormat("####0.00");

    // ****************************** //
    //   Constructor(s)               //
    // ****************************** //

    /**
     * Generic public constructor for a Bank object, requiring no specific
     * params other than a reference to the Bank's GUI BankDisplay.
     * This then produces a Bank object with default location = localhost
     * and default port number 1234.
     */
    public Bank(BankDisplay bankDisplay) {
        this("bank", "localhost", 1234, bankDisplay);
    }

    /**
     * Public constructor for a Bank object, allowing the initial specification
     * of the Bank's (machine) location (e.g. localhost or network machine name)
     * the Bank's port number used for communications, and a reference to the
     * Bank's GUI BankDisplay object.
     * @param bankName String name of bank
     * @param location   String hostname
     * @param portNumber int communication port
     * @param bankDisplay A BankDisplay object
     */
    public Bank(String bankName,
                String location,
                int portNumber,
                BankDisplay bankDisplay) {

        this.bankName = bankName;
        this.location = location;
        this.portNumber = portNumber;
        this.bankDisplay = bankDisplay;

        // several lists for keeping track of account-related information
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
    //   (alphabetical order)         //
    // ****************************** //

    // --  Many of these public methods are intended for use by the  --//
    // --  BankProtocol class, which specifies how incoming requests --//
    // --  are handled                                               --//

    /**
     * Adds given amount to given account, providing a general method for
     * testing and future development.
     * @param theAcctNum int account number
     * @param amtToAdd double amount of money to add to account
     * @return updated BankAccount object
     */
    public BankAccount addFunds(int theAcctNum, double amtToAdd) {

        BankAccount currentBankAccount;

        // find BankAccount from Bank's account -> BankAccount HashMap
        currentBankAccount = hashMapOfAllAccts.get(theAcctNum);

        if ( currentBankAccount == null) {

            // if no account was found, generate a generic empty account
            currentBankAccount = new BankAccount();

        } else {

            // increase funds by amtToAdd
            currentBankAccount.increaseTotalBalance(amtToAdd);
        }

        updateBankDisplay();
        return currentBankAccount;

    }

    /**
     * Provides a way to (1) check an account to see if it has at least a
     * certain amount of unfrozen funds, and if so, to (2) freeze that amount
     * in funds
     * @param secretKey int secret key allowing access to the account
     * @param proposedFreeze double amount to check for and freeze
     * @return boolean true if checking and freezing the funds was successful,
     *                 false otherwise
     */
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

        if (fundsFrozen) {
            updateBankDisplay();
        }

        return fundsFrozen;
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
                // Complicated because process doesn't know how to compare
                // IDRecords (IDRecord class not over-riding .equals() method),
                // so using list.remove(Obj) does not work!
                // so we try something different …
                int indexToRemove = -1;
                for(int i = 0; i < listOfAuctionHouseIDRecords.size(); i++){
                    int tempAcctNum =
                        (listOfAuctionHouseIDRecords.get(i)).getNumericalID();
                    if (tempAcctNum == theBankAccountNumber ) {
                        indexToRemove = i;
                        break;
                    }
                }
                if ( indexToRemove >= 0 ) {
                    listOfAuctionHouseIDRecords.remove(indexToRemove);
                }

                // 3(c) More difficult: remove from the HashMap of secretKeys
                Set<Integer> setOfSecretKeys = hashMapOfSecretKeys.keySet();
                List<Integer> secretKeysToRemove = new ArrayList<>();
                for (int i : setOfSecretKeys) {
                    int tempAcctNum =
                        (hashMapOfSecretKeys.get(i)).getAH_ACCOUNT_NUMBER();
                    if ( tempAcctNum == theBankAccountNumber ) {
                        secretKeysToRemove.add(i);
                    }
                }
                // and only THEN remove the HashMap items
                // associated with those saved secretKeyToRemove items:
                if (secretKeysToRemove.size() > 0) {
                    for (int i = 0; i < secretKeysToRemove.size(); i++) {
                        hashMapOfSecretKeys.remove(secretKeysToRemove.get(i));
                    }
                }

                // 3(d) then update display and return BankAccount
                updateBankDisplay();
                return theBankAccount;
            }
            // (4) if account type is AGENT and Agent has no frozen funds
            else if ( theBankAccount.getAccountType() ==
                BankAccount.AccountType.AGENT &&
                theBankAccount.getTotalFrozen() == 0.0) {

                // 4(a) Remove account from hashMapOfAllAccts
                hashMapOfAllAccts.remove(theBankAccountNumber);

                // 4(b) Remove account from listOfAgentIDRecords
                // Complicated because process doesn't know how to compare
                // IDRecords (IDRecord class not over-riding .equals() method),
                // so using list.remove(Obj) does not work!
                // so we try something different …
                int indexToRemove = -1;
                for ( int i = 0; i < listOfAgentIDRecords.size(); i++ ) {
                    int tempAcctNum =
                        (listOfAgentIDRecords.get(i)).getNumericalID();
                    if (tempAcctNum == theBankAccountNumber ) {
                        indexToRemove = i;
                        break;
                    }
                }
                if ( indexToRemove >= 0 ) {
                    listOfAgentIDRecords.remove(indexToRemove);
                }

                // 4(c) More difficult: remove from the HashMap of secretKeys
                Set<Integer> setOfSecretKeys = hashMapOfSecretKeys.keySet();
                List<Integer> secretKeysToRemove = new ArrayList<>();
                for (int i : setOfSecretKeys) {
                    int tempAcctNum =
                        (hashMapOfSecretKeys.get(i)).getAGENT_ACCOUNT_NUMBER();
                    if ( tempAcctNum == theBankAccountNumber ) {
                        secretKeysToRemove.add(i);
                    }
                }
                // and only THEN remove the HashMap items
                // associated with those saved secretKeyToRemove items:
                if (secretKeysToRemove.size() > 0) {
                    for (int i = 0; i < secretKeysToRemove.size(); i++) {
                        hashMapOfSecretKeys.remove(secretKeysToRemove.get(i));
                    }
                }

                // 4(d) then  update display and return BankAccount
                updateBankDisplay();
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

    /**
     * Creates an account with the Bank, using information in the given
     * IDRecord to create a BankAccount object of type AGENT or AUCTION_HOUSE.
     * @param theIDRecord An IDRecord
     * @return IDRecord An updated IDRecord with new bank account number
     */
    public IDRecord createAccount(IDRecord theIDRecord) {

        IDRecord updatedIDRecord = theIDRecord;

        // pull out or generate info for BankAccount
        String userName = updatedIDRecord.getName();

        // generate an account number
        int acctNum = getUniqueAccountNumber();
        double initBalance = updatedIDRecord.getInitialBalance();
        updatedIDRecord.setNumericalID(acctNum);

        // Formalize info into an actual BankAccount object.
        // Some as-yet unused options here for future development.
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

        // Update Bank's list(s) of accounts.
        // Note: at account initiation, secretKeys HashMap not relevant.
        hashMapOfAllAccts.put(acctNum, newBankAccount);
        if (baType == BankAccount.AccountType.AUCTION_HOUSE) {

            listOfAuctionHouseIDRecords.add(updatedIDRecord);

        } else if (baType == BankAccount.AccountType.AGENT) {

            listOfAgentIDRecords.add(updatedIDRecord);
        }

        // Send requests to the BankDisplay thread to update display
        // to reflect new account information
        updateBankDisplay();

        return updatedIDRecord;
    }

    /**
     * Generates and returns an integer "secret key" used to link an Agent
     * account with an Auction House account, enabling the Agent and
     * Auction House to do business with each other.
     * @param theAccountLink An AccountLink object pairing two integer bank
     *                       account numbers
     * @return a unique multi-digit random integer
     */
    public int createSecretKey (AccountLink theAccountLink) {

        // should verify that the accountlink contains valid account #s
        // generate a unique secret key
        int aSecretKey = getUniqueSecretKey();
        // store away that secret key in HashMap with theAccountLink
        hashMapOfSecretKeys.put(aSecretKey, theAccountLink);

        return aSecretKey;
    }

    /**
     * Gets account balance information for the account whose
     * account number appears in the given IDRecord.
     * @param idRecord An IDRecord
     * @return BankAccount object with account balance information
     */
    public BankAccount getBalance (IDRecord idRecord) {

        BankAccount currentBankAccount;
        // extract the bank account number
        int theAcctNum = idRecord.getNumericalID();

        // find BankAccount from account -> BankAccount HashMap
        currentBankAccount = hashMapOfAllAccts.get(theAcctNum);

        if ( currentBankAccount == null) {
            // if no account was found, generate a generic empty account
            currentBankAccount = new BankAccount();

        }

        return currentBankAccount;
    }


    /**
     * Gets a BankAccount object associated with a secret key. Not all
     * BankAccount objects have an associated secret key, just those for
     * Agents who have joined up to do business with a specific chosen
     * Auction House.
     * @param secretKey int secret key associated with an AccountLink
     * @return BankAccount object
     */
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


    /**
     * Returns an ArrayList<IDRecord> of Auction House IDRecords corresponding
     * to Auction Houses currently having accounts with the Bank. The ArrayList
     * might be empty, but should never be null.
     * @return ArrayList<IDRecord> of Auction House IDRecords
     */
    public ArrayList<IDRecord> getListOfAuctionHouses () {

        return listOfAuctionHouseIDRecords;
    }

    /**
     * Checks if it is safe for the Bank to close -- basically returning
     * false if the Bank has any active client accounts.
     * @return Boolean True if Bank has no current clients, else false.
     */
    public boolean safeToClose(){
        if(hashMapOfAllAccts.isEmpty()){
            return true;
        }
        return false;
    }

    /**
     * Transfers the specified amount from one account to another, with the
     * secret key specifying a LinkedAccount object giving the source and
     * target/destination accounts for the transfer. Transfer executed only
     * if there are frozen funds in the source account equal to or greater
     * than the desired transfer amount. If frozen funds less than desired
     * transferred amount, no money is transferred at all.
     * @param secretKey int secret key specifying to an AccountLink
     * @param amtToTransfer double amount to transfer
     * @return boolean true if transfer successful; false otherwise
     */
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
        // This effects a transfer only if frozen funds >= amtToTransfer
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

    /**
     * Unfreezes funds in an account if the account's frozen funds exceed the
     * amount specified. If frozen funds are less than specified amount, entire
     * request is rejected.
     * @param secretKey int secret key allowing access to the bank account
     * @param amtToUnfreeze double amount to unfreeze
     * @return boolean true if unfreezing was successful (requiring there
     *                 to have been frozen funds at least equal to or greater
     *                 than the amount specified to be unfrozen); false
     *                 otherwise
     */
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


    // ****************************** //
    //   Private Utility Fxns         //
    // ****************************** //

    /**
     * Establishes the Bank's BankProtocol (handing it a reference to the
     * Bank) and the Bank's NotificationServer (using a port number defined
     * elsewhere and the BankProtocol object). The corresponding
     * NotificationServer created is then started on its own thread.
     * @throws IOException
     */
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

        // Make an ObservableArrayList of the BankAccounts
        // to pass to the BankDisplay
        ObservableList<BankAccount> tempListOfBankAccounts =
            FXCollections.observableArrayList();
        Set<Integer> tempSetOfAcctNumbers = hashMapOfAllAccts.keySet();
        for ( int tempAcctNum : tempSetOfAcctNumbers ) {
            BankAccount tempBA = hashMapOfAllAccts.get(tempAcctNum);
            tempListOfBankAccounts.add(tempBA);
        }

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                bankDisplay.updateNumberOfAccounts(hashMapOfAllAccts.size());
                bankDisplay.updateNumberOfAgentAccounts(
                    listOfAgentIDRecords.size());
                bankDisplay.updateNumberOfAHAccounts(
                    listOfAuctionHouseIDRecords.size());
                bankDisplay.updateAccountData(tempListOfBankAccounts);

            }
        });
    }

}
