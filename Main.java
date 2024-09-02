import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import javax.imageio.ImageIO;
class Main 
{
  static Tree tree;
  public static void main(String[] args) 
  {

    // Tree tree = new Tree();
    tree = deserialize("tree.ser");
    // System.out.println(tree.toString());

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

      // Check if all images have the same dimensions
      boolean matchingDimensions = (topWidth == backWidth && backWidth == bottomWidth && bottomWidth == frontWidth
              && frontHeight == leftHeight && leftHeight == backHeight && backHeight == rightHeight
              && topHeight == leftWidth && leftWidth == bottomHeight && bottomHeight == rightWidth);
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

      Cube[][][] cube = setCubeArraySides(top, bottom, right, left, front, back);
      theAlgorithm(cube);
      printCubeArray(cube);
      printToPhotos(cube);


    } catch (IOException e) {
      e.printStackTrace();
      return;
    }
    
  }

  public static Cube[][][] setCubeArraySides(boolean[][] top, boolean[][] bottom, boolean[][] right, boolean[][] left, boolean[][] front, boolean[][] back) {
    int w = top.length;
    int h = left[0].length;
    int l = top[0].length;
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
    int l = cube[0][0].length;
    
    for (int i = 0; i < l - 3; i++) {
      generalSlice(cube, i);
    }
    setThirdLastSlice(cube);
    setSecondLastSlice(cube);
    setLastSlice(cube);
  }

  private static void generalSlice(Cube[][][] cube, int l) {
    int w = cube.length;
    int h = cube[0].length;

    for (int i = 0; i < w; i++) {
      for (int k = 0; k < h; k++) {

        if (i >= w - 2 && k >= h - 3)
          setFinalSix(cube, i, k, l);

          if (!cube[i][k][l].setUnSet(tree)) {
            i++;
            i--;
          }
      }
    }
  }

  private static void setLastSlice(Cube[][][] cube) {
    int w = cube.length;
    int h = cube[0].length;
    int l = cube[0][0].length;

    for (int i = w - 1; i >= 0; i--) {
      for (int k = h - 1; k >= 0; k--) {
        if (i == 0 && k == 0) {
          cube[i][k][l-1].setSideN(Cube.BOTTOM, 0);
          cube[i][k][l-1].setActive(false);
        }
        if (!cube[i][k][l-1].setUnSet(tree)) {
          i++;
          i--;
        }
      }
    }
  }
  
  private static void setSecondLastSlice(Cube[][][] cube) {
    int w = cube.length;
    int h = cube[0].length;
    int l = cube[0][0].length;

    for (int i = 0; i < w; i++) {
      for (int k = 0; k < h; k++) {

        if (i >= w - 2 && k >= h - 3)
          setFinalSix(cube, i, k, l - 2);

        else {
          cube[i][k][l-2].setSideN(Cube.FRONT, cube[i][k][l-1].getSideN(Cube.FRONT));
          cube[i][k][l-1].setSideN(Cube.BACK, -1*cube[i][k][l-1].getSideN(Cube.FRONT));
        }
        if (!cube[i][k][l-2].setUnSet(tree)) {
          i++;
          i--;
        }
      }
    }
  }

  private static void setThirdLastSlice(Cube[][][] cube) {
    int w = cube.length;
    int h = cube[0].length;
    int l = cube[0][0].length;

    for (int i = 0; i < w; i++) {
      for (int k = 0; k < h; k++) {

        if (i >= w - 2 && k >= h - 3)
          setFinalSix(cube, i, k, l - 3);

        else if (i == w - 2 || k == h - 2) {
          cube[i][k][l-3].setSideN(Cube.FRONT, cube[i][k][l-1].getSideN(Cube.FRONT));
          cube[i][k][l-2].setSideN(Cube.BACK, -1*cube[i][k][l-1].getSideN(Cube.FRONT));
        }
        if (!cube[i][k][l-3].setUnSet(tree)) {
          i++;
          i--;
        }
      }
    }
  }

  private static void setFinalSix(Cube[][][] cube, int w, int h, int l) {
    int tW = cube.length;
    int tH = cube[0].length;
    
    if (w == tW - 2 && (h == tH - 3 || h == tH - 1)) {
      cube[w][h][l].setSideN(Cube.RIGHT, cube[tW-1][h][l].getSideN(Cube.RIGHT));
      cube[w+1][h][l].setSideN(Cube.LEFT, -1*cube[tW-1][h][l].getSideN(Cube.RIGHT));

    }
    if ((w == tW - 1 && h == tH - 3) || ((w == tW - 1 || w == tW - 2) && h == tH - 2)) {
      cube[w][h][l].setSideN(Cube.BOTTOM, cube[w][tH-1][l].getSideN(Cube.BOTTOM));
      cube[w][h+1][l].setSideN(Cube.TOP, -1*cube[w][tH-1][l].getSideN(Cube.BOTTOM));
    }
  }

  public static void printToPhotos(Cube[][][] cube) {
    String folderPath = "Vars"; // Path to the folder containing the images
    int photoWidth = 350;  // Adjust photo size as needed
    int photoHeight = 350;
    
    for (int i = 0; i < cube.length; i++) {
      int rows = cube[i].length;
      int cols = cube[i][0].length;
      
      // Create a BufferedImage to hold the combined photos
      BufferedImage combinedImage = new BufferedImage(cols * photoWidth, rows * photoHeight, BufferedImage.TYPE_INT_RGB);
      Graphics2D g = combinedImage.createGraphics();
      
      g.setColor(Color.WHITE);
      g.fillRect(0, 0, combinedImage.getWidth(), combinedImage.getHeight());

      // Loop through each cell in the 2D array
      for (int row = 0; row < rows; row++) {
        for (int col = 0; col < cols; col++) {
          int photoNum = cube[i][row][col].getVarNum();
          try {
            // Load the photo from the Var folder
            File imageFile = new File(folderPath + "/" + photoNum + ".png");
            BufferedImage photo = ImageIO.read(imageFile);
            
            // Draw the photo into the combined image at the appropriate position
            g.drawImage(photo, col * photoWidth, row * photoHeight, photoWidth, photoHeight, null);
          } catch (Exception e) {
                e.printStackTrace();
          }
        }
      }
      
      g.dispose();
      
      // Save the combined image
      try {
          ImageIO.write(combinedImage, "jpg", new File("grid_" + i + ".jpg"));
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
    int w = cube.length;
    int h = cube[0].length;
    int l = cube[0][0].length;
    switch (side) {
      case Cube.TOP:
        for (int i = 0; i < w; i++) {
          for (int k = 0; k < l; k++) {
            cube[i][h-1][k].setSideN(side, sidesArr[i][k]);
          }
        }
        break;
    
        case Cube.BOTTOM:
        for (int i = 0; i < w; i++) {
          for (int k = 0; k < l; k++) {
            cube[i][0][k].setSideN(side, sidesArr[i][k]);
          }
        }
        break;
    
        case Cube.RIGHT:
        for (int i = 0; i < h; i++) {
          for (int k = 0; k < l; k++) {
            cube[w-1][i][k].setSideN(side, sidesArr[i][k]);
          }
        }
        break;
    
        case Cube.LEFT:
        for (int i = 0; i < h; i++) {
          for (int k = 0; k < l; k++) {
            cube[0][i][k].setSideN(side, sidesArr[i][k]);
          }
        }
        break;
    
        case Cube.FRONT:
        for (int i = 0; i < w; i++) {
          for (int k = 0; k < h; k++) {
            cube[i][k][l-1].setSideN(side, sidesArr[i][k]);
          }
        }
        break;
    
        case Cube.BACK:
        for (int i = 0; i < w; i++) {
          for (int k = 0; k < h; k++) {
            cube[i][k][0].setSideN(side, sidesArr[i][k]);
          }
        }
        break;
    
        
      default:
        break;
    }
  }

  public static Cube[][][] initializeSquares(int w, int h, int l) {
    Cube[][][] cube = new Cube[w][h][l];
    for (int i = 0; i < w; i++) {
      for (int j = 0; j < h; j++) {
          for (int k = 0; k < l; k++) {
              cube[i][j][k] = new Cube();
          }
      }
    }
    return cube;
  }

  public static boolean[][] imageTo2DArray(BufferedImage image) {
    int width = image.getWidth(); 
    int height = image.getHeight(); 
    boolean[][] result = new boolean[width][height];

    for (int i = 0; i < width; i++) {
      for (int j = 0; j < height; j++) {
        int pixel = image.getRGB(i, j);
        Color color = new Color(pixel);

        // Check if the pixel is white (you may adjust this condition based on your definition of white)
        if (color.getRed() == 255 && color.getGreen() == 255 && color.getBlue() == 255) 
          result[i][j] = false; // Pixel is white
        else 
          result[i][j] = true; // Pixel is not white
            
      }
    }

    return result;
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

}