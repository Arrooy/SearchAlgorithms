package Algorithmics;

import Logica.Algorithm;
import Logica.Distancia;
import Logica.Graph;
import Logica.GraphNode;
import Model.SearchResult;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;

public class DijsktraParent implements Algorithm {


    private Graph graph;

    public DijsktraParent(Graph graph) {
        this.graph = graph;
    }

    @Override
    public String getName() {
        return "Dijsktra Parents";
    }

    @Override
    public SearchResult computeSolution(GraphNode origin, GraphNode destination) {
        LinkedList<GraphNode> stack = new LinkedList<>();

        HashMap<String, Boolean> visitats = new HashMap<>();
        HashMap<String, Double> distancies = new HashMap<>();

        // Cost N
        for (GraphNode n : graph.getNodes().values()) {
            distancies.put(n.key(), -1.0);
        }

        stack.push(origin);

        while (!stack.isEmpty()) {
            GraphNode actual = stack.removeLast();
            visitats.put(actual.key(), true);

            for (Distancia dist : actual.getConnexions()) {
                GraphNode adj = dist.getDesti();

                // Es calcula la distancia al nou node adjacent
                Double distNova = distancies.get(actual.key()) + dist.getDistancia();
                Double distAdj = distancies.get(adj.key());

                if (distAdj == -1 || (distNova < distAdj)) {
                    distancies.replace(adj.key(), distNova);
                    adj.setParent(actual);
                }

                // Revisem si hem visitat el node adjacent.
                if (visitats.get(adj.key()) == null) {

                    stack.addFirst(adj);
                }
            }
        }
        origin.setParent(null);
        return generateResult(destination,distancies.get(destination.key()));
    }

    private SearchResult generateResult(GraphNode destination, Double distanciaTotal) {
        LinkedList<GraphNode> result = new LinkedList<>();

        result.add(destination);

        GraphNode parent = destination.getParent();

        while (parent != null) {
            result.add(parent);
            parent = parent.getParent();
        }

        Collections.reverse(result);
        // S'afegeix 1 per a corregir el -1 processat en l'algorisme.
        return new SearchResult(result, distanciaTotal + 1);
    }
}
