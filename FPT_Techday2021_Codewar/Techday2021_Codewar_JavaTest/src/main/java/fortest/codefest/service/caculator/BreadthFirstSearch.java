package fortest.codefest.service.caculator;

import fortest.codefest.service.caculator.test.Node;
import fortest.codefest.service.caculator.test.Path;
import fortest.codefest.service.socket.data.MapInfo;
import fortest.codefest.service.socket.data.Spoil;
import fortest.codefest.utils.constant.Constants;

import java.util.*;

public class BreadthFirstSearch {

    public int cols;
    public int rows;
    public Node[][] nodes;
    public Queue<Node> open;

    public BreadthFirstSearch() {
        open = new LinkedList<>();
    }


    public Node[][] getNodes() {
        return nodes;
    }

    public List<Node> findPath(MapInfo mapInfo, Node head, Node food, boolean force) {
        resetOpenList();
        createMapNode(mapInfo);
        if (force) {
            nodes[food.x][food.y].value = Constants.BLANK;
        }
        // start thuat toan BreadthFirstSearch
        open.add(head);
        Node node;
        while (!open.isEmpty()) {
            node = open.poll();
            if (node != null) {
                if (node.x == food.x && node.y == food.y) {
                    return getPathFound(node);
                }
                updateNodeListByMap(node);
            } else
                break;
        }
        return new ArrayList<>();

    }

    private void createMapNode(MapInfo mapInfo) {
        nodes = mapInfo.createMapNode();
        cols = mapInfo.size.cols;
        rows = mapInfo.size.rows;
    }

    private void updateNodeListByMap(Node node) {
        int min = 1;
        int maxCol = cols - 1;
        int maxRow = rows - 1;
        if (node.x > min) {
            addNoteToOpenList(node.x - 1, node.y, node);
        }
        if (node.y > min) {
            addNoteToOpenList(node.x, node.y - 1, node);
        }

        if (node.x < maxCol) {
            addNoteToOpenList(node.x + 1, node.y, node);
        }
        if (node.y < maxRow) {
            addNoteToOpenList(node.x, node.y + 1, node);
        }

    }

    private void addNoteToOpenList(int x, int y, Node previous) {
        Node node = nodes[x][y];
        if (node.value == Constants.BLANK) {
            node.value = Constants.VISITED;
            node.parrent = previous;
            open.add(node);
        }
    }

    private List<Node> getPathFound(Node node) {
        List<Node> nodes = new ArrayList<>();
        nodes.add(node);
        while (node.parrent != null) {
            nodes.add(node.parrent);
            node = node.parrent;
        }
        return nodes;
    }
    public synchronized List<Path> findAllPathWithoutResetNode(MapInfo mapInfo, Node head, List<Node> foods, boolean force) {
        resetOpenList();
//        createMapNode(mapInfo);
        nodes = mapInfo.nodes;
        cols = mapInfo.size.cols;
        rows = mapInfo.size.rows;

        // start thuat toan BreadthFirstSearch
        open.add(head);
        Node node;
        if (force) {
            Iterator iterator = foods.iterator();
            while (iterator.hasNext()) {
                Node f = (Node) iterator.next();
                nodes[f.x][f.y].value = Constants.BLANK;
            }
        }

        List<Path> allPath = new ArrayList<>();
        while (!open.isEmpty()) {
            node = open.poll();
            if (node != null) {
                for (Node abc : foods) {
                    if (abc != null) {
                        if (node.x == abc.x && node.y == abc.y) {
                            Path path = new Path();
                            path.target = node;
                            path.nodePath = getPathFound(node);
                            allPath.add(path);
                        }
                        updateNodeListByMap(node);
                    }
                }
            } else {
                break;
            }
        }
        return allPath;
    }

       public synchronized List<Path> findAllPathByNodes(MapInfo mapInfo, Node head, List<Node> foods, boolean force) {
        resetOpenList();
        createMapNode(mapInfo);
        // start thuat toan BreadthFirstSearch
        open.add(head);
        Node node;
        if (force) {
            Iterator iterator = foods.iterator();
            while (iterator.hasNext()) {
                Node f = (Node) iterator.next();
                nodes[f.x][f.y].value = Constants.BLANK;
            }
        }

        List<Path> allPath = new ArrayList<>();
        while (!open.isEmpty()) {
            node = open.poll();
            if (node != null) {
                for (Node abc : foods) {
                    if (abc != null) {
                        if (node.x == abc.x && node.y == abc.y) {
                            Path path = new Path();
                            path.nodePath = getPathFound(node);
                            allPath.add(path);
                        }
                        updateNodeListByMap(node);
                    }
                }
            } else {
                break;
            }
        }
        return allPath;
    }

    public List<Path> findAllPathBySpoil(MapInfo mapInfo, Node head, List<Spoil> foods, boolean force) {
        resetOpenList();
        createMapNode(mapInfo);
        // start thuat toan BreadthFirstSearch
        open.add(head);
        Node node;
        if (force) {
            for (Spoil f : foods) {
                nodes[f.col][f.row].value = Constants.BLANK;
            }
        }
        List<Path> allPath = new ArrayList<>();
        while (!open.isEmpty()) {
            node = open.poll();
            if (node != null) {
                for (Spoil spoil : foods) {
                    if (spoil != null) {
                        if (node.x == spoil.col && node.y == spoil.row) {
                            Path path = new Path();
                            path.nodePath = getPathFound(node);
                            allPath.add(path);
                        }
                        updateNodeListByMap(node);
                    }
                }
            } else {
                break;
            }
        }
        return allPath;
    }

    private void resetOpenList() {
        if (open != null) {
            open.clear();
        } else {
            open = new LinkedList<>();
        }
    }
}