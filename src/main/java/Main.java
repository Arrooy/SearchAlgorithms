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
            algorithm = tree.getAlgorithm("DFS");
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

        //TODO: A*
        tree.addAlgorithm(new Algorithm() {
            @Override
            public String getName() {
                return "A*";
            }

            @Override
            public LinkedList<TreeNode> computeSolution(TreeNode origin, TreeNode destination) {
                //Aqui s'ha de computar la solucio i retornar la llista de nodes del cami mes curt.
                return null;
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
