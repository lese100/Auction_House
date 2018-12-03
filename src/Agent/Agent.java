package Agent;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import Utility.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Agent extends Application {
    private int holdPort, holdMyPort;
    private String holdHost, localHost;
    private IDRecord myRecords;
    private Display display;
    private HashMap<Integer, AuctionHouseLink> auctionHouses;
    private Button bid, leaveAuc, leaveBank, getAuction, getBalance, transfer, join;
    private BankProxy bankProxy;

    /**
     * initial constructor
     */
    public Agent() {
    }

    /**
     * Sets up the user port and makes its protocol and establishes its
     * notification server to receive update messages.
     *
     * @param port the port im going to run my client on
     */
    public Agent(int port) {
        AgentProtocol protocol = new AgentProtocol(this);
        auctionHouses = new HashMap<>();
        try {
            NotificationServer notificationserver = new NotificationServer(port, protocol);
            Thread t = new Thread(notificationserver);
            t.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addTransferItem(AuctionItem item) {
        display.addTransferItem(item);
        BankAccount info = bankProxy.requestBalance(myRecords);
        if(info != null) {
            display.updateLabels(info);
        }else{
            System.out.println("can't get balance");
        }
    }

    public void itemsUpdate(AuctionHouseInventory newInventory) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                display.updateAuctionItems(newInventory);
            }
        });
    }

    /**
     * closes everything when exiting the main menu
     */
    @Override
    public void stop() {
        System.exit(1);
    }

    @Override
    public void start(Stage stage) {
        Stage inputs = new Stage();
        inputs.setTitle("Connect");
        auctionHouses = new HashMap<>();

        /*set up host information*/
        GridPane grid = new GridPane();
        Label port = new Label("Port Number:");
        Label host = new Label("Host Name:");
        Label myPort = new Label("My Port Number:");
        Label myHost = new Label("My Host Name:");
        final TextField myPortNum = new TextField("8765");
        final TextField portNum = new TextField("1234");
        final TextField hostName = new TextField("localhost");
        final TextField myHostName = new TextField("localhost");
        Button submit = new Button("Submit");

        grid.add(myPort, 0, 0);
        grid.add(myPortNum, 1, 0);
        grid.add(host, 0, 2);
        grid.add(port, 0, 3);
        grid.add(hostName, 1, 2);
        grid.add(portNum, 1, 3);
        grid.add(myHost, 0, 1);
        grid.add(myHostName, 1, 1);
        grid.add(submit, 1, 4);

        Scene scene = new Scene(grid, 240, 120);
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
        localHost = myHostName.getText();

        //Agent agent = new Agent(holdMyPort);
        AgentProtocol protocol = new AgentProtocol(this);
        auctionHouses = new HashMap<>();
        try {
            NotificationServer notificationserver = new NotificationServer(holdMyPort, protocol);
            Thread t = new Thread(notificationserver);
            t.start();
        } catch (IOException e) {
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

        grid2.add(user, 0, 0);
        grid2.add(name, 1, 0);
        grid2.add(deposit, 0, 1);
        grid2.add(balance, 1, 1);
        grid2.add(account, 1, 2);

        Scene scene2 = new Scene(grid2, 240, 75);
        account.setOnAction(event -> create.close());
        create.setResizable(false);
        create.setScene(scene2);
        create.initOwner(stage);
        create.showAndWait();

        /*connect to the bank*/
        myRecords = new IDRecord(IDRecord.RecordType.AGENT, name.getText(), Integer.parseInt(balance.getText()),
                localHost, holdMyPort);

        bankProxy = new BankProxy(holdHost, holdPort);
        myRecords = bankProxy.createBankAccount(myRecords);
        BankAccount me = bankProxy.requestBalance(myRecords);

        bid = new Button("Bid");
        leaveAuc = new Button("Leave");
        leaveBank = new Button("Leave");
        getAuction = new Button("Ask for Auctions");
        getBalance = new Button("Get Balance");
        transfer = new Button("Transfer Funds");
        join = new Button("Join");
        join.setDisable(true);
        display = new Display(stage, myRecords, bid, leaveAuc, leaveBank, getAuction, getBalance, transfer, join);
        if(me != null) {
            display.updateLabels(me);
        }else{
            System.out.println("can't get balance");
        }

        /*event handlers*/
        bid.setOnAction(event -> {
            AuctionItem bid = display.getBid();
            if(bid != null) {
                int id = bid.getHouseID();
                AuctionHouseLink link = auctionHouses.get(id);
                AuctionHouseProxy proxy = link.getProxy();
                int secretKey = link.getSecretKey();
                int response = proxy.makeBid(bid, secretKey);
                if (response == 0) {
                    display.displayNotification("Bid Failed: Bid was to low");
                } else if (response == 1) {
                    display.displayNotification("Bid Failed: Insufficient funds");
                } else if (response == 2) {
                    display.displayNotification("Bid Accepted");
                } else {
                    System.out.println("case not found for bid");
                }
            }else{
                System.out.println("Can't Bid");
            }
        });
        leaveAuc.setOnAction(event -> {

        });
        leaveBank.setOnAction(event -> {

        });
        getAuction.setOnAction(event -> {
            join.setDisable(false);
            ArrayList<IDRecord> auctions = bankProxy.getListOfAuctionHouses();
            if(auctions != null) {
                display.displayAuctionHouses(auctions);
            }else{
                System.out.println("can't get Auction");
            }
        });
        getBalance.setOnAction(event -> {
            BankAccount info = bankProxy.requestBalance(myRecords);
            if(info != null) {
                display.updateLabels(info);
            }else{
                System.out.println("can't get balance");
            }
        });
        transfer.setOnAction(event -> {
            AuctionItem item = display.getSelectedTransfer();
            if (item != null) {
                bankProxy.transferFunds(item);
                BankAccount info = bankProxy.requestBalance(myRecords);
                if(info != null) {
                    display.updateLabels(info);
                }else{
                    System.out.println("can't get balance");
                }
            }else{
                System.out.println("can't Transfer Funds");
            }
        });
        join.setOnAction(event -> {
            IDRecord newAuctionHouse = display.getSelectedAuctionHouse();
            join.setDisable(true);
            if(newAuctionHouse != null) {
                if(!display.doesAuctionExist(newAuctionHouse.getNumericalID())) {
                    AuctionHouseProxy proxy = new AuctionHouseProxy(newAuctionHouse.getHostname(),
                            newAuctionHouse.getPortNumber());
                    AccountLink link = new AccountLink(myRecords.getNumericalID(), newAuctionHouse.getNumericalID());
                    int secretKey = bankProxy.getSecretKey(link);
                    display.addAuctionTab(proxy.joinAH(myRecords, secretKey), newAuctionHouse);
                    AuctionHouseLink linkToAuction = new AuctionHouseLink(newAuctionHouse, secretKey, proxy);
                    auctionHouses.put(newAuctionHouse.getNumericalID(), linkToAuction);
                }
            }else{
                display.addAuctionTab(null,null);
                System.out.println("can't join auction house");
            }
        });
    }
}
