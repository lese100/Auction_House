package Utility;

import java.io.Serializable;
import java.util.List;

/**
 * Provides a simple structure for linking an AuctionHouse's set of
 * AuctionItems, and the AuctionHouse's account number.
 * created: 12/02/18 by thf
 * last modified: 12/02/18 by thf
 * @author Liam Brady (lb)
 * @author Warren D. Craft
 * @author Tyler Fenske (thf)
 */
public class AuctionHouseInventory implements Serializable{

    private int accountNumber;
    private List<AuctionItem> auctions;

    /**
     * Constructor for AuctionHouseInventory, linking a list of AuctionItems
     * and an AuctionHouses' account number.
     * @param accountNumber AuctionHouse's account number assigned by the bank
     * @param auctions List of AuctionItems in the AuctionHouse
     */
    public AuctionHouseInventory(int accountNumber, List<AuctionItem> auctions){
        this.accountNumber = accountNumber;
        this.auctions = auctions;
    }

    /**
     * Useful getter to retrieve AuctionHouse account number.
     * @return AuctionHouse account number
     */
    public int getAccountNumber() {
        return accountNumber;
    }

    /**
     * Useful getter to retrieve AuctionHouse list of AuctionItems.
     * @return List<AuctionItem> in the AuctionHouse
     */
    public List<AuctionItem> getAuctions() {
        return auctions;
    }
}
