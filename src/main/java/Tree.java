import Model.City;
import Model.Connection;
import Model.Routes;
import com.google.gson.Gson;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class Tree {

    private HashMap<String, TreeNode> nodes;
    private HashMap<String, Algorithm> algorithms;

    private StringBuilder originNames;
    private StringBuilder algorithmNames;
    
    public Tree(String file_route) {
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
                TreeNode tn = new TreeNode(c);
                nodes.put(c.getName(), tn);
                originNames.append(c.getName()).append(", ");
            }

            for (Connection c : connections){
                TreeNode origen = nodes.get(c.getFrom());
                TreeNode desti = nodes.get(c.getTo());

                Distancia distanciaAB = new Distancia(origen, desti, c.getDistance(), c.getDuration());
                Distancia distanciaBA = new Distancia(desti, origen, c.getDistance(), c.getDuration());

                origen.addDist(distanciaAB);
                desti.addDist(distanciaBA);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public TreeNode getCity(String cityName){
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

    public LinkedList<TreeNode> compute(TreeNode origin, TreeNode destination, Algorithm algorithm){
        return algorithm.computeSolution(origin, destination);
    }

    public void addAlgorithm(Algorithm algorithm) {
        algorithmNames.append(algorithm.getName()).append(", ");
        this.algorithms.put(algorithm.getName(),algorithm);
    }
}

class TreeNode {

    private City city;
    private final LinkedList<Distancia> connexions;

    public TreeNode(City city) {
        this.city = city;
        connexions = new LinkedList<>();
    }

    public void addDist(Distancia distancia) {
        connexions.add(distancia);
    }

    public City getCity() {
        return city;
    }
    public LinkedList<Distancia> getConnexions() {
        return this.connexions;
    }
}
