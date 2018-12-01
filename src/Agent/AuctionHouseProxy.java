package Agent;

import Utility.*;

import java.io.IOException;
import java.util.List;

import static Utility.Message.MessageIdentifier.*;

public class AuctionHouseProxy {
    private CommunicationService coms;
    public AuctionHouseProxy(String hostName, int port){
        try {
            coms = new CommunicationService(hostName, port);
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    public List<AuctionItem> joinAH(IDRecord myRecord, int secretKey){
        myRecord.setNumericalID(secretKey);
        Message<IDRecord> message = new Message<>(Message.MessageIdentifier.JOIN_AUCTION_HOUSE,myRecord);
        Message<List<AuctionItem>> reply = sendMSG(message);
        if(reply != null){
            if(reply.getMessageIdentifier() == Message.MessageIdentifier.LIST_OF_AUCTION_HOUSE_ITEMS){
                return reply.getMessageContent();
            }else if(reply.getMessageIdentifier() == Message.MessageIdentifier.CASE_NOT_FOUND){
                System.out.println("AuctionHouse missing list of items");
            }
        }
        return null;

    }
    public int makeBid(AuctionItem item, double bid, int secretKey){
        Bid oldBid = item.getBid();
        oldBid.setBidState(Bid.BidState.BIDDING);
        oldBid.setSecretKey(secretKey);
        oldBid.setProposedBid(bid);
        item.setBid(oldBid);
        Message<AuctionItem> message = new Message<>(Message.MessageIdentifier.MAKE_BID,item);
        Message<String> reply = sendMSG(message);
        switch( reply.getMessageIdentifier() ) {
            case BID_REJECTED_INADEQUATE:
                return 0;
            case BID_REJECTED_NSF:
                return 1;
            case BID_ACCEPTED:
                return 2;
            case CASE_NOT_FOUND:
            default:
                return -1;
        }
    }
    private Message sendMSG(Message message){
        Message reply = null;
        try {
            reply = coms.sendMessage(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return reply;
    }
}
