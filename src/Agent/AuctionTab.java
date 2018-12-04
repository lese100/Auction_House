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
    private int selectedItem;
    private Label itemName, itemID, currentPrice, minBid;
    private TextField proposedBid;
    private HBox leaveHold, biddingArea;
    /**
     * stores the eahc auction houses tab info.
     * @param items the items available
     * @param houseInfo house info
     * @param bid button for placing a bid
     * @param leave button for leaving the auction house
     */
    public AuctionTab(List<AuctionItem> items, IDRecord houseInfo, Button bid, Button leave){
        list = new ListView<>();
        list.setPrefSize(100,575);
        list.setOrientation(Orientation.VERTICAL);
        itemDisp = FXCollections.observableArrayList();
        list.setItems(itemDisp);
        pane = new BorderPane();
        pane.setLeft(list);

        selectedItem = -1;
        this.items = items;
        this.bid = bid;
        this.leave = leave;
        this.houseInfo = houseInfo;

        auctionHouse = new Tab();
        auctionHouse.setId(Integer.toString(houseInfo.getNumericalID()));
        auctionHouse.setClosable(false);
        if(houseInfo == null){
            auctionHouse.setId(null);
        }else {
            auctionHouse.setId(Integer.toString(houseInfo.getNumericalID()));
        }
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
                        int select = (int) newValue;
                        if(select < items.size() && select >= 0) {
                            selectedItem = select;
                            DisplayItem();
                        }
                    }
                });

        proposedBid = new TextField("0000");
        itemName = new Label("     Item");
        itemID = new Label("     ID");
        currentPrice = new Label("price");
        minBid = new Label("min");
        Label spacing = new Label("     My Bid:");
        VBox auctionLabels = new VBox();
        biddingArea = new HBox();
        auctionLabels.getChildren().addAll(itemName,itemID);
        biddingArea.getChildren().addAll(spacing,proposedBid,this.bid);
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

        leaveHold = new HBox();
        Label leaveSpacing = new Label("                                                                       ");
        leaveHold.getChildren().addAll(leaveSpacing,this.leave);

        pane.setPadding(new Insets(10,10,10,10));
        pane.setCenter(hold);
        pane.setBottom(leaveHold);
        auctionHouse.setContent(pane);

    }

    public void replaceButtons(Button leave, Button bid){
        leaveHold.getChildren().remove(this.leave);
        leaveHold.getChildren().add(leave);
        biddingArea.getChildren().remove(this.bid);
        biddingArea.getChildren().add(bid);
        this.leave = leave;
        this.bid = bid;
    }
    /**
     * adds the available items to the auction house display
     */
    private void addItems(){
        if(items != null) {
            for (AuctionItem item : items) {
                Double bid;
                if(item.getBid().getCurrentBid() == 0){
                    bid = item.getBid().getMinBid();
                }else{
                    bid = item.getBid().getCurrentBid();
                }
                String info;
                if(item.getBid().getBidState() == Bid.BidState.SOLD){
                    info = item.getItemName() + "\nSOLD";
                }else {
                    info = item.getItemName() + "\n$" + bid;
                }
                itemDisp.add(info);
            }
        }
    }
    public void DisplayItem(){
        AuctionItem hold = items.get(selectedItem);
        if(hold != null) {
            if(hold.getBid().getBidState() == Bid.BidState.SOLD) {
                currentPrice.setText("Current Bid: SOLD");
                minBid.setText("Min Bid: SOLD");
                bid.setDisable(true);
            }else{
                currentPrice.setText("Current Bid: $" + Double.toString(hold.getBid().getCurrentBid()));
                minBid.setText("Min Bid: $" + Double.toString(hold.getBid().getMinBid()));
                bid.setDisable(false);
            }
            itemID.setText("     Item ID: " + Integer.toString(hold.getItemID()));
            itemName.setText("     Item Name: " + hold.getItemName());
        }else{
            System.out.println("no selected item");
        }
    }
    /**
     * updates the display with any new items list
     * @param newItems the new items list
     */
    public void updateItems(List<AuctionItem> newItems){
        items = newItems;
        itemDisp.clear();
        addItems();
        if(selectedItem >= 0 && selectedItem < items.size()) {
            DisplayItem();
        }
    }

    /**
     * gets the selected item and returns it.
     * @return the selected item.
     */
    public AuctionItem getSelectedItem(){return items.get(selectedItem);}
    /**
     * allows the display to request the tab inorder to add it to the list of tabs
     * @return the tab
     */
    public Tab getTab(){return auctionHouse;}
    public double getProposedBid(){
        double bid = Double.parseDouble(proposedBid.getText())*100;
        int holdBid = (int)bid;
        bid = ((double) holdBid)/100;
        proposedBid.setText(Double.toString(bid));
        return bid;}
}
