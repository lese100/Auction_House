package AuctionHouse;

import Utility.AuctionHouseInventory;
import Utility.IDRecord;
import Utility.Message;
import Utility.PublicAuctionProtocol;

import java.io.IOException;

public class AuctionHouseProtocol implements PublicAuctionProtocol {

    private AuctionHouse auctionHouse;

    public AuctionHouseProtocol(AuctionHouse auctionHouse){
        this.auctionHouse = auctionHouse;
    }

    @Override
    public Message handleMessage(Message message) {

        Message reply = null;

        try{
            switch(message.getMessageIdentifier()){

                case CLOSE_REQUEST:
                    break;
                case JOIN_AUCTION_HOUSE:
                    auctionHouse.joinAuctionHouse((IDRecord)
                            message.getMessageContent());

                    AuctionHouseInventory ahi = new AuctionHouseInventory
                            (auctionHouse.getIdRecord().getNumericalID(),
                                    auctionHouse.getAuctions());

                    reply = new Message<>
                            (Message.MessageIdentifier.
                                    LIST_OF_AUCTION_HOUSE_ITEMS, ahi);
                    break;
                case MAKE_BID:
                    break;
                default:
                    reply = new Message<>
                            (Message.MessageIdentifier.CASE_NOT_FOUND, null);

            }
        }catch(IOException e){
            e.printStackTrace();
        }





        return reply;
    }
}
