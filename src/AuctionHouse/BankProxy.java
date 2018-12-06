package AuctionHouse;

import Utility.*;
import java.io.IOException;
import java.util.Random;

/**
 * A Bank Client to mediate communications with a Bank. Communicates with the
 * Bank by sending serialized messages through a CommunicationService.
 * A null reference of CommunicationService will be passed if no Bank is
 * available for connection. If a null CommunicationService is detected, all
 * methods will return "fake" values to allow the AuctionHouse to still run.
 * created: 11/30/18 by thf
 * last modified: 12/02/18 by thf
 * @author Liam Brady (lb)
 * @author Warren D. Craft (wdc)
 * @author Tyler Fenske (thf)
 */
public class BankProxy {

    private CommunicationService cs;

    // ****************************** //
    //   Constructor(s)               //
    // ****************************** //

    /**
     * Constructor for the BankProxy. A BankProxy only stores its
     * CommunicationService it uses to talk to the actual Bank. A null
     * reference of CommunicationService will be passed if no Bank is
     * available for connection.
     * @param cs CommunicationService used to communicate with the Bank
     */
    public BankProxy(CommunicationService cs){
        this.cs = cs;
    }


    // ****************************** //
    //   Private Methods              //
    // ****************************** //

    /**
     * Creates a fake account number and returns it.
     * @param idRecord missing a numericalID field.
     * @return IDRecord with a fake account number added to it.
     */
    private IDRecord openFakeAccount(IDRecord idRecord){
        idRecord.setNumericalID(111111 * (new Random().nextInt(9) + 1));

        return idRecord;
    }

    /**
     * Asks the connected bank for a unique account number to be created.
     * The account number is received from the bank, stored in the
     * passed IDRecord, and returned.
     * @param idRecord with a missing numericalID field.
     * @return IDRecord with real account number added in the numericalID field.
     */
    private IDRecord openRealAccount(IDRecord idRecord){
        Message<IDRecord> msg = new Message<>
                (Message.MessageIdentifier.OPEN_AUCTIONHOUSE_ACCT, idRecord);

        Message<IDRecord> replyMessage = sendMsg(msg);

        if(replyMessage !=  null){
            if(replyMessage.getMessageIdentifier() ==
                    Message.MessageIdentifier.AUCTIONHOUSE_ACCT_CONFIRMED){
                return replyMessage.getMessageContent();
            }else if(replyMessage.getMessageIdentifier() ==
                    Message.MessageIdentifier.CASE_NOT_FOUND){
                System.out.println("Bank Functionality Unimplemented\n" +
                        " Provided fake account number.");
            }
        }
        return openFakeAccount(idRecord);
    }

    /**
     * Utility method that sends a Message, and returns the response Message.
     * @param message to be sent
     * @return Message (reply message)
     */
    private Message sendMsg(Message message){
        Message<IDRecord> returnMessage = null;

        try{
             returnMessage = cs.sendMessage(message);
        }catch(IOException e){
            e.printStackTrace();
        }

        return returnMessage;
    }

    // ****************************** //
    //   Public Methods               //
    // ****************************** //

    /**
     * Returns a copy of the IDRecord passed to it, with the numericalID field
     * filled in with an account number. If the bank is able to generate an
     * account number, that number will be used. If the bank cannot, or the
     * bank cannot be connected to, a fake account number will be provided.
     * @param idRecord missing numericalID field.
     * @return IDRecord with numericalID field filled in with account number.
     */
    public IDRecord openAccount(IDRecord idRecord){
        if(cs == null){
            return openFakeAccount(idRecord);
        }else{
            return openRealAccount(idRecord);
        }
    }

    /**
     * Sends a bid object to the Bank to let it know a bidder was outbidded
     * and needs their funds unfrozen.
     * @param bidderInfo bid object containing bidders secretKey and amount
     *                   that needs to be unfrozen in the "proposedBid" field.
     */
    public void unfreezeAgentFunds(Bid bidderInfo){
        if(cs != null){
            sendMsg(new Message<Bid>
                    (Message.MessageIdentifier.UNFREEZE_FUNDS, bidderInfo));
        }
    }

    /**
     * Sends a bid object to the Bank to ask if the agent in question has
     * enough funds to place the bid they are requesting to bid on. If
     * the bank returns "CHECK_SUCCESS", the method returns true, else it
     * returns false.
     * @param agentBidInfo bid object agent is interested in bidding on.
     * @return true if the bank returns CHECK_SUCCESS else false
     */
    public boolean checkAgentFunds(Bid agentBidInfo){
        if(cs == null){
            return true;
        }else{
            Message<Bid> msg = new Message<>
                    (Message.MessageIdentifier.CHECK_FUNDS, agentBidInfo);

            Message replyMessage = sendMsg(msg);

            return replyMessage.getMessageIdentifier().equals
                    (Message.MessageIdentifier.CHECK_SUCCESS);
        }
    }

    /**
     * Sends a message to the bank to let it know that the AuctionHouse is
     * disconnecting from it. The return message is irrelevant to the
     * AuctionHouse as long as it receives some sort of confirmation.
     * @param idRecord that contains this AuctionHouse's account number so the
     *                 bank can remove it from it's records.
     */
    public void closeAccount(IDRecord idRecord) {
        if (cs != null) {
            Message<IDRecord> msg = new Message<>
                    (Message.MessageIdentifier.CLOSE_REQUEST, idRecord);

            sendMsg(msg);
        }
    }

    /**
     * Asks the bank to send it it's BankAccount information. BankAccount's
     * inform the AuctionHouse of how much money it has in its account from
     * agents purchasing AuctionItems from it.
     * @param idRecord with account number in numericalID field.
     * @return BankAccount of this AuctionHouse
     */
    public BankAccount checkFunds(IDRecord idRecord){
        if(cs != null){
            Message<IDRecord> msg = new Message<>
                    (Message.MessageIdentifier.REQUEST_BALANCE, idRecord);

            return (BankAccount) sendMsg(msg).getMessageContent();
        }
        return null;
    }
}
