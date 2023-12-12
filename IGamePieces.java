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

class StartingText implements IGamePieces {
  String text;
  int size;
  StartingText(String text, int size) {
    this.text = text;
    this.size = size;
  }
  // Draw Text
  public WorldImage draw() {
    return new TextImage(this.text, this.size, FontStyle.BOLD, Color.BLACK);
  }
}

class Mine implements IGamePieces {
  // Draw a mine
  public WorldImage draw() {
    return new CircleImage(15, OutlineMode.SOLID, Color.RED);
  }
}

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
  // Draw a Cell
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
      cellImage = new OverlayOffsetAlign(AlignModeX.CENTER, AlignModeY.MIDDLE, cellImage, 0,0, new Mine().draw());
      if (isRevealed) {
        cellImage = new Mine().draw();
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
  public boolean neighborBombs() {
    return this.neighborBombsHelper(0);
  }
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

class Flag implements IGamePieces {
  // Draw a Flag
  public WorldImage draw() {
    return new TriangleImage(new Posn(0, 0), new Posn(10,10), new Posn(20, 0), OutlineMode.SOLID, Color.orange);
  }
}

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


