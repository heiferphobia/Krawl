package com.palmer.krawl.DungeonGenerator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;


public class RoomGenerator {

    private int mapWidth;
    private int mapHeight;
    private Rectangle mapRectangle;
    private Vector2 cameraStartPosition;
    private int mapObjects;
    private TiledMapTileLayer tiledMapTileLayer;
    private TextureRegion wall = new TextureRegion(new Texture(Gdx.files.internal("textures/wall.png")));
    private TextureRegion door = new TextureRegion(new Texture(Gdx.files.internal("textures/door.png")));
    private TextureRegion stoneWall = new TextureRegion(new Texture(Gdx.files.internal("textures/stonewall.png")));
    private TextureRegion dirt = new TextureRegion(new Texture(Gdx.files.internal("textures/dirt.png")));
    private TextureRegion floor = new TextureRegion(new Texture(Gdx.files.internal("textures/floor.png")));

    public enum Direction {
        NORTH, SOUTH, EAST, WEST
    }
    public RoomGenerator() {

    }

    public TiledMapTileLayer generateRooms(int mapWidth, int mapHeight, int mapObjects) {
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
        this.mapObjects = mapObjects;
        this.mapRectangle = new Rectangle(0, 0, this.mapWidth, this.mapHeight);
        tiledMapTileLayer = new TiledMapTileLayer(mapWidth, mapHeight, 32, 32);
        initialize();
        createStartRoom();


        return tiledMapTileLayer;
    }


    private void initialize() {
        for (int y = 0; y < this.mapHeight; y++)
        {
            for (int x = 0; x < this.mapWidth; x++)
            {
                // ie, making the borders of unwalkable walls
                if (y == 0 || y == this.mapHeight - 1 || x == 0 || x == this.mapWidth - 1)
                {
                    TiledMapTileLayer.Cell newWallCell = new TiledMapTileLayer.Cell();
                    newWallCell.setTile( new StaticTiledMapTile(wall));
                    newWallCell.getTile().getProperties().put("stonewall", true);
                    tiledMapTileLayer.setCell(x, y, newWallCell);
                }
//                else
//                {                        // and fill the rest with dirt
//                    TiledMapTileLayer.Cell dirtCell = new TiledMapTileLayer.Cell();
//                    dirtCell.setTile(new StaticTiledMapTile(dirt));
//                    dirtCell.getTile().getProperties().put("empty", true);
//                    tiledMapTileLayer.setCell(x, y, dirtCell);
//                }
            }
        }
    }
    private int getRandom(int start, int end) {
        return MathUtils.random(start, end);
    }

    private void createStartRoom() {
        //map rectangle to use for overlap checks
        Rectangle room = generateValidRoom(0, 0, 6, 10, 6, 10, false, false);
        createRoom(room);
        createDoorways(room);
        this.cameraStartPosition = new Vector2(room.x, room.y);

    }

    public Vector2 getCameraStartPosition() {
        return this.cameraStartPosition;
    }
    private Rectangle generateValidRoom(int minStartPosX, int minStartPosY, int minWidth, int maxWidth, int minHeight, int maxHeight, boolean reverseX, boolean reverseY) {
        boolean roomIsValid = false;
        Rectangle room = null;
        if(reverseX){maxWidth *= -1;}
        if(reverseY){maxHeight *= -1;}
        while(!roomIsValid) {
            room = generateRandomRoom(minStartPosX, minStartPosY, minWidth, maxWidth, minHeight, maxHeight);
            if (room.x > 0 && (room.x + room.width) < mapWidth &&
                    room.y < mapWidth && (room.y + room.height) < mapHeight){
                roomIsValid = true;
            }
        }


        Gdx.app.log("Map Data:", "Width: " + room.width + " | Height: " +room.height+" | Start Position: " +room.x+"," +room.y);

        return room;
    }

    private Rectangle generateRandomRoom(int minStartPosX, int minStartPosY, int minWidth, int maxWidth, int minHeight, int maxHeight) {
        return new Rectangle(getRandom(minStartPosX, mapWidth -1),
                getRandom(minStartPosY, mapHeight -1),
                getRandom(minWidth, maxWidth),
                getRandom(minHeight, maxHeight));
    }

    private void createRoom(Rectangle room){
        for (int tileX = 0; tileX < room.width; tileX++) {
            for (int tileY = 0; tileY < room.height; tileY++) {
                //Add a wall to the left and right edges
                if (tileX == 0 || tileX == room.width -1) {
                    tiledMapTileLayer.setCell((int)room.x + tileX, (int)room.y + tileY, createWallCell());
                }
                //Add a wall to the top and bottom edges
                else if (tileY == 0 || tileY == room.height -1) {
                    tiledMapTileLayer.setCell((int) room.x + tileX, (int) room.y + tileY, createWallCell());
                }
                else {
                    tiledMapTileLayer.setCell((int)room.x + tileX, (int)room.y + tileY, createFloorCell());
                }
            }
        }
    }

