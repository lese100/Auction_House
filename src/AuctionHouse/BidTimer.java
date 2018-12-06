package AuctionHouse;

import Utility.AuctionItem;
import Utility.Bid;
import javafx.application.Platform;
import java.io.Serializable;

/**
 * A runnable Timer class, used for measuring how long it has been since an
 * agent has bid on an AuctionItem of interest. BidTimers are contained inside
 * of an AuctionItem, and are started/restarted by an AuctionHouse.
 * created: 12/1/18 by thf
 * last modified: 12/02/18 by thf
 * @author Liam Brady (lb)
 * @author Warren D Craft (wdc)
 * @author Tyler Fenske (thf)*/
public class BidTimer implements Runnable, Serializable {

    private final long TIME;
    private AgentProxy ap;
    private AuctionItem ai;
    private boolean stillValid;
    private AuctionHouse ah;

    // ****************************** //
    //   Constructor(s)               //
    // ****************************** //

    /**
     * Constructor for a BidTimer. Bid timers are initialized with a long time,
     * which decides how long the timer will last, and uses references of the
     * related AgentProxy, AuctionItem, and AuctionHouse to notify the
     * appropriate parties of the timer finishing.
     * @param time How long the BidTimer will last for
     * @param ap The AgentProxy reference of the agent who bid on the item
     * @param ai The AuctionItem the agent bid on
     * @param ah The AuctionHouse the item that was bid on is in
     */
    public BidTimer(long time, AgentProxy ap, AuctionItem ai, AuctionHouse ah){
        this.TIME = time;
        this.ap = ap;
        this.ai = ai;
        this.ah = ah;
        stillValid = true;
    }
    // ****************************** //
    //   Public Methods               //
    // ****************************** //

    /**
     * If this method is called, when the timer is finished, it will no
     * longer notify anyone, and will die in silence.
     */
    public void cancelTimer(){
        stillValid = false;
    }

    // ****************************** //
    //   Override Methods             //
    // ****************************** //

    /**
     * Called when the thread is started. After TIME seconds, if the
     * cancelTimer() method was never called, the agent will be notified
     * of winning the bid, and the AuctionHouse GUI will be updated.
     */
    @Override
    public void run() {
        try{
            Thread.sleep(TIME);
            if(stillValid){
                ap.notifyWinner(ai);
                ai.getBid().setBidState(Bid.BidState.SOLD);
                ah.updateDisplay();
                ah.updateAgentsAboutChanges();
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        ah.updateAmountOwed(ai.getBid().getCurrentBid());
                    }
                });
            }
        }catch(InterruptedException e){
            e.printStackTrace();
        }
    }
}
