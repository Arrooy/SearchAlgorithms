import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Scanner;

public class Main {

    private static boolean TESTING = true;

    public static void main(String[] args) {
        Tree tree = new Tree("src/main/resources/spain_routes.json");

        addAlgorithms(tree);

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

                System.out.println("Search Algorithm? Possible Algorithms: [" + tree.getPossibleAlgorithms() + "]");
                System.out.print("Algorithm: ");
                algorithm = tree.getAlgorithm(reader.next());
                keepLooping = algorithm == null;

                if (keepLooping) {
                    System.out.println("Algorithm not found!");
                }

            } while (keepLooping);
            reader.close();
        }else{
            //BCN - VAL - MUR
            origin = tree.getCity("Barcelona");
            destination = tree.getCity("Murcia");
            algorithm = tree.getAlgorithm("A*");
        }
        // Ja sabem el Node origen, el node desti i el algoritme a aplicar.
        LinkedList<TreeNode> cami_mes_curt = tree.compute(origin, destination, algorithm);

        // Mostrem el resultat

        StringBuilder cityNames = new StringBuilder();
        for (TreeNode tn : cami_mes_curt){
            cityNames.append(tn.getCity().getName()).append(", ");
        }

        System.out.println("Fastest route: " + cityNames.substring(0, cityNames.length() - 2));
    }

    private static void addAlgorithms(Tree tree) {

        //TODO: A* (not working yet)
        tree.addAlgorithm(new Algorithm() {

            ArrayList<TreeNode> oberts = new ArrayList();
            ArrayList<TreeNode> tancats = new ArrayList();
            LinkedList<TreeNode> aux;
            TreeNode origin;
            TreeNode destination;

            @Override
            public String getName() {
                return "A*";
            }

            @Override
            public LinkedList<TreeNode> computeSolution(TreeNode origin, TreeNode destination) {
                //Aqui s'ha de computar la solucio i retornar la llista de nodes del cami mes curt.
                this.origin = origin;
                this.destination = destination;
                for (Distancia connection: this.origin.getConnexions()) {
                    this.oberts.add(new TreeNode(connection.getDesti().getCity(), connection.getDesti().getConnexions(), connection.getDistancia(), h(connection.getDesti())));
                }
                return Astar(origin, destination);
            }


            private LinkedList<TreeNode> Astar(TreeNode actual, TreeNode destination) {

                boolean end = false;
                while (!end) {
                    TreeNode n1 = getLowest();
                    this.tancats.add(n1);
                    if (n1 == this.destination) {
                        end = true;
                        // TODO: Tractar solucio i reconstruir el cami
                    } else {
                            for (Distancia n: n1.getConnexions()) {
                                if (!isTancat(n.getDesti())) {          // Comprovem que el node desti no esta tancat
                                    this.oberts.add(new TreeNode (n.getDesti().getCity(), n.getDesti().getConnexions(), n.getDistancia() + n1.getG(), h(n.getDesti())));      // Marquem l'antecessor
                                } else {
                                    Astar(new TreeNode (n.getDesti().getCity(), n.getDesti().getConnexions(), n.getDistancia() + n1.getG(), h(n.getDesti())), destination);
                                }

                            }
                    }
                    // TODO: Reordenació dels oberts en funció de l'heuristica f(n) = g(n) + h(n),
                    // De menys distància a més distància
                }
                return this.aux;    // Aqui haurem de retornar la solucio trobada, no un null
            }

            // Funció heurística per calcular el cost de cada node
            private double f(TreeNode n) {
                return n.getG() + n.getH();
            }

            private double h(TreeNode n) {
                // De moment, la h serà un càlcul simple i directe de la distància que hi ha en línia recta des del node actual fins al destí basant-nos en la latitud i longitud
                return distancia(n.getCity().getLatitude(), n.getCity().getLongitude(), this.destination.getCity().getLatitude(),  this.destination.getCity().getLongitude());
            }

            public static double distancia(double lat1, double lon1, double lat2, double lon2) {
                double R = 6378.137;                        // Radi de la Terra en KM
                double dLat = (lat2 - lat1) * Math.PI/180;
                double dLong = (lon2 - lon1) * Math.PI/180;
                double a = Math.sin(dLat/2) * Math.sin(dLat/2) + Math.cos(lat1*Math.PI/180) * Math.cos(lat2*Math.PI/180) * Math.sin(dLong/2) * Math.sin(dLong/2);
                double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
                //Retornem la distancia en metres, ja que les conexions guarden la distancia en metres
                return R * c * 1000;
            }

            // Funcio que retorna el node amb menys cost de tots els oberts
            private TreeNode getLowest() {
                TreeNode x = this.oberts.get(0);
                for (TreeNode a : this.oberts) {
                    if (a.getH() == 0) {
                        return a;
                    }
                    if (f(a) < f(x)) x = a;

                }
                return x;
            }

            // Funcio que comprova si una node figura com a tancat
            private boolean isTancat(TreeNode n) {
                for (TreeNode node: this.tancats) {
                    if (node == n) return true;
                }
                return false;
            }



        });

        // TODO: DFS (no funciona)
        // Resulta que DFS serveix per a rtgobar el cami pero no hi ha res que asseguri que el cami es el mes curt.
        // Ens hem de petar tot l'espai de cerca. POtser algu li interesa matarse a ferho.
        tree.addAlgorithm(new Algorithm() {

            LinkedList<TreeNode> aux;
            int shortestList = 0;

            @Override
            public String getName() {
                return "DFS";
            }

            @Override
            public LinkedList<TreeNode> computeSolution(TreeNode origin, TreeNode destination) {
                return dfs(origin,destination);
            }

            private LinkedList<TreeNode> dfs(TreeNode actual, TreeNode dessss){
                if(actual.getCity().getName().equals(dessss.getCity().getName())){
                    System.out.println("FOUND MURCIA, tornant");
                    LinkedList<TreeNode> list = new LinkedList<>();
                    list.add(actual);
                    return list;
                }

                visitats.put(actual.getCity().getName(), actual);
                System.out.println("Visitat " + actual.getCity().getName());
                for (Distancia d : actual.getConnexions()){
                    if(visitats.get(d.getDesti().getCity().getName()) == null){
                        LinkedList<TreeNode> resList = dfs(d.getDesti(), dessss);
                        if(resList != null && resList.size() < shortestList){
                            aux = (LinkedList<TreeNode>) resList.clone();
                            shortestList = resList.size();
                        }
                    }
                }
                return aux;
            }
        });
    }
}
