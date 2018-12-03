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

    public AgentProxy(CommunicationService cs){
        this.cs = cs;
    }

    public void notifyOutbidded(AuctionItem ai){

        sendMsg(new Message<AuctionItem>
                (Message.MessageIdentifier.BID_OUTBIDDED, ai));
    }

    public void updateAuctions(AuctionHouseInventory ahi){

        sendMsg(new Message<AuctionHouseInventory>
                (Message.MessageIdentifier.UPDATE_AUCTION_ITEMS, ahi));

    }

    public void testFoo(){
        System.out.println("FOOOOOO");
    }

    public void notifyWinner(AuctionItem ai) {

        sendMsg(new Message<AuctionItem>
                (Message.MessageIdentifier.BID_WON, ai));
    }

    private Message sendMsg(Message message){
        Message<IDRecord> returnMessage = null;

        try{
            returnMessage = cs.sendMessage(message);
        }catch(IOException e){
            e.printStackTrace();
        }

        return returnMessage;
    }



}
