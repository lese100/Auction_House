package Testing;

import Utility.*;

import java.io.IOException;
import java.util.ArrayList;

/**
 * A temporary class for testing the Bank class, a BankProxyForTesting is
 * used by a (simulated) BankClient to mediate communications with a Bank.
 * created: 11/30/18 by Warren D. Craft (wdc)
 * last modified: 12/06/18 by wdc
 * @author Liam Brady (lb)
 * @author Warren D. Craft (wdc)
 * @author Tyler Fenske (thf)
 */
public class BankProxyForTesting {

    private CommunicationService cs;

    // ****************************** //
    //   Constructor(s)               //
    // ****************************** //

    /**
     * Public constructor for a BankProxyForTesting, which is used by a
     * (simulated) BankClient to mediate communications with an actual Bank.
     * @param cs CommunicationService
     */
    public BankProxyForTesting(CommunicationService cs){
        this.cs = cs;
    }

    // ****************************** //
    //   Public Methods               //
    //   most of which then use the   //
    //   CommunicationService to send //
    //   messages to actual Bank      //
    // ****************************** //

    /**
     * Opens a bank account with an initial balance using an IDRecord to
     * identify the client opening the account.
     * @param idRecord
     * @param initialBalance
     * @return An updated IDRecord now containing a valid bank account number
     */
    public IDRecord openBankAccount (IDRecord idRecord, double initialBalance) {

        System.out.println("Entering BankProxyForTesting.openBankAccount()");
        idRecord.setInitialBalance( initialBalance );
        System.out.println("openBankAccount(): set initial balance");
        Message message = null;
        try {
            message =
                new Message<>(Message.MessageIdentifier.OPEN_AGENT_ACCT,
                            idRecord);
            System.out.println("openBankAccount(): constructed message");

            message = cs.sendMessage(message);
            System.out.println("openBankAccount(): sent message");

        } catch ( IOException ioe ) {
            ioe.printStackTrace();
        }
        if ( message.getMessageIdentifier().equals(
                Message.MessageIdentifier.AGENT_ACCT_CONFIRMED
             ) && message.getMessageContent() instanceof IDRecord ) {
            System.out.println("openBankAccount(): modified IDRecord returned");
            System.out.println("with account # " +
                ((IDRecord) message.getMessageContent()).getNumericalID());
            return (IDRecord) message.getMessageContent();
        }
        System.out.println("openBankAccount(): DENIED");
        return idRecord;

    }

    /**
     * Gets a secret key for an Agent to use when contacting and doing business
     * with a specific Auction House.
     * @param theAccountLink An AccountLink object containing the Agent bank
     *                       account number and the account number for the
     *                       Auction House the Agent desires to do business
     *                       with.
     * @return int secret key
     */
    public int getSecretKey(AccountLink theAccountLink) {
        System.out.println("BankProxyForTesting.getSecretKey() ");
        Message message = null;
        Message<Integer> reply = null;
        try {
            message =
                new Message<>(Message.MessageIdentifier.GET_SECRET_KEY,
                    theAccountLink);
            reply = cs.sendMessage(message);

        } catch( IOException ioe ) {
            ioe.printStackTrace();
        }

        return reply.getMessageContent();
    }

    /**
     * Checks an account for suffient funds to be frozen, and freezes that
     * amount if available.
     * @param theBid A Bid object, which will contain an account number and the
     *               amount to check and freeze.
     * @return boolean true if check and freeze successful; false otherwise
     */
    public boolean checkAndFreezeFunds(Bid theBid) {
        Message message = null;
        Message reply = null;
        try {
            message =
                new Message<>(Message.MessageIdentifier.CHECK_FUNDS,
                    theBid);
            reply = cs.sendMessage(message);

        } catch( IOException ioe ) {
            ioe.printStackTrace();
        }

        if ( reply.getMessageIdentifier() ==
             Message.MessageIdentifier.CHECK_SUCCESS ) {

            return true;

        } else {
            return false;
        }
    }

    /**
     * Checks an account for sufficient funds to be unfrozen, and unfreezes
     * that amount if available.
     * @param theBid A Bid object, which will contain an account number and the
     *               amount to check and unfreeze.
     * @return boolean true if check and unfreeze successful; false otherwise
     */
    public boolean unfreezeFunds(Bid theBid) {
        Message message = null;
        Message reply = null;
        try {
            message =
                new Message<>(Message.MessageIdentifier.UNFREEZE_FUNDS,
                    theBid);
            reply = cs.sendMessage(message);

        } catch( IOException ioe ) {
            ioe.printStackTrace();
        }

        if ( reply.getMessageIdentifier() ==
            Message.MessageIdentifier.REQUEST_SUCCEEDED ) {

            return true;

        } else {
            return false;
        }

    }

