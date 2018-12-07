package Agent;

import Utility.*;
import javafx.application.Platform;
/**
 * Handles messages received by the notification server.
 * created: 11/30/18 by lb
 * last modified: 12/07/18 by lb
 * @author Liam Brady (lb)
 * @author Warren D. Craft (wdc)
 * @author Tyler Fenske (thf)
 */
public class AgentProtocol implements PublicAuctionProtocol{
    private Message msgToSend;
    private Agent agent;
    public AgentProtocol(Agent agent){
        this.agent = agent;
    }
    @Override
    /**
     * Provides the handleMessage() method required in implementing the
     * AgentProtocol, establishing the appropriate actions and reply
     * messages for messages coming in from Bank and Auction House clients.
     */
    public Message handleMessage(Message msgReceived) {

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                switch( msgReceived.getMessageIdentifier() ) {

                    case BID_OUTBIDDED:
                        msgToSend = new Message<>(Message.MessageIdentifier.
                                ACKNOWLEDGED,
                                null);
                        Message<AuctionItem> msg = msgReceived;
                        agent.displayOutbid(msg.getMessageContent());
                        break;

                    case BID_WON:
                        msgToSend = new Message<>(Message.MessageIdentifier.
                                ACKNOWLEDGED,
                                null);
                        Message<AuctionItem> item = msgReceived;
                        agent.addTransferItem(item.getMessageContent());
                        break;
                    case UPDATE_AUCTION_ITEMS:
                        msgToSend = new Message<>(Message.MessageIdentifier.
                                ACKNOWLEDGED,
                                null);
                        Message<AuctionHouseInventory> auction = msgReceived;
                        agent.itemsUpdate(auction.getMessageContent());
                        break;
                    default:
                        msgToSend = new Message<>(Message.MessageIdentifier.
                                CASE_NOT_FOUND,
                                null);
                        break;
                }
            }
        });

        return msgToSend;

    }
}
