package com.mygdx.battleship;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Affine2;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;


@SuppressWarnings("unused")
/**
 * This class originally created by Walker White for CS 3152.
 */
public class GameCanvas {

    private SpriteBatch sb;
    /** Cache object to handle raw textures */
    private TextureRegion holder;
    /** Affine cache for current sprite to draw */
    private Affine2 local;

    private boolean active = false;

    private ShapeRenderer debugRender;
    /** Camera for the underlying SpriteBatch */
    private OrthographicCamera camera;
    private Vector2 vertex;
    private boolean debug = false;

    public GameCanvas(){
        sb = new SpriteBatch();
        holder = new TextureRegion();
        local = new Affine2();

        debugRender = new ShapeRenderer();
        // Set the projection matrix (for proper scaling)
        camera = new OrthographicCamera(getWidth(),getHeight());
        camera.setToOrtho(false);
        sb.setProjectionMatrix(camera.combined);
        debugRender.setProjectionMatrix(camera.combined);
        vertex = new Vector2();
    }

    /**
     * Compute the affine transform (and store it in local) for this image.
     *
     * @param ox 	The x-coordinate of texture origin (in pixels)
     * @param oy 	The y-coordinate of texture origin (in pixels)
     * @param x 	The x-coordinate of the texture origin (on screen)
     * @param y 	The y-coordinate of the texture origin (on screen)
     * @param angle The rotation angle (in degrees) about the origin.
     * @param sx 	The x-axis scaling factor
     * @param sy 	The y-axis scaling factor
     */
    private void computeTransform(float ox, float oy, float x, float y, float angle, float sx, float sy) {
        local.setToTranslation(x,y);
        local.rotate(180.0f*angle/(float)Math.PI);
        local.scale(sx,sy);
        local.translate(-ox,-oy);
    }

    public void draw(Texture texture, float x, float y){
        sb.draw(texture, x, y);
    }

    /**
     * Draws the tinted texture at the given position.
     *
     * The texture colors will be multiplied by the given color.  This will turn
     * any white into the given color.  Other colors will be similarly affected.
     *
     * Unless otherwise transformed by the global transform (@see begin(Affine2)),
     * the texture will be unscaled.  The bottom left of the texture will be positioned
     * at the given coordinates.
     *
     * @param image The texture to draw
     * @param tint  The color tint
     * @param x 	The x-coordinate of the bottom left corner
     * @param y 	The y-coordinate of the bottom left corner
     * @param width	The texture width
     * @param height The texture height
     */
    public void draw(Texture image, Color tint, float x, float y, float width, float height) {
        // Unlike Lab 1, we can shortcut without a master drawing method
        sb.setColor(tint);
        sb.draw(image, x,  y, width, height);
    }

    /**
     * Draws the tinted texture at the given position.
     *
     * The texture colors will be multiplied by the given color.  This will turn
     * any white into the given color.  Other colors will be similarly affected.
     *
     * Unless otherwise transformed by the global transform (@see begin(Affine2)),
     * the texture will be unscaled.  The bottom left of the texture will be positioned
     * at the given coordinates.
     *
     * @param image The texture to draw
     * @param tint  The color tint
     * @param ox 	The x-coordinate of texture origin (in pixels)
     * @param oy 	The y-coordinate of texture origin (in pixels)
     * @param x 	The x-coordinate of the texture origin (on screen)
     * @param y 	The y-coordinate of the texture origin (on screen)
     * @param width	The texture width
     * @param height The texture height
     */
    public void draw(Texture image, Color tint, float ox, float oy, float x, float y, float width, float height) {

        // Call the master drawing method (more efficient that base method)
        holder.setRegion(image);
        draw(holder, tint, x-ox, y-oy, width, height);
    }


