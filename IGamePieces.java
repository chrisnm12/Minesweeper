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
 * neighborBombs()-- void
 *
 * // gives a cell a bomb
 * // EFFECT: sets the cells hasBomb field to true
 * giveBomb()-- void
 *
 * // adds a cell to a ths cells ArrayList of neighbors
 * // EFFECT: this cell's list of neighbors is increasing.
 * addNeighbor(Cell c)-- void
 *
 * // checks to see if this cell has a bomb
 * isHasBomb()--boolean
 *
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
    WorldImage mineImage = new Mine(this.height / 3).draw();

    if (hasFlag) {
      cellImage = new OverlayOffsetAlign(AlignModeX.CENTER, AlignModeY.MIDDLE, flagImage, 0, 0, cellImage);
    }
    else if (hasBomb) {
      cellImage = new OverlayOffsetAlign(AlignModeX.CENTER, AlignModeY.MIDDLE, cellImage, 0,0, mineImage);
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
  // EFFECT: changes the reveal to true if there are no bombs found in this.neighbors
  public void neighborBombs() {
    this.neighborBombsHelper(0);
  }

  // reveals neighboring cells without bombs in this.neighbors
  // EFFECT: changes the reveal to true if there are no bombs found in this.neighbors
  public void neighborBombsHelper(int index) {
    if (index >= this.neighbors.size()) {
      return;
    }
    Cell currentNeighbor = this.neighbors.get(index);

    if (currentNeighbor.hasBomb || currentNeighbor.isRevealed || currentNeighbor.hasFlag) {
      this.neighborBombsHelper(index + 1);
    } else if (currentNeighbor.countNeighboringMines() == 0) {
      currentNeighbor.isRevealed = true;
      currentNeighbor.neighborBombs();
      this.neighborBombsHelper(index + 1);
    } else if (currentNeighbor.countNeighboringMines() != 0) {
      currentNeighbor.isRevealed = true;
      this.neighborBombsHelper(index + 1);
    }
    this.neighborBombsHelper(index + 1);
  }

  // gives a cell a bomb
  // EFFECT: sets the cells hasBomb field to true
  public void giveBomb(){
    this.hasBomb = true;
  }

  // adds a cell to a ths cells ArrayList of neighbors
  // EFFECT: this cell's list of neighbors is increasing.
  public void addNeighbor(Cell c){
    this.neighbors.add(c);
  }

  // checks to see if this cell has a bomb
  public boolean isBomb(){
    return this.hasBomb;
  }

  // checks to see if the bomb is revealed
  public boolean isRevealed(){
    return this.isRevealed;
  }

  public ArrayList<Cell> getNeighbors() {
    return neighbors;
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
  // Draws the Number onto the cell
  public WorldImage draw() {
    return new TextImage("" + this.num, this.size, FontStyle.BOLD, Color.WHITE);
  }
}


class ExampleCells {

  Cell c1 = new Cell(1, 1);
  Cell c2 = new Cell(1, 1);
  Cell c3 = new Cell(1, 1);
  Cell c4 = new Cell(1, 1);
  Cell c5 = new Cell(1, 1);


  Cell c6 = new Cell(1, 1);
  Cell c7 = new Cell(1, 1);
  Cell c8 = new Cell(1, 1);
  Cell c9 = new Cell(1, 1);
  Cell c10 = new Cell(1, 1);



  // testing the linking of the addCell method.

  void testAdd(Tester t) {
    Cell exampleCell = new Cell(1, 1);
    exampleCell.addNeighbor(c1);
    exampleCell.addNeighbor(c2);
    exampleCell.addNeighbor(c3);
    t.checkExpect(exampleCell.neighbors.get(0), c1);
    t.checkExpect(exampleCell.neighbors.get(1), c2);
    t.checkExpect(exampleCell.neighbors.get(2),c3);

  }


  void testgiveBomb(Tester t){
    Cell exampleCell = new Cell(1,1);
    Cell exampleCell2 = new Cell(1,1);

    exampleCell2.giveBomb();


    t.checkExpect(exampleCell.isBomb(), false);
    t.checkExpect(exampleCell2.isBomb(), true);

  }


  void testcountNeighboringMines(Tester t){
    Cell exampleCell = new Cell(1,1);
    exampleCell.addNeighbor(c1);
    exampleCell.addNeighbor(c2);
    exampleCell.addNeighbor(c3);
    exampleCell.addNeighbor(c4);
    exampleCell.addNeighbor(c5);

    Cell exampleCell2 = new Cell (1,1);

    exampleCell2.addNeighbor(c6);
    exampleCell2.addNeighbor(c7);
    exampleCell2.addNeighbor(c8);
    exampleCell2.addNeighbor(c9);
    exampleCell2.addNeighbor(c10);


    Cell exampleCell3 = new Cell (1,1);

    exampleCell3.addNeighbor(c9);
    exampleCell3.addNeighbor(c6);
    exampleCell3.addNeighbor(c2);
    exampleCell3.addNeighbor(c8);
    exampleCell3.addNeighbor(c1);

    c8.giveBomb();
    c9.giveBomb();
    c10.giveBomb();


    t.checkExpect(exampleCell.countNeighboringMines(), 0);
    t.checkExpect(exampleCell2.countNeighboringMines(), 3);
    t.checkExpect(exampleCell3.countNeighboringMines(), 2);


  }


  void testneighborBombs(Tester t){

    // wrote another example that is cyclical based on a 3x3 grid scale with a bomb in 9

    Cell exampleCell1 = new Cell(1,1);
    Cell exampleCell2 = new Cell(1,1);
    Cell exampleCell3 = new Cell(1,1);
    Cell exampleCell4 = new Cell(1,1);
    Cell exampleCell5 = new Cell(1,1);
    Cell exampleCell6 = new Cell(1,1);
    Cell exampleCell7 = new Cell(1,1);
    Cell exampleCell8 = new Cell(1,1);
    Cell exampleCell9 = new Cell(1,1);


    // cell 1 gets 2, 5, 4
    exampleCell1.addNeighbor(exampleCell2);
    exampleCell1.addNeighbor(exampleCell4);
    exampleCell1.addNeighbor(exampleCell5);

    // cell 2 gets 1, 4, 5 ,6 ,3
    exampleCell2.addNeighbor(exampleCell1);
    exampleCell2.addNeighbor(exampleCell4);
    exampleCell2.addNeighbor(exampleCell5);
    exampleCell2.addNeighbor(exampleCell6);
    exampleCell2.addNeighbor(exampleCell3);


    // cell 3 gets 2, 5, 6
    exampleCell3.addNeighbor(exampleCell2);
    exampleCell3.addNeighbor(exampleCell5);
    exampleCell3.addNeighbor(exampleCell6);

    // cell 4 gets 1, 2, 5, 8, 7
    exampleCell4.addNeighbor(exampleCell1);
    exampleCell4.addNeighbor(exampleCell2);
    exampleCell4.addNeighbor(exampleCell5);
    exampleCell4.addNeighbor(exampleCell8);
    exampleCell4.addNeighbor(exampleCell7);

    // cell 5 gets 1, 2, 3, 4, 6, 7, 8, 9
    exampleCell5.addNeighbor(exampleCell1);
    exampleCell5.addNeighbor(exampleCell2);
    exampleCell5.addNeighbor(exampleCell3);
    exampleCell5.addNeighbor(exampleCell4);
    exampleCell5.addNeighbor(exampleCell6);
    exampleCell5.addNeighbor(exampleCell7);
    exampleCell5.addNeighbor(exampleCell8);
    exampleCell5.addNeighbor(exampleCell9);

    // cell 6 gets 3, 2, 5, 8, 9

    exampleCell6.addNeighbor(exampleCell3);
    exampleCell6.addNeighbor(exampleCell2);
    exampleCell6.addNeighbor(exampleCell5);
    exampleCell6.addNeighbor(exampleCell8);
    exampleCell6.addNeighbor(exampleCell9);

    // cell 7 gets 4, 5, 8

    exampleCell7.addNeighbor(exampleCell4);
    exampleCell7.addNeighbor(exampleCell5);
    exampleCell7.addNeighbor(exampleCell8);

    // cell 8 gets 7, 4, 5, 6, 9

    exampleCell8.addNeighbor(exampleCell7);
    exampleCell8.addNeighbor(exampleCell4);
    exampleCell8.addNeighbor(exampleCell5);
    exampleCell8.addNeighbor(exampleCell6);
    exampleCell8.addNeighbor(exampleCell9);

    // cell 9 gets 8, 5, 6

    exampleCell9.addNeighbor(exampleCell8);
    exampleCell9.addNeighbor(exampleCell5);
    exampleCell9.addNeighbor(exampleCell6);


    // implanting bomb into cell 9
    exampleCell9.giveBomb();

    // flooding here when clicked on exampleCell1
    exampleCell1.neighborBombs();

    t.checkExpect(exampleCell1.isRevealed(), true);
    t.checkExpect(exampleCell2.isRevealed(), true);
    t.checkExpect(exampleCell3.isRevealed(), true);
    t.checkExpect(exampleCell4.isRevealed(), true);
    t.checkExpect(exampleCell5.isRevealed(), true);
    t.checkExpect(exampleCell6.isRevealed(), true);
    t.checkExpect(exampleCell7.isRevealed(), true);
    t.checkExpect(exampleCell8.isRevealed(), true);
    t.checkExpect(exampleCell9.isRevealed(), false);
  }

  void testneighborBombs2(Tester t){

    // this one test if the correct response comes from a cell that does not get to flood passed the number hit because it is adjacent to a bomb.

    Cell exampleCell1 = new Cell(1,1);
    Cell exampleCell2 = new Cell(1,1);
    Cell exampleCell3 = new Cell(1,1);
    Cell exampleCell4 = new Cell(1,1);
    Cell exampleCell5 = new Cell(1,1);
    Cell exampleCell6 = new Cell(1,1);
    Cell exampleCell7 = new Cell(1,1);
    Cell exampleCell8 = new Cell(1,1);
    Cell exampleCell9 = new Cell(1,1);


    // cell 1 gets 2, 5, 4
    exampleCell1.addNeighbor(exampleCell2);
    exampleCell1.addNeighbor(exampleCell4);
    exampleCell1.addNeighbor(exampleCell5);

    // cell 2 gets 1, 4, 5 ,6 ,3
    exampleCell2.addNeighbor(exampleCell1);
    exampleCell2.addNeighbor(exampleCell3);
    exampleCell2.addNeighbor(exampleCell4);
    exampleCell2.addNeighbor(exampleCell5);
    exampleCell2.addNeighbor(exampleCell6);


    // cell 3 gets 2, 5, 6
    exampleCell3.addNeighbor(exampleCell2);
    exampleCell3.addNeighbor(exampleCell5);
    exampleCell3.addNeighbor(exampleCell6);

    // cell 4 gets 1, 2, 5, 8, 7
    exampleCell4.addNeighbor(exampleCell1);
    exampleCell4.addNeighbor(exampleCell2);
    exampleCell4.addNeighbor(exampleCell5);
    exampleCell4.addNeighbor(exampleCell7);
    exampleCell4.addNeighbor(exampleCell8);

    // cell 5 gets 1, 2, 3, 4, 6, 7, 8, 9
    exampleCell5.addNeighbor(exampleCell1);
    exampleCell5.addNeighbor(exampleCell2);
    exampleCell5.addNeighbor(exampleCell3);
    exampleCell5.addNeighbor(exampleCell4);
    exampleCell5.addNeighbor(exampleCell6);
    exampleCell5.addNeighbor(exampleCell7);
    exampleCell5.addNeighbor(exampleCell8);
    exampleCell5.addNeighbor(exampleCell9);

    // cell 6 gets 3, 2, 5, 8, 9

    exampleCell6.addNeighbor(exampleCell2);
    exampleCell6.addNeighbor(exampleCell3);
    exampleCell6.addNeighbor(exampleCell5);
    exampleCell6.addNeighbor(exampleCell8);
    exampleCell6.addNeighbor(exampleCell9);

    // cell 7 gets 4, 5, 8

    exampleCell7.addNeighbor(exampleCell4);
    exampleCell7.addNeighbor(exampleCell5);
    exampleCell7.addNeighbor(exampleCell8);

    // cell 8 gets 7, 4, 5, 6, 9

    exampleCell8.addNeighbor(exampleCell4);
    exampleCell8.addNeighbor(exampleCell5);
    exampleCell8.addNeighbor(exampleCell6);
    exampleCell8.addNeighbor(exampleCell7);
    exampleCell8.addNeighbor(exampleCell9);

    // cell 9 gets 8, 5, 6

    exampleCell9.addNeighbor(exampleCell5);
    exampleCell9.addNeighbor(exampleCell6);
    exampleCell9.addNeighbor(exampleCell8);


    // implanting bomb into cell 5
    exampleCell5.giveBomb();

    // flooding here when clicked on exampleCell2
    exampleCell2.neighborBombs();

    t.checkExpect(exampleCell1.isRevealed(), false);
    t.checkExpect(exampleCell2.isRevealed(), true);
    t.checkExpect(exampleCell3.isRevealed(), false);
    t.checkExpect(exampleCell4.isRevealed(), false);
    t.checkExpect(exampleCell5.isRevealed(), false);
    t.checkExpect(exampleCell6.isRevealed(), false);
    t.checkExpect(exampleCell7.isRevealed(), false);
    t.checkExpect(exampleCell8.isRevealed(), false);
    t.checkExpect(exampleCell9.isRevealed(), false);
  }

}






