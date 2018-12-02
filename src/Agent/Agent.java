package Agent;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import Utility.*;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;

public class Agent extends Application {
    private int holdPort,holdMyPort;
    private String holdHost, localHost;
    private IDRecord myRecords;
    private Display display;
    private HashMap<AuctionHouseProxy,IDRecord> auctionHouses;
    private Button bid,leaveAuc,leaveBank,getAuction,getBalance,transfer,join;

    /**
     * initial constructor
     */
    public Agent(){
    }

    /**
     * Sets up the user port and makes its protocol and establishes its
     * notification server to receive update messages.
     * @param port the port im going to run my client on
     */
    public Agent(int port){
        AgentProtocol protocol = new AgentProtocol(this);
        auctionHouses = new HashMap<AuctionHouseProxy, IDRecord>();
        try {
            NotificationServer notificationserver = new NotificationServer(port, protocol);
            Thread t = new Thread(notificationserver);
            t.start();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * closes everything when exiting the main menu
     */
    @Override
    public void stop(){
        System.exit(1);
    }
    @Override
    public void start(Stage stage) {
        Stage inputs = new Stage();
        inputs.setTitle("Connect");

        /*set up host information*/
        GridPane grid = new GridPane();
        Label port = new Label("Port Number:");
        Label host = new Label("Host Name:");
        Label myPort = new Label("My Port Number:");
        final TextField myPortNum = new TextField("8765");
        final TextField portNum = new TextField("1234");
        final TextField hostName = new TextField("localhost");
        Button submit = new Button("Submit");

        grid.add(myPort,0,0);
        grid.add(myPortNum,1,0);
        grid.add(host,0,1);
        grid.add(port,0,2);
        grid.add(hostName,1,1);
        grid.add(portNum,1,2);
        grid.add(submit,1,3);

        Scene scene = new Scene(grid, 240, 100);
        submit.setOnAction(event -> {
            inputs.close();
        });
        inputs.setResizable(false);
        inputs.setScene(scene);
        inputs.initOwner(stage);
        inputs.showAndWait();
        holdMyPort = Integer.parseInt(myPortNum.getText());
        holdHost = hostName.getText();
        holdPort = Integer.parseInt(portNum.getText());


        Agent agent = new Agent(holdMyPort);
        localHost = null;
        try {
            localHost = InetAddress.getLocalHost().getHostName();
        }catch(UnknownHostException e){
            e.printStackTrace();
        }
        Stage create = new Stage();
        create.setTitle("Connect");


        /*establish user info*/
        GridPane grid2 = new GridPane();
        Label user = new Label("UserName:");
        Label deposit = new Label("Initial Deposit:");
        final TextField name = new TextField("My Name");
        final TextField balance = new TextField("2500");
        Button account = new Button("Create Account");

        grid2.add(user,0,0);
        grid2.add(name,1,0);
        grid2.add(deposit,0,1);
        grid2.add(balance,1,1);
        grid2.add(account,1,2);

        Scene scene2 = new Scene(grid2, 240, 75);
        account.setOnAction(event -> create.close());
        create.setResizable(false);
        create.setScene(scene2);
        create.initOwner(stage);
        create.showAndWait();

        /*connect to the bank*/
        myRecords = new IDRecord(IDRecord.RecordType.AGENT,name.getText(),Integer.parseInt(balance.getText()),
                localHost,holdMyPort);
        //BankProxy bank = new BankProxy(holdHost,holdPort);
        //myRecords = bank.createBankAccount(myRecords);
        bid = new Button("Bid");
        leaveAuc = new Button("Leave");
        leaveBank = new Button("Leave");
        getAuction = new Button("Ask for Auctions");
        getBalance = new Button("Get Balance");
        transfer = new Button("Transfer Funds");
        join = new Button("Join");
        join.setDisable(true);
        display = new Display(stage,myRecords,bid,leaveAuc,leaveBank,getAuction,getBalance,transfer,join);

        /*event handlers*/
        bid.setOnAction(event -> {

        });
        leaveAuc.setOnAction(event -> {

        });
        leaveBank.setOnAction(event -> {

        });
        getAuction.setOnAction(event -> {
            join.setDisable(false);
        });
        getBalance.setOnAction(event -> {

        });
        transfer.setOnAction(event -> {
        });
        join.setOnAction(event -> {

        });
    }
}
