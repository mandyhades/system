package fortest.codefest.service.caculator;

import fortest.codefest.service.caculator.test.Move;
import fortest.codefest.service.caculator.test.Node;
import fortest.codefest.service.caculator.test.Path;
import fortest.codefest.service.socket.data.*;
import fortest.codefest.utils.Logger;
import fortest.codefest.utils.constant.Constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class DirectionFinder {
    private static final String TAG = "DirectionFinder => ";
    private Player myPlayer;
    private Position myPosition;
    private Player enemyPlayer;
    private Position enemyPosition;
    private MapInfo mapInfo;
    private int rows, cols;
    private GameInfo gameInfo;
    private MapSize mapSize;
    private fortest.codefest.service.caculator.test.Node[][] nodeMaps;
    private List<int[]> map;

    public String find(GameInfo game_infor) {
        this.gameInfo = game_infor;
        if (gameInfo != null) {
            mapInfo = gameInfo.map_info;
            if (mapInfo != null) {
                mapSize = mapInfo.size;
                if (mapSize != null) {
                    return getDirection();
                } else {
                    Logger.println(TAG + "err find() mapSize null");
                    return Dir.INVALID;
                }
            } else {
                Logger.println(TAG + "err find()mapInfo null");
                return Dir.INVALID;
            }
        } else {
            Logger.println(TAG + "err find() gameInfo null");
            return Dir.INVALID;
        }
    }

    private boolean validateNodes() {

        if (mapSize != null) {
            rows = mapSize.rows;
            cols = mapSize.cols;
            if (rows <= 0 || cols <= 0) {
                Logger.println(TAG + "err validateNodes()  rows <= 0 || cols <= 0 - " + rows + "/" + cols);
                return false;
            } else {
                map = mapInfo.map;
                if (map == null) {
                    Logger.println(TAG + "err validateNodes()  mapInfo.map null");
                    return false;
                } else {
                    nodeMaps = mapInfo.createMapNode();
                }
                return true;
            }
        }
        return false;
    }

    private boolean validateData() {
        if (!validatePlayers()) {
            Logger.println(TAG + "err validateData() mapInfo.players null or empty");
            return false;
        }
        if (!validateNodes()) {
            Logger.println(TAG + "err validateNodes() mapSize null or row col<=0");
            return false;
        }
        return true;
    }

    private boolean validatePlayers() {

        List<Player> players = mapInfo.players;
//        Logger.println(TAG + "validatePlayers Constants.MY_ID:" + Constants.MY_ID);
        if (players != null && players.size() != 0) {
            for (Player player : players) {
                player.reInitValue();
                if (Constants.MY_ID != null && Constants.MY_ID.length() != 0) {
                    if (Constants.MY_ID.contains(player.id)) {
                        myPlayer = player;
                        myPosition = myPlayer.currentPosition;
                    } else {
                        enemyPlayer = player;
                        enemyPosition = enemyPlayer.currentPosition;
                    }
                } else {
                    if (Constants.KEY_TEAM.contains(player.id)) {
                        myPlayer = player;
                        myPosition = myPlayer.currentPosition;
                    } else {
                        enemyPlayer = player;
                        enemyPosition = enemyPlayer.currentPosition;
                    }
                }
            }
            if (myPlayer == null || myPosition == null) {
//                Logger.println(TAG + "validatePlayers myPlayer == null || myPosition == null");
                return false;
            }
            if (enemyPlayer == null || enemyPosition == null) {
//                Logger.println(TAG + "validatePlayers enemyPlayer == null || enemyPosition == null");
                return false;
            }
            mapInfo.setEnemyPlayer(enemyPlayer);
            mapInfo.setMyPlayer(myPlayer);
            return true;
        } else {
            return false;
        }
    }


    private boolean shouldEat() {
        if (mapInfo.spoils == null || mapInfo.spoils.isEmpty()) {
            // ko co do an thi quay di dat bom luon
            return false;
        } else {
            // tinh khoang cach den do an, neu nho thi moi an, ko thi quay sang dat bom
            //
            Node head = Node.fromPosition(myPosition);
            BreadthFirstSearch algorithm = new BreadthFirstSearch();
            List<Path> paths = algorithm.findAllPathBySpoil(mapInfo, head, mapInfo.spoils, true);
            if (paths == null || paths.isEmpty()) {
                return false;
            }
            Collections.sort(paths, new Comparator<Path>() {
                @Override
                public int compare(Path o1, Path o2) {
                    return o1.nodePath.size() - o2.nodePath.size();
                }
            });
            Path path = paths.get(0);
            if (path == null) {
                return false;
            }
            if (path.nodePath == null || path.nodePath.isEmpty()) {
                return false;
            }
//            if (path.nodePath.size() <= 10) {
//                eatDirection = MapInfo.buildStringDirectionFromPath(path.nodePath, head);
//                return true;
//            }
            eatDirection = MapInfo.buildStringDirectionFromPath(path.nodePath, head);
            return true;
        }
//        return false;
    }

    String eatDirection;

    private List<fortest.codefest.service.caculator.test.Node> getBomPlaceNearBoxs() {
        List<fortest.codefest.service.caculator.test.Node> boxs = new ArrayList<>();
        for (int i = 1; i < cols - 1; i++) {
            for (int j = 1; j < rows - 1; j++) {
                if (nodeMaps[i][j].value == Constants.BOX) {
                    Logger.println("#shouldEat getBomPlaceNearBoxs:" + i + "/" + j);
                    if (nodeMaps[i + 1][j + 1].value == Constants.BLANK) {
                        fortest.codefest.service.caculator.test.Node node = new fortest.codefest.service.caculator.test.Node(i, j);
                        node.value = Constants.BLANK;
                        boxs.add(node);
                    }
                    if (nodeMaps[i - 1][j - 1].value == Constants.BLANK) {
                        fortest.codefest.service.caculator.test.Node node = new fortest.codefest.service.caculator.test.Node(i, j);
                        node.value = Constants.BLANK;
                        boxs.add(node);
                    }
                    if (nodeMaps[i + 1][j - 1].value == Constants.BLANK) {
                        fortest.codefest.service.caculator.test.Node node = new fortest.codefest.service.caculator.test.Node(i, j);
                        node.value = Constants.BLANK;
                        boxs.add(node);
                    }
                    if (nodeMaps[i - 1][j + 1].value == Constants.BLANK) {
                        fortest.codefest.service.caculator.test.Node node = new fortest.codefest.service.caculator.test.Node(i, j);
                        node.value = Constants.BLANK;
                        boxs.add(node);
                    }
                }
            }
        }
        return boxs;
    }

    private String getDirection() {
        if (!validateData()) {
            return Dir.INVALID;
        } else {
            if (inDangerArea()) {
                Logger.println("inDangerArea --------- byebye()");
                return byebye();
            } else {
                if (shouldEat()) {
                    Logger.println("shouldEat()");
                    return eatDirection;
                } else {
                    return iaBombnao();
                }
            }
        }
    }


    private boolean inDangerArea() {
        Node head = Node.fromPosition(myPosition);
        for (Node node : mapInfo.bombsNode) {
            if (head.equals(node)) {
                return true;
            }
        }
        return false;
    }

    BreadthFirstSearch firstSearch = new BreadthFirstSearch();

    private String byebye() {
        Node head = Node.fromPosition(myPosition);
        Node enemyHead = Node.fromPosition(enemyPosition);
        System.out.println("myhead:" + head.toString() +" - "+ nodeMaps[head.x][head.y].value + "  /  enemyhead:" + enemyHead.toString() +" - "+ nodeMaps[enemyHead.x][enemyHead.y].value);
        int extendSafeZone = 3;

        List<Node> safe = mapInfo.getSafeZone(head, extendSafeZone);
        if (safe.isEmpty()) {
            for (int i = 0; i < 3; i++) {
                extendSafeZone++;
                safe = mapInfo.getSafeZone(head, extendSafeZone);
                if (!safe.isEmpty()) {
                    break;
                }
            }

        }
        System.out.println("safe zone Size:" + safe.size());
        if (safe.isEmpty()) {
            return Move.INVALID;
        }
        System.out.println("myhead:" + head.toString() + "safe zone:" + safe.size() + "/" + safe.toString());
        MapInfo newMapInfor = mapToRun(extendSafeZone);

        List<Path> paths = firstSearch.findAllPathWithoutResetNode(newMapInfor, head, safe, true);
        System.out.println("paths Can run:" + paths.size());
        if (paths.isEmpty()) {
            return Move.INVALID;
        }
        Collections.sort(paths, new Comparator<Path>() {
            @Override
            public int compare(Path o1, Path o2) {
                return o1.nodePath.size() - o2.nodePath.size();
            }
        });
        Path pathRun = paths.get(0);
        Node target = pathRun.target;

        List<Node> path = pathRun.nodePath;
        Logger.println("-----------------------------------------BYE BYE Target:" + target);
        eatDirection = MapInfo.buildStringDirectionFromPath(path, head);
        Logger.println("-----------------------------------------BYE BYE PATH:" + eatDirection);
        return eatDirection;
    }

    private String iaBombnao() {
        Logger.println("findAStupidDirection() start");

        Node head = Node.fromPosition(myPosition);
        Logger.println("findAStupidDirection() head:" + head);
        Logger.println(" mapInfo.placeWillBeBombs:" + mapInfo.placeWillBeBombs.size());
        List<Path> placeBomb = firstSearch.findAllPathByNodes(mapInfo, head, mapInfo.placeWillBeBombs, true);

        Logger.println("placeBomb:" + placeBomb.size());
        if (placeBomb != null && !placeBomb.isEmpty()) {
            Collections.sort(placeBomb, new Comparator<Path>() {
                @Override
                public int compare(Path o1, Path o2) {
                    return o1.nodePath.size() - o2.nodePath.size();
                }
            });
            Path path = placeBomb.get(0);
            if (path == null) {
                Logger.println("path == null");
                return Dir.INVALID;
            }
            if (path.nodePath == null || path.nodePath.isEmpty()) {
                Logger.println("path.nodePath == null || path.nodePath.isEmpty()");
                return Dir.INVALID;
            }
            Node bombTarget = path.nodePath.get(0);

            if (bombTarget.equals(head) && nodeMaps[bombTarget.x][bombTarget.y].value == Constants.BLANK) {
                Logger.println("Ia bom nhe------------------------------------------------------------->>>>");
//                nodeMaps[head.x][head.y].value = Constants.WALL;
                mapInfo.addBomb(head);
                return Move.DROP_BOMB;
            }

            eatDirection = MapInfo.buildStringDirectionFromPath(path.nodePath, head);

            Logger.println("To to Place bomb:" + eatDirection);
            return eatDirection;
        }
        return Dir.INVALID;
    }

    private MapInfo mapToRun(int extend) {
        int width = mapSize.cols;
        int height = mapSize.rows;
        Node head = new Node(myPosition.col, myPosition.row);
        for (int i = 0; i < extend; i++) {
            Node node1 = new Node(head.x + i, head.y);
            Node node2 = new Node(head.x - i, head.y);
            Node node3 = new Node(head.x, head.y + i);
            Node node4 = new Node(head.x, head.y - i);
            if (node1.isValid(width, height)) {
                if (mapInfo.nodes[node1.x][node1.y].value == Constants.BOMB) {
                    mapInfo.nodes[node1.x][node1.y].value = Constants.BLANK;
                }
            }
            if (node2.isValid(width, height)) {
                System.out.println("node2"+node2+"/" + mapInfo.nodes[node2.x][node2.y].value);
                if (mapInfo.nodes[node2.x][node2.y].value == Constants.BOMB) {
                    mapInfo.nodes[node2.x][node2.y].value = Constants.BLANK;
                }
            }
            if (node3.isValid(width, height)) {
                if (mapInfo.nodes[node3.x][node3.y].value == Constants.BOMB) {
                    mapInfo.nodes[node3.x][node3.y].value = Constants.BLANK;
                }
            }
            if (node4.isValid(width, height)) {
                if (mapInfo.nodes[node4.x][node4.y].value == Constants.BOMB) {
                    mapInfo.nodes[node4.x][node4.y].value = Constants.BLANK;
                }
            }
        }

        return mapInfo;
    }
}
