package User;

import java.io.Serializable;

public class Message implements Serializable {

    public enum Content {GETSTRING, SWITCHDICTIONARY, NEWSTRING, SDCOMPLETE,
    TIMERUPDATE, NOTIFICATIONSERVERSETUP}
    private Content content;
    private Object obj;

    public Message(Content content, Object obj){
        this.content = content;
        this.obj = obj;
    }

    public Content getContent(){
        return content;
    }

    public Object getObj(){
        return obj;
    }

    public void setObj(Object obj){
        this.obj = obj;
    }
}
