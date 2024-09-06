import java.io.Serializable;

public class Cube implements Serializable {
    private static final long serialVersionUID = 1L;
    public static final int TOP = 1;
    public static final int BOTTOM = 2;
    public static final int RIGHT = 3;
    public static final int LEFT = 4;
    public static final int FRONT = 5;
    public static final int BACK = 6;

    private int top; // -1 = in
    private int bottom; // 0 = not pressed
    private int right; // 1 = out
    private int left;
    private int front;
    private int back;
    private boolean active;

    // Constructor
    public Cube(int top, int bottom, int right, int left, int front, int back) {
        this.top = top;
        this.bottom = bottom;
        this.right = right;
        this.left = left;
        this.front = front;
        this.back = back;
        this.active = true;
    }

    public Cube(Cube cube) {
        this.top = cube.top;
        this.bottom = cube.bottom;
        this.right = cube.right;
        this.left = cube.left;
        this.front = cube.front;
        this.back = cube.back;
        this.active = cube.active;
    }

    public boolean setUnSet(Tree tree) {
        if (active)
            return isLegal();
        TreeNode pNode = tree.root;
        for (int i = 1; i <= 6; i++) {
            if (getSideN(i) != 0) {
                String str = Cube.sideNumToString(i)+ " " + Cube.directionToString(getSideN(i));
                pNode = pNode.findChildByName(str);
                if (pNode == null)
                    return false;
            }
        }
        if (pNode.allowedVars.getValue() == -1)
            return false;
        setVarNum(pNode.allowedVars.getValue());
        return true;
    }

    public Cube() {
        top = 0;
        bottom = 0;
        right = 0;
        left = 0;
        front = 0;
        back = 0;
        active = false;
    }

    // Getters and Setters
    public int isTop() {
        return top;
    }

    public void setTop(int top) {
        this.top = top;
    }

    public int isBottom() {
        return bottom;
    }

    public void setBottom(int bottom) {
        this.bottom = bottom;
    }

    public int isRight() {
        return right;
    }

    public void setRight(int right) {
        this.right = right;
    }

    public int isLeft() {
        return left;
    }

    public void setLeft(int left) {
        this.left = left;
    }

    public int isFront() {
        return front;
    }

    public void setFront(int forward) {
        this.front = forward;
    }

    public int isBack() {
        return back;
    }

    public void setBack(int backward) {
        this.back = backward;
    }

    public boolean getActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public int getVarNum() {
        if (isLegal() == false)
            return -1;
        
        int num = 1;
        if (top == -1) 
            num += 12;

        if (front == -1) 
            num += 6;
        
        if (left == -1)
            num += 3;
        
        if (back == 1) {
            if (front == 1 || right == 1)
                return num;
            return num + 1;
        }
        if (right == 1) {
            if (front == 1)
                return num + 1;
            return num + 2;
        }
        return num + 2;

    }

    public boolean isLegal() {
        Cube check = new Cube();
        for (int i = 1; i <= 24; i++) {
            check.setVarNum(i);
            if (isEqual(check) == true)
                return true;
        }
        return false;
    }

    public void setVarNum(int num) {
        top = (num <= 12) ? 1 : -1;
        front = ( (num-1) % 12 < 6) ? 1 : -1;
        left = ( (num-1) % 6 < 3) ? 1 : -1;

        bottom = (top == ((num % 12 == 3) || (num % 12 == 5) || (num % 12 == 8) || (num % 12 == 10) ? 1 : -1)) ? 1 : -1;
        back = ( (num % 3 == 1) || (num % 12 == 8) || (num % 12 == 11) ) ? 1 : -1;
        right = ( (num % 12 == 2) || (num % 12 == 4) || (num % 12 == 5) || (num % 12 == 7) || (num % 12 == 10) || (num % 12 == 0) ) ? 1 : -1;
        
        active = true;
    }

    public int getSideN(int sideNum) {
        if (sideNum == TOP)
            return top;
        if (sideNum == BOTTOM)
            return bottom;
        if (sideNum == RIGHT)
            return right;
        if (sideNum == LEFT)
            return left;
        if (sideNum == FRONT)
            return front;
        if (sideNum == BACK)
            return back;
        
        return  -2;
    }

    public void setSideN(int sideNum, int n) {
        if (sideNum == TOP)
            top = n;
        else if (sideNum == BOTTOM)
            bottom = n;
        else if (sideNum == RIGHT)
            right = n;
        else if (sideNum == LEFT)
            left = n;
        else if (sideNum == FRONT)
            front = n;
        else if (sideNum == BACK)
            back = n;
    }

    public void setSideN(int sideNum, boolean out) {
        if (out) 
            setSideN(sideNum, 1);
        else 
            setSideN(sideNum, -1);
    }

    public static String sideNumToString(int sideNum) {
        if (sideNum == TOP)
            return "top";
        if (sideNum == BOTTOM)
            return "bottom";
        if (sideNum == RIGHT)
            return "right";
        if (sideNum == LEFT)
            return "left";
        if (sideNum == FRONT)
            return "front";
        if (sideNum == BACK)
            return "back";

        return null;
    }

    public static String directionToString(int direction) {
        if (direction == -1)
            return "in";
        if (direction == 0)
            return "not pressed";
        if (direction == 1)
            return "out";
        
        return null;
    }

    public boolean isEqual(Cube cube) {
        return (cube.top == top && cube.bottom == bottom && cube.right == right && cube.left == left && cube.front == front && cube.back == back);
    }

    public IntNode allAllowedVars() {
        IntNode allowedVars = new IntNode();
        allAllowedVars(allowedVars, this);
        return allowedVars;
    }

    public static void allAllowedVars(IntNode allowedVars, Cube cube) {
        Cube copy = new Cube(cube);
        boolean full = true;

        for (int i = 1; i <= 6; i++) {
            if (copy.getSideN(i) != 0) 
                continue;
            full = false;
            copy.setSideN(i, 1);
            allAllowedVars(allowedVars, copy);
            copy.setSideN(i, -1);
            allAllowedVars(allowedVars, copy);
            break;
        }

        if (full && copy.isLegal())
            allowedVars.addValue(copy.getVarNum());
    }

    public String nonFinalCubeToString() {
        String str = "";
        if (top != 0)
            str += " t:"+top;
        if (bottom != 0)
            str += " bo:"+bottom;
        if (right != 0)
            str += " r:"+right;
        if (left != 0)
            str += " l:"+left;
        if (front != 0)
            str += " f:"+front;
        if (back != 0)
            str += " ba:"+back;
        return str;
    }

    // toString method
    @Override
    public String toString() {
        return getVarNum()+"";
    }
}
