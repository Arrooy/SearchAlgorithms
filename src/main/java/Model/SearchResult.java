package Model;

import Logica.TreeNode;
import java.util.LinkedList;

public class SearchResult {
    private final LinkedList<TreeNode> routes;
    double totalDistance;
    boolean seachFailed;

    public SearchResult() {
        seachFailed = true;
        routes = null;
    }

    public SearchResult(LinkedList<TreeNode> routes, double totalDistance) {
        this.routes = routes;
        this.totalDistance = totalDistance;
        seachFailed = false;
    }

    public LinkedList<TreeNode> getRoutes() {
        return routes;
    }

    public double getTotalDistance() {
        return totalDistance;
    }
}
