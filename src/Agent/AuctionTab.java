package Agent;

import Utility.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.util.List;

public class AuctionTab {
    private ListView<String> list;
    private ObservableList<String> itemDisp;
    private Tab auctionHouse;
    private List<AuctionItem> items;
    private IDRecord houseInfo;
    private Button bid, leave;
    private BorderPane pane;
    private AuctionItem selectedItem;
    private Label itemName, itemID, currentPrice, minBid;
    private TextField proposedBid;
    /**
     * stores the eahc auction houses tab info.
     * @param items the items available
     * @param houseInfo house info
     * @param bid button for placing a bid
     * @param leave button for leaving the auction house
     */
    public AuctionTab(List<AuctionItem> items, IDRecord houseInfo, Button bid, Button leave){
        list = new ListView<>();
        selectedItem = null;
        list.setPrefSize(100,600);
        list.setOrientation(Orientation.VERTICAL);
        itemDisp = FXCollections.observableArrayList();
        list.setItems(itemDisp);
        pane = new BorderPane();
        pane.setLeft(list);

        this.items = items;
        this.bid = bid;
        this.leave = leave;
        this.houseInfo = houseInfo;

        auctionHouse = new Tab();
        auctionHouse.setClosable(false);
        if(houseInfo != null) {
            auctionHouse.setText(houseInfo.getName());
            auctionHouse.setId(Integer.toString(houseInfo.getNumericalID()));
        }
        addItems();
        list.getSelectionModel().selectedIndexProperty().addListener(
                new ChangeListener<Number>() {
                    @Override
                    public void changed(ObservableValue<? extends Number> observable,
                                        Number oldValue, Number newValue) {
                        selectedItem = items.get((int)newValue);
                        DisplayItem();
                    }
                });

        proposedBid = new TextField("0000");
        itemName = new Label("     Item");
        itemID = new Label("     ID");
        currentPrice = new Label("price");
        minBid = new Label("min");
        Label spacing = new Label("     My Bid:");
        VBox auctionLabels = new VBox();
        HBox biddingArea = new HBox();
        auctionLabels.getChildren().addAll(itemName,itemID);
        biddingArea.getChildren().addAll(spacing,proposedBid,bid);
        biddingArea.setSpacing(10);

        VBox pricing = new VBox();
        pricing.getChildren().addAll(currentPrice,minBid);
        HBox finalFormat = new HBox();
        finalFormat.getChildren().addAll(auctionLabels,pricing);
        finalFormat.setSpacing(10);
        finalFormat.setAlignment(Pos.BASELINE_LEFT);

        VBox mergeBoxes = new VBox();
        mergeBoxes.getChildren().addAll(finalFormat,biddingArea);

        Canvas image = new Canvas(250,275);
        GraphicsContext gc = image.getGraphicsContext2D();
        gc.setFill(Color.WHITE);
        gc.fillRect(0,25,250,250);
        gc.setFill(Color.BLACK);
        gc.fillText("No Image Added",75,150);

        VBox hold = new VBox();
        hold.getChildren().addAll(image,mergeBoxes);
        hold.setAlignment(Pos.BASELINE_CENTER);

        HBox leaveHold = new HBox();
        Label leaveSpacing = new Label("                                                                       " +
                "                                       ");
        leaveHold.getChildren().addAll(leaveSpacing,leave);

        pane.setPadding(new Insets(10,10,10,10));
        pane.setCenter(hold);
        pane.setBottom(leaveHold);
        auctionHouse.setContent(pane);

    }

    /**
     * adds the available items to the auction house display
     */
    private void addItems(){
        if(items != null) {
            for (AuctionItem item : items) {
                String info = item.getItemName() + "\n" + item.getBid();
                itemDisp.add(info);
            }
        }
    }
    private void DisplayItem(){
        itemID.setText("     Item ID: "+Integer.toString(selectedItem.getItemID()));
        itemName.setText("     Item Name: "+selectedItem.getItemName());
        currentPrice.setText("Current Bid: " + Double.toString(selectedItem.getBid().getCurrentBid()));
        minBid.setText("Min Bid: "+Double.toString(selectedItem.getBid().getMinBid()));
    }
    /**
     * updates the display with any new items list
     * @param newItems the new items list
     */
    public void updateItems(List<AuctionItem> newItems){
        items = newItems;
        itemDisp.removeAll();
        addItems();
    }

    /**
     * gets the selected item and returns it.
     * @return the selected item.
     */
    public AuctionItem getSelectedItem(){return selectedItem;}
    /**
     * allows the display to request the tab inorder to add it to the list of tabs
     * @return the tab
     */
    public Tab getTab(){return auctionHouse;}
    public Double getProposedBid(){return Double.parseDouble(proposedBid.getText());}
}
