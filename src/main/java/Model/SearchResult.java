package Model;

import Logica.GraphNode;

import java.util.LinkedList;

public class SearchResult {
    private final LinkedList<GraphNode> routes;
    private final double totalDistance;

    public SearchResult() {
        totalDistance = 0;
        routes = null;
    }

    public SearchResult(LinkedList<GraphNode> routes, double totalDistance) {
        this.routes = routes;
        this.totalDistance = totalDistance;
    }

    public LinkedList<GraphNode> getRoutes() {
        return routes;
    }

    public double getTotalDistance() {
        return totalDistance;
    }
}
