package AuctionHouse;

import Utility.Message;
import Utility.PublicAuctionProtocol;

public class AuctionHouseProtocol implements PublicAuctionProtocol {

    @Override
    public Message handleMessage(Message message) {

        Message msg = null;

        switch(message.getMessageIdentifier()){

            case CLOSE_REQUEST:
                break;
            case JOIN_AUCTION_HOUSE:
                break;
            case MAKE_BID:
                break;
            default:
                msg = new Message<>
                        (Message.MessageIdentifier.CASE_NOT_FOUND, null);

        }

        return msg;
    }
}
