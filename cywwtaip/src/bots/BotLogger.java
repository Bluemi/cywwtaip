package bots;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

public class BotLogger {
    PrintWriter writer;

    public BotLogger(String teamName, BotType botType) {
        try {
            String filename = getFilename(teamName, botType);
            this.writer = new PrintWriter(filename, "UTF-8");
            writer.println(filename + ":\n");
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    public static String getFilename(String teamName, BotType botType) {
        return "logs/" + teamName + "_" + botType.toString() + ".log";
    }

    public void log(String s) {
        writer.println(s);
        writer.flush();
    }
}
