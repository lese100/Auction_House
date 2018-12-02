package AuctionHouse;

import Utility.*;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

public class FakeAgentMain extends Application {

    private static FakeAgent fa;

    public static void main(String[] args){
        fa = new FakeAgent();

    }

    @Override
    public void start(Stage primaryStage) throws Exception {

    }

    @Override
    public void stop() throws Exception{
        super.stop();
        System.exit(0);
    }
}
