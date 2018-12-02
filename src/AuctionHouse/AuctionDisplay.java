package AuctionHouse;

import Utility.AuctionItem;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.text.DecimalFormat;
import java.util.List;

public class AuctionDisplay {

    private VBox vBox;

    private TextField auctionHouseName;
    private TextField auctionHouseHostName;
    private TextField auctionHousePort;
    private TextField bankHostName;
    private TextField bankPort;

    private TextArea auctionTextArea;
    private TextArea consoleTextArea;
    private Label auctionLabel;

    private Stage window;
    private Stage newWindow;
    private Button createAuctionHouse;

    private DecimalFormat df;


    public AuctionDisplay(Stage window){
        this.window = window;

        df = new DecimalFormat("####0.00");

        initializeDisplay();
    }

    private void initializeDisplay(){
        window.setMinHeight(200);
        window.setMinWidth(200);
        window.setMaxHeight(200);
        window.setMaxWidth(200);

        auctionHouseName = new TextField("AuctionHouse Name");
        auctionHouseName.setMaxSize(175, 25);

        auctionHouseHostName = new TextField("AuctionHouse Host Name");
        auctionHouseHostName.setMaxSize(175, 25);

        auctionHousePort = new TextField("AuctionHouse Port Number");
        auctionHousePort.setMaxSize(175, 25);

        bankHostName = new TextField("Bank Host Name");
        bankHostName.setMaxSize(175, 25);

        bankPort = new TextField("Bank Port Number");
        bankPort.setMaxSize(175, 25);

       /* createAuctionHouse = new Button("Press me");

        createAuctionHouse.setOnAction(event -> {
            System.out.println(auctionHouseName.getText().trim());
            System.out.println(auctionHouseHostName.getText().trim());
            System.out.println(auctionHousePort.getText().trim());
            System.out.println(bankHostName.getText().trim());
            System.out.println(bankPort.getText().trim());
        });*/

        vBox = new VBox();

        vBox.getChildren().addAll(auctionHouseName, auctionHouseHostName,
                auctionHousePort, bankHostName, bankPort);

        Scene scene = new Scene(vBox);

        window.setScene(scene);

        window.show();

    }

    public String getAuctionHouseName() {
        return auctionHouseName.getText().trim();
    }

    public String getAuctionHouseHostName() {
        return auctionHouseHostName.getText().trim();
    }

    public int getAuctionHousePort() {
        return Integer.parseInt(auctionHousePort.getText());
    }

    public String getBankHostName() {
        return bankHostName.getText().trim();
    }

    public int getBankPort() {
        return Integer.parseInt(bankPort.getText().trim());
    }

    public boolean infoFilledOut(){
        boolean infoFilledOut = true;

        if(auctionHouseName.getText().trim().equals("AuctionHouse Name" ) ||
                auctionHouseHostName.getText().trim()
                        .equals("AuctionHouse Host Name") ||
                auctionHousePort.getText().trim()
                        .equals("AuctionHouse Port Number") ||
                bankHostName.getText().trim().equals("Bank Host Name") ||
                bankPort.getText().trim().equals("Bank Port Number")){

            System.out.println("PLEASE CHANGE ALL FIELDS");
            infoFilledOut = false;
        }

        try{
           Integer.parseInt(auctionHousePort.getText().trim());
           Integer.parseInt(bankPort.getText().trim());
        }catch(NumberFormatException e){
            System.out.println("NUMBER EXPECTED FOR PORT NUMBER FIELDS");
            infoFilledOut = false;
        }

        return infoFilledOut;
    }

    public void openTerminalWindow(){
        System.out.println("Successful Setup");

        window.close();

        newWindow = new Stage();

        newWindow.setMaxHeight(450);
        newWindow.setMaxWidth(700);
        newWindow.setMinHeight(450);
        newWindow.setMinWidth(700);

        BorderPane auctionPane = new BorderPane();
        BorderPane consolePane = new BorderPane();

        auctionTextArea = new TextArea("auctions will appear here");
        consoleTextArea = new TextArea("connection information will appear here");

        auctionTextArea.setMaxSize(700, 325);
        consoleTextArea.setMaxSize(700, 325);

        auctionTextArea.setEditable(false);
        consoleTextArea.setEditable(false);

        Label consoleLabel = new Label("Console");
        auctionLabel = new Label("AuctionHouse");

        auctionPane.setTop(auctionLabel);
        auctionPane.setBottom(auctionTextArea);

        consolePane.setTop(consoleLabel);
        consolePane.setCenter(consoleTextArea);

        BorderPane borderPane = new BorderPane();

        borderPane.setTop(auctionPane);
        borderPane.setBottom(consolePane);

        Scene scene = new Scene(borderPane);

        newWindow.setScene(scene);

        newWindow.show();
    }

    public void displayErrorMessage(){
        System.out.println("TEST");
    }

    public void setupAHInitializeButton(Button createAuctionHouse){
        this.createAuctionHouse = createAuctionHouse;
        vBox.getChildren().add(createAuctionHouse);
    }

    public void setupAHLabelInfo(String name, int id){
        auctionLabel.setText(name + " — ID: " + id);
    }

    public void updateAuctionItemDisplay(List<AuctionItem> auctionItems){
        String output = "Auction Item Information:";

        for(int i = 0; i < auctionItems.size(); i++){
            AuctionItem ai = auctionItems.get(i);
            output = output + "\nITEM ID: " + ai.getItemID() +
                    "\t BID STATE: " + ai.getBid().getBidState() +
                    "\t MIN BID: $" + df.format(ai.getBid().getMinBid()) +
                    "\t CURRENT BID: $" + df.format(ai.getBid().getCurrentBid()) +
                    "\t ITEM NAME: " + ai.getItemName();
        }

        auctionTextArea.setText(output);
    }

}
