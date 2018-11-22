package Utility;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Provides encapsulated method for establishing the socket and
 * ObjectOutputStream & ObjectInputStream to a specified host and host port.
 * created: 11/20/18 by thf
 * last modified: 11/21/18 by wdc (adapting to Utility package)
 * previously modified: 11/20/18 by thf (creation)
 * @author Tyler Fenske (thf)
 * @author Warren D. Craft (wdc)
 * @author Liam Brady (lb)
 */
public class CommunicationService {

    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    /**
     * Public constructor for a CommunicationService, establishing the socket
     * and ObjectOutputStream & ObjectInputStream to a specified host and
     * host port.
     * @param hostName String specifying the host name of an
     *                 already-established server
     * @param port     int specifying the port for the already-established
     *                 server
     * @throws IOException
     */
    public CommunicationService(String hostName, int port) throws IOException {

        socket = new Socket(hostName, port);
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());

    }

    public Message sendMessage(Message message) throws IOException {
        Message msg = null;
        out.writeObject(message);
        out.flush();

        try {
            msg =  (Message) in.readObject();

        } catch (ClassNotFoundException cnf) {
            cnf.printStackTrace();
        }

        return msg;
    }

}
