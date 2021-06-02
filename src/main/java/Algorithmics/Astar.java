package Algorithmics;

import Logica.Algorithm;
import Logica.Distancia;
import Logica.GraphNode;
import Model.SearchResult;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;

public class Astar implements Algorithm {

    private HashMap<String, GraphNode> open;
    private HashMap<String, GraphNode> closed;
    private GraphNode destination;


    @Override
    public String getName() {
        return "A*";
    }
    //Aqui s'ha de computar la solucio i retornar la llista de nodes del cami mes curt.
    @Override
    public SearchResult computeSolution(GraphNode origin, GraphNode destination) {
        open = new HashMap<>();
        closed = new HashMap<>();

        this.destination = destination;
        this.closed.put(origin.key(), origin);
        for (Distancia connection : origin.getConnexions()) {
            addToOpenDataStructure(origin, connection);
        }
        return Astar();
    }


    private SearchResult Astar() {
        while (!this.open.isEmpty()) {

            // Gather best option
            GraphNode n1 = getBestNode();

            // Openset to closed set swap
            this.open.remove(n1.key());
            this.closed.put(n1.key(), n1);

            if (n1.getCity().equals(this.destination.getCity())) {
                return generateResult(n1);
            } else {
                for (Distancia n : n1.getConnexions()) {
                    GraphNode node = n.getDesti();
                    // Comprovem que el node desti no esta tancat, tampoc esta obert ja (per no repetir)
                    if (!isTancat(node) && !isObert(node)) {
                        // S'afegeix el node a la llista d'open amb la referencia al seu pare.
                        addToOpenDataStructure(n1, n);
                    }
                }
            }
        }

        // No s'ha trobat el camí.
        return new SearchResult();
    }

    private void addToOpenDataStructure(GraphNode origin, Distancia connection) {
        GraphNode newNode = connection.getDesti();

        newNode.setParent(origin);

        newNode.setG(connection.getDistancia() + origin.getG());
        newNode.setH(h(newNode));

        this.open.put(newNode.key(), newNode);
    }

    private SearchResult generateResult(GraphNode nodeFinal) {
        LinkedList<GraphNode> result = new LinkedList<>();

        result.add(this.destination);

        GraphNode parent = nodeFinal.getParent();

        while (parent != null) {
            result.add(parent);
            parent = parent.getParent();
        }

        Collections.reverse(result);
        return new SearchResult(result, nodeFinal.getG());
    }

    // Funció heurística per calcular el cost de cada node
    private double f(GraphNode n) {
        return n.getG() + n.getH();
    }

    private double h(GraphNode n) {
        // De moment, la h serà un càlcul simple i directe de la distància que hi ha en línia recta des del node actual fins al destí basant-nos en la latitud i longitud
        return distancia(n.getCity().getLatitude(), n.getCity().getLongitude(), this.destination.getCity().getLatitude(), this.destination.getCity().getLongitude());
    }

    public static double distancia(double lat1, double lon1, double lat2, double lon2) {
        double R = 6378.137;                        // Radi de la Terra en KM
        double dLat = (lat2 - lat1) * Math.PI / 180;
        double dLong = (lon2 - lon1) * Math.PI / 180;
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(lat1 * Math.PI / 180) * Math.cos(lat2 * Math.PI / 180) * Math.sin(dLong / 2) * Math.sin(dLong / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        //Retornem la distancia en metres, ja que les conexions guarden la distancia en metres
        return R * c * 1000;
    }

    // Funcio que retorna el node amb menys cost de tots els open
    private GraphNode getBestNode() {
        double minCost = 0;
        GraphNode bestNode = null;
        boolean firstIteration = true;

        for (GraphNode a : this.open.values()) {
            if (firstIteration) {
                minCost = f(a);
                bestNode = a;
                firstIteration = false;

            } else {

                double newf = f(a);
                if (newf < minCost) {
                    minCost = newf;
                    bestNode = a;
                }
            }
        }
        return bestNode;
    }

    // Funcio que comprova si una node figura com a tancat
    private boolean isTancat(GraphNode n) {
        return this.closed.get(n.key()) != null;
    }

    // Funcio que comprova si una ciutat d'node figura com a obert
    private boolean isObert(GraphNode n) {
        return this.open.get(n.key()) != null;
    }
}
