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
  public WorldImage draw() {
    return new TextImage(this.text, this.size, FontStyle.BOLD, Color.BLACK);
  }
}

class Mine implements IGamePieces {
  public WorldImage draw() {
    return new CircleImage(15, OutlineMode.SOLID, Color.RED);
  }
}

class Cell implements IGamePieces {
  ArrayList<Cell>() neighbors;
  boolean hasBomb;
  Cell(){
    this.neighbors = new ArrayList<Cell>();
    this.hasBomb = false;
  }



  public WorldImage draw() {return new RectangleImage(10, 10, OutlineMode.SOLID, Color.cyan);
  }
}

class Flag implements IGamePieces {
  public WorldImage draw() {
    return new TriangleImage(new Posn(0, 0), new Posn(10,10), new Posn(20, 0), OutlineMode.SOLID, Color.orange);
  }
}

class Numbers implements IGamePieces {
  int num;
  Numbers(int num) {
    this.num = num;
  }
  public WorldImage draw() {
    return new TextImage("" + this.num, Color.BLACK);
  }
}
