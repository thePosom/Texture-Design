import java.io.Serializable;

class IntNode implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int value;
    private IntNode next;

    public IntNode(int value) {
        this.value = value;
        this.next = null;
    }

    public IntNode() {
        this.value = -1;
        this.next = null;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public IntNode getNext() {
        return next;
    }

    public void setNext(IntNode next) {
        this.next = next;
    }

    public void addValue(int n) {
        if (this.value == -1) { // Check if current node is "empty"
            this.value = n; // Set value to n if empty
        } else {
            IntNode newNode = new IntNode(n);
            newNode.next = this.next;
            this.next = newNode;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        IntNode current = this;
        while (current != null) {
            sb.append(current.value);
            if (current.next != null) {
                sb.append(", ");
            }
            current = current.next;
        }
        return sb.toString();
    }
}
