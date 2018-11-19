package Testing;

import java.util.ArrayList;

/**
 * Experimental class providing the structure for an agent account
 * to be held at/by a TestBank.
 * created: 11/18/18 by wdc
 * last modified: 11/18/18 by wdc
 * @author Warren D. Craft (wdc)
 * @author Tyler Fenske (thf)
 * @author Liam Brady
 */
public class TestBankAccount {

    private AccountType accountType;
    public enum AccountType { AGENT, AUCTION_HOUSE };
    private String userName;
    private int accountNumber;
    private double totalBalance;
    private double totalBlocked = 0.0;
    private double totalUnblocked;
    private ArrayList<Long> secretKeys;

    // ****************************** //
    //   Constructor(s)               //
    // ****************************** //

    /**
     * Public constructor for a TestBankAccount.
     * @param accountType AccountType enum AGENT or AUCTION_HOUSE
     * @param userName String id for account, e.g. "Bob Smith" or "JJ Auctions"
     * @param accountNumber int
     * @param totalBalance
     */
    public TestBankAccount(AccountType accountType,
                           String userName,
                           int accountNumber,
                           double totalBalance) {

        this.accountType = accountType;
        this.userName = userName;
        this.accountNumber = accountNumber;
        this.totalBalance = totalBalance;
        this.totalUnblocked = totalBalance;
    }

    // ****************************** //
    //   Getter(s) & Setter(s)        //
    // ****************************** //

    /**
     * Returns account type of the form AGENT vs. AUCTION_HOUSE
     * @return AccountType enum AGENT or AUCTION_HOUSE
     */
    public AccountType getAccountType() {
        return accountType;
    }

    /**
     * Returns total balance for the account, the sum of both blocked
     * and unblocked funds.
     * @return double total balance = blocked funds + unblocked funds
     */
    public double getTotalBalance() {
        return totalBalance;
    }

    /**
     * Returns the total blocked funds -- i.e. funds that have been at least
     * temporarily committed to the bidding process
     * @return double funds currently in account but blocked or on hold
     */
    public double getTotalBlocked() {
        return totalBlocked;
    }

    /**
     * Returns the total unblocked funds -- i.e. funds still available for
     * use that haven't been blocked, put on hold, or otherwise previously
     * committed
     * @return double unblocked funds = total balance - blocked funds
     */
    public double getTotalUnblocked() {
        return totalUnblocked;
    }

    // ****************************** //
    //   Public Methods               //
    // ****************************** //

    /**
     * Increases balance by balanceIncrease amount in $s. Increases both
     * the total balance and the unblocked funds.
     * @param balanceIncrease double $ to add to total account balance
     */
    public void increaseTotalBalance (double balanceIncrease) {
        totalBalance += balanceIncrease;
        totalUnblocked += balanceIncrease;
    }

    /**
     * Increases the portion of account's total funds that are blocked (i.e.
     * committed for some purpose, such as the bidding process). Can only
     * increase the blocked funds up to the total balance available in the
     * account.
     * @param blockIncrease double $ to add to total of blocked funds
     * @return true if increase successful; false if not possible
     */
    public boolean increaseBlock (double blockIncrease) {
        if ( totalUnblocked >= blockIncrease ) {
            totalBlocked += blockIncrease;
            totalUnblocked = totalUnblocked - blockIncrease;
            // and does not change total balance
            return true;
        }
        return false;
    }

    /**
     * Decreases overall account total balance and the account's blocked
     * funds total by the given amount. This is intended to be used to effect
     * a transfer of blocked funds to another bank account. Can only decrease
     * if blocked funds are greater than or equal to requested decrease amount.
     * @param decrease double $ to decrease in account
     * @return true if decrease successful; false if not possible
     */
    public boolean decreaseBlockAndBalance (double decrease) {
        // used when transferring previously blocked funds
        // over to an auction house
        if ( totalBlocked >= decrease ) {
            totalBlocked -= decrease;
            totalBalance -= decrease;
            return true;
        }
        return false;
    }

    // ****************************** //
    //   Utility Fxns                 //
    // ****************************** //

}
