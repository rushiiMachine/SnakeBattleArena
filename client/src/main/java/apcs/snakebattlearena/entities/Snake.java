package apcs.snakebattlearena.entities;

import apcs.snakebattlearena.Point;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Snake implements Entity {
    Point head;
    LinkedList<Point> body;

    public Snake(Point initial) {
        this.head = initial;
        this.body = new LinkedList<>();
    }

    public Point getHead() {
        return head;
    }

    public List<Point> getBody() {
        return Collections.unmodifiableList(body);
    }

    public void _moveHead(Point newHead, boolean growing) {
        if (growing) body.removeLast();
        body.addFirst(head);
        head = newHead;
    }
}
