package Agent;

import Utility.*;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Display {
    private HashMap<Integer,AuctionTab> auctions;
    private BankTab bank;
    private Tab currentTab;
    private TabPane tabs;
    private Button bid,leaveAuc,leaveBank,getAuction,getBalance,transfer,join;
    private IDRecord myInfo;
    private Stage stage;
    /**
     * constructs the initial display for the bank.
     * @param stage the main display stage
     */
    public Display(Stage stage,IDRecord myInfo,Button bid,Button leaveAuc,Button leaveBank,Button getAuction,Button getBalance,
                   Button transfer,Button join){
        this.myInfo = myInfo;
        this.bid = bid;
        this.stage = stage;
        this.leaveAuc = leaveAuc;
        this.leaveBank = leaveBank;
        this.getAuction = getAuction;
        this.getBalance = getBalance;
        this.transfer = transfer;
        this.join = join;
        auctions = new HashMap<>();
        stage.setTitle("Agent Interface");
        tabs = new TabPane();
        Scene layout = new Scene(tabs,420,620, Color.WHITE);
        bank = new BankTab(leaveBank,getAuction,getBalance,transfer,join);
        tabs.getTabs().add(bank.getBankTab());
        tabs.getSelectionModel().selectedItemProperty().addListener((ov, oldTab, newTab) -> {
            currentTab = newTab;
        });
        stage.setScene(layout);
        stage.setResizable(false);
        stage.show();
    }

    /**
     * creates a new tab for a auction i have connected to.
     * @param items list of items i need to display in the tab
     * @param auctionHouse auction house info
     */
    public void addAuctionTab(List<AuctionItem> items, IDRecord auctionHouse){
        AuctionTab auction = new AuctionTab(items,auctionHouse,bid,leaveAuc);
        auctions.put(auctionHouse.getNumericalID(),auction);
    }
    public AuctionItem getBid(){
        AuctionTab tab = auctions.get(Integer.parseInt(currentTab.getId()));
        AuctionItem item = tab.getSelectedItem();
        Bid currentBid = item.getBid();
        currentBid.setBidState(Bid.BidState.BIDDING);
        currentBid.setProposedBid(tab.getProposedBid());
        item.setBid(currentBid);
        return item;
    }
    public void updateAuctionItems(AuctionHouseInventory update){
        List<AuctionItem> items = update.getAuctions();
        AuctionTab auction = auctions.get(update.getAccountNumber());
        auction.updateItems(items);
    }
    public void displayAuctionHouses(ArrayList<IDRecord> auctionHouses){
        bank.setAucHouses(auctionHouses);
    }
    public IDRecord getSelectedAuctionHouse(){return bank.getSelectedItem();}
    public AuctionItem getSelectedTransfer(){return bank.getSelectedToTransfer();}
    public void addTransferItem(AuctionItem wonItem){
        bank.addTransferItem(wonItem);
        displayNotification("New Transfer Request");

    }
    public void updateLabels(BankAccount account){bank.updateLabels(account);}
    public void displayNotification(String msg){
        Stage newTransfer = new Stage();
        Label transfer = new Label(msg);
        transfer.setAlignment(Pos.CENTER);
        Scene window = new Scene(transfer,150,25);
        newTransfer.setScene(window);
        newTransfer.setResizable(false);
        newTransfer.initOwner(stage);
        newTransfer.show();
    }
}
