package Bank;

import Utility.BankAccount;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.math.RoundingMode;
import java.text.NumberFormat;

/**
 * Provides a 2-phase GUI display for a Bank.
 * (1) Phase 1 is a window in which the user can specify info about the
 * Bank (name, machine location, port number) and use that info to
 * construct a Bank; (2) Phase 2 is then a final window that represents the
 * established, working bank and displays information about the Bank and its
 * operations and accounts.
 * created: 12/02/2018 by wdc
 * last modified: 12/06/2018 by wdc
 * @author Liam Brady (lb)
 * @author Warren D. Craft
 * @author Tyler Fenske (thf)
 */
public class BankDisplay {

    private Stage stage01;
    private Stage stage02;

    private BorderPane borderPane;
    private BorderPane borderPane01;

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
    private TableView theTable = new TableView();

    // an observable list to hold data from the Bank and
    // used in displaying Bank's accounts information
    private ObservableList<BankAccount> accountData =
        FXCollections.observableArrayList();

    // ****************************** //
    //   Constructor(s)               //
    // ****************************** //

    /**
     * Public constructor for the initial GUI/display associated with a Bank,
     * which provides a simple interactive window that accepts info
     * (name, hostname, and port number) for constructing a Bank. The
     * constructing entity that call this constructor is also expected to
     * place an action button in the initial GUI, to accept the information
     * and use the BankDisplay methods to construct the final GUI that
     * exhibits Bank-related information.
     * @param theStage A JavaFX Stage object, passed from the
     *                 constructing entity.
     */
    public BankDisplay(Stage theStage){

        this.stage01 = theStage;
        initializeDisplay01();
    }

    // ****************************** //
    //   Public Methods               //
    //   (alphabetical order)         //
    // ****************************** //


    /**
     * Displays an error message pop up with the passed String.
     * @param errorMessage to be displayed
     */
    public void displayErrorMessage(String errorMessage){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(errorMessage);
        alert.show();
    }

    /**
     * Gets the bank name text entered in the Phase 1 GUI.
     * @return String trimmed version of default or user-entered bank name
     */
    public String getBankName() {
        return textFieldBankName.getText().trim();
    }

    /**
     * Gets the bank hostname text entered in the Phase 1 GUI.
     * @return String trimmed version of default or user-entered hostname
     */
    public String getBankHostName() {
        return textFieldBankHost.getText().trim();
    }

    /**
     * Gets the bank port number entered in the Phase 1 GUI.
     * @return Int trimmed version of default or user-entered port number
     */
    public int getBankPort() {
        return Integer.parseInt(textFieldBankPort.getText());
    }

    /**
     * Checks if text fields of Phase 1 GUI have minimally-valid info,
     * returning true if so, false otherwise.
     * @return boolean true if Phase 1 GUI text fields are all non-empty,
     *                 and port number appears to be an integer;
     *                 false otherwise
     */
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

    /**
     * Closes the Phase 1 GUI and sets up the Phase 2 GUI
     */
    public void openStage02 () {
        stage01.close();

        // The Stage stage02 is initialized from stage01
        // using setupBankGUIComponents() defined further below

        // Then set up details for stage02
        initializeDisplay02();
    }


    /**
     * Sets up a control button within the Phase 1 GUI and initiates a given
     * JavaFX stage to be the display's stage02 (used for Phase 2 GUI).
     * The public method allows an external entity to create the button
     * and stage, perhaps linking them with important event handlers, and
     * then hand them to the display.
     * @param btnCreateBank JavaFX Button
     * @param stage02 JavaFX Stage
     */
    public void setupBankGUIComponents(Button btnCreateBank, Stage stage02){
        this.btnCreateBank = btnCreateBank;
        this.stage02 = stage02;
        hBoxForBottomPanel.getChildren().add(btnCreateBank);
    }

    /**
     * Updates the textfield representing the number of currently-active/open
     * Bank Accounts at the Bank.
     * @param numOfAccts int number of currently open bank accounts
     */
    public void updateNumberOfAccounts (int numOfAccts) {
        textLabelNumberOfActiveAccounts.setText("" + numOfAccts);
    }

