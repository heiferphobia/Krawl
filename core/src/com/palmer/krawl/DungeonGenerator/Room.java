package com.palmer.krawl.DungeonGenerator;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;

public class Room {

    private Color color;
    private Rectangle rectangle;
    private int movedLeft = 0;
    private int movedRight = 0;
    private int movedDown = 0;
    private int movedUp = 0;
    private String name;

    public Room(Rectangle rectangle) {
        this.rectangle = rectangle;
        this.color = new Color(Color.BLUE);
//        this.color = new Color(MathUtils.random(1, 255)/255f, MathUtils.random(1, 255)/255f, MathUtils.random(1, 255)/255f, 1);
    }
    public void setName(String name) { this.name = name;}
    public String getName() { return this.name;}
    public void setColor(Color color) {this.color = color;}
    public Color getColor() {
        return this.color;
    }
    public Rectangle getRectangle() {
        return this.rectangle;
    }

    public int getMovedLeft() {
        return movedLeft;
    }

    public void setMovedLeft(int movedLeft) {
        this.movedLeft = movedLeft;
    }

    public int getMovedRight() {
        return movedRight;
    }

    public void setMovedRight(int movedRight) {
        this.movedRight = movedRight;
    }

    public int getMovedDown() {
        return movedDown;
    }

    public void setMovedDown(int movedDown) {
        this.movedDown = movedDown;
    }

    public int getMovedUp() {
        return movedUp;
    }

    public void setMovedUp(int movedUp) {
        this.movedUp = movedUp;
    }
}
