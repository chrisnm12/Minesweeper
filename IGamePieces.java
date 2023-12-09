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
  int height;
  int width;
  Cell(int height, int width){
    this.neighbors = new ArrayList<>();
    this.hasBomb = false;
    this.hasFlag = false;
    this.height = height;
    this.width = width;
  }
  // Draw a Cell
  public WorldImage draw() {
    WorldImage cellImage = new RectangleImage(this.width, this.height, OutlineMode.SOLID, Color.cyan);
    WorldImage flagImage = new Flag().draw();

    if (hasFlag) {
      cellImage = new OverlayOffsetAlign(AlignModeX.CENTER, AlignModeY.MIDDLE, flagImage, 0, 0, cellImage);
      return cellImage;
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

}

class Flag implements IGamePieces {
  // Draw a Flag
  public WorldImage draw() {
    return new TriangleImage(new Posn(0, 0), new Posn(10,10), new Posn(20, 0), OutlineMode.SOLID, Color.orange);
  }
}

class Numbers implements IGamePieces {
  int num;
  Numbers(int num) {
    this.num = num;
  }
  // Draw a Number
  public WorldImage draw() {
    return new TextImage("" + this.num, Color.BLACK);
  }
}


