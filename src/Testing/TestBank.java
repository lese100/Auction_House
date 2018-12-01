package Testing;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A preliminary Bank-like class for experimenting with a Bank, Bank Accounts,
 * and an Agent Proxy.
 * created: 11/18/18 by wdc
 * last modified: 11/18/18 by wdc
 * @author Warren D. Craft (wdc)
 * @author Tyler Fenske (thf)
 * @author Liam Brady (lb)
 */
public class TestBank {

    private String locationID;
    private int operatingPort = 1234;
    private ArrayList<TestBankAccount> accounts;
    private ArrayList<String> auctionHouseNames;
    private HashMap<String, Integer> auctionHouseAccountNumbers;
    private HashMap<Long, TestBankAccount> secretNumbersToAccounts;

    // ****************************** //
    //   Constructor(s)               //
    // ****************************** //

    /**
     * Public constructor for a TestBank, a Bank-like object for experimenting
     * with Bank, Bank Accounts, and Agent Proxies.
     * @param locationID String giving computer location for TestBank thread
     * @param operatingPort int used for the TestBank's default port
     */
    public TestBank(String locationID, int operatingPort) {
        this.operatingPort = operatingPort;
        this.locationID = locationID;
        this.accounts = new ArrayList<>();
        this.auctionHouseNames = new ArrayList<>();
        this.auctionHouseAccountNumbers = new HashMap<>();
        this.secretNumbersToAccounts = new HashMap<>();
    }

    // ****************************** //
    //   Getter(s) & Setter(s)        //
    // ****************************** //

    // ****************************** //
    //   Public Methods               //
    // ****************************** //

    /**
     * Provides a means for blocking or holding funds in an account, if one
     * has a valid secret number that links to a valid account.
     * @param secretNumber long number that links to a valid bank account
     * @param amountToBlock double the amount to block or hold in the account
     * @return boolean true if the hold was successful; false if not
     */
    public synchronized boolean blockFunds (long secretNumber, double amountToBlock) {
        TestBankAccount theAccount = secretNumbersToAccounts.get(secretNumber);
        if (theAccount != null) {
            if (theAccount.getTotalUnblocked() > amountToBlock ) {
                // block the amount
                // all or some portion of this process will need synchronization
                // what happens if two block requests come in about same time
                // and are interleaved?
                theAccount.increaseBlock(amountToBlock);
                return true;
            }
            System.out.println("TestBank, blockFunds(): Insufficient funds.");
            return false; // not enough funds to block
        }
        System.out.println("TestBank, blockFunds(): No account found.");
        return false;     // no such secret number; no such account
    }

    // ****************************** //
    //   Utility Fxns                 //
    // ****************************** //



}
