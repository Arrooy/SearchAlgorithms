package Logica;

import Algorithmics.Astar;
import Algorithmics.DijsktraCamins;
import Algorithmics.DijsktraParent;
import Model.SearchResult;
import Test.Evaluation;
import Test.Test;

import java.io.IOException;
import java.util.*;

public class Main {

    // Desactiva/activa el menu, segons sigui true o false respectivament.
    private static final boolean TESTING = true;

    // Executa una evaluació complerta.
    private static final boolean EXECUTE_EVALUATION = true;

    public static void main(String[] args) {
        Graph graph = new Graph("src/main/resources/spain_routes.json");

        defineAlgorithms(graph);

        GraphNode origin, destination = null;
        Algorithm algorithm = null;

        if (!TESTING) {
            Scanner reader = new Scanner(System.in);  // Reading from System.in
            boolean keepLooping;

            do {
                System.out.println("Origin city? Possible Origins: [" + graph.getPossibleCities() + "]");
                System.out.print("Origin: ");
                origin = graph.getCity(reader.nextLine());
                keepLooping = origin == null;

                if (keepLooping) {
                    System.out.println("Origin not found!");
                    continue;
                }

                System.out.println("Destination city? Possible Destinations: [" + graph.getPossibleCities() + "]");
                System.out.print("Destination: ");
                destination = graph.getCity(reader.nextLine());
                keepLooping = destination == null;

                if (keepLooping) {
                    System.out.println("Destination not found!");
                    continue;
                }

                System.out.println("Search Algorithm? Possible Algorithms: [" + graph.getPossibleAlgorithms() + "]");
                System.out.print("Algorithm: ");
                algorithm = graph.getAlgorithm(reader.nextLine());
                keepLooping = algorithm == null;

                if (keepLooping) {
                    System.out.println("Algorithm not found!");
                }

            } while (keepLooping);
            reader.close();
        } else if (!EXECUTE_EVALUATION) {

            //BCN - VAL - MUR
            origin = graph.getCity("Zaragoza");
            destination = graph.getCity("Málaga");
            algorithm = graph.getAlgorithm("Dijsktra");

        } else {
            LinkedList<Test> tests = new LinkedList<Test>();
            tests.add(new Test("Valladolid", "Zaragoza", 650668, "Valladolid", "Salamanca", "Madrid", "Zaragoza"));
            tests.add(new Test("Zaragoza", "Málaga", 1209148, "Zaragoza", "Madrid", "Salamanca", "Sevilla", "Málaga"));
            tests.add(new Test("Barcelona", "Sevilla", 1190908, "Barcelona", "Valencia", "Murcia", "Málaga", "Sevilla"));
            tests.add(new Test("A Coruña", "Madrid", 795685, "A Coruña", "León", "Valladolid", "Salamanca", "Madrid"));

            Evaluation evaluation = new Evaluation(graph, tests);

            try {
                evaluation.execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        // Ja sabem el Node origen, el node desti i el algoritme a aplicar.
        SearchResult solucio = graph.compute(origin, destination, algorithm);
        LinkedList<GraphNode> cami_mes_curt = solucio.getRoutes();

        // Mostrem el resultat

        StringBuilder cityNames = new StringBuilder();
        for (GraphNode tn : cami_mes_curt) {
            cityNames.append(tn.getCity().getName()).append(", ");
        }

        System.out.println("Fastest route (" + (int) solucio.getTotalDistance() + " m): " + cityNames.substring(0, cityNames.length() - 2));
    }

    private static void defineAlgorithms(Graph graph) {
        graph.addAlgorithm(new Astar());
        graph.addAlgorithm(new DijsktraCamins(graph));
        graph.addAlgorithm(new DijsktraParent(graph));
    }
}
