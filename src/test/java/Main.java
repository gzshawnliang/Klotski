import com.codingame.gameengine.runner.SoloGameRunner;


import java.util.*;
import java.io.*;


public class Main {
    public static void main(String[] args) throws IOException
    {
        SoloGameRunner gameRunner = new SoloGameRunner();


        // Sets the player
        gameRunner.setAgent(Solution.class);

        // Sets a test case
        gameRunner.setTestCase("test1.json");

        gameRunner.start();
    }
}
