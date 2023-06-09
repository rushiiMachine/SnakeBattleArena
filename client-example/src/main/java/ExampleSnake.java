import apcs.snakebattlearena.*;
import apcs.snakebattlearena.client.*;
import apcs.snakebattlearena.commands.*;
import apcs.snakebattlearena.entities.*;

import java.awt.*;

public class ExampleSnake extends SnakeController {
    // Here we create our snake and connect to the server
    public static void main(String[] args) {
        new ExampleSnake().connect("localhost:4000");
    }

    // Tell the server our snake's name and color to draw us with
    @Override
    public SnakeInfo getSnakeInfo() {
        return new SnakeInfo("A crazy frog", Color.GREEN);
    }

    // When the server decides it's time for all the snakes to move,
    // you calculate the best direction to go in.
    // The valid directions are L (left), U (up), R (right), D (down)
    @Override
    public MoveCommand onMove(BoardInfo board) {
        return new MoveCommand('U');
    }

    // This is called when you die from a collision or from running into a wall.
    @Override
    public void onDeath(BoardInfo board) {
        System.out.println("I die");
    }

    // This is called when you respawn after dying
    @Override
    public void onRespawn(BoardInfo board) {
        System.out.println("Back alive!");
    }

    // This is called when the client finishes connecting to the server,
    // or when the board is erased.
    // Use this for whatever pre-calculations you need.
    @Override
    public void onWorldCreated(BoardInfo board) {
        System.out.println("The new world is " + board.getWidth()
                + " by " + board.getHeight() + "squares tall.");
    }

    // This is called when another snake joins the server
    // Not sure why you'd need this besides trolling.
    @Override
    public void onPlayerJoined(SnakeInfo snake) {
        System.out.println(snake.getName() + " has joined!");
    }

    // This is called when another snake leaves the server.
    // Not sure why you'd need this.
    @Override
    public void onPlayerLeave(SnakeInfo snake) {
        System.out.println(snake.getName() + " has left the game!");
    }

    // This is called when a new apple appears on the board
    // If you only want to track one apple at a time, then use this
    @Override
    public void onAppleCreated(Apple apple) {
        System.out.println("A new apple has appeared at " + apple.getPosition());
    }

    // Uh oh! It seems someone else has already eaten our apple!
    // If this has been called then it means a new apple has been created elsewhere!
    @Override
    public void onAppleDestroyed(Apple apple) {
        System.out.println("The apple at " + apple.getPosition() + " has been eaten! Uh oh!");
    }
}
