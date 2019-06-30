import bots.Bot;
import bots.BotType;
import bots.behaviour.DriveToPointBehaviour;
import graphInformation.GraphInformation;
import lenz.htw.cywwtaip.net.NetworkClient;
import math.Vector3D;

public class Main {
    private class ClientRunner implements Runnable {
        NetworkClient client;
        String teamName;
        String winSlogan;
        int playerNumber;
        Bot[] bots;

        public ClientRunner(String teamName, String winSlogan) {
            this.teamName = teamName;
            this.winSlogan = winSlogan;
        }

        private void createBots() {
            this.bots = new Bot[] {
                    new Bot(
                            BotType.NORMAL,
                            client.getGraph()[0],
                            new DriveToPointBehaviour(GraphInformation.getRandomNode(client.getGraph())),
                            teamName
                    ),
                    new Bot(
                            BotType.MOBILE,
                            client.getGraph()[0],
                            new DriveToPointBehaviour(GraphInformation.getRandomNode(client.getGraph())),
                            teamName
                    ),
                    new Bot(
                            BotType.WIDE,
                            client.getGraph()[0],
                            new DriveToPointBehaviour(GraphInformation.getRandomNode(client.getGraph())),
                            teamName
                    )
            };
        }

        private void initializeBots() {
            for (int botIndex = 0; botIndex < 3; botIndex++) {
                Bot bot = bots[botIndex];
                bot.updatePosition(new Vector3D(client.getBotPosition(playerNumber, botIndex)));
                bot.updateDirection(new Vector3D(client.getBotDirection(botIndex)));
            }
        }

        private void setup() {
            client = new NetworkClient(null, teamName, winSlogan);
            System.out.println("client " + teamName + " connected!");
            playerNumber = client.getMyPlayerNumber();
        }

        private void waitForGameStart() {
            while (client.getScore(0) == 0) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException ignored) { }
            }
        }

        @Override
        public void run() {
            setup();
            createBots();
            initializeBots();

            waitForGameStart();

            while (client.isGameRunning()) {
                for (int botIndex = 0; botIndex < 3; botIndex++) {
                    Bot bot = bots[botIndex];
                    bot.updatePosition(new Vector3D(client.getBotPosition(playerNumber, botIndex)));
                    bot.updateDirection(new Vector3D(client.getBotDirection(botIndex)));

                    if (bot.hasFinished()) {
                        bot.setBehaviour(new DriveToPointBehaviour(GraphInformation.getRandomNode(client.getGraph())));
                        // bot.setBehaviour(new RandomBehaviour());
                    }
                    float directionUpdate = bot.getDirectionUpdate();

                    if (directionUpdate != 0.f) {
                        client.changeMoveDirection(botIndex, directionUpdate);
                    }
                }
                try {
                    Thread.sleep(50);
                } catch (InterruptedException ignored) { }
            }
        }
    }

    public static void main(String[] args) {
        Main m = new Main();
        m.start();
    }

    private void start() {
        Thread[] threads = new Thread[3];
        for (int i = 0; i < 3; i++)
            threads[i] = new Thread(new ClientRunner("team"+i, "team " + i + " win"));

        for (Thread t : threads)
            t.start();

        try {
            for (Thread t : threads)
                t.join();
        } catch (InterruptedException ignored) { }
    }
}
