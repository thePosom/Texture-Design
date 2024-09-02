import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class Serialize {
    public static void main(String[] args) {
        Tree tree = new Tree();
        // Initialize the treeNode object

        try (FileOutputStream fileOut = new FileOutputStream("tree.ser");
             ObjectOutputStream out = new ObjectOutputStream(fileOut)) {
            out.writeObject(tree);
            System.out.println("Serialized data is saved in tree.ser");
        } catch (IOException i) {
            i.printStackTrace();
        }
    }
}
