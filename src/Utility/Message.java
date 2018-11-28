package Utility;

import java.io.Serializable;

/**
 * Provides the underlying structure of a Message to allow
 * communication among the triplet of entities Bank, Auction House,
 * and Agent.
 * created: 11/20/18 by thf
 * last modified: 11/25/18 by thf (adding generics to message structure)
 * previously modified: 11/21/18 by wdc (adapting to Utility package)
 * @author Tyler Fenske (thf)
 * @author Warren D. Craft (wdc)
 * @author Liam Brady (lb)
 */
public class Message <T> implements Serializable {

    /*
    enum comments use A = Agent, B = BANK, AH = Auction House
    most recent count is 29 items, defined in the code in alphab order
    organized in comments below by category
    IDs for Agent-Sent Messages:
        ACKNOWLEDGED               (sent to AH)
        CLOSE_REQUEST              (sent to B or AH)
        GET_LIST_OF_AUCTION_HOUSES (sent to B)
        GET_SECRET_KEY             (sent to B)
        JOIN_AUCTION_HOUSE         (sent to AH)
        MAKE_BID                   (sent to AH)
        OPEN_AGENT_ACCT            (sent to B)
        REQUEST_BALANCE            (sent to B)
        TRANSFER_FUNDS             (sent to B)

    IDS for AH-Sent Messages:
        BID_ACCEPTED                (sent to A, bid from A was accepted)
        BID_OUTBIDDED               (sent to A, A was outbidded)
        BID_REJECTED_INADEQUATE     (sent to A, less than min bid)
        BID_REJECTED_NSF            (sent to A, insufficient funds)
        BID_WON                     (sent to A, bid from A won auction)
        MAKE_BID                    (sent to AH)
        CHECK_FUNDS                 (sent to B, check A's funds for bid)
        CLOSE_ACCEPTED              (sent to A if OK to close out)
        CLOSE_REJECTED              (sent to A if not OK to close out)
        LIST_OF_AUCTION_HOUSE_ITEMS (sent to A)
        OPEN_AUCTIONHOUSE_ACCT      (sent to B)
        UNFREEZE_FUNDS              (sent to B)
        UPDATE_AUCTION_ITEMS        (sent to A)

    IDS for Bank-Sent Messages:
        AGENT_ACCT_CONFIRMED        (sent to A)
        AUCTIONHOUSE_ACCT_CONFIRMED (sent to AH)
        BALANCE                     (sent to A)
        CHECK_FAILURE               (sent to AH)
        CHECK_SUCCESS               (sent to AH)
        CLOSE_ACCEPTED              (sent to A or AH)
        CLOSE_REJECTED              (sent to A or AH)
        LIST_OF_AUCTION_HOUSES      (sent to A)
        SECRET_KEY                  (sent to A)
        TRANSFER_SUCCESS            (sent to A)
    */

    public enum MessageIdentifier {
        ACKNOWLEDGED,                // A reply to BID_OUTBIDDED or BID_WON
        AGENT_ACCT_CONFIRMED,        // B reply to OPEN_AGENT_ACCT
        AUCTIONHOUSE_ACCT_CONFIRMED, // B reply to OPEN_AUCTIONHOUSE_ACCT
        BALANCE,                     // B reply to REQUEST_BALANCE
        BID_ACCEPTED,                // AH reply to MAKE_BID
        BID_OUTBIDDED,               // AH send to A
        BID_REJECTED_INADEQUATE,     // AH reply to MAKE_BID
        BID_REJECTED_NSF,            // AH reply to MAKE_BID
        BID_WON,                     // AH send to A
        CHECK_FUNDS,                 // AH send to B
        CHECK_FAILURE,               // B reply to CHECK_FUNDS
        CHECK_SUCCESS,               // B reply to CHECK_FUNDS
        CLOSE_ACCEPTED,              // B or AH reply to CLOSE_REQUEST
        CLOSE_REJECTED,              // B or AH reply to CLOSE_REQUEST
        CLOSE_REQUEST,               // A request to B or AH; AH request to B
        GET_LIST_OF_AUCTION_HOUSES,  // A request list of AHs from B
        GET_SECRET_KEY,              // A request secret key from B for AH
        JOIN_AUCTION_HOUSE,          // A request to AH
        LIST_OF_AUCTION_HOUSE_ITEMS, // AH reply to JOIN_AUCTION_HOUSE
        LIST_OF_AUCTION_HOUSES,      // B reply to GET_LIST_OF_AUCTION_HOUSES
        MAKE_BID,                    // A send to AH
        OPEN_AGENT_ACCT,             // A request to B
        OPEN_AUCTIONHOUSE_ACCT,      // AH request to B
        REQUEST_BALANCE,             // A request to B
        SECRET_KEY,                  // B reply to GET_SECRET_KEY
        TRANSFER_FUNDS,              // A request to B
        TRANSFER_SUCCESS,            // B reply to TRANSFER_FUNDS
        UNFREEZE_FUNDS,              // AH request to B
        UPDATE_AUCTION_ITEMS         // AH send to A
    };

    private MessageIdentifier messageIdentifier;
    private T messageContent;

    // ****************************** //
    //   Constructor(s)               //
    // ****************************** //

    /**
     * Public constructor for a Message, which is an information structure
     * used for communication among the triplet of entities Bank, Auction
     * House, and Agent.
     * @param messageIdentifier enum identifying the type of message
     * @param messageContent Object containing message information
     */
    public Message (MessageIdentifier messageIdentifier,
                    T messageContent) {

        this.messageIdentifier = messageIdentifier;
        this.messageContent = messageContent;

    }

    // ****************************** //
    //   Getter(s) & Setter(s)        //
    // ****************************** //

    /**
     * Returns the Message's identifier, which specifies the type of message
     * @return MessageIdentifier in the form of a predefined enum.
     */
    public MessageIdentifier getMessageIdentifier() {
        return messageIdentifier;
    }

    /**
     * Returns the Message's actual content in the form of an Object, which
     * can then be cast or interpreted based on the Message's MessageIdentifier
     * @return Object to be typecast representing the Message content.
     */
    public T getMessageContent() {
        return messageContent;
    }

    /**
     * Allows the setting of the Message content in the general form of an
     * Object, so that a specific predetermined form is not required
     * (for example, one could use an ArrayList, a String, etc).
     * @param messageContent An Object representing message information.
     */
    public void setMessageContent(T messageContent) {
        this.messageContent = messageContent;
    }

    // ****************************** //
    //   Public Methods               //
    // ****************************** //

    // ****************************** //
    //   Utility Fxns                 //
    // ****************************** //


}
