package AuctionHouse;

import Utility.*;

import java.io.IOException;

/**
 * Message-handling protocol for messages received by a AuctionHouse from an
 * Agent or Bank.
 * created: 11/30/18 by thf
 * last modified: 12/02/18 by thf
 * @author Liam Brady (lb)
 * @author Warren D Craft (wdc)
 * @author Tyler Fenske (thf)*/
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
                    AuctionItem ai = (AuctionItem) message.getMessageContent();

                    Message.MessageIdentifier msgID = auctionHouse.makeBid(ai);

                    reply = new Message<>(msgID, null);
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
