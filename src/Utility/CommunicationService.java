package Utility;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Provides encapsulated method for establishing the socket and
 * ObjectOutputStream & ObjectInputStream to a specified host and host port.
 * Used to reach out to a server and wait for a response.
 * created: 11/20/18 by thf
 * last modified: 11/23/18 by thf (cleaning up comments)
 * previously modified: 11/21/18 by wdc (adapting to Utility package)
 * @author Tyler Fenske (thf)
 * @author Warren D. Craft (wdc)
 * @author Liam Brady (lb)
 */
public class CommunicationService {

    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    // ****************************** //
    //   Constructor(s)               //
    // ****************************** //

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

    // ****************************** //
    //   Utility Fxns                 //
    // ****************************** //

    /**
     * Sends a message through this communication service's output stream,
     * then waits for a response. The response message is then returned.
     * @param message message to be sent through output stream
     * @return reply message
     * @throws IOException
     */
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
