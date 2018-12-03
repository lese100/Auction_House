package AuctionHouse;

import Utility.AuctionHouseInventory;
import Utility.AuctionItem;
import Utility.Message;
import Utility.PublicAuctionProtocol;

public class FakeAgentProtocol implements PublicAuctionProtocol {

    private FakeAgent fa;

    public FakeAgentProtocol(FakeAgent fa){
        this.fa = fa;
    }


    @Override
    public Message handleMessage(Message message) {
        Message reply = null;

        switch( message.getMessageIdentifier() ) {

            case BID_OUTBIDDED:

                break;

            case BID_WON:

                break;
            case UPDATE_AUCTION_ITEMS:

                break;
            default:

                break;
        }
        return reply;
    }
}
