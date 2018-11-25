package User;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.util.Scanner;

public class Controller extends Application {

    private Display display;

    @Override
    public void start(Stage primaryStage) throws Exception{



        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter the hostname of the StringGen server" +
                " you would like to connect to.");
        String hostName = scanner.nextLine();
        System.out.println("Thanks! You are being connect to host: " + hostName);


        CommunicationService cs = new CommunicationService(hostName, 1234);
        StringGenProxy sgp = new StringGenProxy(cs);


        System.out.println("Please enter the Status port you would like to connect" +
                " to.");
        int portNumber = Integer.parseInt(scanner.nextLine());
        System.out.println("Thank you! You are being connected to Port Number:"
                + portNumber);

        System.out.println("Please enter the hostname of the Status server" +
                " you would like to connect to.");
        String hostName2 = scanner.nextLine();
        System.out.println("Thanks! You are being connect to host: " + hostName2);

        CommunicationService statusCS = new CommunicationService(hostName2, portNumber);

        System.out.println("What port would you like to use for your notification server?");
        String notificationPortNumber = scanner.nextLine();

        System.out.println("What is the host name of your current PC?");
        String hostName3 = scanner.nextLine();


        StatusProxy statusProxy = new StatusProxy(statusCS);

        NotificationServer nServer = new NotificationServer(Integer.parseInt(notificationPortNumber), this);

        Thread thread = new Thread(nServer);
        thread.start();

        Message message = new Message<>(Message.Content.NOTIFICATIONSERVERSETUP,
                notificationPortNumber + " " +  hostName3);

        statusCS.sendMessage(message);

        TheString theString = new TheString();

        display = new Display(primaryStage);

        Button getString = new Button("Get String");
        getString.setOnAction(event -> {
            theString.setTheString(sgp.requestString());
            display.setStringLabel(theString.getTheString());
        });

        Button switchDictionary = new Button("Switch Dictionary");
        switchDictionary.setOnAction(event ->{
            display.setSwitchDictCountLabel(statusProxy.changeDictionary());
        });

        display.setupButtons(getString, switchDictionary);

    }

    public void notifyUser(){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                display.notifyUser();
            }
        });
    }


    public static void main(String[] args) {
        launch(args);
    }


}
