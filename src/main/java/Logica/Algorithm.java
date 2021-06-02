package Logica;

import Model.SearchResult;

import java.util.HashMap;
import java.util.LinkedList;

public interface Algorithm {
    String getName();
    SearchResult computeSolution(GraphNode origin, GraphNode destination);
}
