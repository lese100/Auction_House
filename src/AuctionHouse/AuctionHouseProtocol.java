package AuctionHouse;

import Utility.*;
import javafx.application.Platform;
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

    // ****************************** //
    //   Constructor(s)               //
    // ****************************** //

    /**
     * Constructor for an AuctionHouseProtocol. Holds a reference to the
     * AuctionHouse class in order to properly execute AuctionHouse methods
     * when routing messages.
     * @param auctionHouse
     */
    public AuctionHouseProtocol(AuctionHouse auctionHouse){
        this.auctionHouse = auctionHouse;
    }

    // ****************************** //
    //   Override Methods             //
    // ****************************** //

    /**
     * Opens a message, executes a set of instructions based on the message's
     * content, then returns a reply message to be sent back to the original
     * sender.
     * @param message received message that will be read
     * @return reply message that will be sent to the original sender in
     * response to the received message
     */
    @Override
    public Message handleMessage(Message message) {

        Message reply = null;

        try{
            switch(message.getMessageIdentifier()){

                case CLOSE_REQUEST:
                    if(auctionHouse.requestToLeaveAuctionHouse
                            ((IDRecord) message.getMessageContent())){
                        reply = new Message<>
                                (Message.MessageIdentifier.
                                        CLOSE_ACCEPTED, null);
                    }else{
                        reply = new Message<>
                                (Message.MessageIdentifier.
                                        CLOSE_REJECTED, null);
                    }
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
                case TRANSFER_FUNDS:
                    AuctionItem auctionItem =
                            (AuctionItem) message.getMessageContent();

                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            auctionHouse.updateBankBalance();
                        }
                    });

                    reply = new Message<>
                            (Message.MessageIdentifier.TRANSFER_SUCCESS, null);
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
