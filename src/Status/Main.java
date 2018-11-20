package Status;

import User.CommunicationService;
import User.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    protected static CommunicationService cs;
    protected static CommunicationService notificationCS;

    public static void main (String[] args) throws IOException {

        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter the hostname of the StringGen server" +
                " you would like to connect to.");
        String hostName = scanner.nextLine();
        System.out.println("Thanks! You are being connect to host: " + hostName);
        cs = new CommunicationService(hostName, 1234);


        System.out.println("Please enter the port you would like to run" +
                " this server on.");
        int portNumber = Integer.parseInt(scanner.nextLine());
        System.out.println("Thank you! Port Number:" + portNumber + " has been" +
                " registered.");

        ServerSocket serverSocket = new ServerSocket(portNumber);

        while(true){
            Socket clientSocket = serverSocket.accept();
            StatusController sc = new StatusController(clientSocket);
            Thread thread = new Thread(sc);
            thread.start();


        }
    }

    public static class StatusController implements Runnable{

        private Socket clientSocket;
        private ObjectOutputStream out;
        private ObjectInputStream in;
        private int numTimesDictChanged = 0;

        public StatusController(Socket clientSocket) throws IOException{
            this.clientSocket = clientSocket;
            out = new ObjectOutputStream(clientSocket.getOutputStream());
            out.flush();
            in = new ObjectInputStream(clientSocket.getInputStream());
        }

        @Override
        public void run(){
            Message message = null;
            do{
                try{
                    message = (Message) in.readObject();

                    Message msg = null;

                    switch(message.getContent()){
                        case SWITCHDICTIONARY:
                            numTimesDictChanged++;
                            msg = cs.sendMessage(new Message(Message.Content.SWITCHDICTIONARY, null));
                            List<String> stringList = new ArrayList<>();
                            stringList.add(Integer.toString(numTimesDictChanged));
                            msg.setObj(stringList);

                            Thread thread = new Thread(new ThreadTimer(this));
                            thread.start();
                            break;
                        case NOTIFICATIONSERVERSETUP:
                            int portNum = Integer.parseInt(((String) message.getObj()).substring(0, 4));
                            String host = ((String) message.getObj()).substring(5);

                            notificationCS = new CommunicationService(host,
                                    portNum);
                    }

                    out.writeObject(msg);
                    out.flush();

                }catch(ClassNotFoundException cnf){
                    cnf.printStackTrace();
                }catch(IOException io){
                    message = null;
                }
            }while(message != null);
        }

        public void sendNotification() {
            try{
                notificationCS.sendMessage(new Message(Message.Content.TIMERUPDATE, null));
            }catch(IOException e){
                e.printStackTrace();
            }

        }
    }
}
