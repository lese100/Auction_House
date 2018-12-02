package AuctionHouse;

import Utility.Message;
import Utility.PublicAuctionProtocol;

public class FakeAgentProtocol implements PublicAuctionProtocol {

    private FakeAgent fa;

    public FakeAgentProtocol(FakeAgent fa){
        this.fa = fa;
    }


    @Override
    public Message handleMessage(Message message) {


        return null;
    }
}
