package User;

import java.io.Serializable;

public class Message <T> implements Serializable {

    public enum Content {GETSTRING, SWITCHDICTIONARY, NEWSTRING, SDCOMPLETE,
    TIMERUPDATE, NOTIFICATIONSERVERSETUP}
    private Content content;
    private T obj;

    public Message(Content content, T obj){
        this.content = content;
        this.obj = obj;
    }

    public Content getContent(){
        return content;
    }

    public T getObj(){
        return obj;
    }

    public void setObj(T obj){
        this.obj = obj;
    }
}
