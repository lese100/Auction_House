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

    /**
     * Standard main method that launches JavaFX application
     * @param args command line arguments (unused in this program)
     */
    public static void main(String[] args){

        launch(args);

    }

    // ****************************** //
    //   Private Methods              //
    // ****************************** //

    /**
     * Creates an AuctionHouse object with the information provided in the
     * setup GUI.
     * @param display
     * @param auctionHouseName
     * @param auctionHouseHostName
     * @param auctionHousePort
     * @param bankHostName
     * @param bankPort
     */
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

    /**
     * Override of stop method, called whenever the default close button
     * is pressed. System.exit(0) will close all active threads of the
     * program at once.
     * @throws Exception
     */
    @Override
    public void stop() throws Exception {
        System.exit(0);
    }

    /**
     * The starting point of the program. Creates the display, and
     * defines the event handlers for the createAuctionHouse button and
     * close button for the AuctionHouse Stage.
     * @param primaryStage
     * @throws Exception
     */
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
