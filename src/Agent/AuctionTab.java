package Agent;

import Utility.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
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

import java.text.DecimalFormat;
import java.util.List;
/**
 * creates a tab that represents the auctionHouse and its properties. This is
 * used to allow the use and displaying of multiple auction houses. This object
 * constructs and handles the auction houses information and provides getters
 * and setters for updating the tab and capturing changes made to the tab.
 * created: 11/30/18 by lb
 * last modified: 12/07/18 by lb
 * @author Liam Brady (lb)
 * @author Warren D. Craft (wdc)
 * @author Tyler Fenske (thf)
 */
public class AuctionTab {
    private ListView<String> list;
    private ObservableList<String> itemDisp;
    private Tab auctionHouse;
    private List<AuctionItem> items;
    private IDRecord houseInfo;
    private Button bid, leave;
    private BorderPane pane;
    private int selectedItem;
    private Label itemName, itemID, currentPrice, minBid,timer;
    private TextField proposedBid;
    private HBox leaveHold, biddingArea;
    private DecimalFormat df;
    /**
     * stores the each auction houses tab info. Formats the auctionHouse tab ,
     * creates the needed items, and sets default values.
     * @param items the items available
     * @param houseInfo house info
     * @param bid button for placing a bid
     * @param leave button for leaving the auction house
     */
    public AuctionTab(List<AuctionItem> items, IDRecord houseInfo, Button bid,
                      Button leave){
        list = new ListView<>();
        list.setPrefSize(100,575);
        list.setOrientation(Orientation.VERTICAL);
        itemDisp = FXCollections.observableArrayList();
        list.setItems(itemDisp);
        pane = new BorderPane();
        pane.setLeft(list);
        df = new DecimalFormat("####0.00");
        selectedItem = -1;
        this.items = items;
        this.bid = bid;
        this.leave = leave;
        this.houseInfo = houseInfo;


        /*creates a new Tab for the auction house*/
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
        /*adds the items available at the auction house to the display*/
        addItems();

        /*
         * event Handler for when a item is selected from the list of auction
         * Items
         */
        list.getSelectionModel().selectedIndexProperty().addListener(
                new ChangeListener<Number>() {
                    @Override
                    public void changed(ObservableValue<? extends Number>
                                                observable,Number oldValue,
                                        Number newValue) {
                        int select = (int) newValue;
                        if(select < items.size() && select >= 0) {
                            selectedItem = select;
                            DisplayItem();
                        }
                    }
                });

        /*
         * creates and formats the labels that hold auction Item information and
         * placing bids
         */
        proposedBid = new TextField("00.00");
        itemName = new Label("     Item");
        itemID = new Label("     ID");
        currentPrice = new Label("price");
        minBid = new Label("min");
        Label spacing = new Label("     My Bid:");
        timer = new Label("     Bid End In: --");
        VBox auctionLabels = new VBox();
        biddingArea = new HBox();
        auctionLabels.getChildren().addAll(itemName,itemID,timer);
        biddingArea.getChildren().addAll(spacing,proposedBid,this.bid);
        biddingArea.setSpacing(10);

        /*more formatting for the above items*/
        VBox pricing = new VBox();
        pricing.getChildren().addAll(currentPrice,minBid);
        HBox finalFormat = new HBox();
        finalFormat.getChildren().addAll(auctionLabels,pricing);
        finalFormat.setSpacing(10);
        finalFormat.setAlignment(Pos.BASELINE_LEFT);
        VBox mergeBoxes = new VBox();
        mergeBoxes.getChildren().addAll(finalFormat,biddingArea);

        /*canvas the represents a placeholder for a Auction item image*/
        Canvas image = new Canvas(250,275);
        GraphicsContext gc = image.getGraphicsContext2D();
        gc.setFill(Color.WHITE);
        gc.fillRect(0,25,250,250);
        gc.setFill(Color.BLACK);
        gc.fillText("No Image Added",75,150);

        /*the placing of the canvas above the item information labels*/
        VBox hold = new VBox();
        hold.getChildren().addAll(image,mergeBoxes);
        hold.setAlignment(Pos.BASELINE_CENTER);

        /*leave button formatting*/
        leaveHold = new HBox();
        Label leaveSpacing = new Label("                               " +
                "                                        ");
        leaveHold.getChildren().addAll(leaveSpacing,this.leave);

        /*
         * adds the leave button formatting to the pane and adds the pane to the
         * "AuctionHouse" tab
         */
        pane.setPadding(new Insets(10,10,10,10));
        pane.setCenter(hold);
        pane.setBottom(leaveHold);
        auctionHouse.setContent(pane);

    }

    /**
     * Since every auction tab uses the same buttons every time you switch tabs
     * the buttons need to be redrawn on the current tab. This method is called
     * to replace these buttons.
     * @param leave the leave button
     * @param bid the bid button
     */
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
                    info = item.getItemName() + "\n$" + df.format(bid);
                }
                itemDisp.add(info);
            }
        }
    }

    /**
     * Displays a item selected from the List of items the auction house has.
     * Edits the labels to match the item information. Also formats the prices
     * to match dollar amounts.
     */
    public void DisplayItem(){
        AuctionItem hold = items.get(selectedItem);
        if(hold != null) {
            if(hold.getBid().getBidState() == Bid.BidState.SOLD) {
                currentPrice.setText("Current Bid: SOLD");
                minBid.setText("Min Bid: SOLD");
                timer.setText("     Bid End In: --");
                bid.setDisable(true);
            }else{
                currentPrice.setText("Current Bid: $" + df.format(hold.getBid().
                        getCurrentBid()));
                minBid.setText("Min Bid: $" + df.format(hold.getBid().
                        getMinBid()));
                if(hold.getTimeLeftOnBid() == 0){
                    timer.setText("     Bid End In: --");
                }else {
                    timer.setText("     Bid End In: " + hold.getTimeLeftOnBid() + " Second(s)");
                }
                bid.setDisable(false);
            }
            itemID.setText("     Item ID: " + Integer.toString(hold.
                    getItemID()));
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
    public AuctionItem getSelectedItem(){
        if(selectedItem >= items.size() || selectedItem < 0){return null;}
        return items.get(selectedItem);
    }
    /**
     * allows the display to request the tab inorder to add it to the list
     * of tabs
     * @return the tab
     */
    public Tab getTab(){return auctionHouse;}

    /**
     * gets the bid that was proposed in the proposedBid textField and formats
     * it to mach a dollar amount
     * @return the bid
     */
    public double getProposedBid(){
        double bid = Double.parseDouble(proposedBid.getText())*100;
        int holdBid = (int)bid;
        bid = ((double) holdBid)/100;
        proposedBid.setText(df.format(bid));
        return bid;}
}
