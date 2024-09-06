import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

class TreeNode implements Serializable {
    private static final long serialVersionUID = 1L;

    Cube cube;
    String name;
    IntNode allowedVars;
    int facesLeft;
    List<TreeNode> children;
    Tree root;
    boolean printed = false;

    public TreeNode(Cube cube, String name, IntNode allowedVars, int facesLeft, Tree root) {
        this.cube = cube;
        this.name = name;
        this.allowedVars = allowedVars;
        this.facesLeft = facesLeft;
        this.children = new ArrayList<>(facesLeft*2);
        this.root = root;
    }

    public void addChild(TreeNode child) {
        this.children.add(child);
    }

    public TreeNode findChildByName(String name) {
        for (TreeNode child : children) {
            if (child != null && child.name.equals(name)) {
                return child;
            }
        }
        return null; // Return null if no child with the given name is found
    }

    public TreeNode findChildByIndex(int index) {
        if (index >= 0 && index < children.size()) {
            return children.get(index);
        }
        return null; // Return null if index is out of bounds
    }

    public void setChildAt(int index, TreeNode child) {
        if (index >= 0 && index < facesLeft*2) {
            children.set(index, child);
        } else {
            System.out.println("Index out of bounds");
        }
    }

    public void autoSetChilds() {
        if (facesLeft <= 0)
            return;

        int adding = 0;
        for (int i=0; i<facesLeft*2; i++) {

            int sideNum = (i+adding)/2;
            int status = ((i+adding)%2)*2 - 1;
            String newName = Cube.sideNumToString(sideNum)+ " " + Cube.directionToString(status);

            if (cube.getSideN(sideNum) != 0) {
                i--;
                adding++;
                continue;
            }

            
            Cube newCube = new Cube(cube);
            newCube.setSideN(sideNum, status);
            newCube.setActive(facesLeft+1 >= 6);
            
            IntNode newAllowedVars = newCube.allAllowedVars();
            if (newAllowedVars.getValue() == -1) {
                addChild(null);;
                continue;
            }

            TreeNode newNode = root.treeNodesIn[facesLeft-1].search(newCube);

            if ( newNode != null ) {
                addChild(newNode);
                continue;
            }

            
            newNode = new TreeNode(newCube, newName, newAllowedVars, facesLeft-1, root);
            root.treeNodesIn[facesLeft-1].addNode(newNode);
            addChild(newNode);

            newNode.autoSetChilds();
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        toStringHelper(this, sb, "");
        return sb.toString();
}

    private void toStringHelper(TreeNode node, StringBuilder sb, String prefix) {
        if (node == null) return;

        sb.append(prefix).append(node.name).append(": ").append(node.cube.toString()).append(":\n(")
            .append(node.allowedVars.toString()).append(")\n");

        for (TreeNode child : node.children) {
            toStringHelper(child, sb, prefix + "--");
        }
    }


}