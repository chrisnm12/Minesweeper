import java.util.ArrayList;

import javalib.funworld.World;
import tester.*;
import javalib.impworld.*;
import java.awt.Color;
import java.util.Random;

import javalib.worldimages.*;



public interface IGamePieces {
  WorldImage draw();
}
/*
*
* Offers text to implement the IGamePieces interface to provide text to the screen.
*
* fields:
* ...text...-- String
* ...size...-- int
*
* methods:
* draw()-- WorldImage
*
* */
class StartingText implements IGamePieces {
  String text;
  int size;
  StartingText(String text, int size) {
    this.text = text;
    this.size = size;
  }
  // returns a WorldImage of text.
  public WorldImage draw() {
    return new TextImage(this.text, this.size, FontStyle.BOLD, Color.BLACK);
  }
}


 /*
 * mainly used to draw the mine onto the screen
 *
 *fields:
 *...size...--int
 *
 *methods:
 *draw()-- WorldImage
 *
 *
 */
class Mine implements IGamePieces {
  int size;
  Mine(int size) {
    this.size = size;
  }
  // Draws a mine
  public WorldImage draw() {
    return new CircleImage(this.size, OutlineMode.SOLID, Color.RED);
  }
}


/*
*
*
* fields:
* ...hasBomb...--boolean
* ...hasFlag...--boolean
* ...isRevealed...-- boolean
* ...heights...-- int
* ...width...--int
*
* methods:
*
* // returns the image of a cell
* draw()--WorldImage
*
* // counts the number of mines within this.neighbors
* countNeighboringMines()-- int
*
* // revealing neighboring cells without bombs.
* // EFFECT: changes the reveal to true if there are no bombs found in this.neighbors
* neighborBombs()-- boolean
*
* //
*
* */


class Cell {
  ArrayList<Cell> neighbors;
  boolean hasBomb;
  boolean hasFlag;
  boolean isRevealed;
  int height;
  int width;
  Cell(int height, int width){
    this.neighbors = new ArrayList<>();
    this.hasBomb = false;
    this.hasFlag = false;
    this.isRevealed = false;
    this.height = height;
    this.width = width;
  }
  // drawing the image of a cell.
  public WorldImage draw() {
    WorldImage cellImage = new RectangleImage(this.width, this.height, OutlineMode.SOLID, Color.cyan);
    WorldImage flagImage = new Flag().draw();

    if (hasFlag) {
      if (isRevealed) {
        cellImage = new RectangleImage(this.width, this.height, OutlineMode.SOLID, Color.GRAY);
      }
      cellImage = new OverlayOffsetAlign(AlignModeX.CENTER, AlignModeY.MIDDLE, flagImage, 0, 0, cellImage);
      return cellImage;
    }
    else if (hasBomb) {
      cellImage = new OverlayOffsetAlign(AlignModeX.CENTER, AlignModeY.MIDDLE, cellImage, 0,0, new Mine(1 / (3 * this.height)).draw());
      if (isRevealed) {
        cellImage = new Mine(1 / (3 * this.height)).draw();
        return cellImage;
      }
      return cellImage;
    }
    else if (isRevealed) {
      cellImage = new RectangleImage(this.width, this.height, OutlineMode.SOLID, Color.GRAY);
      cellImage = new OverlayOffsetAlign(AlignModeX.CENTER, AlignModeY.MIDDLE, new Numbers(this.countNeighboringMines(), (this.height / 3)).draw(), 0, 0, cellImage);
    }
    return cellImage;
  }

  // Count the number of neighboring mines
  public int countNeighboringMines() {
    int count = 0;
    for (Cell neighbor : this.neighbors) {
      if (neighbor.hasBomb) {
        count++;
      }
    }
    return count;
  }

// revealing neighboring cells without bombs.
  public boolean neighborBombs() {
    return this.neighborBombsHelper(0);
  }

  // reveals neighboring cells without bombs in this.neighbors
  // EFFECT: changes the reveal to true if there are no bombs found in this.neighbors
  public boolean neighborBombsHelper(int index) {
    if (index >= this.neighbors.size()) {
      return false;
    }
    Cell currentNeighbor = this.neighbors.get(index);

    if (currentNeighbor.hasBomb || currentNeighbor.isRevealed || currentNeighbor.hasFlag) {
      return this.neighborBombsHelper(index + 1);
    } else if (currentNeighbor.countNeighboringMines() == 0) {
      currentNeighbor.isRevealed = true;
      currentNeighbor.neighborBombs();
      return this.neighborBombsHelper(index + 1);
    } else if (currentNeighbor.countNeighboringMines() != 0) {
      currentNeighbor.isRevealed = true;
      return this.neighborBombsHelper(index + 1);
    }
    return this.neighborBombsHelper(index + 1);
  }
}

/*
* To draw the image of a flag
*
* methods:
*
* draw()-- WorldImage
*
* */
class Flag implements IGamePieces {
  public WorldImage draw() {
    return new TriangleImage(new Posn(0, 0), new Posn(10,10), new Posn(20, 0), OutlineMode.SOLID, Color.orange);
  }
}

/*
*
* Used to draw the text for a number to put into the cell later on.
*
* fields:
*
* ...num...--int
* ...size...--int
*
* methods:
*
* draw()-- WorldImage
*
* */
class Numbers implements IGamePieces {
  int num;
  int size;
  Numbers(int num, int size) {
    this.num = num;
    this.size = size;
  }
  // Draw a Number
  public WorldImage draw() {
    return new TextImage("" + this.num, this.size, FontStyle.BOLD, Color.WHITE);
  }
}


