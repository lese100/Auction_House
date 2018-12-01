package Agent;
import Utility.*;

import java.io.IOException;
import java.util.ArrayList;

public class BankProxy {
    private CommunicationService coms;
    public BankProxy(String hostName, int port){
        try {
            coms = new CommunicationService(hostName, port);
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    public IDRecord createBankAccount(IDRecord myInfo) {
        Message<IDRecord> message = new Message<>(Message.MessageIdentifier.OPEN_AGENT_ACCT, myInfo);
        Message<IDRecord> reply;
        reply = sendMSG(message);
        if(reply != null){
            if(reply.getMessageIdentifier() == Message.MessageIdentifier.AGENT_ACCT_CONFIRMED){
                return reply.getMessageContent();
            }else if(reply.getMessageIdentifier() == Message.MessageIdentifier.CASE_NOT_FOUND){
                System.out.println("bank missing create account");
                myInfo.setNumericalID(11111);
                return myInfo;
            }
        }
        return null;
    }
    public ArrayList<IDRecord> getListOfAutionHouses(){
        Message<ArrayList<IDRecord>> message = new Message(Message.MessageIdentifier.GET_LIST_OF_AUCTION_HOUSES, null);
        Message<ArrayList<IDRecord>> reply;
        reply = sendMSG(message);
        if(reply != null){
            if(reply.getMessageIdentifier() == Message.MessageIdentifier.AGENT_ACCT_CONFIRMED){
                return reply.getMessageContent();
            }else if(reply.getMessageIdentifier() == Message.MessageIdentifier.CASE_NOT_FOUND){
                System.out.println("bank missing list of Auction Houses");
            }
        }
        return null;
    }
    public int getSecretKey(AccountLink selection){
        Message<AccountLink> message = new Message<>(Message.MessageIdentifier.GET_SECRET_KEY,selection);
        Message<Integer> reply;
        reply = sendMSG(message);
        if(reply != null){
            if(reply.getMessageIdentifier() == Message.MessageIdentifier.SECRET_KEY){
                return reply.getMessageContent();
            }else if(reply.getMessageIdentifier() == Message.MessageIdentifier.CASE_NOT_FOUND){
                System.out.println("bank missing get Secret Key");
                return 123456;
            }
        }
        return -1;
    }
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
