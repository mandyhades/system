package fortest.codefest.controller;

import com.google.gson.Gson;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyEvent;
import org.json.JSONException;
import org.json.JSONObject;
import fortest.codefest.service.caculator.DirectionFinder;
import fortest.codefest.service.caculator.test.Move;
import fortest.codefest.service.socket.ClientConfig;
import fortest.codefest.service.socket.data.Dir;
import fortest.codefest.service.socket.data.Game;
import fortest.codefest.service.socket.data.GameInfo;
import fortest.codefest.utils.Logger;
import fortest.codefest.utils.SocketUtils;
import fortest.codefest.utils.TextUtils;
import fortest.codefest.utils.constant.Constants;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SocketController implements Initializable {

    @FXML
    javafx.scene.control.TextField edtGameId;
    @FXML
    javafx.scene.control.TextField edtPlayerId;

    @FXML
    javafx.scene.control.TextField editTextURL;

    @FXML
    javafx.scene.control.Button btnStart;
    @FXML
    javafx.scene.control.Button btnStop;
    @FXML
    javafx.scene.control.TextField editTextAction;
    @FXML
    TextArea txtController;
    @FXML
    TextArea txtMessage;
    @FXML
    CheckBox cbDebugMode, cbFProxy;

    private String mPlayerId, mGameId;

    private static final String URL = "https://codefest.techover.io/";

    private Socket mSocket;
    private DirectionFinder directionFinder = new DirectionFinder();
    private long responseTime;

    private boolean mIsCanMove = true;
    private static GameInfo gameInfo;
    private final Emitter.Listener mOnTickTackListener = new Emitter.Listener() {
        @Override
        public void call(Object... objects) {
            if (objects != null) {
                if (objects.length != 0) {
                    long step1 = System.currentTimeMillis();
                    String data = objects[0].toString();
//                    Logger.println("MAP_STATE: " + data);
                    if (!TextUtils.isEmpty(data)) {
                        gameInfo = new Gson().fromJson(data, GameInfo.class);
                        long step2 = System.currentTimeMillis();
//                        Logger.println("=========================");
//                        Logger.println("Response time: " + (step1 - responseTime));
                        responseTime = step1;
//                        Logger.println("Parse time: " + (step2 - step1));
                        if (gameInfo != null) {
                            if (gameInfo.map_info != null) {
                                if (gameInfo.map_info.size != null) {
//                                    Logger.println("Size: " + gameInfo.map_info.size.cols + " x " + gameInfo.map_info.size.rows);
                                    handleResponse();
                                } else {
                                    Logger.println("gameInfo.map_info.size == null - STOP");
                                    return;
                                }
                            } else {
                                Logger.println("gameInfo.map_info null");
                                // handle random move
                                return;
                            }

                        } else {
                            Logger.println("gameInfo null - STOP");
                        }
                    }
//                    Logger.println("=========================");
                }
            }
        }
    };

    private void handleResponse() {
        if (gameInfo != null) {
            String dir = directionFinder.find(gameInfo);
            if (!dir.equals(Dir.INVALID)) {
//                movePlayer(dir.trim().substring(0, 1));
                if (dir.startsWith(Move.DROP_BOMB)) {
                    movePlayer(Move.DROP_BOMB);
                    dir = directionFinder.find(gameInfo);
                    movePlayer(dir);
                } else {
                    movePlayer(dir.trim());
                }
            } else {
                Logger.println("Dir = INVALID - STOP");
            }
        }

    }

    void handleRandom() {

    }

    private final Emitter.Listener mOnDriveStateListener = objects -> {
        String response = objects[0].toString();
        Logger.println("ClientConfig.PLAYER.INCOMMING.DRIVE_PLAYER: " + response);
    };
    private Emitter.Listener mOnJoinGameListener = new Emitter.Listener() {
        @Override
        public void call(Object... objects) {
            String response = objects[0].toString();
            Logger.println("ClientConfig.PLAYER.INCOMMING.JOIN_GAME: " + response);
        }
    };

    ///==============================================================================================================
//    ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        edtGameId.setText(Constants.KEY_MAP);
        edtPlayerId.setText(Constants.KEY_TEAM);
        editTextURL.setText(URL);
        btnStop.setDisable(true);
        txtMessage.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                switch (event.getCode()) {
                    case DOWN:
                        movePlayer(Move.DOWN);
                        break;
                    case UP:
                        movePlayer(Move.UP);
                        break;
                    case LEFT:
                        movePlayer(Move.LEFT);
                        break;
                    case RIGHT:
                        movePlayer(Move.RIGHT);
                    case B:
                        movePlayer(Move.DROP_BOMB);
                        break;
                }
            }
        });
        txtController.setOnKeyPressed(event -> {
            txtController.setText(event.getCode().toString() + " - " + event.getCode().ordinal());
            int key = event.getCode().ordinal();
            String step = Dir.KEY_TO_STEP.get(key);
            if (!TextUtils.isEmpty(step)) {
                movePlayer(step);
            }
        });
