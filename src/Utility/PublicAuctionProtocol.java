package Utility;

/**
 * Provides an interface for more specific protocols to use as an outline. Each
 * component (Bank, AH, Agent) of a PublicAuction has a definition for
 * their own protocol which defines how messages should be handled.
 * created: 11/23/18 by thf
 * last modified: 11/23/18 by thf
 * previously modified: 11/23/18 by thf (creation)
 * @author Tyler Fenske (thf)
 * @author Warren D. Craft (wdc)
 * @author Liam Brady (lb)
 */
public interface PublicAuctionProtocol {

    /**
     * Opens a message, executes a set of instructions based on the messages
     * content, then returns a reply message to be sent back to the original
     * sender.
     * @param message received message that will be read
     * @return reply message that will be sent to the original sender in
     * response to the received message
     */
    public Message handleMessage(Message message);

}
