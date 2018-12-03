package Agent;

import Utility.AuctionItem;
import Utility.BankAccount;
import Utility.IDRecord;
import javafx.beans.binding.ListBinding;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

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
    private Label name,accuountNum,totalBal,frozenBal;

    /**
     * sets up the bank display
     */
    public BankTab(Button leave,Button getAuctions, Button getBalance,Button transfer,Button join){
        this.getAuctions = getAuctions;
        this.join = join;
        aucHouses = null;
        selectedItem = null;

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
        VBox transactions = new VBox();
        VBox transferHold = new VBox();
        transferHold.getChildren().addAll(pendingTransactions,pending,transfer);
        transactions.getChildren().addAll(transferHold,purchaseHistory,purchases);
        transactions.setSpacing(10);

        pending.getSelectionModel().selectedIndexProperty().addListener(
                new ChangeListener<Number>() {
                    @Override
                    public void changed(ObservableValue<? extends Number> observable,
                                        Number oldValue, Number newValue) {
                        selectedToTransfer = (int)newValue;
                    }
                });


        bankTab = new Tab();
        bankTab.setText("Bank");
        bankTab.setClosable(false);
        bankTab.setId("Bank");

        VBox auctionInfo = new VBox();
        list = new ListView<>();
        list.setPrefSize(100,500);
        list.setOrientation(Orientation.VERTICAL);
        aucDisp = FXCollections.observableArrayList();
        list.setItems(aucDisp);
        auctionInfo.getChildren().addAll(list,this.getAuctions,this.join);

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

        HBox leaveHold = new HBox();
        Label leaveSpacing = new Label("                                                                       " +
                "                                       ");
        leaveHold.getChildren().addAll(leaveSpacing,leave);

        name = new Label("  name");
        name.setStyle("-fx-font: 24 arial;");
        frozenBal = new Label(" (000)");
        frozenBal.setTextFill(Color.GRAY);
        totalBal = new Label("    0000");
        accuountNum = new Label("    Account");

        VBox userInfo = new VBox();
        HBox balance = new HBox();
        balance.getChildren().addAll(totalBal,frozenBal,getBalance);
        userInfo.getChildren().addAll(name,accuountNum,balance);

        VBox centerHold = new VBox();
        centerHold.getChildren().addAll(userInfo,transactions);
        centerHold.setSpacing(50);
        pane = new BorderPane();
        pane.setLeft(auctionInfo);
        pane.setPadding(new Insets(10,10,10,10));
        pane.setCenter(centerHold);
        pane.setBottom(leaveHold);
        bankTab.setContent(pane);
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
    public void setAucHouses(ArrayList<IDRecord> houses){
        aucHouses = houses;
        aucDisp.clear();
        displayAucHouses();
    }
    public IDRecord getSelectedItem(){
        aucDisp.clear();
        return selectedItem;
    }
    private void updatePurchased(){
        purchasedDisp.clear();
        for(AuctionItem item : purchased){
            String info = item.getItemName() + "_" + item.getItemID()+"                                      -"+
                    item.getBid();
            purchasedDisp.add(info);
        }
    }
    private void updatePending(){
        pendingDisp.clear();
        for(AuctionItem item : pendingTransfer){
            String info = item.getItemName() + "_" + item.getItemID()+"                                      -"+
                    item.getBid();
            pendingDisp.add(info);
        }
    }
    public AuctionItem getSelectedToTransfer(){
        if(pendingTransfer.isEmpty()){
            return null;
        }
        AuctionItem hold = pendingTransfer.get(selectedToTransfer);
        pendingTransfer.remove(selectedToTransfer);
        purchased.add(hold);
        updatePending();
        updatePurchased();
        return hold;
    }
    public void addTransferItem(AuctionItem item){
        pendingTransfer.add(item);
        updatePending();
    }
    public void updateLabels(BankAccount account){
        name.setText("  "+account.getUserName());
        accuountNum.setText("    Account#: " + Integer.toString(account.getAccountNumber()));
        frozenBal.setText(" (" + Double.toString(account.getTotalUnfrozen()) + ")");
        totalBal.setText("    Balance: " + Double.toString(account.getTotalBalance()));
    }
}
