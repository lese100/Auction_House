package AuctionHouse;

import Utility.*;

import java.io.IOException;
import java.util.Random;

public class BankProxy {

    private CommunicationService cs;

    public BankProxy(CommunicationService cs){
        this.cs = cs;
    }

    public IDRecord openAccount(IDRecord idRecord){
        if(cs == null){
            return openFakeAccount(idRecord);
        }else{
            return openRealAccount(idRecord);
        }
    }

    private IDRecord openFakeAccount(IDRecord idRecord){
        idRecord.setNumericalID(111111 * (new Random().nextInt(9) + 1));

        return idRecord;
    }

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


    private Message sendMsg(Message message){
        Message<IDRecord> returnMessage = null;

        try{
             returnMessage = cs.sendMessage(message);
        }catch(IOException e){
            e.printStackTrace();
        }

        return returnMessage;
    }


}
