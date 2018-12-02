package Bank;

import Utility.BankAccount;
import Utility.IDRecord;
import Utility.Message;
import Utility.PublicAuctionProtocol;

import java.util.ArrayList;
import java.util.Random;

/**
 * Message-handling protocol for messages received by a Bank through
 * a BankClientConnection.
 * created: 11/28/18 by Warren D. Craft (wdc)
 * last modified: 12/01/18 by wdc
 * @author Liam Brady (lb)
 * @author Warren D Craft (wdc)
 * @author Tyler Fenske (thf)
 */
public class BankProtocol implements PublicAuctionProtocol {


    Bank bank;
    Random rng;

    // ****************************** //
    //   Constructor(s)               //
    // ****************************** //

    /**
     * The public constuctor for a BankProtocol, which implements the
     * PublicAuctionProtocol interface and provides elaboration for the
     * required handleMessage() method.
     *
     * @param bank Bank object.
     */
    public BankProtocol(Bank bank) {
        // hand the protocol a reference to the creator Bank
        this.bank = bank;
        this.rng = new Random();
    }


    // ****************************** //
    //   Override Methods             //
    // ****************************** //

    @Override
    /**
     * Provides the handleMessage() method required in implementing the
     * PublicAuctionProtocol, establishing the appropriate actions and reply
     * messages for messages coming in from Agent and Auction House clients.
     */
    public Message handleMessage(Message msgReceived) {

        Message msgToSend = null;
        Object msgContent = msgReceived.getMessageContent();
        switch (msgReceived.getMessageIdentifier()) {

            // cases listed in alphabetical order by Message identifier

            case CLOSE_REQUEST:
                msgToSend = new Message<>(Message.MessageIdentifier.
                    ACKNOWLEDGED,
                    null);
                break;

            case GET_LIST_OF_AUCTION_HOUSES:
                ArrayList<IDRecord> theList = bank.getListOfAuctionHouses();
                msgToSend = new Message<>(Message.MessageIdentifier.
                    LIST_OF_AUCTION_HOUSES,
                    theList);
                break;

            case OPEN_AGENT_ACCT:
                if (msgContent instanceof IDRecord) {
                    // if msg content a valid IDRecord, prepare
                    // to reply with updated IDRecord
                    IDRecord updatedIDRecord =
                        bank.createAccount((IDRecord) msgContent);
                    msgToSend = new Message<>(Message.MessageIdentifier.
                        AGENT_ACCT_CONFIRMED,
                        updatedIDRecord);
                } else {
                    // if msg content not a valid IDRecord, prepare
                    // to simply send back original content
                    msgToSend = new Message<>(Message.MessageIdentifier.
                        ACCOUNT_DENIED,
                        msgContent);
                }
                break;

            case OPEN_AUCTIONHOUSE_ACCT:
                if (msgContent instanceof IDRecord) {
                    // if msg content a valid IDRecord, prepare
                    // to reply with updated IDRecord
                    IDRecord updatedIDRecord =
                        bank.createAccount((IDRecord) msgContent);
                    msgToSend = new Message<>(Message.MessageIdentifier.
                        AUCTIONHOUSE_ACCT_CONFIRMED,
                        updatedIDRecord);
                } else {
                    // if msg content not a valid IDRecord, prepare
                    // to simply send back original content
                    msgToSend = new Message<>(Message.MessageIdentifier.
                        ACCOUNT_DENIED,
                        msgContent);
                }
                break;

            case REQUEST_BALANCE:
                System.out.println("BankProtocol: case REQUEST_BALANCE");
                BankAccount theBankAccount;
                if ( msgContent instanceof IDRecord ) {
                    // if request supplied an IDRecord, get the BankAccount
                    theBankAccount =
                        bank.getBalance((IDRecord) msgContent);
                } else {
                    // otherwise, prepare a generic bank account object
                    theBankAccount = new BankAccount();
                }
                msgToSend =
                    new Message<>(Message.MessageIdentifier.BALANCE,
                                  theBankAccount);
                break;

            case TEST_MESSAGE:
                msgToSend = new Message<>(Message.MessageIdentifier.
                    ACKNOWLEDGED,
                    "Test Message Received");
                break;

            default:
                msgToSend = new Message<>(Message.MessageIdentifier.
                    CASE_NOT_FOUND,
                    null);
                break;

        } // end switch()

        // return the message built in one of the switch() cases above
        return msgToSend;

    }

    // ****************************** //
    //   Utility Fxns                 //
    // ****************************** //

}
