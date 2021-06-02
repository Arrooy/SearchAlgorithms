package Logica;

import Model.City;
import Model.Connection;
import Model.Routes;
import Model.SearchResult;
import com.google.gson.Gson;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.List;

public class Graph {

    private HashMap<String, GraphNode> nodes;
    private HashMap<String, Algorithm> algorithms;

    private StringBuilder originNames;
    private StringBuilder algorithmNames;
    
    public Graph(String file_route) {
        Gson gson = new Gson();
        nodes = new HashMap<>();
        algorithms = new HashMap<>();

        originNames = new StringBuilder();
        algorithmNames = new StringBuilder();

        try {
            Routes r = gson.fromJson(new FileReader(file_route), Routes.class);
            List<City> cities = r.getCities();
            List<Connection> connections = r.getConnections();

            for (City c : cities) {
                GraphNode tn = new GraphNode(c);
                nodes.put(c.getName(), tn);
                originNames.append(c.getName()).append(", ");
            }

            for (Connection c : connections){
                GraphNode origen = nodes.get(c.getFrom());
                GraphNode desti = nodes.get(c.getTo());

                Distancia distanciaAB = new Distancia(origen, desti, c.getDistance(), c.getDuration());

                origen.addDist(distanciaAB);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public GraphNode getCity(String cityName){
        return this.nodes.get(cityName);
    }

    public String getPossibleCities() {
        return originNames.substring(0, originNames.length() - 2);
    }

    public String getPossibleAlgorithms() {
        return algorithmNames.substring(0, algorithmNames.length() - 2);
    }

    public Algorithm getAlgorithm(String algorithmName) {
        return algorithms.get(algorithmName);
    }

    public SearchResult compute(GraphNode origin, GraphNode destination, Algorithm algorithm){
        return algorithm.computeSolution(origin, destination);
    }

    public void addAlgorithm(Algorithm algorithm) {
        algorithmNames.append(algorithm.getName()).append(", ");
        this.algorithms.put(algorithm.getName(),algorithm);
    }
}
