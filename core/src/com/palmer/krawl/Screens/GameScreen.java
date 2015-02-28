package com.palmer.krawl.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.palmer.krawl.DungeonGenerator.DungeonGenerator;
import com.palmer.krawl.DungeonGenerator.Room;
import com.palmer.krawl.Krawl;
import com.palmer.krawl.delaunay_triangulation.PointDT;
import com.palmer.krawl.delaunay_triangulation.TriangleDT;

import java.util.Arrays;
import java.util.List;

public class GameScreen implements Screen, GestureDetector.GestureListener, InputProcessor {

    private Krawl krawl;
    private OrthographicCamera camera;
    private ShapeRenderer shapeRenderer;
    private List<Room> rooms;
    private OrthogonalTiledMapRenderer mapRenderer;
    int maxRoomSize;
    int radius;
    float initialZoom;
    private SpriteBatch spriteBatch;
    private BitmapFont font;
    boolean showSingleCellRooms = false;
    boolean showRoomPoints = false;
    boolean showSpanningTree = true;
    boolean roomsGenerated = false;
    private static final int MIN_ROOMS = 5;
    private static final int MAX_ROOMS = 10;

    private DungeonGenerator dungeonGenerator;

    public GameScreen(Krawl krawl) {
        this.krawl = krawl;
        GestureDetector gestureDetector = new GestureDetector(this);
        Gdx.input.setInputProcessor(gestureDetector);
    }

    @Override
    public void show() {
    FreeTypeFontGenerator fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/raleway.ttf"));
        font =fontGenerator.generateFont(32);
        spriteBatch = new SpriteBatch();
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());


        dungeonGenerator = new DungeonGenerator();
        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setAutoShapeType(true);
        maxRoomSize = MathUtils.random(MIN_ROOMS, MAX_ROOMS);
        radius = 0;
        rooms = dungeonGenerator.generateDungeon(maxRoomSize, radius);
        roomsGenerated = true;
        camera.position.set(dungeonGenerator.getStartVector(), 0);
        camera.zoom =  ((float)Gdx.graphics.getHeight() / (float)Gdx.graphics.getWidth()) * .04f;

        camera.update();



    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        shapeRenderer.setProjectionMatrix(camera.combined);

        //Show the rooms generated
        shapeRenderer.begin();
            for (Room room : rooms) {
                shapeRenderer.setColor(room.getColor());
                shapeRenderer.rect(room.getRectangle().x, room.getRectangle().y, room.getRectangle().width, room.getRectangle().height);
            }
        shapeRenderer.end();

