package assignment3.assignment3;

import java.util.ArrayList;
import java.util.Random;
import java.awt.Color;

public class Block {
 private int xCoord;
 private int yCoord;
 private int size; // height/width of the square
 private int level; // the root (outter most block) is at level 0
 private int maxDepth; 
 private Color color;

 private Block[] children; // {UR, UL, LL, LR}

 public static Random gen = new Random(2);

public static void main(String[] args) {
 Block blockDepth3 = new Block(0, 2);
 blockDepth3.printColoredBlock();
}

 /*
  * These two constructors are here for testing purposes. 
  */
 public Block() {}
 
 public Block(int x, int y, int size, int lvl, int  maxD, Color c, Block[] subBlocks) {
  this.xCoord=x;
  this.yCoord=y;
  this.size=size;
  this.level=lvl;
  this.maxDepth = maxD;
  this.color=c;
  this.children = subBlocks;
 }

 /*
  * Creates a random block given its level and a max depth.
  */
 public Block(int lvl, int maxDepth) {
  this.level = lvl;
  this.maxDepth = maxDepth;
  this.children = new Block[0];

  if (level < maxDepth) {
   double randomValue = gen.nextDouble(1);
   if (randomValue < Math.exp(-0.25 * lvl)) {
    this.color = null;
    this.children = new Block[4];
    for (int i = 0; i < 4; i++) {
     this.children[i] = new Block(lvl + 1, maxDepth);
    }
   } else {
    int randomInteger = gen.nextInt(4);
    this.color = GameColors.BLOCK_COLORS[randomInteger];
   }
  } else {
   int randomInteger = gen.nextInt(4);
   this.color = GameColors.BLOCK_COLORS[randomInteger];
  }
 }

 /*
  * Updates size and position for the block and all of its sub-blocks, while
  * ensuring consistency between the attributes and the relationship of the 
  * blocks. 
  * 
  *  The size is the height and width of the block. (xCoord, yCoord) are the 
  *  coordinates of the top left corner of the block. 
  */
 public void updateSizeAndPosition (int size, int xCoord, int yCoord) {
  if (size < 0 || size % Math.pow(2, this.maxDepth - this.level) != 0) {
   throw new IllegalArgumentException("Size input is invalid");
  }

  this.size = size;
  this.xCoord = xCoord;
  this.yCoord = yCoord;
  if (this.children != null && this.children.length != 0) {
   this.children[0].updateSizeAndPosition(this.size / 2, this.xCoord + size / 2, this.yCoord);
   this.children[1].updateSizeAndPosition(this.size / 2, this.xCoord, this.yCoord);
   this.children[2].updateSizeAndPosition(this.size / 2, this.xCoord, this.yCoord + size / 2);
   this.children[3].updateSizeAndPosition(this.size / 2, this.xCoord + size / 2, this.yCoord + size / 2);
  }
 }

 
 /*
  * Returns a List of blocks to be drawn to get a graphical representation of this block.
  * 
  * This includes, for each undivided Block:
  * - one BlockToDraw in the color of the block
  * - another one in the FRAME_COLOR and stroke thickness 3
  * 
  * Note that a stroke thickness equal to 0 indicates that the block should be filled with its color.
  */
 public ArrayList<BlockToDraw> getBlocksToDraw() {
  ArrayList<BlockToDraw> blockList = new ArrayList<>();
  if (blockList != null) {
   blockToDrawAdder(blockList);
  }
  return blockList;
 }

 private void blockToDrawAdder(ArrayList<BlockToDraw> blockList) {
  if (this.children.length == 0) {
   BlockToDraw newBlock = new BlockToDraw(this.color, this.xCoord, this.yCoord, this.size, 0);
   BlockToDraw anotherBlock = new BlockToDraw(GameColors.FRAME_COLOR, this.xCoord, this.yCoord, this.size, 3);
   blockList.add(newBlock);
   blockList.add(anotherBlock);
  } else {
   for (int i = 0; i < 4; i ++) {
    this.children[i].blockToDrawAdder(blockList);
   }
  }
 }