    /**
     * Transfers funds from a source account to a target/destination account,
     * with the source, target, and transfer amounts specified in the
     * AuctionItem object.
     * @param theAuctionItem An AuctionItem object, which contains a Bid oject.
     * @return boolean true if transfer successful; false otherwise
     */
    public boolean transferFunds ( AuctionItem theAuctionItem ) {
        Message message = null;
        Message reply = null;
        try {
            message =
                new Message<>(Message.MessageIdentifier.TRANSFER_FUNDS,
                    theAuctionItem);
            reply = cs.sendMessage(message);

        } catch( IOException ioe ) {
            ioe.printStackTrace();
        }

        if ( reply.getMessageIdentifier() ==
             Message.MessageIdentifier.TRANSFER_SUCCESS ) {

            return true;

        } else {
            return false;
        }
    }

    /**
     * Closes an account specified by the account number in the accompanying
     * IDRecord.
     * @param theIDRecord An IDRecord object.
     * @return boolean true if account was successfully closed; false otherwise
     */
    public boolean closeAccount( IDRecord theIDRecord ) {

        Message message = null;
        Message reply = null;
        try {
            message =
                new Message<>(Message.MessageIdentifier.CLOSE_REQUEST,
                    theIDRecord);
            reply = cs.sendMessage(message);

        } catch( IOException ioe ) {
            ioe.printStackTrace();
        }

        if ( reply.getMessageIdentifier() ==
             Message.MessageIdentifier.CLOSE_ACCEPTED ) {

            return true;

        } else {
            return false;
        }
    }

    /**
     * Sends a simple test message, used to test communications.
     * @return String content of returning message
     */
    public String sendTestMessage () {
        Message message = null;
        try {
            message =
                new Message(Message.MessageIdentifier.TEST_MESSAGE, null);

            message = cs.sendMessage(message);

        } catch( IOException ioe ) {
            ioe.printStackTrace();
        }

        return (String) message.getMessageContent();
    }


    /**
     * Checks for an account's balance information, returning details
     * about the balance and frozen balance.
     * @param theIDRecord An IDRecord object specifying the account number
     * @return BankAccount object giving account balance information
     */
    public BankAccount checkBalance (IDRecord theIDRecord) {
        System.out.println("BankProxyForTesting: checkBalance() ");
        Message message = null;

        try{
            message =
                new Message<>(Message.MessageIdentifier.REQUEST_BALANCE,
                              theIDRecord);

            System.out.println("BankProxyForTesting: made new message");
            message = cs.sendMessage(message);
            System.out.println("BankProxyForTesting: received reply message");

        } catch(IOException io) {
            io.printStackTrace();
        }

        System.out.println("BankProxyForTesting: content balance: " +
            ((BankAccount)message.getMessageContent()).getTotalBalance() );
        return (BankAccount) message.getMessageContent();
    }

    /**
     * Returns an ArrayList<IDRecord> of Auction House IDRecords for all
     * Auction Houses currently having accounts at the Bank.
     * @return ArrayList<IDRecord>
     */
    public ArrayList<IDRecord> getListOfAuctionHouses () {
        Message message = null;
        Message<ArrayList<IDRecord>> reply = null;
        try{
            message =
                new Message<>(Message.MessageIdentifier.
                    GET_LIST_OF_AUCTION_HOUSES,
                    null);

            reply = cs.sendMessage(message);
            ArrayList<IDRecord> theList = reply.getMessageContent();
            System.out.println("BankProxy: getListOfAHs() try");
            System.out.println("reply type: " +
                reply.getMessageIdentifier().toString());
            for (IDRecord rec : theList) {
                System.out.println("Acct #: " + rec.getNumericalID());
            }

        } catch(IOException io) {
            io.printStackTrace();
        }

        System.out.println("BankProxy: getListOfAHs(): ");
        for (IDRecord rec : reply.getMessageContent()) {
            System.out.println("Acct #: " + rec.getNumericalID());
        }
        return reply.getMessageContent();
    }


    /**
     * Adds funds to an account, using an IDRecord to specify the account
     * number and the amount to be added to the account.
     * @param idRecord An IDRecord specifying account number and amount to
     *                 be added
     * @return BankAccount object giving account balance information
     */
    public BankAccount addFunds (IDRecord idRecord) {

        System.out.println("BankProxyForTesting.addFunds() ");
        Message message = null;

        try{
            message =
                new Message<>(Message.MessageIdentifier.ADD_FUNDS,
                    idRecord);

            System.out.println(
                "BankProxyForTesting.addFunds(): made new message");
            message = cs.sendMessage(message);
            System.out.println(
                "BankProxyForTesting.addFunds(): received reply message");

        } catch(IOException io) {
            io.printStackTrace();
        }

        BankAccount updatedBankAccount =
            (BankAccount) message.getMessageContent();
        System.out.println("BankProxyForTesting.addFunds(): " +
            "updatedBankAccount shows balance of $" +
            updatedBankAccount.getTotalBalance());
        return (BankAccount) message.getMessageContent();

    }

}
