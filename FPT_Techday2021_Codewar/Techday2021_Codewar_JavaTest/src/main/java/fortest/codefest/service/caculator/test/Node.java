package fortest.codefest.service.caculator.test;

import fortest.codefest.service.socket.data.Position;

public class Node {
    public int x;
    public int y;
    public int value;
    public String type;

    public Node parrent;

    public Node() {

    }

    public Node(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Node(int x, int y, int value) {
        this.x = x;
        this.y = y;
        this.value = value;
    }

    public static Node createNewNodeByDir(Node baseNode, String randomDir) {
        Node newNode = new Node();
        newNode.x = baseNode.x;
        newNode.y = baseNode.y;
        switch (randomDir) {
            case Move.LEFT:
                newNode.x -= 1;
                break;
            case Move.RIGHT:
                newNode.x += 1;
                break;
            case Move.UP:
                newNode.y -= 1;
                break;
            case Move.DOWN:
                newNode.y += 1;
                break;
        }
        return newNode;
    }

    public static Node createNewNodeByDir(Node baseNode, String randomDir, int size) {
        Node newNode = new Node();
        newNode.x = baseNode.x;
        newNode.y = baseNode.y;
        switch (randomDir) {
            case Move.LEFT:
                newNode.x -= size;
                break;
            case Move.RIGHT:
                newNode.x += size;
                break;
            case Move.UP:
                newNode.y -= size;
                break;
            case Move.DOWN:
                newNode.y += size;
                break;
        }
        return newNode;
    }

    public static Node fromPosition(Position position) {
        return new Node(position.col, position.row);
    }

    public boolean equals(Node obj) {
        if (obj == null) {
            return false;
        }
        return this.x == obj.x && this.y == obj.y;
    }

    @Override
    public String toString() {
        return "[" + x + "," + y + "]";
    }

    public boolean isValid(int cols, int rows) {
        return x > 0 && x < cols && y > 0 && y < rows;
    }
    /////////////////////////////////////////////////////////////////
}
