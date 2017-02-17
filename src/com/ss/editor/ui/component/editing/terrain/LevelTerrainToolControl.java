package com.ss.editor.ui.component.editing.terrain;

import static com.ss.editor.util.EditingUtils.*;
import static java.util.Objects.requireNonNull;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Sphere;
import com.jme3.terrain.Terrain;
import com.ss.editor.control.editing.EditingInput;
import com.ss.editor.util.EditingUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * The implementation of terrain tool to change height by level.
 *
 * @author JavaSaBr
 */
public class LevelTerrainToolControl extends ChangeHeightTerrainToolControl {

    /**
     * The level marker.
     */
    @NotNull
    protected final Geometry levelMarker;

    /**
     * The target height level.
     */
    private float level;

    /**
     * The flag of using marker.
     */
    private boolean useMarker;

    /**
     * The flag of using precision changing.
     */
    private boolean precision;

    public LevelTerrainToolControl(@NotNull final TerrainEditingComponent component) {
        super(component);

        this.levelMarker = new Geometry("LevelMarker", new Sphere(8, 8, 1));
        this.levelMarker.setMaterial(createMaterial(getBrushColor()));

        //FIXME need to remove
        //setUseMarker(true);
        setLevel(2);
        setPrecision(true);
    }

    @Override
    protected void onAttached(@NotNull final Node node) {
        super.onAttached(node);

        final Spatial editedModel = requireNonNull(getEditedModel());

        component.getMarkersNode().attachChild(levelMarker);
        levelMarker.setLocalTranslation(editedModel.getWorldTranslation());
    }

    @Override
    protected void onDetached(@NotNull final Node node) {
        super.onDetached(node);
        component.getMarkersNode().detachChild(levelMarker);
    }

    @Override
    protected void controlUpdate(final float tpf) {
        super.controlUpdate(tpf);
        levelMarker.setCullHint(isUseMarker() ? Spatial.CullHint.Never : Spatial.CullHint.Always);
    }

    @NotNull
    @Override
    protected ColorRGBA getBrushColor() {
        return ColorRGBA.Red;
    }

    @Override
    public void startEditing(@NotNull final EditingInput editingInput, @NotNull final Vector3f contactPoint) {
        super.startEditing(editingInput, contactPoint);

        switch (editingInput) {
            case MOUSE_PRIMARY: {
                startChange();
                modifyHeight(contactPoint);
                break;
            }
            case MOUSE_SECONDARY: {
                levelMarker.setLocalTranslation(contactPoint);
                break;
            }
        }
    }

    @Override
    public void updateEditing(@NotNull final Vector3f contactPoint) {

        final EditingInput editingInput = requireNonNull(getCurrentInput());

        switch (editingInput) {
            case MOUSE_PRIMARY: {
                modifyHeight(contactPoint);
                break;
            }
            case MOUSE_SECONDARY: {
                levelMarker.setLocalTranslation(contactPoint);
                break;
            }
        }
    }

    @Override
    public void finishEditing(@NotNull final Vector3f contactPoint) {
        super.finishEditing(contactPoint);

        final EditingInput editingInput = requireNonNull(getCurrentInput());

        switch (editingInput) {
            case MOUSE_PRIMARY: {
                modifyHeight(contactPoint);
                commitChanges();
                break;
            }
            case MOUSE_SECONDARY: {
                levelMarker.setLocalTranslation(contactPoint);
                break;
            }
        }
    }

    /**
     * Modify height of terrain points.
     *
     * @param contactPoint the contact point.
     */
    private void modifyHeight(@NotNull final Vector3f contactPoint) {

        final Node terrainNode = (Node) requireNonNull(getEditedModel());
        final Geometry levelMarker = getLevelMarker();

        final Vector3f markerTranslation = levelMarker.getLocalTranslation();
        final Vector3f worldTranslation = terrainNode.getWorldTranslation();

        final Terrain terrain = (Terrain) terrainNode;
        final Vector3f localScale = terrainNode.getLocalScale();

        final Geometry brush = getBrush();

        final float brushSize = getBrushSize();
        final float brushPower = getBrushPower();

        final float markerHeight = markerTranslation.getY() - worldTranslation.getY();
        final float desiredHeight = isUseMarker() ? markerHeight : getLevel();

        final int radiusStepsX = (int) (brushSize / localScale.getX());
        final int radiusStepsZ = (int) (brushSize / localScale.getZ());

        final float xStepAmount = localScale.getX();
        final float zStepAmount = localScale.getZ();

        final List<Vector2f> locs = new ArrayList<>();
        final List<Float> heights = new ArrayList<>();

        for (int z = -radiusStepsZ; z < radiusStepsZ; z++) {
            for (int x = -radiusStepsX; x < radiusStepsX; x++) {

                float locX = contactPoint.getX() + (x * xStepAmount);
                float locZ = contactPoint.getZ() + (z * zStepAmount);

                if (!isContains(brush, locX - contactPoint.getX(), locZ - contactPoint.getZ())) {
                    continue;
                }

                final Vector2f terrainLoc = new Vector2f(locX, locZ);

                // adjust height based on radius of the tool
                final float currentHeight = terrain.getHeightmapHeight(terrainLoc) * localScale.getY();

                if (isPrecision()) {
                    locs.add(terrainLoc);
                    heights.add(desiredHeight / localScale.getY());
                } else {

                    float epsilon = 0.001f * brushPower; // rounding error for snapping
                    float adj = 0;

                    if (currentHeight < desiredHeight) adj = 1;
                    else if (currentHeight > desiredHeight) adj = -1;

                    adj *= brushPower;
                    adj *= EditingUtils.calculateRadiusPercent(brushSize, locX - contactPoint.x, locZ - contactPoint.z);

                    // test if adjusting too far and then cap it
                    if (adj > 0 && floatGreaterThan((currentHeight + adj), desiredHeight, epsilon)) {
                        adj = desiredHeight - currentHeight;
                    } else if (adj < 0 && floatLessThan((currentHeight + adj), desiredHeight, epsilon)) {
                        adj = currentHeight - desiredHeight;
                    }

                    if (!floatEquals(adj, 0, 0.001f)) {
                        locs.add(terrainLoc);
                        heights.add(adj);
                    }
                }
            }
        }

        locs.forEach(this::change);

        // do the actual height adjustment
        if (isPrecision()) {
            terrain.setHeight(locs, heights);
        } else {
            terrain.adjustHeight(locs, heights);
        }

        terrainNode.updateModelBound(); // or else we won't collide with it where we just edited
    }

    /**
     * @param level the target level.
     */
    public void setLevel(final float level) {
        this.level = level;
    }

    /**
     * @return the target level.
     */
    public float getLevel() {
        return level;
    }

    /**
     * @return true if using precision changing.
     */
    public boolean isPrecision() {
        return precision;
    }

    /**
     * @param precision the flag of using precision changing.
     */
    public void setPrecision(final boolean precision) {
        this.precision = precision;
    }

    /**
     * @param useMarker the flag of using marker.
     */
    public void setUseMarker(final boolean useMarker) {
        this.useMarker = useMarker;
    }

    /**
     * @return the flag of using marker.
     */
    public boolean isUseMarker() {
        return useMarker;
    }

    /**
     * @return the level marker.
     */
    @NotNull
    private Geometry getLevelMarker() {
        return levelMarker;
    }
}
