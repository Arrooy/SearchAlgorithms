package Algorithmics;

import Logica.Algorithm;
import Logica.Distancia;
import Logica.Graph;
import Logica.GraphNode;
import Model.SearchResult;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;

public class DijsktraCamins implements Algorithm {

    private Graph graph;

    public DijsktraCamins(Graph graph) {
        this.graph = graph;
    }

    @Override
    public String getName() {
        return "Dijsktra Camins";
    }

    @Override
    public SearchResult computeSolution(GraphNode origin, GraphNode destination) {
        LinkedList<GraphNode> stack = new LinkedList<>();

        HashMap<String, LinkedList<Distancia>> camins = new HashMap<>();
        HashMap<String, Boolean> visitats = new HashMap<>();
        HashMap<String, Double> distancies = new HashMap<>();

        for (GraphNode n : graph.getNodes().values()) {
            distancies.put(n.key(), -1.0);
            camins.put(n.key(), new LinkedList<>());
        }

        stack.push(origin);

        while (!stack.isEmpty()) {
            GraphNode actual = stack.removeLast();

            for (Distancia dist : actual.getConnexions()) {
                GraphNode adj = dist.getDesti();

                // Revisem si hem visitat el node adjacent.
                if (visitats.get(dist.key()) == null) {
                    visitats.put(dist.key(), true);
                    // Es calcula la distancia al nou node adjacent

                    Double distNova = distancies.get(actual.key()) + dist.getDistancia();
                    Double distAdj = distancies.get(adj.key());

                    if (distAdj == -1 || (distNova < distAdj)) {
                        distancies.replace(adj.key(), distNova);

                        LinkedList<Distancia> cami = new LinkedList<>(camins.get(actual.key()));
                        cami.addLast(dist);
                        camins.replace(adj.key(), cami);
                    }
                    stack.addFirst(adj);
                }
            }
        }

        return generateSolution(camins, origin, destination);
    }

    private SearchResult generateSolution(HashMap<String, LinkedList<Distancia>> camins, GraphNode origin, GraphNode destination) {
        LinkedList<GraphNode> nodes = new LinkedList<>();
        int distanciaTotal = 0;
        GraphNode currentNode = destination;

        do {

            LinkedList<Distancia> cami = camins.get(currentNode.key());
            Distancia dist = cami.getLast();
            nodes.add(dist.getDesti());
            currentNode = dist.getOrigen();


            distanciaTotal += dist.getDistancia();

        } while (!currentNode.getCity().equals(origin.getCity()));

        nodes.add(currentNode);
        Collections.reverse(nodes);

        return new SearchResult(nodes, distanciaTotal);
    }
}