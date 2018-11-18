package Testing;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Server class for telling Knock! Knock! jokes to clients.
 * Code originally provided by Brooke Chenoweth (CS 351), which she
 * borrowed and modifed from an Oracle tutorial on sockets
 * (see https://docs.oracle.com/javase/tutorial/networking/sockets/
 * clientServer.html). Originally built to communicate via strings;
 * later modified by thf to test communication using more general
 * serializable object(s).
 * created: 11/17/18 by thf
 * last modified: 11/18/18 by wdc (updating comments)
 * previously modified: 11/17/18 by thf (creation)
 * @author Tyler Fenske (thf)
 * @author Warren D. Craft (wdc)
 * @author Liam Brady (lb)
 *
 */
public class KnockKnockServer {

    public  static  String  BYE = "Bye.";
    public  enum  State {
        WAITING ,
        SENT_KNOCK_KNOCK ,
        SENT_CLUE ,
        ANOTHER
    }

    public  static  String []  clues =
        { "Turnip",
            "Little Old Lady",
            "Atch", "Who", "Who" };

    public  static  String []  answers =
        { "Turnip the heat, it's cold in here!",
            "I didn't know you could yodel!",
            "Bless you!",
            "Is there an owl in here?",
            "Is there an echo in here?" };

    public  static  void  main(String [] args) throws IOException {
        // int  portNumber = Integer.parseInt(args [0]);
        int portNumber = 1234;
        ServerSocket serverSocket = new  ServerSocket(portNumber );

        //  Listen  for  new  clients  forever
        while(true) {
            //  Create  new  thread  to  handle  each  client
            Socket clientSocket = serverSocket.accept();
            KnockKnock kk = new  KnockKnock(clientSocket);
            Thread t = new Thread(kk);
            t.start();
        }
    }

    public static class KnockKnock implements Runnable {
        private final Socket clientSocket;
        private ObjectOutputStream out;
        private ObjectInputStream in;
        // private PrintWriter out;    // leftover from original
        // private BufferedReader in;  // leftover from original
        private KnockKnockServer.State state = KnockKnockServer.State.WAITING;
        private int currentJoke = 0;

        public KnockKnock(Socket clientSocket) throws IOException {
            this.clientSocket = clientSocket;

            // the commented-out code over the next few lines was part of
            // the original code, which passed strings (instead of objects)
            // over the network
//            out =
//                new PrintWriter(clientSocket.getOutputStream(),
//                    true);
//            in = new BufferedReader(new InputStreamReader(
//                clientSocket.getInputStream()));
            out = new ObjectOutputStream(clientSocket.getOutputStream());
            out.flush();
            in = new ObjectInputStream(clientSocket.getInputStream());
        }

        public void run() {
            String inputLine = null;
            String outputLine;
            do {
                outputLine = processInput(inputLine);

                try {
                    out.writeObject(new TestSerial( outputLine ));

                } catch( IOException e ) {
                    e.printStackTrace();
                }

                // out.println(outputLine);

                if (outputLine.equals(KnockKnockServer.BYE)) {
                    break;
                }

                try {
                    TestSerial ts = (TestSerial) in.readObject();
                    // inputLine = in.readLine();
                    inputLine = ts.getStringField();
                    System.out.println(inputLine);

                } catch (IOException ex) {
                    inputLine = null;
                } catch (ClassNotFoundException cnf) {
                    cnf.printStackTrace();
                }

            } while (inputLine != null);
        }

        private String processInput(String input) {
            String output = null;
            switch (state) {
                case WAITING:
                    output = "Knock! Knock!";
                    state = KnockKnockServer.State.SENT_KNOCK_KNOCK;
                    break;
                case SENT_KNOCK_KNOCK:
                    if (input.equalsIgnoreCase("Who's there?")) {
                        output = KnockKnockServer.clues[currentJoke];
                        state = KnockKnockServer.State.SENT_CLUE;
                    } else {
                        output =
                            "You're supposed to say \"Who's there?\"! " +
                                "Try again. Knock! Knock!";
                    }
                    break;
                case SENT_CLUE:
                    if (input.equalsIgnoreCase(KnockKnockServer.clues[currentJoke] + " who?")) {
                        output = KnockKnockServer.answers[currentJoke] +
                            "\nWant another? (y/n)";
                        state = KnockKnockServer.State.ANOTHER;
                    } else {
                        output = "You're supposed to say \"" +
                            KnockKnockServer.clues[currentJoke] + " who?\"! " +
                            "Try again. Knock! Knock!";
                        state = KnockKnockServer.State.SENT_KNOCK_KNOCK;
                    }
                    break;
                case ANOTHER:
                    if (input.equalsIgnoreCase("y")) {
                        output = "Knock! Knock!";
                        currentJoke = (currentJoke + 1) % KnockKnockServer.clues.length;
                        state = KnockKnockServer.State.SENT_KNOCK_KNOCK;
                    } else {
                        output = KnockKnockServer.BYE;
                        state = KnockKnockServer.State.WAITING;
                    }
                    break;
            }
            return output;
        }
    }
}

