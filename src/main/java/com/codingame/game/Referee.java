package com.codingame.game;

import java.awt.color.ColorSpace;
import java.util.*;

import com.codingame.gameengine.core.AbstractPlayer.TimeoutException;
import com.codingame.gameengine.core.AbstractReferee;
import com.codingame.gameengine.core.GameManager;
import com.codingame.gameengine.core.SoloGameManager;
import com.codingame.gameengine.module.entities.*;
import com.google.inject.Inject;
import org.apache.logging.log4j.core.tools.picocli.CommandLine;

public class Referee extends AbstractReferee {
    @Inject private SoloGameManager<Player> gameManager;
    @Inject private GraphicEntityModule graphicEntityModule;

    private static final int _intX = Constants.VIEWER_WIDTH / 2-375;
    private static final int _intY = 170;

    private static final int _offsetX = 227;
    private static final int _offsetY = 225;

    private static final int _maxTurns = 500;

    private Sprite[][] imageArray = new Sprite[4][4];
    private int[][] numberArray = new int[4][4];

    @Override
    public void init()
    {
        gameManager.setFrameDuration(200);

        gameManager.setMaxTurns(_maxTurns);

        gameManager.setFirstTurnMaxTime(3000);
        gameManager.setTurnMaxTime(100);

        List<String> testInput = gameManager.getTestCaseInput();

        graphicEntityModule.createSprite()
                .setImage(Constants.BACKGROUND_SPRITE)
                .setVisible(true)
                .setX(Constants.VIEWER_WIDTH/2)
                .setY(Constants.VIEWER_HEIGHT/2)
                .setAnchorX(0.5)
                .setAnchorY(0.5)
                .setScale(1.5)
                ;

        for (int i = 0; i <= testInput.size()-1; i++) {
            String[] a = testInput.get(i).split(" ");
            for (int j = 0; j <= a.length-1; j++) {
                numberArray[j][i]= Integer.parseInt(a[j]);
            }
        }


        for (int i = 0; i <= 3; i++) {
            for (int j = 0; j <= 3; j++) {
                if(numberArray[j][i]>0)
                    imageArray[j][i] = graphicEntityModule.createSprite()
                            .setImage("number-" + numberArray[j][i] + ".png")
                            .setVisible(true)
                            .setX(_intX + (j* _offsetX))
                            .setY(_intY + + (i* _offsetY))
                            .setScale(1.5)
                        ;
            }
        }

        testInput.forEach(s->{
            gameManager.getPlayer().sendInputLine(s);
        });


    }

    @Override
    public void gameTurn(int turn)
    {

        //gameManager.getPlayer().sendInputLine(Integer.toString(turn));
        //System.out.println(_maxTurns - turn);
        gameManager.getPlayer().execute();

        try {

            List<String> outputs = gameManager.getPlayer().getOutputs();

            if (outputs.size() != 1)
            {
                loseGame("Invalid input:  " + outputs);
                return;
            }

            int currY = -1;
            int currX = -1;

            try
            {
                Scanner playerIn = new Scanner(outputs.get(0));

                if (playerIn.hasNext())
                {
                    currY = playerIn.nextInt();
                }
                else
                {
                    loseGame("Invalid input:  " + outputs.get(0));
                    return;
                }

                if (playerIn.hasNext())
                {
                    currX = playerIn.nextInt();
                }
                else
                {
                    loseGame("Invalid input:  " + outputs.get(0));
                    return;
                }

                if (playerIn.hasNext())
                {
                    loseGame("Invalid input:  " + outputs.get(0));
                    return;
                }

            } catch (Exception e) {
                loseGame("Invalid input:  " + outputs.get(0));
                return;
            }

            if(checkInput(currY,currX)==false)
            {
                loseGame("Invalid position:  (" + currY + "," + currX + ")");
                return;
            }

            if(execMove(currY,currX)==false)
            {
                graphicEntityModule.createRectangle()
                        .setAlpha(0.5)
                        .setFillColor(0xff0000)
                        .setLineColor(0xff0000)
                        .setX(_intX+currX * _offsetX)
                        .setY(_intY + currY * _offsetY)
                        .setWidth(_offsetX-52)
                        .setHeight(_offsetY-52)
                ;

                loseGame("Invalid position:  (" + currY + "," + currX + ")");
                return;
            }

            if(checkWin())
            {
                winGame("You win!");
                return;
            }

            if(turn == _maxTurns)
            {
                loseGame("Turn limit exceeded");
                return;
            }


        } catch (TimeoutException e) {
            gameManager.loseGame("Timeout");
            return;
        }

    }

