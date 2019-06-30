package manage;

import bots.Bot;
import bots.behaviour.GotoNextSupplyBehaviour;
import bots.behaviour.PaintBehaviour;
import bots.behaviour.PaintOverBehaviour;
import lenz.htw.cywwtaip.world.GraphNode;

public class GameManager {

    enum BotTask{
        REPAINT, ENERGY, PASSIVE;

        int value;
    }

    private RateSystem evaluator;
    private Bot[] bots;

    private int[] scores;
    private int currentScore;

    private int playernumber;

    private final short BOTINDEX_NORMAL = 0;
    private final short BOTINDEX_MOBILE = 1;
    private final short BOTINDEX_WIDE = 2;

    private BotTask currentTaskNormal;
    private BotTask currentTaskMobile;
    private BotTask currentTaskWide;

    private float taskfitNormal;
    private float taskfitMobile;
    private float taskfitWide;

    private float normalBotSpeed;
    private float mobileBotSpeed;
    private float wideBotSpeed;

    private boolean improveStrategy;

    public GameManager(Bot[] bots, int playernumber){
        this.bots = bots;
        this.evaluator = new RateSystem();
        this.improveStrategy = false;
        this.playernumber = playernumber;

        initBotTasks();
    }

    public void coordinateBots() {


        improveStrategy = checkCurrentGameState();

        if (improveStrategy){
            updateStrategy();
        }
    }

    private void updateStrategy(){
        //TODO
    }

    private boolean checkCurrentGameState(){
        //TODO returns true if a bot need to change considering rating, current scores (+ ?).


        return false;
    }

    private void applyBotTask(BotTask task, Bot bot, int playernumber){
        switch(task){
            case ENERGY:
                bot.setBehaviour(new GotoNextSupplyBehaviour());
                break;
            case REPAINT:
                bot.setBehaviour(new PaintOverBehaviour(playernumber));
                break;
            case PASSIVE:
                bot.setBehaviour(new PaintBehaviour());
                break;
        }
    }

    public void updateGameState(int[] scores, float[] speeds){
        this.scores = scores;
        currentScore = scores[playernumber];

        this.normalBotSpeed = speeds[BOTINDEX_NORMAL];
        this.mobileBotSpeed = speeds[BOTINDEX_MOBILE];
        this.wideBotSpeed = speeds[BOTINDEX_WIDE];
    }

    private void initBotTasks(){
        bots[BOTINDEX_NORMAL].setBehaviour(new GotoNextSupplyBehaviour());
        currentTaskNormal = BotTask.ENERGY;
        taskfitNormal = 1.0f;

        bots[BOTINDEX_MOBILE].setBehaviour(new PaintBehaviour());
        currentTaskMobile = BotTask.PASSIVE;
        taskfitMobile = 1.0f;

        bots[BOTINDEX_WIDE].setBehaviour(new PaintBehaviour());
        currentTaskWide = BotTask.PASSIVE;
        taskfitWide = 1.0f;
    }

    private float getPointGainBalance(){
        return 0;
    }

    private GraphNode checkForCluster(){
        return null;
    }
}