        if (roomsGenerated) {
            //Fill the start room  && boss room for Visual Aide
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(dungeonGenerator.getStartRoom().getColor());
            shapeRenderer.rect(dungeonGenerator.getStartRoom().getRectangle().x, dungeonGenerator.getStartRoom().getRectangle().y, dungeonGenerator.getStartRoom().getRectangle().width, dungeonGenerator.getStartRoom().getRectangle().height);
            shapeRenderer.setColor(dungeonGenerator.getBossRoom().getColor());
            shapeRenderer.rect(dungeonGenerator.getBossRoom().getRectangle().x, dungeonGenerator.getBossRoom().getRectangle().y, dungeonGenerator.getBossRoom().getRectangle().width, dungeonGenerator.getBossRoom().getRectangle().height);
            shapeRenderer.end();

            //Show the Parent Container
            shapeRenderer.begin();
            shapeRenderer.setColor(Color.ORANGE);
            shapeRenderer.rect(dungeonGenerator.getParentRectangle().x, dungeonGenerator.getParentRectangle().y, dungeonGenerator.getParentRectangle().width, dungeonGenerator.getParentRectangle().height);
            shapeRenderer.end();

            //Show single rooms if enabled
            if (showSingleCellRooms) {
                shapeRenderer.begin();
                for (Room singleRoom : dungeonGenerator.getSingleCellRooms()) {
                    shapeRenderer.setColor(singleRoom.getColor());
                    shapeRenderer.rect(singleRoom.getRectangle().x, singleRoom.getRectangle().y, singleRoom.getRectangle().width, singleRoom.getRectangle().height);
                }
                shapeRenderer.end();
            }

            //Show all rooms' center points
            if (showRoomPoints) {
                shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
                for (Room room : dungeonGenerator.getRooms()) {
                    shapeRenderer.setColor(Color.MAGENTA);
                    shapeRenderer.circle(room.getRectangle().x + room.getRectangle().width / 2, room.getRectangle().y + room.getRectangle().height / 2, .5f);
                }
                shapeRenderer.end();
            }

//            Show all triangles

            if (showRoomPoints) {
                shapeRenderer.begin();
                Gdx.gl20.glLineWidth(1f);
                shapeRenderer.setColor(Color.MAGENTA);
                List<TriangleDT> triangles = dungeonGenerator.getTriangles();
                for (TriangleDT curr : triangles) {
                    if (!curr.isHalfplane()) {
                        shapeRenderer.triangle((float) curr.p1().x(), (float) curr.p1().y(), (float) curr.p2().x(), (float) curr.p2().y(), (float) curr.p3().x(), (float) curr.p3().y());
//                    shapeRenderer.line((int)curr.p1().x(), (int)curr.p1().y(), (int)curr.p2().x(), (int)curr.p2().y());
//                    shapeRenderer.line((int)curr.p2().x(), (int)curr.p2().y(), (int)curr.p3().x(), (int)curr.p3().y());
//                    shapeRenderer.line((int)curr.p3().x(), (int)curr.p3().y(), (int)curr.p1().x(), (int)curr.p1().y());
                        //                shapeRenderer.triangle((int)curr.p1().x(), (int)curr.p1().y(), (int)curr.p2().x(), (int)curr.p2().y(), (int)curr.p3().x(), (int)curr.p3().y());
                        //                System.out.println(curr.p1() + ", " + curr.p2() + ", "
                        //                        + curr.p3());
                    }
                }
                shapeRenderer.end();

            }
            if (showSpanningTree) {
                shapeRenderer.begin();
                shapeRenderer.setColor(Color.ORANGE);
                List<Object> spanningTree = Arrays.asList(dungeonGenerator.getMinimumSpanningTree().toArray());
                for (int x = 0; x < spanningTree.size() -2; x++) {
                    if (x != spanningTree.size() -1) {
                        PointDT spanningTreeX = (PointDT)spanningTree.get(x);
                        PointDT spanningTreeXPlus1 = (PointDT)spanningTree.get(x +1);
                        shapeRenderer.line((float)spanningTreeX.x(), (float) spanningTreeX.y(), (float) spanningTreeXPlus1.x(), (float) spanningTreeXPlus1.y());
                    }

                }
                shapeRenderer.end();
            }

            //Draw all debug info to screen
            spriteBatch.begin();
            font.setColor(1.0f, 1.0f, 1.0f, 1.0f);
            font.draw(spriteBatch, "Zoom: " + camera.zoom, 10, Gdx.graphics.getHeight() - 10);
            font.draw(spriteBatch, "Camera.X: " + camera.position.x, 10, Gdx.graphics.getHeight() - 40);
            font.draw(spriteBatch, "Camera.Y: " + camera.position.y, 10, Gdx.graphics.getHeight() - 70);
            font.draw(spriteBatch, "Rooms Start Count: " + maxRoomSize, 10, Gdx.graphics.getHeight() - 100);
            font.draw(spriteBatch, "Rooms Removed: " + dungeonGenerator.getRoomsRemovedCount(), 10, Gdx.graphics.getHeight() - 130);
            font.draw(spriteBatch, "Rooms End Count: " + dungeonGenerator.getRooms().size(), 10, Gdx.graphics.getHeight() - 160);
            font.draw(spriteBatch, "Radius: " + radius, 10, Gdx.graphics.getHeight() - 190);
            font.draw(spriteBatch, "Level Generation Time (ms): " + dungeonGenerator.getRenderTime(), 10, 40);
//            font.draw(spriteBatch, "Parent Container: " + dungeonGenerator.getParentRectangle().x + "," + dungeonGenerator.getParentRectangle().y + " WIDTH: " + dungeonGenerator.getParentRectangle().width + " HEIGHT: " + dungeonGenerator.getParentRectangle().height, 10, 70);
            spriteBatch.end();
        }
            handleInput(Gdx.graphics.getDeltaTime());

    }

    public boolean handleInput(float delta) {
        if (Gdx.input.isKeyPressed(Input.Keys.PERIOD)) {
            camera.zoom += .01f;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.COMMA)) {
            camera.zoom -= .01f;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            camera.position.x -= camera.zoom * 4;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            camera.position.x += camera.zoom * 4;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {

                camera.position.y += camera.zoom * 4;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            camera.position.y -= camera.zoom * 4;
        }
        camera.update();
        return false;
    }


    @Override
    public void resize(int width, int height) {
        camera.update();
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        mapRenderer.dispose();
        shapeRenderer.dispose();
        spriteBatch.dispose();
        font.dispose();
    }

    //MOVEMENT STUFF
    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        initialZoom = camera.zoom;
        return false;
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {
        camera.position.set(dungeonGenerator.getStartVector(), 0);
        return false;
    }

    @Override
    public boolean longPress(float x, float y) {
        maxRoomSize = MathUtils.random(MIN_ROOMS, MAX_ROOMS);
        rooms = dungeonGenerator.generateDungeon(maxRoomSize, radius);
        return false;
    }

    @Override
    public boolean fling(float velocityX, float velocityY, int button) {
//        showSingleCellRooms = !showSingleCellRooms;
        showRoomPoints = !showRoomPoints;

        return false;
    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
        camera.translate(-deltaX * camera.zoom, deltaY * camera.zoom , 0);
        camera.update();
        return false;
    }

    @Override
    public boolean zoom(float initialDistance, float distance) {
        float ratio = initialDistance / distance  ;
        camera.zoom = initialZoom * ratio;
        return false;
    }

    @Override
    public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2,
                         Vector2 pointer1, Vector2 pointer2) {

        return false;
    }

    @Override
    public boolean keyDown(int keycode) {
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        return true;
    }

    @Override
    public boolean keyTyped(char character) {
        return true;
    }

    @Override
    public boolean panStop(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {

        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
