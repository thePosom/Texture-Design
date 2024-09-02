import java.io.Serializable;

class Tree implements Serializable {
    private static final long serialVersionUID = 1L;
    
    TreeNode root;
    TreeNodeNode[] treeNodesIn;

    public Tree () {
        IntNode allowedVars = new IntNode();
        treeNodesIn = new TreeNodeNode[6];

        for (int i = 0; i < treeNodesIn.length; i++) 
            treeNodesIn[i] = new TreeNodeNode();
    
        for (int i = 1; i <= 24; i++)
            allowedVars.addValue(i);
        this.root = new TreeNode(new Cube(), "root", allowedVars, 6, this);

        root.autoSetChilds();

        for (int i = 0; i < treeNodesIn.length; i++) 
            treeNodesIn[i].empty();
    }


    @Override
    public String toString() {
        return root.toString();
    }
    
}