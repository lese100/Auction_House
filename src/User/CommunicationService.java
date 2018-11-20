package User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class CommunicationService {


    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;


    public CommunicationService(String hostName, int port) throws IOException {
        socket = new Socket(hostName, port);
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());
    }

    public Message sendMessage(Message message) throws IOException{
        Message msg = null;
        out.writeObject(message);
        out.flush();

        try{
            msg = (Message) in.readObject();

        }catch(ClassNotFoundException cnf){
            cnf.printStackTrace();
        }

        return msg;
    }

}
