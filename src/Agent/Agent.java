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
/**
 * The Agent class initialized the heads of most objects, bank proxy, display, auctionHouseProxy, NotificationServer,
 * and the AgentProtocol. The agent is the routing system for any messages received by the notification server. The
 * Agent also handles getting all inputs required for launching the rest of the program. The Agent handles the displays
 * button eventHandlers and sends out messages accordingly.
 * created: 11/30/18 by lb
 * last modified: 12/07/18 by lb
 * @author Liam Brady (lb)
 * @author Warren D. Craft (wdc)
 * @author Tyler Fenske (thf)
 */
public class Agent extends Application {
    private int holdPort, holdMyPort;
    private String holdHost, localHost;
    private IDRecord myRecords;
    private Display display;
    private HashMap<Integer, AuctionHouseLink> auctionHouses;
    private Button bid, leaveAuc, leaveBank, getAuction, getBalance, transfer, join;
    private BankProxy bankProxy;
    private ArrayList<AuctionItem> waiting;
    private boolean bankFlag;

    /**
     * initial constructor
     */
    public Agent() {
    }

    /**
     * If you have the winning bid on a auction item a notification is received and this method is called with the
     * item you have won. This item is passed to the display so the user knows they won the item and to transfer the
     * funds.
     * @param item Item won at a auction house
     */
    public void addTransferItem(AuctionItem item) {
        display.addTransferItem(item);
        BankAccount info = bankProxy.requestBalance(myRecords);
        waiting.add(item);
        if(info != null) {
            display.updateLabels(info);
        }else{
            System.out.println("can't get balance");
        }
    }

