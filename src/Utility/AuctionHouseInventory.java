package Utility;

import java.io.Serializable;
import java.util.List;

public class AuctionHouseInventory implements Serializable{

    private int accountNumber;
    private List<AuctionItem> auctions;

    public AuctionHouseInventory(int accountNumber, List<AuctionItem> auctions){
        this.accountNumber = accountNumber;
        this.auctions = auctions;
    }

    public int getAccountNumber() {
        return accountNumber;
    }

    public List<AuctionItem> getAuctions() {
        return auctions;
    }
}
