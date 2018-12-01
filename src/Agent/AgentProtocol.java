package Agent;

import Utility.*;
public class AgentProtocol implements PublicAuctionProtocol{
    private Agent agent;
    public AgentProtocol(Agent agent){
        this.agent = agent;
    }
    @Override
    /**
     * Provides the handleMessage() method required in implementing the
     * PublicAuctionProtocol, establishing the appropriate actions and reply
     * messages for messages coming in from Agent and Auction House clients.
     */
    public Message handleMessage(Message msgReceived) {

        Message msgToSend = null;
        switch( msgReceived.getMessageIdentifier() ) {

            case BID_OUTBIDDED:
                msgToSend = new Message<>(Message.MessageIdentifier.
                        ACKNOWLEDGED,
                        null);
                break;

            case BID_WON:
                msgToSend = new Message<>(Message.MessageIdentifier.
                        ACKNOWLEDGED,
                        null);
                break;
            case UPDATE_AUCTION_ITEMS:
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
}
