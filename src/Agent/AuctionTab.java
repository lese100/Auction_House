package Agent;

import Utility.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.layout.BorderPane;

import java.util.List;

public class AuctionTab {
    private ListView<String> list;
    private ObservableList<String> itemDisp;
    private Tab auctionHouse;
    private List<AuctionItem> items;
    private IDRecord houseInfo;
    private Button bid, leave;
    private BorderPane pane;

    /**
     * stores the eahc auction houses tab info.
     * @param items the items available
     * @param houseInfo house info
     * @param bid button for placing a bid
     * @param leave button for leaving the auction house
     */
    public AuctionTab(List<AuctionItem> items, IDRecord houseInfo, Button bid, Button leave){
        list = new ListView<>();
        list.setPrefSize(50,600);
        list.setOrientation(Orientation.VERTICAL);
        itemDisp = FXCollections.observableArrayList();
        list.setItems(itemDisp);
        pane = new BorderPane();
        pane.setRight(list);
        this.items = items;
        this.bid = bid;
        this.leave = leave;
        this.houseInfo = houseInfo;
        auctionHouse = new Tab();
        auctionHouse.setClosable(false);
        auctionHouse.setText(houseInfo.getName());
        addItems();
    }

    /**
     * adds the available items to the auction house display
     */
    private void addItems(){
        for(AuctionItem item : items){
            String info = item.getItemName() + "\n" + item.getBid();
            itemDisp.add(info);
        }
    }

    /**
     * updates the display with any new items list
     * @param newItems the new items list
     */
    private void updateItems(List<AuctionItem> newItems){
        items = newItems;
        itemDisp.removeAll();
        addItems();
    }

    /**
     * allows the display to request the tab inorder to add it to the list of tabs
     * @return the tab
     */
    public Tab getTab(){return auctionHouse;}
}