    /**
     * Draws the tinted texture with the given transformations
     *
     * The texture colors will be multiplied by the given color.  This will turn
     * any white into the given color.  Other colors will be similarly affected.
     *
     * The transformations are BEFORE after the global transform (@see begin(Affine2)).
     * As a result, the specified texture origin will be applied to all transforms
     * (both the local and global).
     *
     * The local transformations in this method are applied in the following order:
     * scaling, then rotation, then translation (e.g. placement at (sx,sy)).
     *
     * @param image The texture to draw
     * @param tint  The color tint
     * @param ox 	The x-coordinate of texture origin (in pixels)
     * @param oy 	The y-coordinate of texture origin (in pixels)
     * @param x 	The x-coordinate of the texture origin (on screen)
     * @param y 	The y-coordinate of the texture origin (on screen)
     * @param angle The rotation angle (in degrees) about the origin.
     * @param sx 	The x-axis scaling factor
     * @param sy 	The y-axis scaling factor
     */
    public void draw(Texture image, Color tint, float ox, float oy,
                     float x, float y, float angle, float sx, float sy) {

        // Call the master drawing method (more efficient that base method)
        holder.setRegion(image);
        draw(holder,tint,ox,oy,x,y,angle,sx,sy);
    }

    /**
     * Draws the tinted texture with the given transformations
     *
     * The texture colors will be multiplied by the given color.  This will turn
     * any white into the given color.  Other colors will be similarly affected.
     *
     * The transformations are BEFORE after the global transform (@see begin(Affine2)).
     * As a result, the specified texture origin will be applied to all transforms
     * (both the local and global).
     *
     * The local transformations in this method are applied in the following order:
     * scaling, then rotation, then translation (e.g. placement at (sx,sy)).
     *
     * @param image The texture to draw
     * @param tint  The color tint
     * @param ox 	The x-coordinate of texture origin (in pixels)
     * @param oy 	The y-coordinate of texture origin (in pixels)
     * @param transform  The image transform
     */
    public void draw(Texture image, Color tint, float ox, float oy, Affine2 transform) {
        // Call the master drawing method (we have to for transforms)
        holder.setRegion(image);
        draw(holder,tint,ox,oy,transform);
    }

    /**
     * Draws the tinted texture region (filmstrip) at the given position.
     *
     * A texture region is a single texture file that can hold one or more textures.
     * It is used for filmstrip animation.
     *
     * The texture colors will be multiplied by the given color.  This will turn
     * any white into the given color.  Other colors will be similarly affected.
     *
     * Unless otherwise transformed by the global transform (@see begin(Affine2)),
     * the texture will be unscaled.  The bottom left of the texture will be positioned
     * at the given coordinates.
     *
     * @param region The texture to draw
     * @param tint  The color tint
     * @param x 	The x-coordinate of the bottom left corner
     * @param y 	The y-coordinate of the bottom left corner
     */
    public void draw(TextureRegion region, float x, float y) {


        // Unlike Lab 1, we can shortcut without a master drawing method
        sb.setColor(Color.WHITE);
        sb.draw(region, x,  y);
    }

    /**
     * Draws the tinted texture at the given position.
     *
     * The texture colors will be multiplied by the given color.  This will turn
     * any white into the given color.  Other colors will be similarly affected.
     *
     * Unless otherwise transformed by the global transform (@see begin(Affine2)),
     * the texture will be unscaled.  The bottom left of the texture will be positioned
     * at the given coordinates.
     *region
     * @param image The texture to draw
     * @param tint  The color tint
     * @param x 	The x-coordinate of the bottom left corner
     * @param y 	The y-coordinate of the bottom left corner
     * @param width	The texture width
     * @param height The texture height
     */
    public void draw(TextureRegion region, Color tint, float x, float y, float width, float height) {
        // Unlike Lab 1, we can shortcut without a master drawing method
        sb.setColor(tint);
        sb.draw(region, x,  y, width, height);
    }

    /**
     * Draws the tinted texture at the given position.
     *
     * The texture colors will be multiplied by the given color.  This will turn
     * any white into the given color.  Other colors will be similarly affected.
     *
     * Unless otherwise transformed by the global transform (@see begin(Affine2)),
     * the texture will be unscaled.  The bottom left of the texture will be positioned
     * at the given coordinates.
     *
     * @param region The texture to draw
     * @param tint  The color tint
     * @param ox 	The x-coordinate of texture origin (in pixels)
     * @param oy 	The y-coordinate of texture origin (in pixels)
     * @param x 	The x-coordinate of the texture origin (on screen)
     * @param y 	The y-coordinate of the texture origin (on screen)
     * @param width	The texture width
     * @param height The texture height
     */
    public void draw(TextureRegion region, Color tint, float ox, float oy, float x, float y, float width, float height) {
        // Unlike Lab 1, we can shortcut without a master drawing method
        sb.setColor(tint);
        sb.draw(region, x-ox, y-oy, width, height);
    }