    /**
     * Notification that there has been a item inventory change in one the displays. Tells the display to update
     * the auction item list.
     * @param newInventory new auction item list
     */
    public void itemsUpdate(AuctionHouseInventory newInventory) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                display.updateAuctionItems(newInventory);
            }
        });
    }

    /**
     * displays a notification for when someone has outbid an item you have bid on.
     * @param item item the user was outbid on.
     */
    public void displayOutbid(AuctionItem item){
        display.displayNotification("Outbid: " + item.getItemName() + "\nNew Price: $" +
                item.getBid().getCurrentBid());
    }
    /**
     * closes everything when exiting the main menu
     */
    @Override
    public void stop() {
        System.exit(1);
    }

    /**
     * Handles setting up the display, input arguments and event handlers. Details explanation written above important
     * sections
     * @param stage starting stage for the display
     */
    @Override
    public void start(Stage stage) {
        bankFlag = false;
        Stage inputs = new Stage();
        inputs.setTitle("Connect");
        auctionHouses = new HashMap<>();
        waiting = new ArrayList<>();

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

        /*places Text fields and labels accordingly on the display*/
        grid.add(myPort, 0, 0);
        grid.add(myPortNum, 1, 0);
        grid.add(host, 0, 2);
        grid.add(port, 0, 3);
        grid.add(hostName, 1, 2);
        grid.add(portNum, 1, 3);
        grid.add(myHost, 0, 1);
        grid.add(myHostName, 1, 1);
        grid.add(submit, 1, 4);
        /*event handler for the submit button it closes the window*/
        Scene scene = new Scene(grid, 275, 150);
        submit.setOnAction(event -> {
            inputs.close();
        });
        /*displays the input display and makes the program wait for it to be submitted/closed*/
        inputs.setResizable(false);
        inputs.setScene(scene);
        inputs.initOwner(stage);
        inputs.showAndWait();
        /*sets the input arguments*/
        holdMyPort = Integer.parseInt(myPortNum.getText());
        holdHost = hostName.getText();
        holdPort = Integer.parseInt(portNum.getText());
        localHost = myHostName.getText();

        /*creates the agent protocol and Notification server, then sets up the link to this object*/
        AgentProtocol protocol = new AgentProtocol(this);
        auctionHouses = new HashMap<>();
        try {
            NotificationServer notificationserver = new NotificationServer(holdMyPort, protocol);
            Thread t = new Thread(notificationserver);
            t.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        /*creates a window for user info*/
        Stage create = new Stage();
        create.setTitle("Connect");


        /*establish user info*/
        GridPane grid2 = new GridPane();
        Label user = new Label("UserName:");
        Label deposit = new Label("Initial Deposit:");
        final TextField name = new TextField("My Name");
        final TextField balance = new TextField("2500");
        Button account = new Button("Create Account");

        /*places the Text Fields and Labels onto the display*/
        grid2.add(user, 0, 0);
        grid2.add(name, 1, 0);
        grid2.add(deposit, 0, 1);
        grid2.add(balance, 1, 1);
        grid2.add(account, 1, 2);

        /*sets up the event handler for submitting and has the program wait for the user to submit*/
        Scene scene2 = new Scene(grid2, 275, 100);
        account.setOnAction(event -> create.close());
        create.setResizable(false);
        create.setScene(scene2);
        create.initOwner(stage);
        create.showAndWait();

        /*connect to the bank by setting up its bank proxy*/
        myRecords = new IDRecord(IDRecord.RecordType.AGENT, name.getText(), Integer.parseInt(balance.getText()),
                localHost, holdMyPort);
        bankProxy = new BankProxy(holdHost, holdPort);
        myRecords = bankProxy.createBankAccount(myRecords);
        BankAccount me = bankProxy.requestBalance(myRecords);

        /*creates the buttons the display will need*/
        bid = new Button("Bid");
        leaveAuc = new Button("Leave");
        leaveBank = new Button("Leave");
        getAuction = new Button("Ask for Auctions");
        getBalance = new Button("Get Balance");
        transfer = new Button("Transfer Funds");
        join = new Button("Join");
        join.setDisable(true);
        /*creates the display object giving it the default bank information it needs to display*/
        display = new Display(stage, myRecords, bid, leaveAuc, leaveBank, getAuction, getBalance, transfer, join,
                myRecords.getInitialBalance());
        if(me != null) {
            display.updateLabels(me);
        }else{
            bankFlag = true;
            display.displayNotification("can't get balance: Failed to connect to the Bank");
        }

        /*event handlers for the displays buttons*/

        /*
        * the same bid button is used for all auction tabs. When you click bid the display is asked for the current
        * item being bid on. This is then used to grab the Auction Houses proxy using a hashmap of auction house ids
        * and Auction House Links. This contains the Auction houses IDRecord, secretKey associated with the auction
        * house and the auction house proxy. Sends the Auction Item that was bid on to the auction house and waits for
        * a reply. Displays a pop up notification if anything goes wrong.
        */
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
                display.displayNotification("Can't Bid");
            }
        });
        /*
        * The leaveBank button and the exit button are made to have the same functionality. It makes sure there is a
        * connection to the bank, if there is a connection is requests the close its account, reacts accordingly. Allows
        * you to close if no bank connection is present.
        */
        stage.setOnCloseRequest(event -> {
            /*flag is added so you can close the tab is you cannot connect to the bank*/
            if(!bankFlag) {
                event.consume();
                if (auctionHouses.isEmpty() && bankProxy.closeRequest(myRecords)) {
                    stop();
                } else {
                    display.displayNotification("Please transfer all funds and leave all auctions");
                }
            }
        });
        leaveBank.setOnAction(event -> {
            if(!bankFlag) {
                if (auctionHouses.isEmpty() && bankProxy.closeRequest(myRecords)) {
                    stop();
                } else {
                    display.displayNotification("Please transfer all funds and leave all auctions");
                }
            }else{
                stop();
            }
        });
        /*
        * Event handler used to leave a auction house, all auction tabs use the same leave button, requests for
        * the auction house id for the current tab. Uses this to get the Auction House Link which contains the auction
        * houses information. Checks to see if there are any pending transfers that need to be sent to the Auction
        * house. If any are found it displays a notification saying there are pending transfers. If no pending transfers
        * are found it requests to leave sends a message to the auctionHouse requesting to leave. If the auction house
        * denies a request a message is displayed saying there are active bids. Otherwise, the agent removes the auction
        * house from its list of Auction House links and tells the display to remove the current tab.
        */
        leaveAuc.setOnAction(event -> {
            AuctionHouseLink link = auctionHouses.get(display.getCurrentTab());
            boolean foundPending = false;
            for(AuctionItem houseId : waiting){
                if(houseId.getHouseID() == link.getId().getNumericalID()){
                    foundPending = true;
                    break;
                }
            }
            if(!foundPending) {
                if (link.getProxy().closeRequest(myRecords, link.getSecretKey())) {
                    auctionHouses.remove(link.getId().getNumericalID());
                    display.removeCurrentTab();
                } else {
                    display.displayNotification("Cannot leave: Active Bids");
                }
            }else{
                display.displayNotification("Cannot leave: Pending Transfers");
            }
        });
        /*
        * This button is used to request a list of auction houses from the bank. If the request is null or there are
        * no open auction houses a notification is displayed. Otherwise tells the bank display to show the list.
        */
        getAuction.setOnAction(event -> {
            ArrayList<IDRecord> auctions = bankProxy.getListOfAuctionHouses();
            if(auctions != null) {
                if(!auctions.isEmpty()) {
                    join.setDisable(false);
                    display.displayAuctionHouses(auctions);
                }else{
                    display.displayNotification("No Open Auction Houses");
                }
            }else{
                display.displayNotification("can't get Auction Houses");
            }
        });
        /*
        * Used the request the my account information the bank using my IDRecord. This is used to
        * set the balance and frozen fund information. Updates the display with the received info.
        */
        getBalance.setOnAction(event -> {
            BankAccount info = bankProxy.requestBalance(myRecords);
            if(info != null) {
                display.updateLabels(info);
            }else{
                display.displayNotification("can't get balance");
            }
        });
        /*
        * Used to transfer the funds for a purchased item. Gets the item selected for transfer and sends that
        * AuctionItem object to the bank using the BankProxy. removes the item from the list of items needing to be
        * transferred. Requests my finances after the transfer and updates the display.
        */
        transfer.setOnAction(event -> {
            AuctionItem item = display.getSelectedTransfer();
            if (item != null) {
                bankProxy.transferFunds(item);
                waiting.remove(item);
                BankAccount info = bankProxy.requestBalance(myRecords);
                auctionHouses.get(item.getHouseID()).getProxy().transferedFunds(item);
                if(info != null) {
                    display.updateLabels(info);
                }else{
                    display.displayNotification("can't get balance");
                }
            }else{
                display.displayNotification("can't Transfer Funds");
            }
        });
        /*
        * Used to join a auctionHouse from the list provided by the RequestAuctions button. Gets the current selected
        * item in the auction house list from the display. clears the list and makes it so you can't click the button
        * again till the list of auction houses is requested again. Makes sure you aren't already connected to that
        * auction house. Sets up the auction houses Proxy and asks for a secret key for the auction house from the bank.
        * creates a new tab for the auction house in the display and sends a join AH message to the auction house.
        * Stores all the information in a auctionHouseLink and adds it to a hashmap.
        */
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
                display.displayNotification("can't join auction house");
            }
        });
    }
}
