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
/**
 * Object for creating and displaying new tabs for auctionHouses and banks. Designed to allow for multiple auctions
 * and future use of multiple banks. Used for communication between the agent and the auctionHouse and Bank tabs.
 * created: 11/30/18 by lb
 * last modified: 12/07/18 by lb
 * @author Liam Brady (lb)
 * @author Warren D. Craft (wdc)
 * @author Tyler Fenske (thf)
 */
public class Display {
    private HashMap<Integer,AuctionTab> auctions;
    private BankTab bank;
    private Tab currentTab;
    private TabPane tabs;
    private Button bid,leaveAuc,leaveBank,getAuction,getBalance,transfer,join;
    private IDRecord myInfo;
    private Stage stage;

    /**
     * Sets the initial values that are needed for creating new tabs. Creates a list of tabs that are displayed on the
     * display. Creates the initial BankTab and adds it to the list of Tabs. Displays the list of tabs and has a event
     * handler for when you select a different tab.
     * @param stage the display
     * @param myInfo the users info
     * @param bid button for placing bids (AH Button)
     * @param leaveAuc button for leaving a auction house (AH Button)
     * @param leaveBank button for leaving the bank (Bank Button)
     * @param getAuction button for requesting the list of auction houses (Bank Button)
     * @param getBalance button for requesting the users banking information (Bank Button)
     * @param transfer button for requesting the bank to transfer funds for a purchased item. (Bank Button)
     * @param join button for joining a selected auction house in the list of auction houses (Bank Button)
     * @param deposit the initial deposit that the bank tab will display in the history list.
     */
    public Display(Stage stage,IDRecord myInfo,Button bid,Button leaveAuc,Button leaveBank,Button getAuction,Button getBalance,
                   Button transfer,Button join,Double deposit){
        this.myInfo = myInfo;
        this.bid = bid;
        this.stage = stage;
        this.leaveAuc = leaveAuc;
        this.leaveBank = leaveBank;
        this.getAuction = getAuction;
        this.getBalance = getBalance;
        this.transfer = transfer;
        this.join = join;
        currentTab = null;
        auctions = new HashMap<>();
        stage.setTitle("Agent Interface");
        tabs = new TabPane();
        Scene layout = new Scene(tabs,450,620, Color.WHITE);
        bank = new BankTab(leaveBank,getAuction,getBalance,transfer,join,deposit);
        currentTab = bank.getBankTab();
        tabs.getTabs().add(bank.getBankTab());
        tabs.getSelectionModel().selectedItemProperty().addListener((ov, oldTab, newTab) -> {
            currentTab = newTab;
            if(currentTab.getId() != "Bank"){
                AuctionTab hold = auctions.get(Integer.parseInt(currentTab.getId()));
                hold.replaceButtons(leaveAuc,bid);
            }
        });
        stage.setScene(layout);
        stage.setResizable(false);
        stage.show();
    }

    /**
     * creates a new tab for the Auction House the user connected to.
     * @param items list of Auction items to display in the tab
     * @param auctionHouse auction house info
     */
    public void addAuctionTab(List<AuctionItem> items, IDRecord auctionHouse){
        if(auctionHouse != null) {
            if (auctions.get(auctionHouse.getNumericalID()) == null) {
                AuctionTab auction = new AuctionTab(items, auctionHouse, bid, leaveAuc);
                tabs.getTabs().add(auction.getTab());
                if (auctionHouse != null) {
                    auctions.put(auctionHouse.getNumericalID(), auction);
                }
            } else {
                displayNotification("That Auction House Exists Already");
            }
        }
    }

    /**
     * removes the current open tab from the list of tabs and removes the tab from the list of Auction house objects.
     * This method is called by the auction leave button event handler after checking if you can leave the auction
     * house.
     */
    public void removeCurrentTab(){
        auctions.remove(Integer.parseInt(currentTab.getId()));
        tabs.getTabs().remove(currentTab);
    }