    /**
     * Draws the tinted texture region (filmstrip) with the given transformations
     *
     * A texture region is a single texture file that can hold one or more textures.
     * It is used for filmstrip animation.
     *
     * The texture colors will be multiplied by the given color.  This will turn
     * any white into the given color.  Other colors will be similarly affected.
     *
     * The transformations are BEFORE after the global transform (@see begin(Affine2)).
     * As a result, the specified texture origin will be applied to all transforms
     * (both the local and global).
     *
     * The local transformations in this method are applied in the following order:
     * scaling, then rotation, then translation (e.g. placement at (sx,sy)).
     *
     * @param region The texture to draw
     * @param tint  The color tint
     * @param ox 	The x-coordinate of texture origin (in pixels)
     * @param oy 	The y-coordinate of texture origin (in pixels)
     * @param x 	The x-coordinate of the texture origin (on screen)
     * @param y 	The y-coordinate of the texture origin (on screen)
     * @param angle The rotation angle (in degrees) about the origin.
     * @param sx 	The x-axis scaling factor
     * @param sy 	The y-axis scaling factor
     */
    public void draw(TextureRegion region, Color tint, float ox, float oy,
                     float x, float y, float angle, float sx, float sy) {

        // BUG: The draw command for texture regions does not work properly.
        // There is a workaround, but it will break if the bug is fixed.
        // For now, it is better to set the affine transform directly.
        computeTransform(ox,oy,x,y,angle,sx,sy);
        sb.setColor(tint);
        sb.draw(region, region.getRegionWidth(), region.getRegionHeight(), local);
    }

    /**
     * Draws the tinted texture with the given transformations
     *
     * The texture colors will be multiplied by the given color.  This will turn
     * any white into the given color.  Other colors will be similarly affected.
     *
     * The transformations are BEFORE after the global transform (@see begin(Affine2)).
     * As a result, the specified texture origin will be applied to all transforms
     * (both the local and global).
     *
     * The local transformations in this method are applied in the following order:
     * scaling, then rotation, then translation (e.g. placement at (sx,sy)).
     *
     * @param image The region to draw
     * @param tint  The color tint
     * @param ox 	The x-coordinate of texture origin (in pixels)
     * @param oy 	The y-coordinate of texture origin (in pixels)
     * @param transform  The image transform
     */
    public void draw(TextureRegion region, Color tint, float ox, float oy, Affine2 affine) {
        local.set(affine);
        local.translate(-ox,-oy);
        sb.setColor(tint);
        sb.draw(region, region.getRegionWidth(), region.getRegionHeight(), local);
    }

    /**
     * Function called to begin drawing with canvas
     */
    public void begin(){
        sb.begin();
        this.active = true;
    }

    /**
     * Function called to end drawing with canvas
     */
    public void end(){
        sb.end();
        this.active = false;
    }

    /**
     * Start the debug drawing sequence.
     *
     * Nothing is flushed to the graphics card until the method end() is called.
     */
    public void beginDebug() {
        debugRender.setProjectionMatrix(camera.combined);
        debugRender.begin(ShapeRenderer.ShapeType.Filled);
        debugRender.setColor(Color.RED);
        debugRender.circle(0, 0, 10);
        debugRender.end();

        debugRender.begin(ShapeRenderer.ShapeType.Line);
        active = true;
        debug = true;
    }

    /**
     * Ends the debug drawing sequence, flushing textures to the graphics card.
     */
    public void endDebug() {
        debugRender.end();
        active = false;
        debug = false;
    }

    /**
     * Returns the width of this canvas
     *
     * This currently gets its value from Gdx.graphics.getWidth()
     *
     * @return the width of this canvas
     */
    public int getWidth() {
        return Gdx.graphics.getWidth();
    }

    /**
     * Returns the height of this canvas
     *
     * This currently gets its value from Gdx.graphics.getHeight()
     *
     * @return the height of this canvas
     */
    public int getHeight() {
        return Gdx.graphics.getHeight();
    }
}