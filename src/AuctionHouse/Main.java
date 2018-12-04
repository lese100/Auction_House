package AuctionHouse;

import javafx.application.Application;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * The Main class within the AuctionHouse package, providing the construction
 * of an AuctionHouse object. Run this to set up an AuctionHouse and
 * open it for business.
 * created: 11/30/18 by thf
 * last modified: 12/02/18 by thf
 * previously modified: 12/02/18 by thf
 * @author Liam Brady (lb)
 * @author Warren D. Craft (wdc)
 * @author Tyler Fenske (thf)
 */
public class Main extends Application {

    private static AuctionHouse auctionHouse;

    // ****************************** //
    //   Main Method                  //
    // ****************************** //

    public static void main(String[] args){

        launch(args);

    }

    // ****************************** //
    //   Private Methods              //
    // ****************************** //

    private static void createAuctionHouse(AuctionDisplay display,
                                           String auctionHouseName,
                                           String auctionHouseHostName,
                                           int auctionHousePort,
                                           String bankHostName,
                                           int bankPort) {

        try{
            auctionHouse = new AuctionHouse(display,
                    auctionHouseName, auctionHouseHostName, auctionHousePort,
                    bankHostName, bankPort);
        }catch(IOException e){
            e.printStackTrace();
        }
    }


    // ****************************** //
    //   Override Methods             //
    // ****************************** //

    @Override
    public void stop() throws Exception {
        System.exit(0);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        AuctionDisplay auctionDisplay = new AuctionDisplay(primaryStage);

        Button createAuctionHouse = new Button("Create Auction House");

        createAuctionHouse.setOnAction(event -> {
            if(auctionDisplay.infoFilledOut()){
                auctionDisplay.openTerminalWindow();

                createAuctionHouse(auctionDisplay,
                        auctionDisplay.getAuctionHouseName(),
                        auctionDisplay.getAuctionHouseHostName(),
                        auctionDisplay.getAuctionHousePort(),
                        auctionDisplay.getBankHostName(),
                        auctionDisplay.getBankPort());
            }
        });

        Stage newWindow = new Stage();

        newWindow.setOnCloseRequest(event -> {
            try{
                if(auctionHouse.safeToClose()){
                    stop();
                }else{
                    auctionDisplay.displayErrorMessage("ALL AGENTS MUST BE " +
                            "DISCONNECTED IN ORDER TO " +
                            "CLOSE DOWN THIS AUCTION HOUSE!");
                    event.consume();
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        });

        auctionDisplay.setupAHGUIComponents(createAuctionHouse, newWindow);

    }
}
