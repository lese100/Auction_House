package User;

import java.io.IOException;
import java.util.Random;

public class StringGenProxy {

    private CommunicationService cs;

    public StringGenProxy(CommunicationService cs){
        this.cs = cs;
    }


    public String pickRandomString(){
        String result;

        String[] strings = {"Zero", "One", "Two", "Three", "Four",
                "Five", "Six", "Seven", "Eight", "Nine", "Ten"};

        Random rand = new Random();

        int randomNum = rand.nextInt(11);
        result = strings[randomNum];

        System.out.println(randomNum);

        return result;
    }

    public String requestString(){
        Message message = null;

        try{
            message = new Message(Message.Content.GETSTRING, null);

            message = cs.sendMessage(message);
        }catch(IOException io){
            io.printStackTrace();
        }

        return (String) message.getObj();
    }
}
