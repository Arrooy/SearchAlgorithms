package Logica;

import Model.SearchResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Scanner;

public class Main {

    private static boolean TESTING = true;

    public static void main(String[] args) {
        Tree tree = new Tree("src/main/resources/spain_routes.json");

        defineAlgorithms(tree);

        TreeNode origin, destination = null;
        Algorithm algorithm = null;

        if (!TESTING) {
            Scanner reader = new Scanner(System.in);  // Reading from System.in
            boolean keepLooping;

            do {
                System.out.println("Origin city? Possible Origins: [" + tree.getPossibleCities() + "]");
                System.out.print("Origin: ");
                origin = tree.getCity(reader.next());
                keepLooping = origin == null;

                if (keepLooping) {
                    System.out.println("Origin not found!");
                    continue;
                }

                System.out.println("Destination city? Possible Destinations: [" + tree.getPossibleCities() + "]");
                System.out.print("Destination: ");
                destination = tree.getCity(reader.next());
                keepLooping = destination == null;

                if (keepLooping) {
                    System.out.println("Destination not found!");
                    continue;
                }

                System.out.println("Search Logica.Algorithm? Possible Algorithms: [" + tree.getPossibleAlgorithms() + "]");
                System.out.print("Logica.Algorithm: ");
                algorithm = tree.getAlgorithm(reader.next());
                keepLooping = algorithm == null;

                if (keepLooping) {
                    System.out.println("Logica.Algorithm not found!");
                }

            } while (keepLooping);
            reader.close();
        } else {
            //BCN - VAL - MUR
            origin = tree.getCity("Barcelona");
            destination = tree.getCity("Sevilla");
            algorithm = tree.getAlgorithm("A*");
        }
        // Ja sabem el Node origen, el node desti i el algoritme a aplicar.
        SearchResult solucio = tree.compute(origin, destination, algorithm);
        LinkedList<TreeNode> cami_mes_curt = solucio.getRoutes();

        // Mostrem el resultat

        StringBuilder cityNames = new StringBuilder();
        for (TreeNode tn : cami_mes_curt) {
            cityNames.append(tn.getCity().getName()).append(", ");
        }

        System.out.println("Fastest route: " + cityNames.substring(0, cityNames.length() - 2));
    }

    private static void defineAlgorithms(Tree tree) {

        tree.addAlgorithm(new Algorithm() {

            private LinkedList<TreeNode> oberts = new LinkedList();
            private LinkedList<TreeNode> tancats = new LinkedList();
            private TreeNode destination;

            @Override
            public String getName() {
                return "A*";
            }

            @Override
            public SearchResult computeSolution(TreeNode origin, TreeNode destination) {
                //Aqui s'ha de computar la solucio i retornar la llista de nodes del cami mes curt.

                this.destination = destination;
                for (Distancia connection : origin.getConnexions()) {
                    this.oberts.add(new TreeNode(connection.getDesti().getCity(), connection.getDesti().getConnexions(), connection.getDistancia(), h(connection.getDesti()), origin,0));
                }
                return Astar();
            }


            private SearchResult Astar() {
                while (!this.oberts.isEmpty()) {
                    TreeNode n1 = getBestNode();
                    // Openset to closed set.
                    this.oberts.remove(n1);
                    this.tancats.add(n1);

                    if (n1.getCity() == this.destination.getCity()) {
                        return generateResult(n1);
                    } else {
                        for (Distancia n : n1.getConnexions()) {
                            TreeNode node = n.getDesti();
                            // Comprovem que el node desti no esta tancat, tampoc esta obert ja (per no repetir)
                            if (!isTancat(node) && !isObert(node)) {
                                // S'afegeix el node a la llista d'oberts amb la referencia al seu pare.
                                this.oberts.add(new TreeNode(node.getCity(), node.getConnexions(), n.getDistancia() + n1.getG(), h(node), n1,0));
                            }
                        }
                    }
                }

                // No s'ha trobat el comí.
                return new SearchResult();
            }

            private SearchResult generateResult(TreeNode nodeFinal) {
                LinkedList<TreeNode> result = new LinkedList<>();
                double distanciaTotal = 0;
                result.add(this.destination);
                TreeNode parent = nodeFinal.getParent();
                while (parent != null){
                    result.add(parent);
                    parent = parent.getParent();
                }
                Collections.reverse(result);
                return new SearchResult(result, distanciaTotal);
            }

            // Funció heurística per calcular el cost de cada node
            private double f(TreeNode n) {
                return n.getG() + n.getH();
            }

            private double h(TreeNode n) {
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
            private TreeNode getBestNode() {
                double minCost = 0;
                TreeNode bestNode = null;
                boolean firstIteration = true;
                for (TreeNode a : this.oberts) {
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
            private boolean isTancat(TreeNode n) {
                for (TreeNode node : this.tancats) {
                    if (node.getCity() == n.getCity()) return true;
                }
                return false;
            }

            // Funcio que comprova si una ciutat d'node figura com a obert
            private boolean isObert(TreeNode n) {
                for (TreeNode node : this.oberts) {
                    if (node.getCity() == n.getCity()) return true;
                }
                return false;
            }
        });

        // TODO: DFS (no funciona)
        // Resulta que DFS serveix per a rtgobar el cami pero no hi ha res que asseguri que el cami es el mes curt.
        // Ens hem de petar tot l'espai de cerca. POtser algu li interesa matarse a ferho.
//        tree.addAlgorithm(new Algorithm() {
//
//            LinkedList<TreeNode> aux;
//            int shortestList = 0;
//
//            @Override
//            public String getName() {
//                return "DFS";
//            }
//
//            @Override
//            public SearchResult computeSolution(TreeNode origin, TreeNode destination) {
//                return dfs(origin, destination);
//            }
//
//            private SearchResult dfs(TreeNode actual, TreeNode dessss) {
//                if (actual.getCity().getName().equals(dessss.getCity().getName())) {
//                    System.out.println("FOUND MURCIA, tornant");
//                    LinkedList<TreeNode> list = new LinkedList<>();
//                    list.add(actual);
//                    return list;
//                }
//
//                visitats.put(actual.getCity().getName(), actual);
//                System.out.println("Visitat " + actual.getCity().getName());
//                for (Distancia d : actual.getConnexions()) {
//                    if (visitats.get(d.getDesti().getCity().getName()) == null) {
//                        LinkedList<TreeNode> resList = dfs(d.getDesti(), dessss);
//                        if (resList != null && resList.size() < shortestList) {
//                            aux = (LinkedList<TreeNode>) resList.clone();
//                            shortestList = resList.size();
//                        }
//                    }
//                }
//                return aux;
//            }
//        });
    }
}
