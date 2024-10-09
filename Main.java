import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.imageio.ImageIO;
class Main 
{
  static Tree tree;
  public static void main(String[] args) 
  {

    File treeFile = new File("tree.ser");

    if (!treeFile.exists()) {
      tree = new Tree();
      serialize(tree);
    }
    tree = deserialize("tree.ser");
    
    try {
      BufferedImage topImage = ImageIO.read(new File("top.png") );
      BufferedImage bottomImage = ImageIO.read(new File("bottom.png") );
      BufferedImage rightImage = ImageIO.read(new File("right.png") );
      BufferedImage leftImage = ImageIO.read(new File("left.png") );
      BufferedImage frontImage = ImageIO.read(new File("front.png") );
      BufferedImage backImage = ImageIO.read(new File("back.png") );

      // Get dimensions of each image
      int topWidth = topImage.getWidth();
      int topHeight = topImage.getHeight();

      int bottomWidth = bottomImage.getWidth();
      int bottomHeight = bottomImage.getHeight();

      int rightWidth = rightImage.getWidth();
      int rightHeight = rightImage.getHeight();

      int leftWidth = leftImage.getWidth();
      int leftHeight = leftImage.getHeight();

      int frontWidth = frontImage.getWidth();
      int frontHeight = frontImage.getHeight();

      int backWidth = backImage.getWidth();
      int backHeight = backImage.getHeight();

      boolean matchingDimensions = validateBoxDimensions(topWidth, topHeight, bottomWidth, bottomHeight, rightWidth, rightHeight, leftWidth, leftHeight, frontWidth, frontHeight, backWidth, backHeight);
      if (!matchingDimensions) {
        System.out.println("non matching dimensions");
        return;
      }

      boolean[][] top = imageTo2DArray(topImage);
      boolean[][] bottom = imageTo2DArray(bottomImage);
      boolean[][] right = imageTo2DArray(rightImage);
      boolean[][] left = imageTo2DArray(leftImage);
      boolean[][] front = imageTo2DArray(frontImage);
      boolean[][] back = imageTo2DArray(backImage);

      Cube[][][] cube = setCubeArraySides(top, bottom, right, left, front, back); // [Bottom-Top][Left-Right][Back-Forward]
      theAlgorithm(cube);
      // printCubeArray(cube);
      printToPhotos(cube);


    } catch (IOException e) {
      e.printStackTrace();
      return;
    }
    
  }

  public static Cube[][][] setCubeArraySides(boolean[][] top, boolean[][] bottom, boolean[][] right, boolean[][] left, boolean[][] front, boolean[][] back) {
    int h = left[0].length;
    int l = top.length;
    int w = top[0].length;
    Cube[][][] cube = initializeSquares(w, h, l);

    setCubeArraySize(cube, Cube.TOP, top);
    setCubeArraySize(cube, Cube.BOTTOM, bottom);
    setCubeArraySize(cube, Cube.RIGHT, right);
    setCubeArraySize(cube, Cube.LEFT, left);
    setCubeArraySize(cube, Cube.FRONT, front);
    setCubeArraySize(cube, Cube.BACK, back);

    return cube;
  }

  public static void theAlgorithm(Cube[][][] cube) {
    int width = cube[0][0].length;
    
    for (int w = 0; w < width - 3; w++) {
      generalSlice(cube, w);
    }
    setThirdLastSlice(cube);
    setSecondLastSlice(cube);
    setLastSlice(cube);
  }

  private static void generalSlice(Cube[][][] cube, int w) {
    int height = cube.length;
    int length = cube[0].length;

    for (int l = 0; l < length; l++) {
      for (int h = 0; h < height; h++) {

        if (l >= length - 2 && h >= height - 2)
          setFinalFour(cube, h, l, w);

        if (!cube[h][l][w].setUnSet(tree)) {
          l++;
          l--;
        }
        else 
          setSurroundingSqueres(cube, h, l, w); 
      }
    }
  }
  
