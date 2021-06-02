package Logica;

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

        graph.addAlgorithm(new Algorithm() {

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
        });


        graph.addAlgorithm(new Algorithm() {

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

                }while(!currentNode.getCity().equals(origin.getCity()));

                nodes.add(currentNode);
                Collections.reverse(nodes);

                return new SearchResult(nodes,distanciaTotal);
            }
        });

        graph.addAlgorithm(new Algorithm() {
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
        });



        // TODO: DFS (no funciona)
        // Resulta que DFS serveix per a rtgobar el cami pero no hi ha res que asseguri que el cami es el mes curt.
        // Ens hem de petar tot l'espai de cerca. POtser algu li interesa matarse a ferho.
//        graph.addAlgorithm(new Algorithm() {
//
//            LinkedList<GraphNode> aux;
//            int shortestList = 0;
//
//            @Override
//            public String getName() {
//                return "DFS";
//            }
//
//            @Override
//            public SearchResult computeSolution(GraphNode origin, GraphNode destination) {
//                return dfs(origin, destination);
//            }
//
//            private SearchResult dfs(GraphNode actual, GraphNode dessss) {
//                if (actual.getCity().getName().equals(dessss.getCity().getName())) {
//                    System.out.println("FOUND MURCIA, tornant");
//                    LinkedList<GraphNode> list = new LinkedList<>();
//                    list.add(actual);
//                    return list;
//                }
//
//                visitats.put(actual.getCity().getName(), actual);
//                System.out.println("Visitat " + actual.getCity().getName());
//                for (Distancia d : actual.getConnexions()) {
//                    if (visitats.get(d.getDesti().getCity().getName()) == null) {
//                        LinkedList<GraphNode> resList = dfs(d.getDesti(), dessss);
//                        if (resList != null && resList.size() < shortestList) {
//                            aux = (LinkedList<GraphNode>) resList.clone();
//                            shortestList = resList.size();
//                        }
//                    }
//                }
//                return aux;
//            }
//        });
    }
}
