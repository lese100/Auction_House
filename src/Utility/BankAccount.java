package Utility;

import java.io.Serializable;

/**
 * Provides the structure for account information for a client of a Bank.
 * A BankAccount is conceived generally to be held or stored by a Bank, but
 * can also be communicated to a remote client of the Bank to update the
 * client with current account information, hence we also make the class
 * implement Serializable. Methods to check and modify balance, frozen funds,
 * and unfrozen funds are all synchronized to prevent multithread-based
 * interference errors (e.g. to avoid two threads "freezing" the same funds).
 * created: 11/18/18 by wdc
 * last modified: 11/29/18 by wdc (udpating to Utility package)
 * previously modified: 11/18/18 by wdc (creation)
 * @author Liam Brady
 * @author Warren D. Craft (wdc)
 * @author Tyler Fenske (thf)
 */
public class BankAccount implements Serializable {

    private AccountType accountType;
    public enum AccountType { AGENT, AUCTION_HOUSE }
    private String userName;
    private int accountNumber;
    private double totalBalance;
    private double totalFrozen = 0.0;
    private double totalUnfrozen;

    // ****************************** //
    //   Constructor(s)               //
    // ****************************** //

    /**
     * Public constructor for a BankAccount, typically kept by a Bank.
     * @param accountType AccountType enum AGENT or AUCTION_HOUSE
     * @param userName String id for account, e.g. "Bob Smith" or "JJ Auctions"
     * @param accountNumber int with no particular internal constraints
     * @param initialBalance double initial balance
     */
    public BankAccount(AccountType accountType,
                       String userName,
                       int accountNumber,
                       double initialBalance) {

        this.accountType = accountType;
        this.userName = userName;
        this.accountNumber = accountNumber;
        this.totalBalance = totalBalance;
        this.totalUnfrozen = totalBalance;
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
     * Returns username associated with the BankAccount
     * @return String account username
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Returns account number associated with the BankAccount
     * @return int account number
     */
    public int getAccountNumber() {
        return accountNumber;
    }

    /**
     * Returns total balance for the account, the sum of both frozen
     * and unfrozen funds.
     * @return double total balance = frozen funds + unfrozen funds
     */
    public synchronized double getTotalBalance() {
        return totalBalance;
    }

    /**
     * Returns the total frozen funds -- i.e. funds that have been at least
     * temporarily committed through a bidding or purchase process
     * @return double funds currently in account but frozen or on hold
     */
    public synchronized double getTotalFrozen() {
        return totalFrozen;
    }

    /**
     * Returns the total unfrozen funds -- i.e. funds still available for
     * use that haven't been frozen, put on hold, or otherwise previously
     * committed
     * @return double unfrozen funds = total balance - frozen funds
     */
    public synchronized double getTotalUnfrozen() {
        return totalUnfrozen;
    }

    // ****************************** //
    //   Public Methods               //
    // ****************************** //

    /**
     * Increases balance by balanceIncrease amount in $s. Increases both
     * the total balance and the unfrozen funds.
     * @param balanceIncrease double $ to add to total account balance
     */
    public synchronized void increaseTotalBalance (double balanceIncrease) {
        totalBalance += balanceIncrease;
        totalUnfrozen += balanceIncrease;
    }

    /**
     * Increases the portion of account's total funds that are frozen (i.e.
     * committed for some purpose, such as the bidding process). Can only
     * increase the frozen funds up to the total balance available in the
     * account. If freeze increase exceeds unfrozen available, entire freeze
     * request fails and method returns false.
     * @param freezeIncrease double $ to add to total of frozen funds
     * @return true if increase successful; false if not possible
     */
    public synchronized boolean increaseFreeze(double freezeIncrease) {
        if ( totalUnfrozen >= freezeIncrease ) {
            totalFrozen += freezeIncrease;
            totalUnfrozen = totalUnfrozen - freezeIncrease;
            // and does not change total balance
            return true;
        }
        return false;
    }

    /**
     * Decreases overall account total balance and the account's frozen
     * funds total by the given amount. This is intended to be used to effect
     * a transfer of frozen funds to another bank account. Can only decrease
     * if frozen funds are greater than or equal to requested decrease amount.
     * @param decrease double $ to decrease in account
     * @return true if decrease successful; false if not possible
     */
    public synchronized boolean decreaseFrozenAndBalance (double decrease) {
        // used when transferring previously blocked funds
        // over to an auction house
        if ( totalFrozen >= decrease ) {
            totalFrozen -= decrease;
            totalBalance -= decrease;
            // and does not change the unfrozen balance
            return true;
        }
        return false;
    }

    /**
     * Checks to see if account has sufficient unfrozen funds to cover an
     * amountToFreeze (e.g. like a "hold" on a credit card), and if so,
     * freezes those funds, adjusts the frozen and unfrozen values, and
     * returns true. If NSF for freezing, returns false.
     * @param amountToFreeze double amount requested to be frozen
     * @return Boolean true if freeze successful, false otherwise
     */
    public synchronized boolean checkAndFreeze (double amountToFreeze) {
        // used when verifying an account has sufficient funds to cover
        // an expenditure, and if it does, then freeze or hold those funds
        if ( totalUnfrozen >= amountToFreeze ) {
             totalFrozen += amountToFreeze;
             totalUnfrozen -= amountToFreeze;
             // and total balance does not change
            return true;
        }
        return false;
    }

    // ****************************** //
    //   Utility Fxns                 //
    // ****************************** //

}
