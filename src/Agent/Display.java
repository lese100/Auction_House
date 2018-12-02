package Agent;

import Utility.AuctionItem;
import Utility.IDRecord;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.List;

public class Display {
    private Tab currentTab;
    private TabPane tabs;
    public Display(Stage stage){
        stage.setTitle("Agent Interface");
        tabs = new TabPane();
        Scene layout = new Scene(tabs,400,600, Color.WHITE);
        Tab bankTab = new Tab();
        bankTab.setText("Bank");
        bankTab.setClosable(false);
        tabs.getTabs().add(bankTab);
        tabs.getSelectionModel().selectedItemProperty().addListener((ov, oldTab, newTab) -> {
            currentTab = newTab;
        });
        stage.setScene(layout);
        stage.setResizable(false);
        stage.show();
    }
    public void addAuctionTab(List<AuctionItem> items, IDRecord AuctionHouse){

    }
}
