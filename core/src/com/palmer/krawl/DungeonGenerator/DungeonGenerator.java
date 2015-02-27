package com.palmer.krawl.DungeonGenerator;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.palmer.krawl.SpanningTree.MinimumSpanningTree;
import com.palmer.krawl.delaunay_triangulation.DelaunayTriangulation;
import com.palmer.krawl.delaunay_triangulation.PointDT;
import com.palmer.krawl.delaunay_triangulation.TriangleDT;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
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
    private Set<PointDT> minimumSpanningTree;
    public DungeonGenerator() {

    }

    public List<Room> generateDungeon(int numberOfRoomsToGenerate, int radius){
        this.radius = radius;
        long startTime = System.currentTimeMillis();
        rooms = new CopyOnWriteArrayList<Room>();
        for (int roomCount = 0; roomCount < numberOfRoomsToGenerate; roomCount++) {
            Room room = null;
            if (roomCount == 0) {
                room = new Room(new Rectangle(0, 0, getRandom(20, 30), getRandom(20, 30)));
                room.setColor(Color.RED);
            } else {
                room = new Room(new Rectangle(0, 0, getRandom(10, 30), getRandom(10, 30)));
            }
            room.setName("Room" + roomCount);

            rooms.add(room);
        }
        System.out.println(numberOfRoomsToGenerate + " rooms generated");
//        removeUnwantedRooms();
        separateRooms(rooms);
        generateParentContainer(rooms);
        generateSingleCellRooms(rooms);
        mapRoomPoints();
        MinimumSpanningTree minimumSpanningTree = new MinimumSpanningTree();
        this.minimumSpanningTree = minimumSpanningTree.generateMiminumSpanningTree(this.getTriangles());
        setStartRoom(rooms);
        setBossRoom(rooms);
        renderTime = System.currentTimeMillis() - startTime;
        return rooms;
    }
    public void separateRooms(List<Room> rooms) {
        int count = 0;

        for (Room room: rooms) {

            while (overlapsAnyOtherRoom(room, rooms)) {
                int dir = getRandom(0, 3);
                switch(dir) {
                    case 0:
                        room.getRectangle().x -= 1;
                        break;
                    case 1:
                        room.getRectangle().x += 1;
                        break;
                    case 2:
                        room.getRectangle().y -= 1;
                        break;
                    case 3:
                        room.getRectangle().y += 1;
                        break;
                }
                count++;
            }
        }
        System.out.println("Separated rooms " + count + " times");
    }
    private boolean overlapsAnyOtherRoom(Room room, List<Room> rooms) {
        boolean overlaps = false;
        for (Room checkRoom : rooms) {
            if (!room.equals(checkRoom)) {
                if (room.getRectangle().overlaps(checkRoom.getRectangle())) {
                    return true;
                }
            }
        }
        return overlaps;
    }
    private void removeSoloRooms(List<Room> rooms) {
        for (Room room : rooms) {
            if (isSoloRoom(room, rooms)) {
                rooms.remove(room);
                System.out.println("Removing Room: " + room.getName());
            }
        }
    }
    private boolean isSoloRoom(Room room, List<Room> rooms) {
        boolean isSolo = true;
        for (Room checkRoom : rooms) {
            if (!room.equals(checkRoom)) {
                if (room.getRectangle().contains(checkRoom.getRectangle().x, checkRoom.getRectangle().y)) {
                    return false;
                }
            }
        }
        return isSolo;
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
    public Set<PointDT> getMinimumSpanningTree(){return  this.minimumSpanningTree;}
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

    private void setStartRoom(List<Room> rooms) {
        int startRoom = getRandom(0, rooms.size() - 1);
        while(!(rooms.get(startRoom).getRectangle().width > 12) || !(rooms.get(startRoom).getRectangle().height > 12)) {
            startRoom = getRandom(0, rooms.size() -1);
        }
        this.startRoom = rooms.get(startRoom);
    }
    private void setBossRoom(List<Room> rooms) {
        int bossRoom = getRandom(0, rooms.size() - 1);
        while(!(rooms.get(bossRoom).getRectangle().width > 12) || !(rooms.get(bossRoom).getRectangle().height > 12) && !rooms.get(bossRoom).getRectangle().contains(startRoom.getRectangle())) {
            bossRoom = getRandom(0, rooms.size() -1);
        }
        this.bossRoom = rooms.get(bossRoom);
    }

    public List<Room> getSingleCellRooms() {
        return this.singleCellRooms;
    }

    public List<Room> getRooms() {
        return this.rooms;
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

    private int getDifference(int num1, int num2) {
        if (num1 > num2) {
            return num1 - num2;
        } else {
            return num2 - num1;
        }
    }
}