 public BlockToDraw getHighlightedFrame() {
  return new BlockToDraw(GameColors.HIGHLIGHT_COLOR, this.xCoord, this.yCoord, this.size, 5);
 }

 /*
  * Return the Block within this Block that includes the given location
  * and is at the given level. If the level specified is lower than 
  * the lowest block at the specified location, then return the block 
  * at the location with the closest level value.
  * 
  * The location is specified by its (x, y) coordinates. The lvl indicates 
  * the level of the desired Block. Note that if a Block includes the location
  * (x, y), and that Block is subdivided, then one of its sub-Blocks will 
  * contain the location (x, y) too. This is why we need lvl to identify 
  * which Block should be returned. 
  * 
  * Input validation: 
  * - this.level <= lvl <= maxDepth (if not throw exception)
  * - if (x,y) is not within this Block, return null.
  */
 public Block getSelectedBlock(int x, int y, int lvl) {
  if (lvl > this.maxDepth || lvl < this.level) {
   throw new IllegalArgumentException("Level input is invalid");
  }

  if ((x > this.xCoord && x < this.xCoord + this.size) && (y > this.yCoord && y < this.yCoord + this.size)) {
   if (this.level == lvl) {
    return this;
   } else if (lvl > this.level) {
    if (this.children.length == 0) {
     return this;
    } else if (this.children.length == 4) {
     for (int i = 0; i < 4; i++) {
      if ((x > this.children[i].xCoord && x < this.children[i].xCoord + this.children[i].size) &&
              (y > this.children[i].yCoord && y < this.children[i].yCoord + this.children[i].size)) {
       return this.children[i].getSelectedBlock(x, y, lvl);
      }
     }
    }
   }
  }
  return null;
 }

 /*
  * Swaps the child Blocks of this Block. 
  * If input is 1, swap vertically. If 0, swap horizontally. 
  * If this Block has no children, do nothing. The swap 
  * should be propagated, effectively implementing a reflection
  * over the x-axis or over the y-axis.
  * 
  */
 public void reflect(int direction) {
  if (direction != 1 && direction != 0) {
   throw new IllegalArgumentException("Input direction is invalid");
  }

  if (this.children.length == 4) {
   Block firstTempBlock = this.children[0];
   Block secondTempBlock = this.children[1];
   Block thirdTempBlock = this.children[2];
   Block fourthTempBlock = this.children[3];
   if (direction == 0) {
    this.children[0] = fourthTempBlock;
    this.children[3] = firstTempBlock;
    this.children[1] = thirdTempBlock;
    this.children[2] = secondTempBlock;
   } else if (direction == 1) {
    this.children[0] = secondTempBlock;
    this.children[1] = firstTempBlock;
    this.children[3] = thirdTempBlock;
    this.children[2] = fourthTempBlock;
   }
   for (int i = 0; i < 4; i++) {
    if (this.children[i].children.length == 4) {
     this.children[i].reflect(direction);
    }
   }
   this.updateSizeAndPosition(this.size, this.xCoord, this.yCoord);
  }
 }

 /*
  * Rotate this Block and all its descendants. 
  * If the input is 1, rotate clockwise. If 0, rotate 
  * counterclockwise. If this Block has no children, do nothing.
  */
 public void rotate(int direction) {
  if (direction != 1 && direction != 0) {
   throw new IllegalArgumentException("Input direction is invalid");
  }

  if (this.children.length == 4) {
   Block firstTempBlock = this.children[0];
   Block secondTempBlock = this.children[1];
   Block thirdTempBlock = this.children[2];
   Block fourthTempBlock = this.children[3];
   if (direction == 0) {
    this.children[3] = thirdTempBlock;
    this.children[2] = secondTempBlock;
    this.children[1] = firstTempBlock;
    this.children[0] = fourthTempBlock;
   } else if (direction == 1) {
    this.children[0] = secondTempBlock;
    this.children[1] = thirdTempBlock;
    this.children[2] = fourthTempBlock;
    this.children[3] = firstTempBlock;
   }
   for (int i = 0; i < 4; i++) {
    if (this.children[i].children.length == 4) {
     this.children[i].rotate(direction);
    }
   }
   this.updateSizeAndPosition(this.size, this.xCoord, this.yCoord);
  }
 }

