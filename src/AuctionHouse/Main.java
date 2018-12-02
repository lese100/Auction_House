package AuctionHouse;

import javafx.application.Application;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;

public class Main extends Application {

    private static AuctionHouse auctionHouse;

    public static void main(String[] args){

        /*String hostname = "Unknown";

        try
        {
            InetAddress addr;
            addr = InetAddress.getLocalHost();
            hostname = addr.getHostName();
            System.out.println(hostname);
        }
        catch (UnknownHostException ex)
        {
            System.out.println("Hostname can not be resolved");
        }*/
        launch(args);

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

            }else{
                auctionDisplay.displayErrorMessage();
            }
        });

        auctionDisplay.setupAHInitializeButton(createAuctionHouse);


    }

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

    @Override
    public void stop() throws Exception {
        super.stop();
        System.exit(0);
    }

/*    private static String getComputerName()
    {
        Map<String, String> env = System.getenv();
        if (env.containsKey("COMPUTERNAME"))
            return env.get("COMPUTERNAME");
        else if (env.containsKey("HOSTNAME"))
            return env.get("HOSTNAME");
        else
            return "Unknown Computer";
    }*/
}