    private void loseGame(String text)
    {
        gameManager.loseGame("You lose! "+text);
        graphicEntityModule.createRectangle()
                .setLineWidth(0)
                .setFillColor(0xd4efdf)
                .setWidth(Constants.VIEWER_WIDTH)
                .setHeight(115)
                .setAlpha(0.85)
                .setX(0)
                .setY(Constants.VIEWER_HEIGHT/2);

        Text loseMsg = graphicEntityModule.createText("You lose!")
                .setStrokeThickness(2) // Adding an outline
                .setStrokeColor(0xffffff) // a white outline
                .setFontSize(75)
                .setFillColor(0x641E16) // Setting the text color to black
                .setAnchorX(0.5)
                .setX(Constants.VIEWER_WIDTH/2,Curve.EASE_IN_AND_OUT)
                .setY(Constants.VIEWER_HEIGHT/2,Curve.EASE_IN_AND_OUT)
                .setZIndex(10000);

        if(!text.equals(null) && !text.isEmpty())
            graphicEntityModule.createText(text)
                    .setStrokeThickness(1) // Adding an outline
                    .setStrokeColor(0xffffff) // a white outline
                    .setFontSize(36)
                    .setFillColor(0x943126)
                    .setAnchorX(0.5)
                    .setX(Constants.VIEWER_WIDTH /2,Curve.EASE_IN_AND_OUT)
                    .setY(loseMsg.getY()+72,Curve.EASE_IN_AND_OUT)
                    .setZIndex(100000);
    }
    private void winGame(String text)
    {
        gameManager.winGame(text);
        graphicEntityModule.createRectangle()
                .setLineWidth(0)
                .setFillColor(0xd4efdf)
                .setWidth(Constants.VIEWER_WIDTH)
                .setHeight(95)
                .setAlpha(0.75)
                .setX(0)
                .setY(Constants.VIEWER_HEIGHT/2);

        graphicEntityModule.createText(text)
                .setStrokeThickness(2) // Adding an outline
                .setStrokeColor(0xffffff) // a white outline
                .setFontSize(75)
                .setAnchorX(0.5)
                .setFillColor(0x0E6251) // Setting the text color to black
                .setX(Constants.VIEWER_WIDTH/2,Curve.EASE_IN_AND_OUT)
                .setY(Constants.VIEWER_HEIGHT/2,Curve.EASE_IN_AND_OUT)
                .setZIndex(10000);

    }

    private boolean checkInput(int y,int x)
    {
        if(y<0 || y >= numberArray.length)
        {
            gameManager.loseGame("Player invalid input:  " + y + "," + x);
            return false;
        }

        if(x<0 || x >= numberArray.length)
        {
            gameManager.loseGame("Player invalid input:  " + y + "," + x);
            return false;
        }

        if(numberArray[x][y]==0)
        {
            gameManager.loseGame("Player invalid input:  " + y + "," + x);
            return false;
        }
        return true;

    }

    private boolean checkWin()
    {
        int currNum=1;
        for (int i = 0; i <= 3; i++) {
            for (int j = 0; j <= 3; j++) {
                if(i==3 && j==3 && numberArray[j][i]==0)
                {
                    continue;
                }
                if(numberArray[j][i]==currNum)
                {
                    ++currNum;
                    continue;
                }
                else
                    return false;
            }
        }
        return true;
    }

    private boolean execMove(int currY,int currX)
    {
        int nextX=currX;
        int nextY=currY;
        if(currX+1<=3)
        {
            if (numberArray[currX + 1][currY] == 0)
                nextX = currX + 1;
        }
        if(currX-1>=0)
        {
            if (numberArray[currX - 1][currY] == 0)
                nextX = currX - 1;
        }

        if(currY+1<=3)
        {
            if (numberArray[currX][currY+1] == 0)
                nextY = currY + 1;
        }
        if(currY-1>=0)
        {
            if (numberArray[currX ][currY-1] == 0)
                nextY = currY - 1;
        }

        if(nextX==currX && nextY==currY)
        {
            return false;
        }

        numberArray[nextX][nextY] = numberArray[currX][currY];
        numberArray[currX][currY] = 0;

        if(imageArray[currX][currY]!=null)
        {
            int x=imageArray[currX][currY].getX();
            int y=imageArray[currX][currY].getY();
            imageArray[currX][currY]
                    .setX(x + (nextX-currX) * _offsetX)
                    .setY(y + (nextY-currY) * _offsetY)
            ;
        }

        imageArray[nextX][nextY] = imageArray[currX][currY];
        imageArray[currX][currY] = null;
        return true;

    }

}