 /*
  * Smash this Block.
  * 
  * If this Block can be smashed,
  * randomly generate four new children Blocks for it.  
  * (If it already had children Blocks, discard them.)
  * Ensure that the invariants of the Blocks remain satisfied.
  * 
  * A Block can be smashed iff it is not the top-level Block 
  * and it is not already at the level of the maximum depth.
  * 
  * Return True if this Block was smashed and False otherwise.
  */
 public boolean smash() {
  if (this.level != 0 && this.level != this.maxDepth) {
   if (this.children.length == 4) {
    for (int i = 0; i < 4; i++) {
     this.children[i] = new Block(this.level + 1, this.maxDepth);
    }
   } else if (this.children.length == 0) {
    this.children = new Block[4];
    for (int i = 0; i < 4; i++) {
     this.children[i] = new Block(this.level + 1, this.maxDepth);
    }
   }
   this.updateSizeAndPosition(this.size, this.xCoord, this.yCoord);
   return true;
  }
  return false;
 }

 /*
  * Return a two-dimensional array representing this Block as rows and columns of unit cells.
  * 
  * Return an array arr where, arr[i] represents the unit cells in row i,
  * arr[i][j] is the color of unit cell in row i and column j.
  * 
  * arr[0][0] is the color of the unit cell in the upper left corner of this Block.
  */
 public Color[][] flatten() {
  if (this != null) {
   int sizeUnitCell;
   if (this.size != 0) {
    sizeUnitCell = (int) (this.size / Math.pow(2, this.maxDepth - this.level));
   } else {
    sizeUnitCell = 1;
   }
   Color[][] colorArray = new Color[this.size / sizeUnitCell][this.size / sizeUnitCell];
   this.flattenBlock(colorArray, sizeUnitCell);
   return colorArray;
  }
  return null;
 }

 private void flattenBlock(Color[][] colorArray, int sizeCell) {
  if (this.children.length != 4) {
   for (int i = this.yCoord / sizeCell; i < (this.yCoord + this.size) / sizeCell; i++) {
    for (int j = this.xCoord / sizeCell; j < (this.xCoord + this.size) / sizeCell; j++) {
     colorArray[i][j] = this.color;
    }
   }
  } else if (this.children.length == 4) {
   for (int i = 0; i < 4; i++) {
    this.children[i].flattenBlock(colorArray, sizeCell);
   }
  }
 }


 public int getMaxDepth() {
  return this.maxDepth;
 }
 
 public int getLevel() {
  return this.level;
 }


 /*
  * The next 5 methods are needed to get a text representation of a block.
  */
 public String toString() {
  return String.format("pos=(%d,%d), size=%d, level=%d"
    , this.xCoord, this.yCoord, this.size, this.level);
 }

 public void printBlock() {
  this.printBlockIndented(0);
 }

 private void printBlockIndented(int indentation) {
  String indent = "";
  for (int i=0; i<indentation; i++) {
   indent += "\t";
  }

  if (this.children.length == 0) {
   // it's a leaf. Print the color!
   String colorInfo = GameColors.colorToString(this.color) + ", ";
   System.out.println(indent + colorInfo + this);   
  } else {
   System.out.println(indent + this);
   for (Block b : this.children)
    b.printBlockIndented(indentation + 1);
  }
 }
 
 private static void coloredPrint(String message, Color color) {
  System.out.print(GameColors.colorToANSIColor(color));
  System.out.print(message);
  System.out.print(GameColors.colorToANSIColor(Color.WHITE));
 }

 public void printColoredBlock(){
  Color[][] colorArray = this.flatten();
  for (Color[] colors : colorArray) {
   for (Color value : colors) {
    String colorName = GameColors.colorToString(value).toUpperCase();
    if(colorName.length() == 0){
     colorName = "\u2588";
    }else{
     colorName = colorName.substring(0, 1);
    }
    coloredPrint(colorName, value);
   }
   System.out.println();
  }
 }
 
}