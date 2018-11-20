package User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class NotificationServer implements Runnable {

    private ServerSocket serverSocket;
    private Controller controller;

    public NotificationServer(int portNumber, Controller controller) throws IOException {
        serverSocket = new ServerSocket(portNumber);
        this.controller = controller;
    }

    @Override
    public void run(){
        while(true){
            try{
                Socket clientSocket = serverSocket.accept();
                Notification notification = new Notification(clientSocket);
                Thread thread = new Thread(notification);
                thread.start();
            }catch(IOException e){
                e.printStackTrace();
            }
        }
    }

    public class Notification implements Runnable{

        private Socket clientSocket;
        private ObjectOutputStream out;
        private ObjectInputStream in;

        public Notification(Socket clientSocket) throws IOException{
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
                        case TIMERUPDATE:
                            controller.notifyUser();
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
