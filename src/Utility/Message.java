package Utility;

import java.io.Serializable;

/**
 * Provides the underlying structure of a Message to allow
 * communication among the triplet of entities Bank, Auction House,
 * and Agent.
 * created: 11/20/18 by thf
 * last modified: 11/21/18 by wdc (adapting to Utility package)
 * previously modified: 11/20/18 by thf (creation)
 * @author Tyler Fenske (thf)
 * @author Warren D. Craft (wdc)
 * @author Liam Brady (lb)
 */
public class Message <T> implements Serializable {

    // enum to be further elaborated with other identifiers
    public enum MessageIdentifier {AGENT_OPENING_ACCOUNT,
                                   AUCTION_HOUSE_OPENING_ACCOUNT};

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
