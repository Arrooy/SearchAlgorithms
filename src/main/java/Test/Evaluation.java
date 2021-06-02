package Test;

import Logica.Algorithm;
import Logica.Graph;
import Logica.GraphNode;
import Model.SearchResult;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;

public class Evaluation {

    private static final double N_ITERATIONS = 5000;
    private static final double WARM_UP = 1000;

    private static final boolean VERBOSE = true;
    private static final String EVALUATION_RESULT_LOCATION = "src/main/Output/EvaluationResults.json";
    private final Graph graph;
    private final LinkedList<Test> tests;

    // Executa els benchmarks de tots els algorismes del graph a tots els tests.
    public Evaluation(Graph graph, LinkedList<Test> tests) {

        this.graph = graph;
        this.tests = tests;
    }

    public void execute() throws IOException {
        int totalTest = 0;
        int testOk = 0;

        HashMap<String, LinkedList<Results>> generalResults = new HashMap<>();

        for (Algorithm algorithm : this.graph.getAlgorithms().values()) {
            LinkedList<Results> results = new LinkedList<>();
            for (Test test : this.tests) {
                totalTest++;
                if (VERBOSE)
                    System.out.printf("\nEvaluating Test from %s to %s with algorithm %s ... ", test.getOrigin(), test.getDestination(), algorithm.getName());

                SearchResult solucio = new SearchResult();

                double avgTime = 0;
                double avgMemory = 0;

                LinkedList<Long> times = new LinkedList<>();
                LinkedList<Long> mems = new LinkedList<>();

                //Executem el test n cops
                for (int i = 0; i < N_ITERATIONS + WARM_UP; i++) {

                    // Per a cada test s'ha de crear un graph nou, ja que els valors d'aquest es veuen alterats en la computacio
                    Graph graph = new Graph(this.graph);

                    HashMap<String, GraphNode> nodes = graph.getNodes();

                    GraphNode origin = nodes.get(test.getOrigin());
                    GraphNode dest = nodes.get(test.getDestination());

                    //Runtime.getRuntime().gc();
                    long memBef = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
                    long before = System.nanoTime();
                    solucio = graph.compute(origin, dest, algorithm);
                    long time = System.nanoTime() - before;
                    long actualMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

                    if (i > WARM_UP){
                        times.add(time);
                        mems.add(actualMemory - memBef);
                        avgTime += time / 1e9;
                        avgMemory = actualMemory - memBef;
                    }
                }

                System.out.println("Average is " + avgTime);
                results.add(new Results((test.getOrigin() + " - " + test.getDestination()), avgTime / N_ITERATIONS, avgMemory / N_ITERATIONS,
                        times,mems));

                StringBuilder cityNames = new StringBuilder();
                for (GraphNode tn : solucio.getRoutes()) {
                    cityNames.append(tn.getCity().getName()).append(", ");
                }
                if (VERBOSE)
                    System.out.println("Solution found: " + cityNames.substring(0, cityNames.length() - 2) + " (" + (int) solucio.getTotalDistance() + " m)");

                if ((int) solucio.getTotalDistance() != test.getDistance()) {
                    System.out.printf("\tTest failed! Distance mismatch. Expected %dm found %dm\n", test.getDistance(), (int) solucio.getTotalDistance());
                    continue;
                }

                if (VERBOSE) System.out.println("\tThe distance is OK.");

                if (solucio.getRoutes().size() != test.getExpectedPlaces().size()) {
                    System.out.print("\tTest failed! Result route is bigger or smaller than the expected result\n");
                    continue;
                }

                // Verifiquem que el contingut també es correcte.
                LinkedList<String> expectedPlaces = test.getExpectedPlaces();
                boolean matched;

                for (GraphNode n : solucio.getRoutes()) {
                    matched = false;
                    for (String ex : expectedPlaces) {
                        if (n.getCity().getName().equals(ex)) {
                            matched = true;
                            break;
                        }
                    }

                    if (matched) {
                        expectedPlaces.remove(n.getCity().getName());
                    }
                }

                if (!expectedPlaces.isEmpty()) {
                    System.out.printf("\tTest failed! Route is missing %s", Arrays.toString(test.getExpectedPlaces().toArray()));
                    continue;
                }

                if (VERBOSE) System.out.println("\tThe route is OK.");

                System.out.println("\tTest passed.");
                testOk++;
            }
            generalResults.put(algorithm.getName(), results);

        }
        System.out.printf("""
                                                
                        ********************************************
                        Completed %d tests. Pass: %d - Failed: %d
                        ********************************************
                        """, totalTest, testOk
                , totalTest - testOk);
        File myObj = new File(EVALUATION_RESULT_LOCATION);
        try (Writer writer = new FileWriter(myObj)) {
            Gson gson = new GsonBuilder().create();
            gson.toJson(generalResults, writer);
        }
    }
}