    private static void setThirdLastSlice(Cube[][][] cube) {
      int height = cube.length;
      int length = cube[0].length;
      int width = cube[0][0].length;
  
      for (int l = 0; l < length; l++) {
        for (int h = 0; h < height; h++) {
  
          if (l >= length - 2 && h >= height - 2)
            setFinalFour(cube, h, l, width - 3);
  
          else if (l == length - 2 || h == height - 2) {
            cube[h][l][width-3].setSideN(Cube.FRONT, cube[h][l][width-1].getSideN(Cube.FRONT));
            cube[h][l][width-2].setSideN(Cube.BACK, -1 * cube[h][l][width-1].getSideN(Cube.FRONT));
          }
  
          if (!cube[h][l][width - 3].setUnSet(tree)) {
            l++;
            l--;
          }
          else 
            setSurroundingSqueres(cube, h, l, width - 3); 
        }
      }
    }

    private static void setSecondLastSlice(Cube[][][] cube) {
      int height = cube.length;
      int length = cube[0].length;
      int width = cube[0][0].length;
  
      for (int l = 0; l < length; l++) {
        for (int h = 0; h < height; h++) {
  
          if (l >= length - 2 && h >= height - 2)
            setFinalFour(cube, h, l, width-2);
            
          else {
            cube[h][l][width-2].setSideN(Cube.FRONT, cube[h][l][width-1].getSideN(Cube.FRONT));
            cube[h][l][width-1].setSideN(Cube.BACK, -1 * cube[h][l][width-1].getSideN(Cube.FRONT));

            if (l == length - 2) {
              cube[h][l][width-2].setSideN(Cube.RIGHT, cube[h][length-1][width-2].getSideN(Cube.RIGHT));
              cube[h][length-1][width-2].setSideN(Cube.LEFT, -1 * cube[h][length-1][width-2].getSideN(Cube.RIGHT));
              
            }
            if (h == height - 2) {
              cube[h][l][width-2].setSideN(Cube.TOP, cube[height-1][l][width-2].getSideN(Cube.TOP));
              cube[height-1][l][width-2].setSideN(Cube.BOTTOM, -1 * cube[height-1][l][width-2].getSideN(Cube.TOP));
              
            }
          }
  
          if (!cube[h][l][width-2].setUnSet(tree)) {
            l++;
            l--;
          }
          else 
            setSurroundingSqueres(cube, h, l, width - 2); 
        }
      }
    }

  private static void setLastSlice(Cube[][][] cube) {
    int height = cube.length;
    int length = cube[0].length;
    int width = cube[0][0].length;

    for (int l = length - 1; l >= 0; l--) {
      for (int h = height - 1; h >= 0; h--) {
        if (l == 0 && h == 0) {
          cube[h][l][width-1].setSideN(Cube.BOTTOM, 0);
          cube[h][l][width-1].setActive(false);
        }

        if (!cube[h][l][width-1].setUnSet(tree)) {
          l++;
          l--;
        }
        else 
            setSurroundingSqueres(cube, h, l, width - 1); 
      }
    }
  }

  private static void setFinalFour(Cube[][][] cube, int h, int l, int w) {
    int height = cube.length;
    int length = cube[0].length;
    
    if (l == length - 2 && h == height - 1) {
      cube[h][l][w].setSideN(Cube.RIGHT, cube[h][length - 1][w].getSideN(Cube.RIGHT));
      cube[h][length-1][w].setSideN(Cube.LEFT, -1 * cube[h][length - 1][w].getSideN(Cube.RIGHT));

    }
    if (l == length - 2 && h == height - 2) {
      cube[h][l][w].setSideN(Cube.TOP, cube[height - 1][l][w].getSideN(Cube.TOP));
      cube[h+1][l][w].setSideN(Cube.BOTTOM, -1 * cube[height - 1][l][w].getSideN(Cube.TOP));
    }
  }

