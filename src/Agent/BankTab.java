package Agent;

import javafx.scene.control.Tab;

public class BankTab {
    private Tab bankTab;

    /**
     * sets up the bank display
     */
    public BankTab(){
        bankTab = new Tab();
        bankTab.setText("Bank");
        bankTab.setClosable(false);
    }

    /**
     * gets the tab so that display can add it to its list of tabs
     * @return the tab
     */
    public Tab getBankTab() {
        return bankTab;
    }
}
