package Testing;

import java.io.*;
import java.net.Socket;

/**
 * Client class for having a KnockKnockServer class tell Knock! Knock jokes.
 * Code originally provided by Brooke Chenoweth (CS 351), which she
 * borrowed and modifed from an Oracle tutorial on sockets
 * (see https://docs.oracle.com/javase/tutorial/networking/sockets/
 * clientServer.html).
 * Originally built to communicate via strings; later modified by thf
 * to test communication using more general serializable object(s).
 * created: 11/17/18 by thf
 * last modified: 11/18/18 by wdc (updating comments)
 * previously modified: 11/17/18 by thf (creation)
 * @author Tyler Fenske (thf)
 * @author Warren D. Craft (wdc)
 * @author Liam Brady (lb)
 *
 */
public class KnockKnockClient {
    public  static  void  main(String [] args) throws IOException {

        // String  hostName = args [0];
        // int  portNumber = Integer.parseInt(args [1]);
        String hostName = "localhost";
        int portNumber = 1234;
        Socket socket = new Socket(hostName, portNumber);
        ObjectInputStream in =
            new ObjectInputStream(socket.getInputStream());
        ObjectOutputStream out =
            new ObjectOutputStream(socket.getOutputStream());
        TestSerial ts = null;

        BufferedReader stdIn =
            new BufferedReader(new InputStreamReader(System.in));

        try {
            ts = (TestSerial) in.readObject();

        } catch (ClassNotFoundException cnf) {
            cnf.printStackTrace();
        }

        String fromServer = ts.getStringField();

        while ( fromServer != null ) {
            System.out.println("Server: " + fromServer);
            if ( fromServer.equals("Bye.") ) {
                break;
            }
            String fromUser = stdIn.readLine();
            if ( fromUser != null ) {
                System.out.println("Client: " + fromUser);
                out.writeObject(new TestSerial(fromUser));
                out.flush();
            }
            try {
                ts = (TestSerial) in.readObject();
            } catch( ClassNotFoundException cnf ) {
                cnf.printStackTrace();
            }

            fromServer = ts.getStringField();
        }

        // the commented-out code below was part of the original code
        // which simply passed strings back and forth over the network
//        try (
//            Socket socket = new  Socket(hostName , portNumber );
//            PrintWriter out =
//                new PrintWriter(socket.getOutputStream (), true);
//             BufferedReader in =
//                 new BufferedReader(
//                     new InputStreamReader(socket.getInputStream ())
//                 );
//        )
//        {
//            BufferedReader  stdIn =
//                new  BufferedReader(new  InputStreamReader(System.in));
//            String  fromServer = in.readLine ();
//            while(fromServer  != null) {
//                System.out.println("Server: " + fromServer );
//                if(fromServer.equals("Bye.")) { break; }
//                String  fromUser = stdIn.readLine ();
//                if(fromUser  != null) {
//                    System.out.println("Client: " + fromUser );
//                    out.println(fromUser );
//                }
//                fromServer = in.readLine ();
//            }
//        }
    }

}
