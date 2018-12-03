package AuctionHouse;

import Utility.AuctionItem;
import Utility.Bid;

/**
 * A runnable Timer class, used for measuring how long it has been since an
 * agent has bid on an AuctionItem of interest. BidTimers are contained inside
 * of an AuctionItem, and are started/restarted by an AuctionHouse.
 * created: 12/1/18 by thf
 * last modified: 12/02/18 by thf
 * @author Liam Brady (lb)
 * @author Warren D Craft (wdc)
 * @author Tyler Fenske (thf)*/
public class BidTimer implements Runnable{

    private final long TIME;
    private AgentProxy ap;
    private AuctionItem ai;
    private boolean stillValid;
    private AuctionHouse ah;

    public BidTimer(long time, AgentProxy ap, AuctionItem ai, AuctionHouse ah){
        this.TIME = time;
        this.ap = ap;
        this.ai = ai;
        this.ah = ah;
        stillValid = true;
    }


    @Override
    public void run() {
        try{
            Thread.sleep(TIME);
            if(stillValid){
                ap.notifyWinner(ai);
                ai.getBid().setBidState(Bid.BidState.SOLD);
                ah.updateDisplay();
            }
        }catch(InterruptedException e){
            e.printStackTrace();
        }


    }

    public void cancelTimer(){
        stillValid = false;
    }


}
