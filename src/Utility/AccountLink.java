package Utility;

import java.io.Serializable;

/**
 * Provides a simple structure for linking two integer account numbers,
 * originally intended to allow the linking of an Agent's Bank account number
 * to an Auction House's Bank account number when the Agent is doing business
 * with the Auction House, but this could be used to link any two associated
 * integer values. This simple structure is useful when needing a pair of
 * accounts to both be values for an associated key value in a HashMap.
 * created: 11/29/18 by wdc
 * last modified: 12/02/18 by wdc (making Serializable)
 * @author Liam Brady (lb)
 * @author Warren D. Craft
 * @author Tyler Fenske (thf)
 */
public class AccountLink implements Serializable {

    private final int AGENT_ACCOUNT_NUMBER;
    private final int AH_ACCOUNT_NUMBER;

    // ****************************** //
    //   Constructor(s)               //
    // ****************************** //

    /**
     * Public constructor for an AccountLink, which is a simple structure
     * linking or grouping two integer values (originally conceived as two
     * integer account numbers, for example at a Bank).
     * @param account01
     * @param account02
     */
    public AccountLink(int account01, int account02 ) {
        AGENT_ACCOUNT_NUMBER = account01;
        AH_ACCOUNT_NUMBER = account02;
    }

    // ****************************** //
    //   Getter(s) & Setter(s)        //
    // ****************************** //

    /**
     * Returns the first of two associated integers (originally conceived as
     * an Agent's integer Bank account number associated with an Auction
     * House's integer Bank account number).
     * @return int first of a pair of associated integer values
     */
    public int getAGENT_ACCOUNT_NUMBER() {
        return AGENT_ACCOUNT_NUMBER;
    }

    /**
     * Returns the second of two associated integers (originally conceived as
     * an Auction House's integer Bank account number associated with an
     * Agent's integer Bank account number).
     * @return int 2nd of a pair of associated integer values
     */
    public int getAH_ACCOUNT_NUMBER() {
        return AH_ACCOUNT_NUMBER;
    }


}
