package apcs.snakebattlearena.entities;

import apcs.snakebattlearena.Point;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Snake implements Entity {
    private final String name;
    private Point head;
    private final LinkedList<Point> body;
    private int curledLength;
    private boolean dead;

    public Snake(String name, Point initial) {
        this.name = name;
        this.head = initial;
        this.body = new LinkedList<>();
        this.curledLength = 0;
        this.dead = false;
    }

    public String getName() {
        return name;
    }

    public Point getHead() {
        return head;
    }

    public List<Point> getBody() {
        return Collections.unmodifiableList(body);
    }

    public int getLength() {
        return body.size() + 1 + curledLength;
    }

    public int getCurledLength() {
        return curledLength;
    }

    public boolean isDead() {
        return dead;
    }

    public void _internalMoveHead(Point newHead) {
        // If end curled then don't remove the tail
        if (curledLength <= 0) {
            body.removeLast();
        } else {
            curledLength--;
        }

        body.addFirst(head);
        head = newHead;
    }

    public void _internalAddCurledLength(int amount) {
        this.curledLength += amount;
    }

    public void _internalSetDead(boolean dead) {
        this.dead = dead;
    }
}
