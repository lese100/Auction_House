package Bank;

import Utility.BankAccount;
import Utility.CommunicationService;
import Utility.IDRecord;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Set;

/**
 * Provides a 2-phase GUI display for a Bank.
 * (1) Phase 1 is a window in which the user can specify info about the
 *     Bank and use that info to construct a Bank;
 * (2) Phase 2 is then a final window that represents the established,
 *     working bank and displays information about the Bank and its
 *     operations.
 * created: 12/02/2018 by wdc
 * last modified: 12/02/2018 by wdc
 * @author Liam Brady (lb)
 * @author Warren D. Craft
 * @author Tyler Fenske (thf)
 */
public class BankDisplay {

    private Stage theStage;
    private Stage stage01;
    private Stage stage02;

    private CommunicationService communicationService;
    private BankProxyForTesting bankProxyForTesting;
    private IDRecord clientIDRecord;
    private BankAccount myBankAccount;

    private BorderPane borderPane;
    private BorderPane borderPane01;
    private BorderPane borderPane02;

    // mutable components of the GUI display
    private Label textLabelBank = new Label("BANK: My Bank");
    private TextField textFieldBankName = new TextField("Bank");
    private TextField textFieldBankHost = new TextField("localhost");
    private TextField textFieldBankPort = new TextField("1234");
    private Label textLabelNumberOfActiveAccounts = new Label("0");
    private Label textLabelNumberOfAgentAccounts = new Label("0");
    private Label textLabelNumberOfAHAccounts = new Label("0");
    private TextArea textAreaOutput = new TextArea("Output Area");
    private Button btnCreateBank;
    private HBox hBoxForBottomPanel;

    private boolean bankOnline = false;

    private DecimalFormat df;

    private Bank bank;

    // ****************************** //
    //   Constructor(s)               //
    // ****************************** //

    /**
     * Public constructor for the GUI/display associated with a Bank.
     * This manifests in two stages:
     * (1) a preliminary window that accepts Bank information (name,
     * hostname, host port);
     * (2) then a final window that represents the active Bank,
     * exhibiting information such as number of accounts, balances, etc.
     * @param theStage A JavaFX Stage object, passed from Main.
     */
    public BankDisplay(Stage theStage){
        this.stage01 = theStage;

        df = new DecimalFormat("####0.00");

        initializeDisplay01();
    }

    private void initializeDisplay01() {

        stage01.setMinWidth(400);
        stage01.setMinHeight(300);
        stage01.setMaxWidth(400);
        stage01.setMaxHeight(300);

        // title at the top
        Label textLabelTitle = new Label("Bank Information");
        textLabelTitle.setFont(Font.font("Arial", 18));
        HBox hBoxTitle = new HBox(textLabelTitle);
        hBoxTitle.setPadding(new Insets(10, 10, 10, 10));
        hBoxTitle.setSpacing(10);
        hBoxTitle.setAlignment(Pos.TOP_CENTER);

        // VBox to hold contents for inputs
        VBox vBoxForInputs = new VBox();
        // Contents of VBox
        Label textLabelBankName = new Label("bank name: ");
        HBox hBoxBankName = new HBox(textLabelBankName, textFieldBankName);
        hBoxBankName.setSpacing(10);
        hBoxBankName.setAlignment(Pos.CENTER_LEFT);
        Label textLabelBankHost = new Label("host: ");
        HBox hBoxBankHost = new HBox(textLabelBankHost, textFieldBankHost);
        hBoxBankHost.setSpacing(10);
        hBoxBankHost.setAlignment(Pos.CENTER_LEFT);
        Label textLabelBankPort = new Label("port: ");
        HBox hBoxBankPort = new HBox(textLabelBankPort, textFieldBankPort);
        hBoxBankPort.setSpacing(10);
        hBoxBankPort.setAlignment(Pos.CENTER_LEFT);

        vBoxForInputs.getChildren().addAll(
            hBoxBankName, hBoxBankHost, hBoxBankPort);
        vBoxForInputs.setPadding(new Insets(10, 10, 10, 10));
        vBoxForInputs.setSpacing(10);

        hBoxForBottomPanel = new HBox();
        hBoxForBottomPanel.setPadding(new Insets(10, 10, 10, 10));
        hBoxForBottomPanel.setSpacing(10);
        hBoxForBottomPanel.setAlignment(Pos.CENTER);

        // borderPane to hold entire contents
        borderPane01 = new BorderPane();

        // put title in top
        borderPane01.setTop(hBoxTitle);
        // put info displays in left
        borderPane01.setCenter(vBoxForInputs);
        // put controls in bottom
        borderPane01.setBottom(hBoxForBottomPanel);

        stage01.setScene(new Scene(borderPane01));
        stage01.show();
    }

    public void openStage02 () {
        System.out.println("Successful Setup");

        stage01.close();

        stage02 = new Stage();

        initializeDisplay02();
    }