    /**
     * Updates the textfield representing the number of currently-active/open
     * AGENT-type Bank Accounts at the Bank.
     * @param numOfAccts int number of currently open bank accounts of
     *                   type AGENT
     */
    public void updateNumberOfAgentAccounts (int numOfAccts) {
        textLabelNumberOfAgentAccounts.setText("" + numOfAccts);
    }

    /**
     * Updates the textfield representing the number of currently-active/open
     * AUCTION_HOUSE-type Bank Accounts at the Bank.
     * @param numOfAccts int number of currently open bank accounts of
     *                   type AUCTION_HOUSE
     */
    public void updateNumberOfAHAccounts (int numOfAccts) {
        textLabelNumberOfAHAccounts.setText("" + numOfAccts);
    }

    /**
     * Updates the ObservableList accountData with given ObservableList.
     * Typically called from the Bank, which passes the new ObservableList
     * when Bank Account information has changed.
     * @param listOfBankAccounts An ObservableList of BankAccount objects
     */
    public void updateAccountData (ObservableList listOfBankAccounts) {

        accountData.setAll(listOfBankAccounts);
        theTable.sort();
        // this should then automatically update the table theTable
        // populating it with most recent info, and sorting it by
        // account type (AGENT vs. AUCTION_HOUSE)
    }

    // ****************************** //
    //   Private Utility Fxns         //
    //   (alphabetical order)         //
    // ****************************** //

    /**
     * Sets up the initial Phase 1 GUI for accepting initial information
     * used to create a Bank. Called from the BankDisplay constructor.
     */
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

        // VBox for Input fields and related labels
        VBox vBoxForInputs = new VBox();
        // Contents of VBox: Input Fields and related Labels
        Label textLabelBankNameTitle = new Label("bank name: ");
        HBox hBoxBankName = new HBox(textLabelBankNameTitle, textFieldBankName);
        hBoxBankName.setSpacing(10);
        hBoxBankName.setAlignment(Pos.CENTER_LEFT);
        Label textLabelBankHostTitle = new Label("host: ");
        HBox hBoxBankHost = new HBox(textLabelBankHostTitle, textFieldBankHost);
        hBoxBankHost.setSpacing(10);
        hBoxBankHost.setAlignment(Pos.CENTER_LEFT);
        Label textLabelBankPortTitle = new Label("port: ");
        HBox hBoxBankPort = new HBox(textLabelBankPortTitle, textFieldBankPort);
        hBoxBankPort.setSpacing(10);
        hBoxBankPort.setAlignment(Pos.CENTER_LEFT);

        vBoxForInputs.getChildren().addAll(
            hBoxBankName, hBoxBankHost, hBoxBankPort);
        vBoxForInputs.setPadding(new Insets(10, 10, 10, 10));
        vBoxForInputs.setSpacing(10);

        // HBox for holding control(s) along the bottom of the GUI
        // made modifiable from outside the BankDisplay class
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

