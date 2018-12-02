package Agent;

import Utility.AuctionItem;
import Utility.IDRecord;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.ArrayList;

public class BankTab {
    private Button leave,getAuctions,getBalance,transfer,join;
    private Tab bankTab;
    private ListView<String> list;
    private ObservableList<String> aucDisp;
    private BorderPane pane;
    private ArrayList<IDRecord> aucHouses;
    private IDRecord selectedItem;

    /**
     * sets up the bank display
     */
    public BankTab(Button leave,Button getAuctions, Button getBalance,Button transfer,Button join){
        this.leave = leave;
        this.getAuctions = getAuctions;
        this.getBalance = getBalance;
        this.transfer = transfer;
        this.join = join;
        aucHouses = null;
        selectedItem = null;


        bankTab = new Tab();
        bankTab.setText("Bank");
        bankTab.setClosable(false);
        bankTab.setId("Bank");

        VBox auctionInfo = new VBox();
        list = new ListView<>();
        list.setPrefSize(50,500);
        list.setOrientation(Orientation.VERTICAL);
        aucDisp = FXCollections.observableArrayList();
        list.setItems(aucDisp);
        auctionInfo.getChildren().addAll(list,this.getAuctions,this.join);

        list.getSelectionModel().selectedIndexProperty().addListener(
                new ChangeListener<Number>() {
                    @Override
                    public void changed(ObservableValue<? extends Number> observable,
                                        Number oldValue, Number newValue) {
                        selectedItem = aucHouses.get((int)newValue);
                    }
                });

        HBox leaveHold = new HBox();
        Label leaveSpacing = new Label("                                                                          " +
                "                                    ");
        leaveHold.getChildren().addAll(leaveSpacing,leave);

        pane = new BorderPane();
        pane.setLeft(auctionInfo);
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
        aucDisp.removeAll();
        displayAucHouses();
    }
    public IDRecord getSelectedItem(){
        aucDisp.removeAll();
        return selectedItem;
    }

}
