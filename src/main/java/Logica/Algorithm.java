package Logica;

import Model.SearchResult;

import java.util.HashMap;
import java.util.LinkedList;

public interface Algorithm {
    HashMap<String, GraphNode> oberts = new HashMap<String, GraphNode>();     // I
    HashMap<String, GraphNode> visitats = new HashMap<String, GraphNode>();    // cjt_buit
    LinkedList<GraphNode> solution = new LinkedList<>();

    String getName();
    SearchResult computeSolution(GraphNode origin, GraphNode destination);
}