    /**
     * Rather long procedure setting up the Phase 2 GUI for a Bank.
     * The Phase 2 GUI is the "final" window in which the Bank is up and
     * running and displaying information about itself and the client
     * Bank Accounts. The length is exacerbated considerably by the work
     * necessary for setting up and maintaining the TableView object for
     * displaying the Bank Account information.
     */
    private void initializeDisplay02() {

        stage02.setMinWidth(500);
        stage02.setMinHeight(400);
        stage02.setMaxHeight(400);

        // ---------------------------------------//
        // Contents of LEFT panel                 //
        // ---------------------------------------//

        // VBox to hold entire contents for left side
        VBox vBoxOuter = new VBox();
        // VBox to hold stats info within left side
        VBox vBoxInner = new VBox();
        // Contents for left side
        Label textLabelBankName = new Label(textFieldBankName.getText());
        textLabelBankName.setFont(new Font("Georgia", 24));
        HBox hBoxBankName = new HBox(textLabelBankName);
        hBoxBankName.setSpacing(10);
        hBoxBankName.setAlignment(Pos.CENTER);
        Label textLabelBankHostTitle = new Label("host: ");
        Label textLabelBankHost = new Label(textFieldBankHost.getText());
        HBox hBoxBankHost = new HBox(textLabelBankHostTitle, textLabelBankHost);
        hBoxBankHost.setSpacing(10);
        hBoxBankHost.setAlignment(Pos.CENTER_LEFT);
        Label textLabelBankPortTitle = new Label("port: ");
        Label textLabelBankPort = new Label(textFieldBankPort.getText());
        HBox hBoxBankPort = new HBox(textLabelBankPortTitle, textLabelBankPort);
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

        vBoxInner.getChildren().addAll(
            hBoxBankHost, hBoxBankPort,
            hBoxActiveAccts, hBoxAgentAccts, hBoxAHAccts);
        vBoxInner.setPadding(new Insets(10, 10, 10, 10));
        vBoxInner.setSpacing(10);
        String cssLayout = "-fx-border-color: deepskyblue;\n" +
            "-fx-border-insets: 10;\n" +
            "-fx-border-width: 2;\n" +
            "-fx-border-style: solid;\n";
        vBoxInner.setStyle(cssLayout);
        vBoxOuter.getChildren().addAll(hBoxBankName, vBoxInner);
        vBoxOuter.setSpacing(10);
        vBoxOuter.setPadding(new Insets(10, 10, 10, 10));

        // ---------------------------------------//
        // Contents of BOTTOM panel               //
        // ---------------------------------------//

        // HBox to hold contents for bottom panel
        // no contents right now — just available for an external entity
        // to insert control(s)
        HBox hBoxForBottomPanel = new HBox();
        hBoxForBottomPanel.setPadding(new Insets(10, 10, 10, 10));
        hBoxForBottomPanel.setSpacing(10);

        // ---------------------------------------//
        // Contents of CENTER panel               //
        // which includes a TableView object for  //
        // for display BankAccount information    //
        // ---------------------------------------//

        // VBox to hold contents of center panel
        VBox vBoxForCenterPanel = new VBox();
        vBoxForCenterPanel.setPadding(new Insets(10, 10, 10, 10));
        vBoxForCenterPanel.setSpacing(20);
        vBoxForCenterPanel.setAlignment(Pos.CENTER);

        // Header Label for top of Table
        Label textLabelTableHeader = new Label("Summary of Current Accounts");
        textLabelTableHeader.setFont(new Font("Georgia", 24));
        textLabelTableHeader.setTextAlignment(TextAlignment.CENTER);

        // Establishing columns to be used in the Table
        TableColumn colAcctNum = new TableColumn("Acct #");
        colAcctNum.setMinWidth(50);
        colAcctNum.setCellValueFactory(
            new PropertyValueFactory<BankAccount, Integer>("accountNumber"));
        TableColumn colAcctType = new TableColumn("Acct Type");
        colAcctType.setMinWidth(125);
        colAcctType.setCellValueFactory(
            new PropertyValueFactory<BankAccount, BankAccount.AccountType>
                ("accountType")
        );
        TableColumn colBalance = new TableColumn("Balance");
        colBalance.setMinWidth(75);
        colBalance.setCellValueFactory(
            new PropertyValueFactory<BankAccount, Double>("totalBalance")
        );
        TableColumn colFrozen = new TableColumn("Frozen");
        colFrozen.setMinWidth(75);
        colFrozen.setCellValueFactory(
            new PropertyValueFactory<BankAccount, Double>("totalFrozen")
        );
        TableColumn colAvailable = new TableColumn("Available");
        colAvailable.setMinWidth(75);
        colAvailable.setCellValueFactory(
            new PropertyValueFactory<BankAccount, Double>("totalUnfrozen")
        );
        TableColumn colClientName = new TableColumn("Client Name");
        colClientName.setMinWidth(125);
        colClientName.setCellValueFactory(
            new PropertyValueFactory<BankAccount, String>("userName")
        );

        // link data and columns to the Table and set some
        // table characteristics
        theTable.setItems(accountData);
        theTable.getColumns().addAll(
            colAcctNum, colAcctType, colBalance,
            colFrozen, colAvailable, colClientName
        );
        theTable.getSortOrder().addAll(colAcctType, colAcctNum);
        Label placeHolderLabel = new Label("No Active Accounts");
        placeHolderLabel.setFont(new Font("Georgia", 24));
        theTable.setPlaceholder(placeHolderLabel);

        vBoxForCenterPanel.getChildren().addAll(
            textLabelTableHeader, theTable);

        // -----------------------------------------//
        // Some excruciating code here to control   //
        // display format of the numerical columns  //
        // Code borrowed from:                      //
        // http://simsam7.blogspot.com/2013/07/     //
        // better-javafx-table-example-with.html    //
        // -----------------------------------------//

        colAcctNum.setCellFactory(new Callback<TableColumn, TableCell>() {
            public TableCell call(TableColumn p) {
                TableCell cell = new TableCell<BankAccount, Integer>() {
                    @Override
                    public void updateItem(Integer item, boolean empty) {
                        super.updateItem(item, empty);
                        setText(empty ? null : getString());
                        setGraphic(null);
                    }

                    private String getString() {
                        String ret = "";
                        if (getItem() != null) {
                            ret = getItem().toString();
                        } else {
                            ret = "0";
                        }
                        return ret;
                    }
                };

                cell.setStyle("-fx-alignment: top-right;");
                return cell;
            }
        });

        colBalance.setCellFactory(new Callback<TableColumn, TableCell>() {
            @Override
            public TableCell call(TableColumn param) {
                TableCell cell = new TableCell<BankAccount, Double>() {
                    @Override
                    public void updateItem(Double item, boolean empty) {
                        super.updateItem(item, empty);
                        setText(empty ? null : getString());
                        setGraphic(null);
                    }
                    private String getString() {
                        String ret = "";
                        if (getItem() != null) {
                            String gi = getItem().toString();
                            // NumberFormat df = DecimalFormat.getInstance();
                            NumberFormat df =
                                NumberFormat.getCurrencyInstance();
                            df.setMinimumFractionDigits(2);
                            df.setRoundingMode(RoundingMode.HALF_UP);
                            ret = df.format(Double.parseDouble(gi));

                        } else {
                            ret = "0.00";
                        }
                        return ret;
                    }
                };
                cell.setStyle("-fx-alignment: top-right;");
                return cell;
            }
        });

        colFrozen.setCellFactory(new Callback<TableColumn, TableCell>() {
            @Override
            public TableCell call(TableColumn param) {
                TableCell cell = new TableCell<BankAccount, Double>() {
                    @Override
                    public void updateItem(Double item, boolean empty) {
                        super.updateItem(item, empty);
                        setText(empty ? null : getString());
                        setGraphic(null);
                    }
                    private String getString() {
                        String ret = "";
                        if (getItem() != null) {
                            String gi = getItem().toString();
                            // NumberFormat df = DecimalFormat.getInstance();
                            NumberFormat df =
                                NumberFormat.getCurrencyInstance();
                            df.setMinimumFractionDigits(2);
                            df.setRoundingMode(RoundingMode.HALF_UP);
                            ret = df.format(Double.parseDouble(gi));

                        } else {
                            ret = "0.00";
                        }
                        return ret;
                    }
                };
                cell.setStyle("-fx-alignment: top-right;");
                return cell;
            }
        });

        colAvailable.setCellFactory(new Callback<TableColumn, TableCell>() {
            @Override
            public TableCell call(TableColumn param) {
                TableCell cell = new TableCell<BankAccount, Double>() {
                    @Override
                    public void updateItem(Double item, boolean empty) {
                        super.updateItem(item, empty);
                        setText(empty ? null : getString());
                        setGraphic(null);
                    }
                    private String getString() {
                        String ret = "";
                        if (getItem() != null) {
                            String gi = getItem().toString();
                            // NumberFormat df = DecimalFormat.getInstance();
                            NumberFormat df =
                                NumberFormat.getCurrencyInstance();
                            df.setMinimumFractionDigits(2);
                            df.setRoundingMode(RoundingMode.HALF_UP);
                            ret = df.format(Double.parseDouble(gi));

                        } else {
                            ret = "0.00";
                        }
                        return ret;
                    }
                };
                cell.setStyle("-fx-alignment: top-right;");
                return cell;
            }
        });

        // -----------------------------------------//
        // Establish BorderPane and fill in panels  //
        // -----------------------------------------//

        // borderPane to hold entire contents
        borderPane = new BorderPane();

        // put info displays in left
        borderPane.setLeft(vBoxOuter);
        // put controls in bottom
        borderPane.setBottom(hBoxForBottomPanel);
        // put Table in center for dynamic output
        borderPane.setCenter(vBoxForCenterPanel);

        stage02.setScene(new Scene(borderPane));
        stage02.show();

    }


}
