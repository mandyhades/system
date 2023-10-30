package fortest.codefest.service.caculator.test;

import fortest.codefest.service.socket.data.Spoil;

import java.util.ArrayList;
import java.util.List;

public class Path {
    public List<Node> nodePath;
    public Node target;
    public Path() {
        nodePath = new ArrayList<>();
    }
}
