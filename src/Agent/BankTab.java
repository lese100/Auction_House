package Agent;

import Utility.AuctionItem;
import Utility.BankAccount;
import Utility.IDRecord;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
/**
 * Used to create a "bank" Tab that will be displayed by the display. This would allow for future implementation of
 * multiple banks. The object also handles events pertaining to its lists and has getter and setters for these lists.
 * created: 11/30/18 by lb
 * last modified: 12/07/18 by lb
 * @author Liam Brady (lb)
 * @author Warren D. Craft (wdc)
 * @author Tyler Fenske (thf)
 */
public class BankTab {
    private Button getAuctions,join;
    private Tab bankTab;
    private ListView<String> list,purchases,pending;
    private ObservableList<String> aucDisp,purchasedDisp,pendingDisp;
    private BorderPane pane;
    private ArrayList<IDRecord> aucHouses;
    private IDRecord selectedItem;
    private List<AuctionItem> purchased, pendingTransfer;
    private int selectedToTransfer;
    private Label name,accountNum,totalBal,frozenBal;
    private DecimalFormat df;

    /**
     * Creates a new bank tab format, places the provides buttons. Does all the formatting for the tab and stores it
     * inside bankTab. Adds a event handlers for selecting things from the listView.
     * @param leave the leave button for the bank
     * @param getAuctions button used to request a list of auction houses from the bank
     * @param getBalance button used to request the users balance from the bank
     * @param transfer button used to send a message to the bank to transfer funds
     * @param join button used to join auction houses from the list of auction houses
     * @param deposit initial deposit of the user to the bank.
     */
    public BankTab(Button leave,Button getAuctions, Button getBalance,Button transfer,Button join,double deposit){
        this.getAuctions = getAuctions;
        this.join = join;
        aucHouses = null;
        selectedItem = null;
        df = new DecimalFormat("####0.00");

        /*Creates the initial labels, buttons, and ListViews on the display*/
        Label pendingTransactions = new Label("Pending Transactions:");
        Label purchaseHistory = new Label("Purchase History:");
        pendingTransfer = new ArrayList<>();
        purchased = new ArrayList<>();
        purchases = new ListView<>();
        pending = new ListView<>();
        purchasedDisp = FXCollections.observableArrayList();
        pendingDisp = FXCollections.observableArrayList();
        purchases.setOrientation(Orientation.VERTICAL);
        pending.setOrientation(Orientation.VERTICAL);
        purchases.setPrefSize(250,200);
        pending.setPrefSize(200,150);
        pending.setItems(pendingDisp);
        purchases.setItems(purchasedDisp);

        /*formatting for the Pending Transfers , the button to transfer funds, and the Purchase history list*/
        VBox transactions = new VBox();
        VBox transferHold = new VBox();
        transferHold.getChildren().addAll(pendingTransactions,pending,transfer);
        transactions.getChildren().addAll(transferHold,purchaseHistory,purchases);
        transactions.setSpacing(10);
        purchasedDisp.add("Deposit" + getSpacing("Deposit") + "+$" + df.format(deposit));

        /*Creates a new Tab object that will be displayed by the display*/
        bankTab = new Tab();
        bankTab.setText("Bank");
        bankTab.setClosable(false);
        bankTab.setId("Bank");

        /*formats the buttons and listView for the list of Auction houses*/
        VBox auctionInfo = new VBox();
        list = new ListView<>();
        list.setPrefSize(100,500);
        list.setOrientation(Orientation.VERTICAL);
        aucDisp = FXCollections.observableArrayList();
        list.setItems(aucDisp);
        auctionInfo.getChildren().addAll(list,this.getAuctions,this.join);

        /*the leave button formatting*/
        HBox leaveHold = new HBox();
        Label leaveSpacing = new Label("                                                                       ");
        leaveHold.getChildren().addAll(leaveSpacing,leave);

        /*sets the style and default text for the labels of the user account information*/
        name = new Label("  name");
        name.setStyle("-fx-font: 24 arial;");
        frozenBal = new Label(" (000)");
        frozenBal.setTextFill(Color.GRAY);
        totalBal = new Label("    0000");
        accountNum = new Label("    Account");

        /*formats the labels and buttons used for displaying user account information*/
        VBox userInfo = new VBox();
        VBox balance = new VBox();
        HBox balances = new HBox();
        balances.getChildren().addAll(totalBal,frozenBal);
        balance.getChildren().addAll(balances,getBalance);
        userInfo.getChildren().addAll(name,accountNum,balance);

        /*places everything accordingly on a boarder pane and adds it to the Tab*/
        VBox centerHold = new VBox();
        centerHold.getChildren().addAll(userInfo,transactions);
        centerHold.setSpacing(50);
        pane = new BorderPane();
        pane.setLeft(auctionInfo);
        pane.setPadding(new Insets(10,10,10,10));
        pane.setCenter(centerHold);
        pane.setBottom(leaveHold);
        bankTab.setContent(pane);

        /*Event handler for the pendingTransfer list. Sets the current Selected Item*/
        pending.getSelectionModel().selectedIndexProperty().addListener(
                new ChangeListener<Number>() {
                    @Override
                    public void changed(ObservableValue<? extends Number> observable,
                                        Number oldValue, Number newValue) {
                        int selected = (int)newValue;
                        if(selected >= 0 && selected <pendingTransfer.size()){
                            selectedToTransfer = selected;
                        }
                    }
                });

        /*Event Handler for the list of Auction Houses. Sets the current selected item.*/
        list.getSelectionModel().selectedIndexProperty().addListener(
                new ChangeListener<Number>() {
                    @Override
                    public void changed(ObservableValue<? extends Number> observable,
                                        Number oldValue, Number newValue) {
                        int selected = (int)newValue;
                        if(selected < aucHouses.size() && selected >= 0) {
                            selectedItem = aucHouses.get((int) newValue);
                        }
                    }
                });

    }

