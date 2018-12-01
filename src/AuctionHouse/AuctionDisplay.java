package AuctionHouse;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class AuctionDisplay {

    private VBox vBox;

    private TextField auctionHouseName;
    private TextField auctionHouseHostName;
    private TextField auctionHousePort;
    private TextField bankHostName;
    private TextField bankPort;

    private Stage window;
    private Button createAuctionHouse;


    public AuctionDisplay(Stage window){
        this.window = window;
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
    }

    public void displayErrorMessage(){
        System.out.println("TEST");
    }

    public void setupAHInitializeButton(Button createAuctionHouse){
        this.createAuctionHouse = createAuctionHouse;
        vBox.getChildren().add(createAuctionHouse);
    }

}
