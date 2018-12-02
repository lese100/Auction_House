package Bank;

import Utility.BankAccount;
import Utility.CommunicationService;
import Utility.IDRecord;
import Utility.Message;

import java.io.IOException;
import java.util.ArrayList;

/**
 * A temporary class for testing the Bank class, a BankProxyForTesting is used by a
 * BankClient to mediate communications with a Bank.
 * created: 11/30/18 by Warren D. Craft (wdc)
 * last modified: 12/01/18 by wdc
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
     * Public constructor for a BankProxyForTesting, which is used by a BankClient to
     * mediate communications with an actual Bank.
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

    public ArrayList<IDRecord> getListOfAuctionHouses () {
        Message message = null;
        try{
            message =
                new Message<>(Message.MessageIdentifier.
                    GET_LIST_OF_AUCTION_HOUSES,
                    null);

            message = cs.sendMessage(message);

        } catch(IOException io) {
            io.printStackTrace();
        }

        return (ArrayList<IDRecord>) message.getMessageContent();
    }

}