  private static void setSurroundingSqueres(Cube[][][] cube, int h, int l, int w) {
    int height = cube.length;
    int length = cube[0].length;
    int width = cube[0][0].length;
    
    if (h != 0 && cube[h-1][l][w].getActive() == false)
      cube[h-1][l][w].setSideN(Cube.TOP, -1 * cube[h][l][w].getSideN(Cube.BOTTOM));
      
    if (h != height - 1 && cube[h+1][l][w].getActive() == false)
      cube[h+1][l][w].setSideN(Cube.BOTTOM, -1 * cube[h][l][w].getSideN(Cube.TOP));
      
    
    if (l != 0 && cube[h][l-1][w].getActive() == false)
      cube[h][l-1][w].setSideN(Cube.RIGHT, -1 * cube[h][l][w].getSideN(Cube.LEFT));
      
    if (l != length - 1 && cube[h][l+1][w].getActive() == false)
      cube[h][l+1][w].setSideN(Cube.LEFT, -1 * cube[h][l][w].getSideN(Cube.RIGHT));
      
    
    if (w != 0 && cube[h][l][w-1].getActive() == false)
      cube[h][l][w-1].setSideN(Cube.FRONT, -1 * cube[h][l][w].getSideN(Cube.BACK));
      
    if (w != width - 1 && cube[h][l][w+1].getActive() == false)
      cube[h][l][w+1].setSideN(Cube.BACK, -1 * cube[h][l][w].getSideN(Cube.FRONT));
  }