    private void createDoorways(Rectangle room) {

        int numberOfExits = getRandom(1, 6);
        Gdx.app.log("Number of exits: ", "" + numberOfExits);
        for (int x = 0; x < numberOfExits; x++) {
            boolean exitNotFound = true;
            Direction direction = getRandomDirection();
            do {
                if (direction.equals(Direction.WEST) && room.x - 1 > 0) {
                    Vector2 leftWallYLocation = new Vector2(room.x, getRandom((int)room.y, (int)(room.y + room.height -1)));
                    Gdx.app.log("WEST: ", "" + leftWallYLocation.x + "," + leftWallYLocation.y);
                    tiledMapTileLayer.setCell((int) leftWallYLocation.x, (int) leftWallYLocation.y, createFloorCell());

                    exitNotFound = false;
                } else if (direction.equals(Direction.EAST) && (room.x + room.width) < mapWidth) {
                    Vector2 rightWallLocation = new Vector2(room.x + room.width -1, getRandom((int)room.y, (int)(room.y + room.height -1)));
                    Gdx.app.log("EAST: ", "" + rightWallLocation.x + "," + rightWallLocation.y);
                    tiledMapTileLayer.setCell((int)rightWallLocation.x, (int)rightWallLocation.y, createFloorCell());

                    exitNotFound = false;
                } else if (direction.equals(Direction.NORTH) && room.y - 1 > 0) {
                    Vector2 topWallLocation = new Vector2(getRandom((int)room.x, (int)room.width-1 + (int)room.x), room.height -1 + room.y);
                    Gdx.app.log("NORTH: ", "" + topWallLocation.x + "," + topWallLocation.y);
                    tiledMapTileLayer.setCell((int)topWallLocation.x, (int)topWallLocation.y, createFloorCell());

                    exitNotFound = false;
                } else if (direction.equals(Direction.SOUTH) && (room.y + room.height) < mapHeight) {
                    Vector2 bottomWallLocation = new Vector2(getRandom((int)room.x, (int)room.width-1 + (int)room.x), room.y);
                    Gdx.app.log("SOUTH: ", "" + bottomWallLocation.x + "," + bottomWallLocation.y);
                    tiledMapTileLayer.setCell((int)bottomWallLocation.x, (int)bottomWallLocation.y, createFloorCell());

                    exitNotFound = false;
                }
                handleCornerDoors(room);

            } while (exitNotFound);

        }
        for (int doorways = 0; doorways<numberOfExits; doorways++) {

        }
    }

    private void handleCornerDoors(Rectangle room) {
        Vector2 roomX1Y1 = new Vector2(room.x, room.y);
        Vector2 roomX2Y1 = new Vector2(room.x + room.width -1, room.y);
        Vector2 roomX1Y2 = new Vector2(room.x, room.y + room.height -1);
        Vector2 roomX2Y2 = new Vector2(room.x +room.width -1, room.y + room.height -1);

            //Handle bottom left
            if (isDoorTile((int)roomX1Y1.x, (int)roomX1Y1.y)){
                tiledMapTileLayer.setCell((int)roomX1Y1.x , (int)roomX1Y1.y +1, createFloorCell());
                tiledMapTileLayer.setCell((int)roomX1Y1.x +1, (int)roomX1Y1.y, createFloorCell());
                Gdx.app.log("BottomLeftAdjusted", "");
            }
            //Handle top left
            if (isDoorTile((int)roomX1Y2.x, (int)roomX1Y2.y)){
                tiledMapTileLayer.setCell((int)roomX1Y2.x + 1, (int)roomX1Y2.y, createFloorCell());
                tiledMapTileLayer.setCell((int)roomX1Y2.x, (int)roomX1Y2.y -1, createFloorCell());
                Gdx.app.log("TopLeftAdjusted", "");

            }
            //Handle top right
            if (isDoorTile((int)roomX2Y2.x, (int)roomX2Y2.y)){
                tiledMapTileLayer.setCell((int)roomX2Y2.x, (int)roomX2Y2.y -1, createFloorCell());
                tiledMapTileLayer.setCell((int)roomX2Y2.x -1, (int)roomX2Y2.y, createFloorCell());
                Gdx.app.log("TopRightAdjusted", "");
            }
            //Handle bottom right
            if (isDoorTile((int)roomX2Y1.x, (int)roomX2Y1.y)){
                tiledMapTileLayer.setCell((int)roomX2Y1.x -1, (int)roomX2Y1.y, createFloorCell());
                tiledMapTileLayer.setCell((int)roomX2Y1.x, (int)roomX2Y1.y +1, createFloorCell());
                Gdx.app.log("BottomRightAdjusted", "");

            }
    }

    private boolean isDoorTile(int x, int y) {
        if (tiledMapTileLayer.getCell(x, y).getTile().getProperties() != null) {
            return tiledMapTileLayer.getCell(x, y).getTile().getProperties().containsKey("floor");
        }
        return false;
    }


    private Direction getRandomDirection() {
        int directionValue = getRandom(0, 3);
        Direction direction = null;
        switch(directionValue) {
            case 0:
                direction =Direction.WEST;
                break;
            case 1:
                direction = Direction.EAST;
                break;
            case 2:
                direction = Direction.NORTH;
                break;
            case 3:
                direction = Direction.SOUTH;
                break;
        }
        return direction;
    }

    private Cell createFloorCell() {
        Cell cell = new Cell();
        cell.setTile(new StaticTiledMapTile(floor));
        cell.getTile().getProperties().put("floor", true);
        cell.getTile().getProperties().put("collidable", false);
        return cell;
    }
    private Cell createWallCell() {
        Cell cell = new Cell();
        cell.setTile(new StaticTiledMapTile(stoneWall));
        cell.getTile().getProperties().put("wall", true);
        cell.getTile().getProperties().put("collidable", true);
        return cell;
    }
    private Cell createDirtCell() {
        Cell cell = new Cell();
        cell.setTile(new StaticTiledMapTile(dirt));
        cell.getTile().getProperties().put("dirt", true);
        cell.getTile().getProperties().put("collidable", false);
        return cell;
    }



}
