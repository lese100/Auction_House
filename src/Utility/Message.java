package Utility;

import java.io.Serializable;

/**
 * Provides the underlying structure of a Message to allow
 * communication among the triplet of entities Bank, Auction House,
 * and Agent.
 * created: 11/20/18 by thf
 * last modified: 12/02/18 by wdc (adding some identifiers)
 * previously modified: 12/01/18 by wdc (adding some identifiers)
 * previously modified: 11/25/18 by thf (adding generics to message structure)
 * previously modified: 11/21/18 by wdc (adapting to Utility package)
 * @author Tyler Fenske (thf)
 * @author Warren D. Craft (wdc)
 * @author Liam Brady (lb)
 */
public class Message <T> implements Serializable {

    /*
    enum comments use A = Agent, B = BANK, AH = Auction House
    Most recent count is 34 items, defined in the code in alphab order.
    Organized in comments below by category. "reply" indicates a reply to
    a message initiated by another entity; "send" indicates a message
    initiated by the entity.

    IDs for Agent-Sent Messages:
        ACKNOWLEDGED               (reply to AH)
        ADD_FUNDS                  (send to B)
        CASE_NOT_FOUND             (reply to B or AH)
        CLOSE_REQUEST              (send to B or AH)
        GET_LIST_OF_AUCTION_HOUSES (send to B)
        GET_SECRET_KEY             (send to B)
        JOIN_AUCTION_HOUSE         (send to AH)
        MAKE_BID                   (send to AH)
        OPEN_AGENT_ACCT            (send to B)
        REQUEST_BALANCE            (send to B)
        TEST_MESSAGE               (send to B)
        TRANSFER_FUNDS             (send to B)

    IDS for AH-Sent Messages:
        BID_ACCEPTED                (reply to A, bid from A was accepted)
        BID_OUTBIDDED               (send to A, A was outbidded)
        BID_REJECTED_INADEQUATE     (reply to A, less than min bid)
        BID_REJECTED_NSF            (reply to A, insufficient funds)
        BID_WON                     (send to A, bid from A won auction)
        CASE_NOT_FOUND              (reply to A or B)
        CHECK_FUNDS                 (send to B, check A's funds for bid)
        CLOSE_ACCEPTED              (reply to A if OK to close out)
        CLOSE_REJECTED              (reply to A if not OK to close out)
        LIST_OF_AUCTION_HOUSE_ITEMS (reply to A)
        OPEN_AUCTIONHOUSE_ACCT      (send to B)
        UNFREEZE_FUNDS              (send to B)
        UPDATE_AUCTION_ITEMS        (send to A)

    IDS for Bank-Sent Messages:
        ACCOUNT_DENIED              (reply to A or AH)
        AGENT_ACCT_CONFIRMED        (reply to A)
        AUCTIONHOUSE_ACCT_CONFIRMED (reply to AH)
        BALANCE                     (reply to A)
        CASE_NOT_FOUND              (reply to A or AH)
        CHECK_FAILURE               (reply to AH)
        CHECK_SUCCESS               (reply to AH)
        CLOSE_ACCEPTED              (reply to A or AH)
        CLOSE_REJECTED              (reply to A or AH)
        LIST_OF_AUCTION_HOUSES      (reply to A)
        REQUEST_FAILED              (reply to A or AH)
        SECRET_KEY                  (reply to A)
        TRANSFER_SUCCESS            (reply to A)
    */

    public enum MessageIdentifier {
        ACCOUNT_DENIED,              // B reply to A or AH for account error
        ACKNOWLEDGED,                // A reply to BID_OUTBIDDED or BID_WON
        ADD_FUNDS,                   // A send to B to increase acct balance
        AGENT_ACCT_CONFIRMED,        // B reply to OPEN_AGENT_ACCT
        AUCTIONHOUSE_ACCT_CONFIRMED, // B reply to OPEN_AUCTIONHOUSE_ACCT
        BALANCE,                     // B reply to REQUEST_BALANCE
        BID_ACCEPTED,                // AH reply to MAKE_BID
        BID_OUTBIDDED,               // AH send to A
        BID_REJECTED_INADEQUATE,     // AH reply to MAKE_BID
        BID_REJECTED_NSF,            // AH reply to MAKE_BID
        BID_WON,                     // AH send to A
        CASE_NOT_FOUND,              // All entities reply to all entities
        CHECK_FUNDS,                 // AH send to B
        CHECK_FAILURE,               // B reply to CHECK_FUNDS
        CHECK_SUCCESS,               // B reply to CHECK_FUNDS
        CLOSE_ACCEPTED,              // B or AH reply to CLOSE_REQUEST
        CLOSE_REJECTED,              // B or AH reply to CLOSE_REQUEST
        CLOSE_REQUEST,               // A send to B or AH; AH send to B
        GET_LIST_OF_AUCTION_HOUSES,  // A send to B
        GET_SECRET_KEY,              // A send to B for AH
        JOIN_AUCTION_HOUSE,          // A send to AH
        LIST_OF_AUCTION_HOUSE_ITEMS, // AH reply to JOIN_AUCTION_HOUSE
        LIST_OF_AUCTION_HOUSES,      // B reply to GET_LIST_OF_AUCTION_HOUSES
        MAKE_BID,                    // A send to AH
        OPEN_AGENT_ACCT,             // A send to B
        OPEN_AUCTIONHOUSE_ACCT,      // AH send to B
        REQUEST_BALANCE,             // A send to B
        REQUEST_FAILED,              // B reply to A or AH
        SECRET_KEY,                  // B reply to GET_SECRET_KEY
        TEST_MESSAGE,                // A send to B
        TRANSFER_FUNDS,              // A send to B
        TRANSFER_SUCCESS,            // B reply to TRANSFER_FUNDS
        UNFREEZE_FUNDS,              // AH send to B
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
