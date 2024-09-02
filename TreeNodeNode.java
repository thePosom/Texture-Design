import java.io.Serializable;

class TreeNodeNode implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private TreeNode node;
    private TreeNodeNode next;

    public TreeNodeNode(TreeNode node) {
        this.node = node;
        this.next = null;
    }

    public TreeNodeNode() {
        this.node = null;
        this.next = null;
    }

    public TreeNode getNode() {
        return node;
    }

    public void setNode(TreeNode node) {
        this.node = node;
    }

    public TreeNodeNode getNext() {
        return next;
    }

    public void setNext(TreeNodeNode next) {
        this.next = next;
    }

    public void addNode(TreeNode node) {
        if (this.node == null) // Check if current node is null
            this.node = node; // Set value to n if empty
        else {
            TreeNodeNode newNode = new TreeNodeNode(node);
            newNode.next = this.next;
            this.next = newNode;
        }
    }

    public void empty () {
        this.next = null;
        this.node = null;
    }

    public TreeNode search(Cube cube) {
        if (node == null)
            return null;
        TreeNodeNode pNode = this;
        while (pNode!=null) {
            if (pNode.node.cube.equals(cube))
                return pNode.node;
            pNode = pNode.getNext();
        }
        return null;
    }
}
