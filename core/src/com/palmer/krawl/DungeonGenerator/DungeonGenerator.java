package com.palmer.krawl.DungeonGenerator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.palmer.krawl.delaunay_triangulation.DelaunayTriangulation;
import com.palmer.krawl.delaunay_triangulation.PointDT;
import com.palmer.krawl.delaunay_triangulation.TriangleDT;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class DungeonGenerator {

    private int timesMoveRoomsCalled = 0;
    private int moveRandomCalled = 0;
    private Room startRoom;
    private Room bossRoom;
    private List<Room> rooms;
    private long renderTime;
    private Rectangle parentRectangle;
    private List<Room> singleCellRooms;
    private int radius;
    private int roomsRemoved;
    private List<Vector2> linePoints;
    private List<TriangleDT> triangles;
    public DungeonGenerator() {

    }

    public List<Room> generateDungeon(int numberOfRoomsToGenerate, int radius){
        this.radius = radius;
        long startTime = System.currentTimeMillis();
        rooms = new CopyOnWriteArrayList<Room>();
        for (int roomCount = 0; roomCount < numberOfRoomsToGenerate; roomCount++) {
            Room room = null;
            if (roomCount == 0) {
                room = new Room(new Rectangle(getRandom(0, radius), getRandom(0, radius), getRandom(12, 15), getRandom(12, 15)));
                room.setColor(Color.GREEN);
                startRoom = room;
            } else if (roomCount == numberOfRoomsToGenerate -1) {
                room = new Room(new Rectangle(getRandom(0, radius), getRandom(0, radius), getRandom(12, 30), getRandom(12, 30)));
                room.setColor(Color.RED);
                bossRoom = room;
            } else {
                room = new Room(new Rectangle(getRandom(0, radius), getRandom(0, radius), getRandom(4, 30), getRandom(4, 30)));
            }
            rooms.add(room);

        }
        Gdx.app.log("#Move Rooms Called:", " " + ++timesMoveRoomsCalled);
        Gdx.app.log("#Move Random Called:", " " + ++moveRandomCalled);
//        for (int x = 0; x < rooms.size(); x++) {
//            Gdx.app.log("------------------------------------------------------------------", "");
//            Gdx.app.log("Room #" + x + " moved left:", " " + rooms.get(x).getMovedLeft());
//            Gdx.app.log("Room #" + x + " moved right:", " " + rooms.get(x).getMovedRight());
//            Gdx.app.log("Room #" + x + " moved up:", " " + rooms.get(x).getMovedUp());
//            Gdx.app.log("Room #" + x + " moved down:", " " + rooms.get(x).getMovedDown());
//            Gdx.app.log("------------------------------------------------------------------", "");
//
//
//        }
        removeUnwantedRooms();
        separateRooms();

        generateParentContainer(rooms);
        generateSingleCellRooms(rooms);
        mapRoomPoints();
        renderTime = System.currentTimeMillis() - startTime;
        return rooms;
    }

    public void mapRoomPoints() {
        linePoints = new CopyOnWriteArrayList<Vector2>();
        for (Room room: rooms) {
            linePoints.add(new Vector2(room.getRectangle().x + room.getRectangle().width / 2, room.getRectangle().y + room.getRectangle().height / 2));
        }
        DelaunayTriangulation dt = new DelaunayTriangulation();
        for (int x = 0; x < linePoints.size(); x++) {
            Vector2 vector2 = linePoints.get(x);
            PointDT newPoint = new PointDT((int)vector2.x, (int)vector2.y);
            dt.insertPoint(newPoint);
        }

        Iterator<TriangleDT> trianglesIterator = dt.trianglesIterator();
        triangles = new CopyOnWriteArrayList<TriangleDT>();
        while (trianglesIterator.hasNext()) {
            TriangleDT curr = trianglesIterator.next();
            if (!curr.isHalfplane()) {
//                System.out.println(curr.p1() + ", " + curr.p2() + ", "
//                        + curr.p3());
                triangles.add(curr);
            }

        }

    }

    public List<Vector2> getLinePoints() {
        return this.linePoints;
    }

    public List<TriangleDT> getTriangles() {
        return this.triangles;
    }
    public void generateParentContainer(List<Room> rooms){
        int highestPoint = -9999;
        int lowestPoint = 9999;
        int farthestPointRight = -9999;
        int farthestPointLeft = 9999;
        for (Room room: rooms) {
            if ((int) room.getRectangle().x < farthestPointLeft) {
                farthestPointLeft = (int) room.getRectangle().x;
            }
            if ((int) room.getRectangle().y < lowestPoint) {
                lowestPoint = (int) room.getRectangle().y;
            }

            if ((int)room.getRectangle().y  + (int)room.getRectangle().height > highestPoint) {
                highestPoint = (int)room.getRectangle().y + (int)room.getRectangle().height;
            }
            if ((int)room.getRectangle().x + (int)room.getRectangle().width> farthestPointRight) {
                farthestPointRight = (int)room.getRectangle().x + (int)room.getRectangle().width;
            }
        }
        this.parentRectangle = new Rectangle(farthestPointLeft, lowestPoint, Math.abs(farthestPointRight) + Math.abs(farthestPointLeft), Math.abs(highestPoint) + Math.abs(lowestPoint));
    }

    public Rectangle getParentRectangle() {
        return this.parentRectangle;
    }

    public void generateSingleCellRooms(List<Room> rooms){
        singleCellRooms = new ArrayList<Room>();
        for (int x = (int)parentRectangle.x; x < (int)parentRectangle.width + (int)parentRectangle.x; x++) {
            for (int y = (int)parentRectangle.y; y < (int)parentRectangle.height + (int)parentRectangle.y; y++) {
                boolean notInExistingRoom = true;
                for (Room room : rooms) {
                    if (containsRecCoords(room.getRectangle(), new Rectangle(x, y, 0, 0))) {
                        notInExistingRoom = false;
                        break;
                    }
                }
                if (notInExistingRoom) {
                    Room newRoom = new Room(new Rectangle(x, y, 1, 1));
                    newRoom.setColor(new Color(1f, 1f, 1f, 0.5f));
                    singleCellRooms.add(newRoom);
                }
            }

        }
    }

    public List<Room> getSingleCellRooms() {
        return this.singleCellRooms;
    }

    public List<Room> getRooms() {
        return this.rooms;
    }
    public void  removeUnwantedRooms() {
        roomsRemoved = 0;
        for (Room room: rooms) {
            if (room.getRectangle().width < 12 || room.getRectangle().height < 12) {
                rooms.remove(room);
                roomsRemoved++;
            }
        }
//        radius = rooms.size() + rooms.size() /2;
//        int startRadius = 1;
//
//        for (Room room: rooms) {
//            room.getRectangle().setPosition(getRandom(0, radius), getRandom(0, radius));
////            if (startRadius >= radius) {
////                startRadius = radius;
////            } else {
////                startRadius += 1;
////            }
//            moveRoomSoThatItDoesNotOverLap(room, rooms);
//        }
    }

    public int getRoomsRemovedCount () {
        return this.roomsRemoved;
    }

    public boolean containsRecCoords (Rectangle rec1, Rectangle rectangle) {
        float xmin = rectangle.x +1;
        float xmax = xmin + rectangle.width -1;

        float ymin = rectangle.y +1;
        float ymax = ymin + rectangle.height -1;

        return ((xmin >= rec1.x && xmin <= rec1.x + rec1.width) && (xmax >= rec1.x && xmax <= rec1.x + rec1.width))
                && ((ymin >= rec1.y && ymin <= rec1.y + rec1.height) && (ymax >= rec1.y && ymax <= rec1.y + rec1.height));
    }
    public boolean containsCoords(int x, int y, Rectangle rectangle) {
        return rectangle.x -1 < x && rectangle.x-1 + rectangle.width > x && rectangle.y-1 < y && rectangle.y-1 + rectangle.height > y;

    }
    public long getRenderTime() {
        return this.renderTime;
    }
    public Vector2 getStartVector(){
        return new Vector2(startRoom.getRectangle().x + startRoom.getRectangle().width / 2, startRoom.getRectangle().y + startRoom.getRectangle().height / 2);
    }

    public Room getStartRoom() {
        return this.startRoom;
    }
    public Room getBossRoom() {
        return this.bossRoom;
    }

    public int getRandom(int min, int max){
        return MathUtils.random(min, max);
    }

    private void separateRooms() {
        for (Room room: rooms) {
//            moveRoomSoThatItDoesNotOverLap(room);
            Vector2 newVector = moveRoomSoThatItDoesNotOverLap(room);
            room.getRectangle().setPosition(newVector.x, newVector.y);
        }
    }
    public Vector2 moveRoomSoThatItDoesNotOverLap(Room currentRoom) {
        int neighbours = 0;
        Vector2 currBottomLeft = new Vector2((int)currentRoom.getRectangle().x, (int)currentRoom.getRectangle().y);
        Vector2 currBottomRight = new Vector2((int)currentRoom.getRectangle().x, (int)currentRoom.getRectangle().x + (int)currentRoom.getRectangle().width -1);
        Vector2 currTopLeft = new Vector2((int)currentRoom.getRectangle().x, (int)currentRoom.getRectangle().x + (int)currentRoom.getRectangle().height -1);
        Vector2 currTopRight = new Vector2((int)currentRoom.getRectangle().x + (int)currentRoom.getRectangle().width -1, (int)currentRoom.getRectangle().y + (int)currentRoom.getRectangle().height -1);
        Vector2 centerPointOfCurrRoom = new Vector2 (((int)currentRoom.getRectangle().x + (int)currentRoom.getRectangle().width -1 )/ 2, (((int)currentRoom.getRectangle().y + (int)currentRoom.getRectangle().height -1)  / 2));

        Vector2 v = new Vector2((int) currentRoom.getRectangle().x, (int) currentRoom.getRectangle().y);
        for (Room room : rooms) {
            if (!currentRoom.equals(room)) {
               if (currentRoom.getRectangle().overlaps(room.getRectangle())) {
                   Vector2 roomBottomLeft = new Vector2((int)room.getRectangle().x, (int)room.getRectangle().y);
                   Vector2 roomBottomRight = new Vector2((int)room.getRectangle().x, (int)room.getRectangle().x + (int)room.getRectangle().width -1);
                   Vector2 roomTopLeft = new Vector2((int)room.getRectangle().x, (int)room.getRectangle().x + (int)room.getRectangle().height -1);
                   Vector2 roomTopRight = new Vector2((int)room.getRectangle().x + (int)room.getRectangle().width -1, (int)room.getRectangle().y + (int)room.getRectangle().height -1);
                   Vector2 centerPointOfRoom = new Vector2 (((int)room.getRectangle().x + (int)room.getRectangle().width -1 )/ 2, (((int)room.getRectangle().y + (int)room.getRectangle().height -1)  / 2));


                   if (roomIsInBottomLeftQuadrant(roomBottomLeft, currBottomLeft)) {
                       int diffLeft = (int)currBottomLeft.x - (int)roomBottomLeft.x;
                       int diffDown = (int)currBottomLeft.x - (int)roomBottomLeft.x;
                       System.out.println("Is in bottom left");
                       System.out.println("CurrRoomCoords:\nX1,Y1: " + currBottomLeft.x + "," + currBottomLeft.y + "\n" +
                               "X1,Y2: " + currTopLeft.x + "," + currTopLeft.y + "\n" +
                               "X2,Y1: " + currBottomRight.x + "," + currBottomRight.y + "\n" +
                               "X2,Y2: " + currTopLeft.x + "," + currTopRight +"\n");
                       System.out.println("RoomCoords:\nX1,Y1: " + roomBottomLeft.x + "," + roomBottomLeft.y + "\n" +
                               "X1,Y2: " + roomTopLeft.x + "," + roomTopLeft.y + "\n" +
                               "X2,Y1: " + roomBottomRight.x + "," + roomBottomRight.y + "\n" +
                               "X2,Y2: " + roomTopLeft.x + "," + roomTopRight.y +"\n");
                       System.out.println("--------------------------------------------------------------");
                   }
                   if (roomIsInTopLeftQuadrant(roomBottomLeft, currBottomLeft)) {
                       System.out.println("Is in top left");
                       System.out.println("CurrRoomCoords:\nX1,Y1: " + currBottomLeft.x + "," + currBottomLeft.y + "\n" +
                               "X1,Y2: " + currTopLeft.x + "," + currTopLeft.y + "\n" +
                               "X2,Y1: " + currBottomRight.x + "," + currBottomRight.y + "\n" +
                               "X2,Y2: " + currTopLeft.x + "," + currTopRight +"\n");
                       System.out.println("RoomCoords:\nX1,Y1: " + roomBottomLeft.x + "," + roomBottomLeft.y + "\n" +
                               "X1,Y2: " + roomTopLeft.x + "," + roomTopLeft.y + "\n" +
                               "X2,Y1: " + roomBottomRight.x + "," + roomBottomRight.y + "\n" +
                               "X2,Y2: " + roomTopLeft.x + "," + roomTopRight.y +"\n");
                       System.out.println("--------------------------------------------------------------");
                   }
                   if (roomIsInTopRightQuadrant(roomBottomLeft, currBottomLeft)) {
                       System.out.println("Is in top right");
                       System.out.println("CurrRoomCoords:\nX1,Y1: " + currBottomLeft.x + "," + currBottomLeft.y + "\n" +
                               "X1,Y2: " + currTopLeft.x + "," + currTopLeft.y + "\n" +
                               "X2,Y1: " + currBottomRight.x + "," + currBottomRight.y + "\n" +
                               "X2,Y2: " + currTopLeft.x + "," + currTopRight.y +"\n");
                       System.out.println("RoomCoords:\nX1,Y1: " + roomBottomLeft.x + "," + roomBottomLeft.y + "\n" +
                               "X1,Y2: " + roomTopLeft.x + "," + roomTopLeft.y + "\n" +
                               "X2,Y1: " + roomBottomRight.x + "," + roomBottomRight.y + "\n" +
                               "X2,Y2: " + roomTopLeft.x + "," + roomTopRight.y +"\n");
                       System.out.println("--------------------------------------------------------------");
                   }

                   neighbours++;


               }
            }
        }
//        System.out.println("NeighborCount: " + neighbours);

        if (neighbours != 0) {
            v.x = v.x / neighbours;
            v.y = v.y / neighbours;
            v.x = v.x * -1;
            v.y = v.y * -1;
            return v.nor();
        } else {
            return v;
        }
    }

    private boolean roomIsInBottomLeftQuadrant(Vector2 room, Vector2 currRoom) {

        return (room.x <= currRoom.x && room.y <= currRoom.y);
    }

    private boolean roomIsInTopLeftQuadrant(Vector2 room, Vector2 currRoom) {

        return (room.x <= currRoom.x && room.y >= currRoom.y);
    }

    private boolean roomIsInTopRightQuadrant(Vector2 room, Vector2 currRoom) {

        return (room.x >= currRoom.x && room.y >= currRoom.y);
    }

    private int getDifference(int num1, int num2) {
        if (num1 > num2) {
            return num1 - num2;
        } else {
            return num2 - num1;
        }
    }
    private void moveRoomInRandomDirection(Room room) {
        ++moveRandomCalled;
        int moveDirection = getRandom(0, 15);
        if (moveDirection <= 5) {
            room.getRectangle().setPosition(room.getRectangle().x -1, room.getRectangle().y);
            room.setMovedLeft(room.getMovedLeft() + 1);
        }
        if (moveDirection > 5 && moveDirection <= 10) {
            room.getRectangle().setPosition(room.getRectangle().x +1, room.getRectangle().y);
            room.setMovedRight(room.getMovedRight() + 1);
        }
        if (moveDirection > 10 && moveDirection <=12) {
            room.getRectangle().setPosition(room.getRectangle().x, room.getRectangle().y + 1);
            room.setMovedUp(room.getMovedUp() + 1);
        }

        if (moveDirection > 12) {
            room.getRectangle().setPosition(room.getRectangle().x, room.getRectangle().y -1);
            room.setMovedDown(room.getMovedDown() + 1);
        }
    }

}
