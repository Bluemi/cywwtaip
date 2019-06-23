import lenz.htw.cywwtaip.net.NetworkClient;

public class Main {
    private class ClientRunner implements Runnable {
        String teamName;
        String winSlogan;

        public ClientRunner(String teamName, String winSlogan) {
            this.teamName = teamName;
            this.winSlogan = winSlogan;
        }

        @Override
        public void run() {
            System.out.println("client " + teamName + " connected");

            NetworkClient client = new NetworkClient(null, teamName, winSlogan);
            while (client.isAlive()) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ignored) { }
            }

            System.out.println("client " + teamName + " is out");
        }
    }

    public static void main(String[] args) {
        Main m = new Main();
        m.start();
    }

    private void start() {
        Thread thread0 = new Thread(new ClientRunner("team 0", "team 0 win"));
        Thread thread1 = new Thread(new ClientRunner("team 1", "team 1 win"));
        Thread thread2 = new Thread(new ClientRunner("team 2", "team 2 win"));

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
