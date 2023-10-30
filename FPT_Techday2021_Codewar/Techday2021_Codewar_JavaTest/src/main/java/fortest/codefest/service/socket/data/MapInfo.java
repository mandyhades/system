package fortest.codefest.service.socket.data;

import com.google.gson.Gson;
import fortest.codefest.service.caculator.test.Move;
import fortest.codefest.service.caculator.test.Node;
import fortest.codefest.utils.Logger;
import fortest.codefest.utils.constant.Constants;

import java.util.ArrayList;
import java.util.List;

public class MapInfo {

    public MapSize size;
    public List<Player> players;
    public List<int[]> map;
    public List<Bomb> bombs;
    public List<Node> bombsNode = new ArrayList<>();
    public List<Spoil> spoils;
    public List<Node> walls = new ArrayList<>();
    public List<Node> boxs = new ArrayList<>();
    public List<Node> placeWillBeBombs = new ArrayList<>();
    public List<Node> blank = new ArrayList<>();
    private Player myPlayer;
    private Player enemyPlayer;
    public Node[][] nodes;
    private List<Node> mSafeZone = new ArrayList<>();

    public List<Node> getSafeZone(Node head, int extend) {
        int x = head.x;
        int y = head.y;
        int maxWidth = size.cols;
        int maxHeight = size.rows;
        for (int i = 1; i < extend; i++) {
            Node node1 = new Node(x + i, y + i);
            Node node2 = new Node(x - i, y - i);
            Node node3 = new Node(x + i, y - i);
            Node node4 = new Node(x - i, y + i);

            Node node5 = new Node(x + i, y);
            Node node6 = new Node(x - i, y);
            Node node7 = new Node(x, y - i);
            Node node8 = new Node(x, y + i);

            addNodeToSafeZone(maxWidth, maxHeight, node1);
            addNodeToSafeZone(maxWidth, maxHeight, node2);
            addNodeToSafeZone(maxWidth, maxHeight, node3);
            addNodeToSafeZone(maxWidth, maxHeight, node4);
//            addNodeToSafeZone(maxWidth, maxHeight, node5);
//            addNodeToSafeZone(maxWidth, maxHeight, node6);
//            addNodeToSafeZone(maxWidth, maxHeight, node7);
//            addNodeToSafeZone(maxWidth, maxHeight, node8);
        }
        return mSafeZone;
    }

    private void addNodeToSafeZone(int maxWidth, int maxHeight, Node node) {
        if (node.isValid(maxWidth, maxHeight)) {
            if (nodes[node.x][node.y].value == Constants.BLANK) {
                mSafeZone.add(node);
            }
        }
    }

    public void setMyPlayer(Player player) {
        myPlayer = player;
    }

    public void setEnemyPlayer(Player player) {
        enemyPlayer = player;
    }


    public Node[][] createMapNode() {
        bombsNode.clear();
        boxs.clear();
        walls.clear();
        blank.clear();
        mSafeZone.clear();
        placeWillBeBombs.clear();
        nodes = new Node[size.cols][size.rows];
        createDefault(size.cols, size.rows, nodes);
        fillMap(nodes, map);
        fillBoms(nodes, myPlayer, enemyPlayer, bombs);
        return nodes;
    }

    private void createDefault(int cols, int rows, Node[][] nodes) {
        //create default
        for (int i = 0; i < cols; i++) {
            for (int j = 0; j < rows; j++) {
                nodes[i][j] = new Node(i, j, Constants.BLANK);
                nodes[i][j].parrent = null;
            }
        }
    }

    private void fillMap(Node[][] nodes, List<int[]> map) {

        for (int i = 0; i < map.size(); i++) {
            for (int j = 0; j < map.get(i).length; j++) {
                nodes[j][i].value = map.get(i)[j];
                if (nodes[j][i].value == Constants.BOX) {
                    boxs.add(nodes[j][i]);
                }
                if (nodes[j][i].value == Constants.WALL) {
                    walls.add(nodes[j][i]);
                }
                if (nodes[j][i].value == Constants.BLANK) {
                    blank.add(nodes[j][i]);
                }
            }
        }
        if (enemyPlayer != null) {

            Position p = enemyPlayer.currentPosition;
            Logger.println("EnemyPlayer:" + p.col + "," + p.row);
            boxs.add(new Node(p.col, p.row));
        } else {
            Logger.println("EnemyPlayer: == null");
        }
        fillPlaceWillbeBomb(nodes);
    }

    private void fillPlaceWillbeBomb(Node[][] nodes) {
        for (Node box : boxs) {
            int x = box.x;
            int y = box.y;
            if (nodes[x + 1][y].value == Constants.BLANK && (x + 1) < size.cols) {
                placeWillBeBombs.add(nodes[x + 1][y]);
            }
            if (nodes[x - 1][y].value == Constants.BLANK && (x - 1) >= 0) {
                placeWillBeBombs.add(nodes[x - 1][y]);
            }
            if (nodes[x][y + 1].value == Constants.BLANK && (y + 1) < size.rows) {
                placeWillBeBombs.add(nodes[x][y + 1]);
            }
            if (nodes[x][y - 1].value == Constants.BLANK && (y - 1) >= 0) {
                placeWillBeBombs.add(nodes[x][y - 1]);
            }
        }

        List<Node> removeDuplicateNode = new ArrayList<>();
        for (Node node : placeWillBeBombs) {
            if (removeDuplicateNode.isEmpty()) {
                removeDuplicateNode.add(node);
            } else {
                boolean found = false;
                for (Node te : removeDuplicateNode) {
                    if (te.equals(node)) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    removeDuplicateNode.add(node);
                }
            }
        }
        placeWillBeBombs.clear();
        placeWillBeBombs.addAll(removeDuplicateNode);
    }

