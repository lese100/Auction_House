package Agent;

import Utility.*;

import java.io.IOException;
import java.util.List;

public class AuctionHouseProxy {
    private CommunicationService coms;

    /**
     * establishes a connection with the auction house.
     * @param hostName the auction houses host name
     * @param port the auction houses port number
     */
    public AuctionHouseProxy(String hostName, int port){
        try {
            coms = new CommunicationService(hostName, port);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * Joins the auction house with my user information and the secret key linking us. The Auction house
     * then provides the list of items up for auction.
     * @param myRecord my user information
     * @param secretKey the secret key that does in place of my account number.
     * @return list of items up for auction
     */
    public List<AuctionItem> joinAH(IDRecord myRecord, int secretKey){
        IDRecord temp = new IDRecord(myRecord.getRecordType(),myRecord.getName(),myRecord.getInitialBalance(),
                myRecord.getHostname(),myRecord.getPortNumber());
        temp.setNumericalID(secretKey);
        Message<IDRecord> message = new Message<>(Message.MessageIdentifier.JOIN_AUCTION_HOUSE,temp);
        Message<AuctionHouseInventory> reply = sendMSG(message);
        if(reply != null){
            if(reply.getMessageIdentifier() == Message.MessageIdentifier.LIST_OF_AUCTION_HOUSE_ITEMS){
                return reply.getMessageContent().getAuctions();
            }else if(reply.getMessageIdentifier() == Message.MessageIdentifier.CASE_NOT_FOUND){
                System.out.println("AuctionHouse missing list of items");
            }
        }
        return null;

    }

    public Boolean closeRequest(IDRecord myRecord, int secretKey){
        IDRecord temp = new IDRecord(myRecord.getRecordType(),myRecord.getName(),myRecord.getInitialBalance(),
                myRecord.getHostname(),myRecord.getPortNumber());
        temp.setNumericalID(secretKey);
        Message<IDRecord> message = new Message<>(Message.MessageIdentifier.CLOSE_REQUEST, temp);
        Message <Integer>reply = sendMSG(message);
        if(reply.getMessageIdentifier() == Message.MessageIdentifier.CLOSE_REJECTED){
            return false;
        }else if(reply.getMessageIdentifier() == Message.MessageIdentifier.CLOSE_ACCEPTED) {
            return true;
        }else if(reply.getMessageIdentifier() == Message.MessageIdentifier.CASE_NOT_FOUND){
            System.out.println("Auction missing close request");
        }
        return true;
    }
    /**
     * sends a message to the auction house telling them to place a bid on a
     * certain item and the bid amount
     * @param item the item im bidding on
     * @param secretKey the secret key associated with my account.
     * @return
     */
    public int makeBid(AuctionItem item, int secretKey){
        Bid oldBid = item.getBid();
        oldBid.setSecretKey(secretKey);
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
    /**
     * Send a message to the auction house and waits for a reply.
     * @param message message being sent to the auction house
     * @return reply received from the auction house
     */
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
