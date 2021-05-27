import java.util.HashMap;
import java.util.LinkedList;

public interface Algorithm {
    HashMap<String, TreeNode> oberts = new HashMap<String, TreeNode>();     // I
    HashMap<String, TreeNode> visitats = new HashMap<String,TreeNode>();    // cjt_buit
    LinkedList<TreeNode> solution = new LinkedList<>();

    String getName();
    LinkedList<TreeNode> computeSolution(TreeNode origin, TreeNode destination);
}
