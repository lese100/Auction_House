package Bank;

import Utility.Message;
import Utility.PublicAuctionProtocol;

/**
 * Message-handling protocol for messages received by a Bank through
 * a BankClientConnection.
 * created: 11/28/18 by Warren D. Craft (wdc)
 * last modified: 11/28/18 by wdc
 * @author Liam Brady (lb)
 * @author Warren D Craft (wdc)
 * @author Tyler Fenske (thf)
 */
public class BankProtocol implements PublicAuctionProtocol {


    Bank bank;

    // ****************************** //
    //   Constructor(s)               //
    // ****************************** //

    /**
     * The public constuctor for a BankProtocol, which implements the
     * PublicAuctionProtocol interface and provides elaboration for the
     * required handleMessage() method.
     * @param bank Bank object.
     */
    public BankProtocol(Bank bank) {
        // hand the protocol a reference to the creator Bank
        this.bank = bank;
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
        switch( msgReceived.getMessageIdentifier() ) {

            case CLOSE_REQUEST:
                msgToSend = new Message<>(Message.MessageIdentifier.
                    ACKNOWLEDGED,
                    null);
                break;

            case GET_LIST_OF_AUCTION_HOUSES:
                msgToSend = new Message<>(Message.MessageIdentifier.
                    ACKNOWLEDGED,
                    null);
                break;

            default:
                msgToSend = new Message<>(Message.MessageIdentifier.
                    CASE_NOT_FOUND,
                    null);
                break;
        }
        return msgToSend;

    }

    /*
    GET_LIST_OF_AUCTION_HOUSES (send to B)
        GET_SECRET_KEY             (send to B)


     */

    // ****************************** //
    //   Utility Fxns                 //
    // ****************************** //

}