    private void initializeDisplay02() {
        stage02.setMinWidth(400);
        stage02.setMinHeight(400);
        // stage02.setMaxWidth(400);
        stage02.setMaxHeight(400);

        // VBox to hold contents for left side
        VBox vBox = new VBox();
        // Contents for left side
        Label textLabelBankName = new Label("bank name: ");
        HBox hBoxBankName = new HBox(textLabelBankName, textFieldBankName);
        hBoxBankName.setSpacing(10);
        hBoxBankName.setAlignment(Pos.CENTER_LEFT);
        Label textLabelBankHost = new Label("host: ");
        HBox hBoxBankHost = new HBox(textLabelBankHost, textFieldBankHost);
        hBoxBankHost.setSpacing(10);
        hBoxBankHost.setAlignment(Pos.CENTER_LEFT);
        Label textLabelBankPort = new Label("port: ");
        HBox hBoxBankPort = new HBox(textLabelBankPort, textFieldBankPort);
        hBoxBankPort.setSpacing(10);
        hBoxBankPort.setAlignment(Pos.CENTER_LEFT);
        Label textLabelActiveAccts = new Label("# Active Accts: ");
        HBox hBoxActiveAccts =
            new HBox(textLabelActiveAccts, textLabelNumberOfActiveAccounts);
        hBoxActiveAccts.setSpacing(10);
        hBoxActiveAccts.setAlignment(Pos.CENTER_LEFT);

        Label textLabelAgentAccts = new Label("# Agent Accts: ");
        HBox hBoxAgentAccts =
            new HBox(textLabelAgentAccts, textLabelNumberOfAgentAccounts);
        hBoxAgentAccts.setSpacing(10);
        hBoxAgentAccts.setAlignment(Pos.CENTER_LEFT);

        Label textLabelAHAccts = new Label("# Auction House Accts: ");
        HBox hBoxAHAccts =
            new HBox(textLabelAHAccts, textLabelNumberOfAHAccounts);
        hBoxAHAccts.setSpacing(10);
        hBoxAHAccts.setAlignment(Pos.CENTER_LEFT);

        vBox.getChildren().addAll(
            hBoxBankName, hBoxBankHost, hBoxBankPort,
            hBoxActiveAccts, hBoxAgentAccts, hBoxAHAccts);
        vBox.setPadding(new Insets(10, 10, 10, 10));
        vBox.setSpacing(10);

        // HBox to hold contents for bottom panel
        HBox hBoxForBottomPanel = new HBox();
        // Contents for bottom panel
        // no contents right now
        hBoxForBottomPanel.setPadding(new Insets(10, 10, 10, 10));
        hBoxForBottomPanel.setSpacing(10);


        // borderPane to hold entire contents
        borderPane = new BorderPane();

        // put info displays in left
        borderPane.setLeft(vBox);
        // put controls in bottom
        borderPane.setBottom(hBoxForBottomPanel);
        // put text area in center for output
        borderPane.setCenter(textAreaOutput);

        stage02.setScene(new Scene(borderPane));
        stage02.show();

    }

    public String getBankName() {
        return textFieldBankName.getText().trim();
    }

    public String getBankHostName() {
        return textFieldBankHost.getText().trim();
    }

    public int getBankPort() {
        return Integer.parseInt(textFieldBankPort.getText());
    }

    public boolean infoFilledOut () {
        boolean infoFilledOut = true;

        if( textFieldBankName.getText().trim().equals("" ) ||
            textFieldBankHost.getText().trim().equals("") ||
            textFieldBankPort.getText().trim().equals("") ){

            System.out.println("PLEASE CHANGE ALL FIELDS");
            infoFilledOut = false;
        }

        try{
           Integer.parseInt(textFieldBankPort.getText().trim());
        }catch(NumberFormatException e){
            System.out.println("NUMBER EXPECTED FOR PORT NUMBER FIELDS");
            infoFilledOut = false;
        }

        return infoFilledOut;
    }

    public void displayErrorMessage(){
        System.out.println("TEST");
    }

    public void updateTextAreaOutput(
        HashMap<Integer, BankAccount> bankAccounts){

        Set<Integer> accountNumbers = bankAccounts.keySet();
        String output = "Account(s) Summary:";
        for ( int acctNum : accountNumbers ) {
            BankAccount tempBankAccount = bankAccounts.get(acctNum);
            output = output + "\nAcct #" + acctNum + "(" +
                     tempBankAccount.getAccountType().toString() + ") $" +
                     tempBankAccount.getTotalBalance();
        }

        textAreaOutput.setText(output);
    }

    /**
     * Sets up the EventHandlers for various buttons, using a switch()
     * on the original button text string
     *
     * @param button
     */
    private void setButtonHandlers(Button button) {

        String buttonText = button.getText();

        switch (buttonText) {

            case "Go Online":
                button.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        System.out.println("Establishing Bank");
                    }
                });
                break;

            default:
                break;

        } // end switch()

    } // end setButtonHandlers()


    public void setupBankInitializeButton(Button btnCreateBank){
        this.btnCreateBank = btnCreateBank;
        hBoxForBottomPanel.getChildren().add(btnCreateBank);
    }

    public void updateNumberOfAccounts (int numOfAccts) {
        textLabelNumberOfActiveAccounts.setText("" + numOfAccts);
    }
    public void updateNumberOfAgentAccounts (int numOfAccts) {
        textLabelNumberOfAgentAccounts.setText("" + numOfAccts);
    }
    public void updateNumberOfAHAccounts (int numOfAccts) {
        textLabelNumberOfAHAccounts.setText("" + numOfAccts);
    }

    public void updateTextAreaOutput (String theString) {
        textAreaOutput.setText(theString);
    }

}
