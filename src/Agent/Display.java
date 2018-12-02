package Agent;

import Utility.AuctionItem;
import Utility.IDRecord;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.List;

public class Display {
    private HashMap<Integer,AuctionTab> auctions;
    private HashMap<String,BankTab> banks;
    private Tab currentTab;
    private TabPane tabs;
    private Button bid,leaveAuc,leaveBank,getAuction,getBalance,transfer,join;
    private IDRecord myInfo;
    /**
     * constructs the initial display for the bank.
     * @param stage the main display stage
     */
    public Display(Stage stage,IDRecord myInfo,Button bid,Button leaveAuc,Button leaveBank,Button getAuction,Button getBalance,
                   Button transfer,Button join){
        this.myInfo = myInfo;
        this.bid = bid;
        this.leaveAuc = leaveAuc;
        this.leaveBank = leaveBank;
        this.getAuction = getAuction;
        this.getBalance = getBalance;
        this.transfer = transfer;
        this.join = join;
        auctions = new HashMap<>();
        banks = new HashMap<>();
        stage.setTitle("Agent Interface");
        tabs = new TabPane();
        Scene layout = new Scene(tabs,400,600, Color.WHITE);
        BankTab bank = new BankTab(leaveBank,getAuction,getBalance,transfer,join);
        banks.put("bank",bank);
        tabs.getTabs().add(banks.get("bank").getBankTab());
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
}