    private void fillBoms(Node[][] nodes, Player myPlayer, Player enemyPlayer, List<Bomb> bombs) {
        Position myPosition;
        int myPower = 1;
        int enemyPower = 1;
        if (myPlayer != null) {
            // vi tri hien tai
            myPosition = myPlayer.currentPosition;
            myPower = myPlayer.power;
        }
        if (enemyPlayer != null) {
            Position position = enemyPlayer.currentPosition;
            // vi tri hien tai cua ke dich - coi la tuong ko di vao
            if (position != null) {
                nodes[position.col][position.row].value = Constants.BOX;
                System.out.println("shit:" + position.getX() + "/"+position.getY() + "value:"+ nodes[position.col][position.row].value);
                boxs.add(nodes[position.col][position.row]);
            }
            enemyPower = enemyPlayer.power;
        }
        if (bombs != null && !bombs.isEmpty()) {
            for (Bomb bomb : bombs) {
                nodes[bomb.col][bomb.row].value = Constants.BOMB;
                bombsNode.add(nodes[bomb.col][bomb.row]);
                walls.add(nodes[bomb.col][bomb.row]);
                int bombPower;
                if (bomb.isMyBom()) {
                    bombPower = myPower;
                } else {
                    bombPower = enemyPower;
                }
                for (int i = 0; i <= bombPower; i++) {
                    Node node1 = new Node(bomb.col + i, bomb.row);
                    Node node2 = new Node(bomb.col - i, bomb.row);
                    Node node3 = new Node(bomb.col, bomb.row + i);
                    Node node4 = new Node(bomb.col, bomb.row - i);
                    fillBombWall(nodes, node1);
                    fillBombWall(nodes, node2);
                    fillBombWall(nodes, node3);
                    fillBombWall(nodes, node4);
                }
            }
        }
    }

    private void fillBombWall(Node[][] nodes, Node node1) {
        if (node1.isValid(size.cols, size.rows)) {
            if (nodes[node1.x][node1.y].value != Constants.WALL && nodes[node1.x][node1.y].value != Constants.BOX) {
                nodes[node1.x][node1.y].value = Constants.BOMB;
                bombsNode.add(node1);
                walls.add(node1);
            }
        }
    }

    public void addBomb(Node bomb) {
        bombsNode.add(bomb);
        int myPower = 1;
        int enemyPower = 1;
        if (myPlayer != null) {
            myPower = myPlayer.power;
        }
        for (int i = 0; i <= myPower; i++) {
            if (bomb.x + i < size.cols) {
                nodes[bomb.x + i][bomb.y].value = Constants.BOMB;
                bombsNode.add(nodes[bomb.x + i][bomb.y]);
                walls.add(nodes[bomb.x + i][bomb.y]);
            }
            if (bomb.y + i < size.rows) {
                nodes[bomb.x][bomb.y + i].value = Constants.BOMB;
                walls.add(nodes[bomb.x][bomb.y + i]);
                bombsNode.add(nodes[bomb.x][bomb.y + i]);
            }
            if (bomb.x - i >= 0) {
                nodes[bomb.x - i][bomb.y].value = Constants.BOMB;
                walls.add(nodes[bomb.x - i][bomb.y]);
                bombsNode.add(nodes[bomb.x - i][bomb.y]);
            }
            if (bomb.y - i >= 0) {
                nodes[bomb.x][bomb.y - i].value = Constants.BOMB;
                walls.add(nodes[bomb.x][bomb.y - i]);
                bombsNode.add(nodes[bomb.x][bomb.y - i]);
            }
        }
    }

    public static String buildStringDirectionFromPath(List<Node> paths, Node currentNode) {
        if (paths != null && !paths.isEmpty() && paths.size() >= 2) {
            int size = paths.size() - 1;
            Node newPos;
            int x = currentNode.x;
            int y = currentNode.y;
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = size; i >= 0; i--) {
                newPos = paths.get(i);
                if (newPos.x < x) {
                    stringBuilder.append(Move.LEFT);
                } else if (newPos.y > y) {
                    stringBuilder.append(Move.DOWN);
                } else if (newPos.x > x) {
                    stringBuilder.append(Move.RIGHT);
                } else if (newPos.y < y) {
                    stringBuilder.append(Move.UP);
                }
            }
            return stringBuilder.toString();
        }
        return "";
    }


    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    public void clearBombToRun(Node head) {

        int myPower = 1;
        int enemyPower = 1;
        if (myPlayer != null) {
            myPower = myPlayer.power;
        }
        nodes[head.x][head.y].value = Constants.BLANK;
        bombsNode.remove(nodes[head.x][head.y]);
        walls.remove(nodes[head.x][head.y]);
        for (int i = 0; i <= myPower; i++) {
            if (head.x + i < size.cols) {
                nodes[head.x + i][head.y].value = Constants.BLANK;
                bombsNode.remove(nodes[head.x + i][head.y]);
                walls.remove(nodes[head.x + i][head.y]);
            }
            if (head.y + i < size.rows) {
                nodes[head.x][head.y + i].value = Constants.BLANK;
                walls.remove(nodes[head.x][head.y + i]);
                bombsNode.remove(nodes[head.x][head.y + i]);
            }
            if (head.x - i >= 0) {
                nodes[head.x - i][head.y].value = Constants.BLANK;
                walls.remove(nodes[head.x - i][head.y]);
                bombsNode.remove(nodes[head.x - i][head.y]);
            }
            if (head.y - i >= 0) {
                nodes[head.x][head.y - i].value = Constants.BLANK;
                walls.remove(nodes[head.x][head.y - i]);
                bombsNode.remove(nodes[head.x][head.y - i]);
            }
        }
    }
}

