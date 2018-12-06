package AuctionHouse;

import Utility.*;
import java.io.IOException;

/**
 * An Agent Client to mediate communications with an Agent. Communicates with
 * the Agent by sending serialized messages through a CommunicationService.
 * created: 11/30/18 by thf
 * last modified: 12/02/18 by thf
 * @author Liam Brady (lb)
 * @author Warren D. Craft (wdc)
 * @author Tyler Fenske (thf)
 */
public class AgentProxy {

    private CommunicationService cs;

    // ****************************** //
    //   Constructor(s)               //
    // ****************************** //

    /**
     * Constructor for the AgentProxy. An AgentProxy only stores its
     * CommunicationService it uses to talk to the actual Agent. A null
     * reference of CommunicationService will be passed if no Agent is
     * available for connection.
     * @param cs CommunicationService used to communicate with the Agent
     */
    public AgentProxy(CommunicationService cs){
        this.cs = cs;
    }

    // ****************************** //
    //   Private Methods              //
    // ****************************** //

    /**
     * Utility method that sends a Message, and returns the response Message.
     * @param message to be sent
     * @return Message (reply message)
     */
    private Message sendMsg(Message message){
        Message<IDRecord> returnMessage = null;

        try{
            returnMessage = cs.sendMessage(message);
        }catch(IOException e){
            e.printStackTrace();
        }

        return returnMessage;
    }

    // ****************************** //
    //   Public Methods               //
    // ****************************** //

    /**
     * Sends a notification to the agent that their bid was outbidded. The
     * AuctionItem that was bid on is sent in a message to the agent so they
     * know which item they were outbid on.
     */
    public void notifyOutbidded(AuctionItem ai){

        sendMsg(new Message<AuctionItem>
                (Message.MessageIdentifier.BID_OUTBIDDED, ai));
    }

    /**
     * Sends a message to the agent with un updated AuctionHouseInventory,
     * including the secretKey. and a List of the updated AuctionItems.
     * @param ahi AuctionHouseInventory object that contains the secretKey
     *            connected this agent and the AuctionHouse, and the current
     *            list of AuctionItems
     */
    public void updateAuctions(AuctionHouseInventory ahi){

        sendMsg(new Message<AuctionHouseInventory>
                (Message.MessageIdentifier.UPDATE_AUCTION_ITEMS, ahi));

    }

    /**
     * Sends a notification the agent that they won the bid on the passed
     * AuctionItem.
     * @param ai AuctionItem that the agent won a bid on
     */
    public void notifyWinner(AuctionItem ai) {

        sendMsg(new Message<AuctionItem>
                (Message.MessageIdentifier.BID_WON, ai));
    }
}
