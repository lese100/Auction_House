package User;

import java.io.Serializable;

public class Message implements Serializable {

    public enum Content {GETSTRING, SWITCHDICTIONARY, NEWSTRING, SDCOMPLETE,
    TIMERUPDATE, NOTIFICATIONSERVERSETUP}
    private Content content;
    private String string;

    public Message(Content content, String string){
        this.content = content;
        this.string = string;
    }

    public Content getContent(){
        return content;
    }

    public String getString(){
        return string;
    }

    public void setString(String string){
        this.string = string;
    }
}
