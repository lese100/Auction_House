package User;

import java.io.IOException;
import java.util.ArrayList;

public class StatusProxy {

    private CommunicationService cs;

    public StatusProxy(CommunicationService cs){
        this.cs = cs;
    }

    public String changeDictionary(){
        Message message = null;

        try{
            message = new Message(Message.Content.SWITCHDICTIONARY, null);

            message = cs.sendMessage(message);
        }catch(IOException io){
            io.printStackTrace();
        }

        return ((ArrayList<String>) message.getObj()).get(0);
    }
}
