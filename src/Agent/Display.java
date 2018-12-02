package Agent;

import Utility.AuctionItem;
import Utility.IDRecord;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.List;

public class Display {
    private HashMap<String,AuctionTab> auctions;
    private HashMap<String,BankTab> banks;
    private Tab currentTab;
    private TabPane tabs;

    /**
     * constructs the initial display for the bank.
     * @param stage the main display stage
     */
    public Display(Stage stage){
        auctions = new HashMap<>();
        banks = new HashMap<>();
        stage.setTitle("Agent Interface");
        tabs = new TabPane();
        Scene layout = new Scene(tabs,400,600, Color.WHITE);
        BankTab bank = new BankTab();
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
     * @param AuctionHouse auction house info
     */
    public void addAuctionTab(List<AuctionItem> items, IDRecord AuctionHouse){

    }
}
