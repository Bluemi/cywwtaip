package manage;

import bots.Bot;
import lenz.htw.cywwtaip.world.GraphNode;

public class GameManager {

    private RateSystem ratinng;
    private Bot[] bots;

    private final short BOTINDEX_NOMAL = 0;
    private final short BOTINDEX_MOBILE = 1;
    private final short BOTINDEX_WIDE = 2;

    public GameManager(Bot[] bots){
        this.bots = bots;
        this.ratinng = new RateSystem();
    }

    private void updateStrategy(){
        // redefines a bot's behavior
    }

    private boolean checkCurrentGameState(){
        // returns true if a bot need to change if rating.
        return false;
    }
    

    private GraphNode checkForCluster(){
     return null;
    }

}
