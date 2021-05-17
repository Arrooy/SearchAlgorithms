import java.util.HashMap;
import java.util.LinkedList;

public interface Algorithm {
    HashMap<String, TreeNode> visitats = new HashMap<String,TreeNode>();
    LinkedList<TreeNode> solution = new LinkedList<>();

    String getName();
    LinkedList<TreeNode> computeSolution(TreeNode origin, TreeNode destination);
}
