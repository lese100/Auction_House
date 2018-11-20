package StringGenerator;

import User.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {

    protected static Dictionary dictionary = new Dictionary();

    public static void main(String[] args) throws IOException {
        int portNumber = 1234;
        ServerSocket serverSocket = new ServerSocket(portNumber);

        while(true){
            Socket clientSocket = serverSocket.accept();
            StringGenerator sg = new StringGenerator(clientSocket);
            Thread thread = new Thread(sg);
            thread.start();
        }

    }

    public static class StringGenerator implements Runnable{

        private Socket clientSocket;
        private ObjectOutputStream out;
        private ObjectInputStream in;

        public StringGenerator(Socket clientSocket) throws IOException{
            this.clientSocket = clientSocket;

            System.out.println("Connected to client: " + clientSocket.getInetAddress().getHostName());

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
                        case GETSTRING:
                            msg = new Message(Message.Content.NEWSTRING,
                                    dictionary.getRandomWord());
                            break;
                        case SWITCHDICTIONARY:
                            msg = new Message(Message.Content.SDCOMPLETE,
                                    null);
                            dictionary.switchDictionary();
                            break;
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

    }
}
