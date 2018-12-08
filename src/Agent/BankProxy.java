package Agent;
import Utility.*;

import java.io.IOException;
import java.net.ConnectException;
import java.util.ArrayList;

/**
 * Handles all messages that will ever be sent to the bank and returns responses
 * from each message to the Agent.
 * created: 11/30/18 by lb
 * last modified: 12/07/18 by lb
 * @author Liam Brady (lb)
 * @author Warren D. Craft (wdc)
 * @author Tyler Fenske (thf)
 */
public class BankProxy {
    private CommunicationService coms;

    /**
     * establishes the initial connection to the bank.
     * @param hostName the banks hostname
     * @param port the banks port number
     */
    public BankProxy(String hostName, int port){
        try {
            coms = new CommunicationService(hostName, port);
        }catch (ConnectException e){
            System.out.println("connection to bank failed");
            coms = null;
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * tell the bank to create a bank account for me given my information
     * @param myInfo my personal information excluding the bank account number
     * @return myInfo updated with a account number provided by the bank
     */
    public IDRecord createBankAccount(IDRecord myInfo) {
        Message<IDRecord> message = new Message<>(Message.MessageIdentifier.
                OPEN_AGENT_ACCT, myInfo);
        Message<IDRecord> reply;
        reply = sendMSG(message);
        if(reply != null){
            if(reply.getMessageIdentifier() == Message.MessageIdentifier.
                    AGENT_ACCT_CONFIRMED){
                return reply.getMessageContent();
            }else if(reply.getMessageIdentifier() == Message.MessageIdentifier.
                    CASE_NOT_FOUND){
                System.out.println("bank missing create account");
            }
        }
        myInfo.setNumericalID(11111);
        return myInfo;
    }

    /**
     * requests a list of auction houses available from the bank
     * @return List of auction houses and their info provided by the bank
     */
    public ArrayList<IDRecord> getListOfAuctionHouses(){
        Message<ArrayList<IDRecord>> message = new Message(Message.
                MessageIdentifier.GET_LIST_OF_AUCTION_HOUSES, null);
        Message<ArrayList<IDRecord>> reply;
        reply = sendMSG(message);
        if(reply != null){
            if(reply.getMessageIdentifier() == Message.MessageIdentifier.
                    LIST_OF_AUCTION_HOUSES){
                return reply.getMessageContent();
            }else if(reply.getMessageIdentifier() == Message.MessageIdentifier.
                    CASE_NOT_FOUND){
                System.out.println("bank missing list of Auction Houses");
            }
        }
        return null;
    }

    /**
     * Requests for a balance check from the bank
     * @param myID My current information
     * @return and updated version of my information containing the correct
     * balance.
     */
    public BankAccount requestBalance(IDRecord myID){
        Message<IDRecord> message = new Message<>(Message.MessageIdentifier.
                REQUEST_BALANCE,myID);
        Message<BankAccount> reply = sendMSG(message);
        if(reply != null){
            if(reply.getMessageIdentifier() == Message.MessageIdentifier.
                    BALANCE){
                return reply.getMessageContent();
            }else if(reply.getMessageIdentifier() == Message.MessageIdentifier.
                    CASE_NOT_FOUND){
                System.out.println("Bank missing get balance");
            }
        }
        return null;
    }

    /**
     * Asks the bank for a secretkey to interact with the selected auction
     * house.
     * @param selection the auction house I selected
     * @return the secretkey I must use to interact with the auction house.
     */
    public int getSecretKey(AccountLink selection){
        Message<AccountLink> message = new Message<>(Message.MessageIdentifier.
                GET_SECRET_KEY,selection);
        Message<Integer> reply;
        reply = sendMSG(message);
        if(reply != null){
            if(reply.getMessageIdentifier() == Message.MessageIdentifier.
                    SECRET_KEY){
                return reply.getMessageContent();
            }else if(reply.getMessageIdentifier() == Message.MessageIdentifier.
                    CASE_NOT_FOUND){
                System.out.println("bank missing get Secret Key");
                return 123456;
            }
        }
        return -1;
    }

    /**
     * Sends a close request to the bank inorder to close the display
     * @param myID my IDRecord so the bank knows who's asking
     * @return a boolean saying whether or not the bank accepted my request
     */
    public Boolean closeRequest(IDRecord myID) {
        Message<IDRecord> message = new Message<>(Message.MessageIdentifier.
                CLOSE_REQUEST, myID);
        Message <Integer>reply = sendMSG(message);
        if (reply.getMessageIdentifier() == Message.MessageIdentifier.
                CLOSE_REJECTED) {
            return false;
        } else if (reply.getMessageIdentifier() == Message.MessageIdentifier.
                CLOSE_ACCEPTED) {
            return true;
        } else if (reply.getMessageIdentifier() == Message.MessageIdentifier.
                CASE_NOT_FOUND) {
            System.out.println("Bank missing close request");
        }
        return true;
    }
    /**
     * Tells the bank to transfer the funds for the auction item I won.
     * @param purchasedItem The item I purchased from a auction house
     * @return get returned my updated account information after the transfer
     */
    public BankAccount transferFunds(AuctionItem purchasedItem){
        Message<AuctionItem> message = new Message<>(Message.MessageIdentifier.
                TRANSFER_FUNDS,purchasedItem);
        Message<BankAccount> reply = sendMSG(message);
        if(reply != null){
            if(reply.getMessageIdentifier() == Message.MessageIdentifier.
                    TRANSFER_SUCCESS){
                return reply.getMessageContent();
            }else{
                System.out.println("Bank missing transfer funds");
            }
        }
        return null;
    }

    /**
     * Send a message to the bank and waits for a reply.
     * @param message message being sent to the bank
     * @return reply received from the bank
     */
    private Message sendMSG(Message message){
        Message reply = null;
        if(coms != null) {

            try {
                reply = coms.sendMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return reply;
    }
}
