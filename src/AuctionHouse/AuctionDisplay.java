package AuctionHouse;

import Utility.AuctionItem;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Provides a 2-phase GUI display for an AuctionHouse.
 * (1) Phase 1 is a window in which the user can specify info about the
 *     AuctionHouse and use that info to construct a AuctionHouse as well
 *     as connect to the Bank.
 * (2) Phase 2 is then a final window that represents the established,
 *     working AuctionHouse and displays information about the AuctionHouse
 *     and contained AuctionItems.
 * created: 11/30/2018 by thf
 * last modified: 12/02/2018 by thf
 * @author Liam Brady (lb)
 * @author Warren D. Craft
 * @author Tyler Fenske (thf)
 */
public class AuctionDisplay {


    private GridPane gridPane;

    private TextField auctionHouseName;
    private TextField auctionHouseHostName;
    private TextField auctionHousePort;
    private TextField bankHostName;
    private TextField bankPort;

    private TextArea auctionTextArea;
    private TextArea consoleTextArea;
    private String consoleText;
    private Label auctionLabel;

    private Stage window;
    private Stage newWindow;

    private DecimalFormat df;


    // ****************************** //
    //   Constructor(s)               //
    // ****************************** //

    public AuctionDisplay(Stage window){
        this.window = window;

        df = new DecimalFormat("####0.00");

        initializeDisplay();
    }


    // ****************************** //
    //   Private Methods              //
    // ****************************** //

    private void initializeDisplay(){
        window.setMinHeight(200);
        window.setMinWidth(330);
        window.setMaxHeight(200);
        window.setMaxWidth(330);

        window.setTitle("AuctionHouse Setup");

        auctionHouseName = new TextField("Clever Name");
        auctionHouseName.setMaxSize(175, 25);

        auctionHouseHostName = new TextField("localhost");
        auctionHouseHostName.setMaxSize(175, 25);

        auctionHousePort = new TextField("1991");
        auctionHousePort.setMaxSize(175, 25);

        bankHostName = new TextField("localhost");
        bankHostName.setMaxSize(175, 25);

        bankPort = new TextField("1234");
        bankPort.setMaxSize(175, 25);

        Label auctionHouseNameLabel = new Label("  AuctionHouse Name");
        Label auctionHouseHostNameLabel = new Label("  AuctionHouse Host Name");
        Label auctionHousePortLabel = new Label("  AuctionHouse Port Number ");
        Label bankHostNameLabel = new Label("  Bank Host Name");
        Label bankPortLabel = new Label("  Bank Port Number");

        auctionHouseNameLabel.setMaxSize(175, 25);
        auctionHouseHostNameLabel.setMaxSize(175, 25);
        auctionHousePortLabel.setMaxSize(175, 25);
        bankHostNameLabel.setMaxSize(175, 25);
        bankPortLabel.setMaxSize(175, 25);

       /* createAuctionHouse = new Button("Press me");

        createAuctionHouse.setOnAction(event -> {
            System.out.println(auctionHouseName.getText().trim());
            System.out.println(auctionHouseHostName.getText().trim());
            System.out.println(auctionHousePort.getText().trim());
            System.out.println(bankHostName.getText().trim());
            System.out.println(bankPort.getText().trim());
        });*/

       gridPane = new GridPane();

       gridPane.add(auctionHouseNameLabel, 0, 0);
       gridPane.add(auctionHouseName, 1, 0);
       gridPane.add(auctionHouseHostNameLabel, 0, 1);
       gridPane.add(auctionHouseHostName, 1, 1);
       gridPane.add(auctionHousePortLabel, 0, 2);
       gridPane.add(auctionHousePort, 1, 2);
       gridPane.add(bankHostNameLabel, 0, 3);
       gridPane.add(bankHostName, 1, 3);
       gridPane.add(bankPortLabel, 0, 4);
       gridPane.add(bankPort, 1, 4);


        Scene scene = new Scene(gridPane);

        window.setScene(scene);

        window.show();

    }

    private void displayErrorMessage(String errorMessage){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(errorMessage);
        alert.show();
    }


    // ****************************** //
    //   Public Methods               //
    // ****************************** //

    public boolean infoFilledOut(){
        boolean infoFilledOut = true;

        /*if(auctionHouseName.getText().trim().equals("AuctionHouse Name") ||
                auctionHouseHostName.getText().trim()
                        .equals("AuctionHouse Host Name") ||
                auctionHousePort.getText().trim()
                        .equals("AuctionHouse Port Number") ||
                bankHostName.getText().trim().equals("Bank Host Name") ||
                bankPort.getText().trim().equals("Bank Port Number")){

            displayErrorMessage("All Fields Must Be Changed From Default");
            infoFilledOut = false;
        }*/

        try{
            Integer.parseInt(auctionHousePort.getText().trim());
            Integer.parseInt(bankPort.getText().trim());
        }catch(NumberFormatException e){
            displayErrorMessage("Number Expected in Port Number Fields");
            infoFilledOut = false;
        }

        return infoFilledOut;
    }

    public void openTerminalWindow(){
        System.out.println("Successful Setup");

        window.close();

        newWindow = new Stage();

        newWindow.setTitle("Auction House");

        newWindow.setMaxHeight(450);
        newWindow.setMaxWidth(700);
        newWindow.setMinHeight(450);
        newWindow.setMinWidth(700);

        BorderPane auctionPane = new BorderPane();
        BorderPane consolePane = new BorderPane();

        consoleText = "Connection Information:";

        auctionTextArea = new TextArea("auctions will appear here");
        consoleTextArea = new TextArea(consoleText);

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

    public void setupAHInitializeButton(Button createAuctionHouse){
        gridPane.add(createAuctionHouse, 1, 5);
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

    public void updateConsoleDisplay(String message){
        consoleText += "\n" + message;
        consoleTextArea.setText(consoleText);
    }


    // ****************************** //
    //   Getter(s) & Setter(s)        //
    // ****************************** //

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

}