  public static void printToPhotos(Cube[][][] cube) {
    String folderPath = "Vars"; // Path to the folder containing the images
    int photoWidth = 296;  // Image width
    int photoHeight = 262; // Image height
    int padding = 25;      // Space between images

    int height = cube.length;
    int length = cube[0].length;
    int width = cube[0][0].length;

    for (int w = 0; w < width; w++) {
      // Calculate total size of the image, accounting for padding
      int combinedWidth = (length * photoWidth) + ((length - 1) * padding);
      int combinedHeight = (height * photoHeight) + ((height - 1) * padding);
      
      // Create a BufferedImage to hold the combined photos with padding
      BufferedImage combinedImage = new BufferedImage(combinedWidth, combinedHeight, BufferedImage.TYPE_INT_RGB);
      Graphics2D g = combinedImage.createGraphics();
      
      g.setColor(Color.WHITE);
      g.fillRect(0, 0, combinedImage.getWidth(), combinedImage.getHeight());

      // Loop through each cell in the 2D array
      for (int h = 0; h < height; h++) {
        for (int l = 0; l < length; l++) {
          int photoNum = cube[height-h-1][l][w].getVarNum();
          try {
            // Load the photo from the Var folder
            File imageFile = new File(folderPath + "/" + photoNum + ".png");
            BufferedImage photo = ImageIO.read(imageFile);
            
            // Calculate the x and y positions, adding padding between images
            int x = l * (photoWidth + padding);
            int y = h * (photoHeight + padding);
            
            // Draw the photo into the combined image at the appropriate position
            g.drawImage(photo, x, y, photoWidth, photoHeight, null);
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      }
      
      g.dispose();
      
      // Save the combined image
      try {
        ImageIO.write(combinedImage, "jpg", new File("grid_" + w + ".jpg"));
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  public static void printCubeArray(Cube[][][] cube) {
    int w = cube.length;
    int h = cube[0].length;
    int l = cube[0][0].length;
    for (int i = 0; i < w; i++) {
      System.out.println(i+":");
      for (int k = 0; k < h; k++) {
        for (int j = 0; j < l; j++) {
          System.out.print(cube[i][k][j].getVarNum()+" ");
        }
        System.out.println(" ");
      }
      System.out.println("\n");
    }
  } 

  public static void printNonFinalCubeArray(Cube[][][] cube) {
    int w = cube.length;
    int h = cube[0].length;
    int l = cube[0][0].length;
    for (int i = 0; i < w; i++) {
      System.out.println(i+":\n");
      for (int k = 0; k < h; k++) {
        for (int j = 0; j < l; j++) {
          System.out.println(cube[i][k][j].nonFinalCubeToString()+" ");
        }
        System.out.println("\n");
      }
      System.out.println("\n\n");
    }
  } 

  private static void setCubeArraySize(Cube[][][] cube, int side, boolean[][] sidesArr) {
    int height = cube.length;
    int length = cube[0].length;
    int width = cube[0][0].length;
    switch (side) {

      case Cube.TOP:
        for (int w = 0; w < width; w++) {
          for (int l = 0; l < length; l++) {
            cube[height - 1][l][w].setSideN(side, sidesArr[l][w]);
          }
        }
        break;
    
        case Cube.BOTTOM:
        for (int w = 0; w < width; w++) {
          for (int l = 0; l < length; l++) {
            cube[0][l][w].setSideN(side, sidesArr[l][w]);
          }
        }
        break;
    
        case Cube.RIGHT:
        for (int h = 0; h < height; h++) {
          for (int w = 0; w < width; w++) {
            cube[h][length - 1][w].setSideN(side, sidesArr[w][h]);
          }
        }
        break;
    
        case Cube.LEFT:
        for (int h = 0; h < height; h++) {
          for (int w = 0; w < width; w++) {
            cube[h][0][w].setSideN(side, sidesArr[w][h]);
          }
        }
        break;
    
        case Cube.FRONT:
        for (int l = 0; l < length; l++) {
          for (int h = 0; h < height; h++) {
            cube[h][l][width - 1].setSideN(side, sidesArr[l][h]);
          }
        }
        break;
    
        case Cube.BACK:
        for (int l = 0; l < length; l++) {
          for (int h = 0; h < height; h++) {
            cube[h][l][0].setSideN(side, sidesArr[l][h]);
          }
        }
        break;
    
        
      default:
        break;
    }
  }

  public static Cube[][][] initializeSquares(int w, int h, int l) {
    Cube[][][] cube = new Cube[h][l][w];
    for (int i = 0; i < h; i++) {
      for (int j = 0; j < l; j++) {
          for (int k = 0; k < w; k++) {
              cube[i][j][k] = new Cube();
          }
      }
    }
    return cube;
  }

  public static boolean[][] imageTo2DArray(BufferedImage image) {
    int length = image.getWidth(); 
    int height = image.getHeight(); 
    boolean[][] result = new boolean[length][height];

    for (int i = 0; i < length; i++) {
      for (int j = 0; j < height; j++) {
        int pixel = image.getRGB(i, j);
        Color color = new Color(pixel);

        // Check if the pixel is white (you may adjust this condition based on your definition of white)
        if (color.getRed() == 255 && color.getGreen() == 255 && color.getBlue() == 255) 
          result[i][height-j-1] = false; // Pixel is white
        else 
          result[i][height-j-1] = true; // Pixel is not white
            
      }
    }

    return result;
}

  public static boolean validateBoxDimensions(int topWidth, int topHeight, int bottomWidth, int bottomHeight,
    int rightWidth, int rightHeight, int leftWidth, int leftHeight,
    int frontWidth, int frontHeight, int backWidth, int backHeight) {

    if (topWidth != frontWidth || frontWidth != bottomWidth || bottomWidth != backWidth)
      return false; //4
      
    if (frontHeight != leftHeight || leftHeight != backHeight || backHeight != rightHeight)
      return false; //5
      
    if (topHeight != leftWidth || leftWidth != bottomHeight || bottomHeight != rightWidth)
      return false; //6

    return true; // All dimensions align
  }

  public static Tree deserialize(String filename) {
    Tree tree = null;

    try (FileInputStream fileIn = new FileInputStream(filename);
      ObjectInputStream in = new ObjectInputStream(fileIn)) {
      tree = (Tree) in.readObject();
      System.out.println("Deserialized Tree from " + filename);
    } catch (IOException | ClassNotFoundException e) {
      e.printStackTrace();
    }

    return tree;
  }

  public static void serialize(Tree tree) {
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