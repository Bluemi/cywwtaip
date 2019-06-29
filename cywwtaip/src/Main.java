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
        boolean debug;

        public ClientRunner(String teamName, String winSlogan) {
            this.teamName = teamName;
            this.winSlogan = winSlogan;
        }

        public ClientRunner(String teamName, String winSlogan, boolean debug) {
            this.teamName = teamName;
            this.winSlogan = winSlogan;
            this.debug = debug;
        }

        @Override
        public void run() {
            client = new NetworkClient(null, teamName, winSlogan);
            System.out.println("client " + teamName + " connected!");
            playerNumber = client.getMyPlayerNumber();

            Bot[] bots = new Bot[] {
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

            // initialize bots
            for (int botIndex = 0; botIndex < 3; botIndex++) {
                Bot bot = bots[botIndex];
                bot.updatePosition(new Vector3D(client.getBotPosition(playerNumber, botIndex)));
                bot.updateDirection(new Vector3D(client.getBotDirection(botIndex)));
            }

            long highestTime = 0;
            // wait for game to start
            while (client.getScore(0) == 0) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException ignored) { }
            }

            while (client.isGameRunning()) {
                long startTime = System.currentTimeMillis();
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
                long timeDuration = (System.currentTimeMillis() - startTime);
                if (timeDuration > highestTime) {
                    System.out.println(teamName + " needed: " + timeDuration + " millis");
                    highestTime = timeDuration;
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
        Thread thread0 = new Thread(new ClientRunner("team0", "team 0 win", true));
        Thread thread1 = new Thread(new ClientRunner("team1", "team 1 win", true));
        Thread thread2 = new Thread(new ClientRunner("team2", "team 2 win", true));

        thread0.start();
        thread1.start();
        thread2.start();

        try {
            thread0.join();
            thread1.join();
            thread2.join();
        } catch (InterruptedException ignored) { }
    }
}
