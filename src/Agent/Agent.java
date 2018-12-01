package Agent;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import Utility.*;

import java.io.IOException;

public class Agent extends Application {
    public Agent(){
        AgentProtocol protocol = new AgentProtocol(this);
        try {
            NotificationServer notificationserver = new NotificationServer(8765, protocol);
            Thread t = new Thread(notificationserver);
            t.start();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    public Agent(int port){
        AgentProtocol protocol = new AgentProtocol(this);
        try {
            NotificationServer notificationserver = new NotificationServer(port, protocol);
            Thread t = new Thread(notificationserver);
            t.start();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    @Override
    public void start(Stage stage) {
        int holdPort,holdMyPort;
        String holdHost;
        Stage inputs = new Stage();
        inputs.setTitle("Establish Connection to Bank");

        GridPane grid = new GridPane();
        Label port = new Label("Port Number:");
        Label host = new Label("Host Name:");
        Label myPort = new Label("My Port Number:");
        final TextField myPortNum = new TextField("8765");
        final TextField portNum = new TextField();
        final TextField hostName = new TextField();
        Button submit = new Button("Submit");

        grid.add(myPort,0,0);
        grid.add(myPortNum,0,1);
        grid.add(host,1,0);
        grid.add(port,2,0);
        grid.add(hostName,1,1);
        grid.add(portNum,2,1);
        grid.add(submit,3,1);

        Scene scene = new Scene(grid, 100, 40);
        submit.setOnAction(event -> {
            holdMyPort = Integer.parseInt(myPortNum.getText());
            holdHost = hostName.getText();
            holdPort = Integer.parseInt(portNum.getText());
            inputs.close();
        });

        Agent agent = new Agent(holdMyPort);
        BankProxy bank = new BankProxy(holdHost,holdPort);
    }

}
