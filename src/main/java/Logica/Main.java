package Logica;

import Model.SearchResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Scanner;

public class Main {

    private static boolean TESTING = true;

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
                origin = graph.getCity(reader.next());
                keepLooping = origin == null;

                if (keepLooping) {
                    System.out.println("Origin not found!");
                    continue;
                }

                System.out.println("Destination city? Possible Destinations: [" + graph.getPossibleCities() + "]");
                System.out.print("Destination: ");
                destination = graph.getCity(reader.next());
                keepLooping = destination == null;

                if (keepLooping) {
                    System.out.println("Destination not found!");
                    continue;
                }

                System.out.println("Search Logica.Algorithm? Possible Algorithms: [" + graph.getPossibleAlgorithms() + "]");
                System.out.print("Logica.Algorithm: ");
                algorithm = graph.getAlgorithm(reader.next());
                keepLooping = algorithm == null;

                if (keepLooping) {
                    System.out.println("Logica.Algorithm not found!");
                }

            } while (keepLooping);
            reader.close();
        } else {
            //BCN - VAL - MUR
            origin = graph.getCity("A Coruña");
            destination = graph.getCity("Madrid");
            algorithm = graph.getAlgorithm("A*");
        }
        // Ja sabem el Node origen, el node desti i el algoritme a aplicar.
        SearchResult solucio = graph.compute(origin, destination, algorithm);
        LinkedList<GraphNode> cami_mes_curt = solucio.getRoutes();

        // Mostrem el resultat

        StringBuilder cityNames = new StringBuilder();
        for (GraphNode tn : cami_mes_curt) {
            cityNames.append(tn.getCity().getName()).append(", ");
        }

        System.out.println("Fastest route (" + (int)solucio.getTotalDistance() + " m): " + cityNames.substring(0, cityNames.length() - 2));
    }

    private static void defineAlgorithms(Graph graph) {

        graph.addAlgorithm(new Algorithm() {

            private LinkedList<GraphNode> oberts = new LinkedList();
            private LinkedList<GraphNode> tancats = new LinkedList();
            private GraphNode destination;

            @Override
            public String getName() {
                return "A*";
            }

            @Override
            public SearchResult computeSolution(GraphNode origin, GraphNode destination) {
                //Aqui s'ha de computar la solucio i retornar la llista de nodes del cami mes curt.

                this.destination = destination;
                this.tancats.add(origin);
                for (Distancia connection : origin.getConnexions()) {
                    addToOpenList(origin, connection);
                }
                return Astar();
            }


            private SearchResult Astar() {
                while (!this.oberts.isEmpty()) {
                    GraphNode n1 = getBestNode();
                    // Openset to closed set.
                    this.oberts.remove(n1);
                    this.tancats.add(n1);

                    if (n1.getCity().equals(this.destination.getCity())) {
                        return generateResult(n1);
                    } else {
                        for (Distancia n : n1.getConnexions()) {
                            GraphNode node = n.getDesti();
                            // Comprovem que el node desti no esta tancat, tampoc esta obert ja (per no repetir)
                            if (!isTancat(node) && !isObert(node)) {
                                // S'afegeix el node a la llista d'oberts amb la referencia al seu pare.
                                addToOpenList(n1,n);
                            }
                        }
                    }
                }

                // No s'ha trobat el camí.
                return new SearchResult();
            }

            private void addToOpenList(GraphNode origin, Distancia connection){
                GraphNode newNode = connection.getDesti();

                newNode.setParent(origin);

                newNode.setG(connection.getDistancia() + origin.getG());
                newNode.setH(h(newNode));

                this.oberts.add(newNode);
            }

            private SearchResult generateResult(GraphNode nodeFinal) {
                LinkedList<GraphNode> result = new LinkedList<>();

                result.add(this.destination);

                GraphNode parent = nodeFinal.getParent();

                while (parent != null){
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

            // Funcio que retorna el node amb menys cost de tots els oberts
            private GraphNode getBestNode() {
                double minCost = 0;
                GraphNode bestNode = null;
                boolean firstIteration = true;
                for (GraphNode a : this.oberts) {
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
                for (GraphNode node : this.tancats) {
                    if (node.getCity() == n.getCity()) return true;
                }
                return false;
            }

            // Funcio que comprova si una ciutat d'node figura com a obert
            private boolean isObert(GraphNode n) {
                for (GraphNode node : this.oberts) {
                    if (node.getCity() == n.getCity()) return true;
                }
                return false;
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