    /**
     * checks to see if a auction house exists in the hashmap of bank tabs. Called when someone tries to join a auction
     * house. This is used to make sure someone doesn't join the same auction house twice.
     * @param ID to check for in the list of tabs
     * @return whether or not the auction house exists.
     */
    public boolean doesAuctionExist(int ID){
        if(auctions.get(ID) != null) {
            displayNotification("Already in this auction house");
            return true;
        }
        return false;
    }

    /**
     * Called by the event handler for the bid button. gets the current selected AuctionTab, requests the current
     * selected item from the tab, and sets the bidding state, and moves the proposed bid to the currentBid position.
     * displays notifications for any potential errors.
     * @return the updated AuctionItem that was selected
     */
    public AuctionItem getBid(){
        AuctionItem item = null;
        if(currentTab.getId() != null) {
            AuctionTab tab = auctions.get(Integer.parseInt(currentTab.getId()));
            item = tab.getSelectedItem();
            if(item != null) {
                Bid currentBid = item.getBid();
                currentBid.setBidState(Bid.BidState.BIDDING);
                currentBid.setProposedBid(tab.getProposedBid());
                item.setBid(currentBid);
            }else{
                displayNotification("Nothing to bid on");
            }
        }else{
            displayNotification("Nothing to bid on");
        }
        return item;
    }

    /**
     * Tells the auctionHouse tab to update its list of auction items with a new updated list. Called when the
     * Agent receives a notification to update an item list. grabs the house id and passes the list to the
     * corresponding AuctionHouse tab.
     * @param update AuctionHouseInventory has AuctionHouse id and the list of updated items
     */
    public void updateAuctionItems(AuctionHouseInventory update){
        List<AuctionItem> items = update.getAuctions();
        AuctionTab auction = auctions.get(update.getAccountNumber());
        auction.updateItems(items);
    }

    /**
     * Tells the BankTab to display a list of AuctionHouses provided by the bank. Called by the Get auctions button
     * event handler.
     * @param auctionHouses list of auction house IDRecords to be displayed.
     */
    public void displayAuctionHouses(ArrayList<IDRecord> auctionHouses){
        bank.setAucHouses(auctionHouses);
    }

    /**
     * asks the bankTab for the auction house that's been selected join and return it. Called when the join button is
     * clicked.
     * @return IDRecord of the auction house the user would like to join.
     */
    public IDRecord getSelectedAuctionHouse(){return bank.getSelectedItem();}

    /**
     * asks the bankTab for the item the have selected to be transferred.
     * @return item that's being transferred
     */
    public AuctionItem getSelectedTransfer(){return bank.getSelectedToTransfer();}

    /**
     * creates a copy of the auction item that the user has won and forwards it ot the bankTab object to be displayed
     * @param wonItem the item won by the user
     */
    public void addTransferItem(AuctionItem wonItem){
        AuctionItem item = new AuctionItem(wonItem.getHouseID(),wonItem.getItemID(),wonItem.getItemName(),
                wonItem.getBid());
        bank.addTransferItem(item);
        displayNotification("New Transfer Request");

    }

    /**
     * called as a setter to update the User information in the bank tab. Mainly called when the bank sends a updated
     * bank account information.
     * @param account bank account information that's forwarded to the bankTab object.
     */
    public void updateLabels(BankAccount account){bank.updateLabels(account);}

    /**
     * displays a popup notification with a given message, mainly used to display notifications for user functionalitys.
     * @param msg message being displayed in the popup
     */
    public void displayNotification(String msg){
        Stage newTransfer = new Stage();
        Label transfer = new Label(msg);
        transfer.setAlignment(Pos.CENTER);
        Scene window = new Scene(transfer,250,35);
        newTransfer.setScene(window);
        newTransfer.setResizable(false);
        newTransfer.initOwner(stage);
        newTransfer.show();
    }

    /**
     * gets the current tab from the list of tabs
     * @return current tab
     */
    public int getCurrentTab(){
        return Integer.parseInt(currentTab.getId());
    }
}
