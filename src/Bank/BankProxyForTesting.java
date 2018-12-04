package Bank;

import Utility.*;

import java.io.IOException;
import java.util.ArrayList;

/**
 * A temporary class for testing the Bank class, a BankProxyForTesting is used by a
 * BankClient to mediate communications with a Bank.
 * created: 11/30/18 by Warren D. Craft (wdc)
 * last modified: 12/02/18 by wdc
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
     * BankClient to mediate communications with an actual Bank.
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
