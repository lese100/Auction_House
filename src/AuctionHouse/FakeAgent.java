package AuctionHouse;

import Utility.*;

import java.io.IOException;

public class FakeAgent {

    private CommunicationService cs;
    private FakeAgentProtocol pap;
    private NotificationServer ns;

    public FakeAgent(){

        try{
            cs = new CommunicationService("localhost", 1991);
            pap = new FakeAgentProtocol(this);
            ns = new NotificationServer(4323, pap);
            Thread thread = new Thread(ns);
            thread.start();
        }catch(IOException e){
            e.printStackTrace();
        }


        IDRecord idRecord = new IDRecord(IDRecord.RecordType.AGENT, "Test Agent", 32.00, "localhost", 4323);
        idRecord.setNumericalID(334);

        Message<IDRecord> msg = new Message<>(Message.MessageIdentifier.JOIN_AUCTION_HOUSE, idRecord);

        Message reply = null;
        try{
            reply = cs.sendMessage(msg);
        }catch(IOException e){
            e.printStackTrace();
        }

        AuctionHouseInventory ahi = (AuctionHouseInventory) reply.getMessageContent();

        if(reply.getMessageIdentifier().equals(Message.MessageIdentifier.LIST_OF_AUCTION_HOUSE_ITEMS)){


            System.out.println("Account Number " + ahi.getAccountNumber());
            for(AuctionItem ai : ahi.getAuctions()){
                System.out.println("ITEM ID: " + ai.getItemID() +
                        "\t BID STATE: " + ai.getBid().getBidState() +
                        "\t MIN BID: $" + ai.getBid().getMinBid() +
                        "\t CURRENT BID: $" + ai.getBid().getCurrentBid() +
                        "\t ITEM NAME: " + ai.getItemName());
            }
        }

        AuctionItem ai = ahi.getAuctions().get(0);
        ai.getBid().setSecretKey(334);
        ai.getBid().setProposedBid(.01);

        Message<AuctionItem> makeBidMessage = new Message<>(Message.MessageIdentifier.MAKE_BID, ai);

        Message reply1 = null;
        try{
            reply1 = cs.sendMessage(makeBidMessage);
        }catch(IOException e){
            e.printStackTrace();
        }

        System.out.println(reply1.getMessageIdentifier());


        AuctionItem ai1 = ahi.getAuctions().get(1);
        ai1.getBid().setSecretKey(334);
        ai1.getBid().setProposedBid(999.99);

        Message<AuctionItem> makeBidMessage1 = new Message<>(Message.MessageIdentifier.MAKE_BID, ai1);

        Message reply2 = null;
        try{
            reply2 = cs.sendMessage(makeBidMessage1);
        }catch(IOException e){
            e.printStackTrace();
        }

        System.out.println(reply2.getMessageIdentifier());

    }
}