    /**
     * gets the tab so that display can add it to its list of tabs
     * @return the tab
     */
    public Tab getBankTab() {
        return bankTab;
    }
    private void displayAucHouses(){
        for(IDRecord house : aucHouses){
            aucDisp.add(house.getName());
        }
    }

    /**
     * sets the list of auction houses in the display
     * @param houses list of auction houses and their IDRecords
     */
    public void setAucHouses(ArrayList<IDRecord> houses){
        aucHouses = houses;
        aucDisp.clear();
        displayAucHouses();
    }

    /**
     * gets the IDRecord of the currently selected auction house in the List of auction houses
     * @return auction house IDRecord
     */
    public IDRecord getSelectedItem(){
        aucDisp.clear();
        return selectedItem;
    }

    /**
     * creates spacing to help formatting the transfer and purchase history listView.
     * @param item the item name that will be displayed
     * @return String with the amount of spaces needed for that item.
     */
    private String getSpacing(String item){
        String spacing = "";
        for(int i = item.length(); i < 55; i++){
            spacing+= " ";
        }
        return spacing;
    }

    /**
     * Updates the Pending list of transfers with the current ArrayList Storing them.
     */
    private void updatePending(){
        pendingDisp.clear();
        for(AuctionItem item : pendingTransfer){
            String info = item.getItemName() + "_" + item.getItemID();
            info += getSpacing(info) + "-$" + df.format(item.getBid().getCurrentBid());
            pendingDisp.add(info);
        }
    }

    /**
     * Gets the selected item from the Transfer list.
     * @return the AuctionItem that was selected
     */
    public AuctionItem getSelectedToTransfer(){
        if(pendingTransfer.isEmpty()){
            return null;
        }
        AuctionItem hold = pendingTransfer.get(selectedToTransfer);
        pendingTransfer.remove(selectedToTransfer);
        purchased.add(hold);
        updatePending();
        String info = hold.getItemName() + "_" + hold.getItemID();
        info += getSpacing(info) + "-$" + df.format(hold.getBid().getCurrentBid());
        purchasedDisp.add(info);
        return hold;
    }

    /**
     * adds a item to the list of items that need funds transferred and updates the display.
     * @param item the AuctionItem being added
     */
    public void addTransferItem(AuctionItem item){
        pendingTransfer.add(item);
        updatePending();
    }

    /**
     * Updates the labels that hold the users balance, username, unfrozen funds, and account number. Called mainly
     * whenever someone requests getBalance from the bank.
     * @param account BankAccount object that holds my balance, unfrozen funds, name, and account number.
     */
    public void updateLabels(BankAccount account){
        name.setText("  "+account.getUserName());
        accountNum.setText("    Account#: " + Integer.toString(account.getAccountNumber()));
        frozenBal.setText(" (" + df.format(account.getTotalUnfrozen()) + ")");
        totalBal.setText("    Balance: " + df.format(account.getTotalBalance()));
    }
}