//        executorService.submit(new Runnable() {
//            @Override
//            public void run() {
//                while (true) {
//                    try {
//                        handleResponse();
//                        Thread.sleep(200);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        });
    }

    public void onBtnStopClicked(ActionEvent actionEvent) {
        try {
            btnStop.setDisable(true);
            txtMessage.setText("");
            if (mSocket != null) {
                mSocket.disconnect();
                mSocket = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void onButtonRegisterClicked(ActionEvent actionEvent) {
        mPlayerId = edtPlayerId.getText().trim();
        mGameId = edtGameId.getText().trim();
        connectToServer();
    }

    public void btnSend(ActionEvent actionEvent) {
        String step = editTextAction.getText().trim();
        movePlayer(step);
    }

    public void onCheckboxDebugModeSelected(ActionEvent actionEvent) {
        Logger.updateDebugMode(cbDebugMode.isSelected());
    }

    ///==============================================================================================================

    private void movePlayer(String step) {
        if (mSocket != null) {
            Dir dir = new Dir(step.trim().substring(0,1));
            Logger.println("Dir = " + dir);
            try {
                mSocket.emit(ClientConfig.PLAYER.OUTGOING.DRIVE_PLAYER, new JSONObject(dir.toString()));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    private void connectToServer() {
        if (mSocket != null) {
            mSocket.disconnect();
            mSocket = null;
        }
//        mSocket = SocketUtils.init(editTextURL.getText(), cbFProxy.isSelected());
        mSocket = SocketUtils.init(editTextURL.getText(), false);
        if (mSocket == null) {
            Logger.println("Socket null - can't connect");
            return;
        }
        mSocket.on(ClientConfig.PLAYER.INCOMMING.JOIN_GAME, mOnJoinGameListener);
        mSocket.on(ClientConfig.PLAYER.INCOMMING.TICKTACK_PLAYER, mOnTickTackListener);
        mSocket.on(ClientConfig.PLAYER.INCOMMING.DRIVE_PLAYER, mOnDriveStateListener);
        mSocket.on(Socket.EVENT_CONNECT, objects -> {
            Logger.println("Connected");
            String gameParams = new Game(mGameId, mPlayerId).toString();
            Logger.println("Game params = " + gameParams);
            try {
                mSocket.emit(ClientConfig.PLAYER.OUTGOING.JOIN_GAME, new JSONObject(gameParams));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
        mSocket.on(Socket.EVENT_CONNECT_ERROR, objects -> Logger.println("Connect Failed"));
        mSocket.on(Socket.EVENT_ERROR, objects -> Logger.println("Error: " + objects[0].toString()));
        mSocket.on(Socket.EVENT_DISCONNECT, objects -> Logger.println("Disconnected"));
        mSocket.connect();
        btnStop.setDisable(false);
        txtMessage.setText("Running!!!");
    }
}
