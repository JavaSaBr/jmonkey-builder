package com.ss.editor.ui.control.property.impl;

import com.jme3.material.Material;
import com.ss.editor.FileExtensions;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.ui.control.property.PropertyControl;
import com.ss.rlib.common.util.array.Array;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of the {@link PropertyControl} to edit the {@link Material}.
 *
 * @param <C> the change consumer's type.
 * @param <E> the edited object's type.
 * @param <T> the material's type.
 * @author JavaSaBr
 */
public class MaterialPropertyControl<C extends ChangeConsumer, E, T> extends
        KeyPropertyControl<C, E, T> {

    protected static final String NO_MATERIAL =
            Messages.MATERIAL_MODEL_PROPERTY_CONTROL_NO_MATERIAL;

    protected static final Array<String> MATERIAL_EXTENSIONS =
            Array.of(FileExtensions.JME_MATERIAL);

    public MaterialPropertyControl(
            @Nullable T element,
            @NotNull String paramName,
            @NotNull C changeConsumer
    ) {
        super(element, paramName, changeConsumer);
        setOnDragOver(this::handleDragOverEvent);
        setOnDragDropped(this::handleDragDroppedEvent);
        setOnDragExited(this::handleDragExitedEvent);
    }

    @Override
    @FromAnyThread
    protected @NotNull String getNoKeyLabel() {
        return NO_MATERIAL;
    }

    @Override
    @FromAnyThread
    protected @NotNull Array<String> getExtensions() {
        return MATERIAL_EXTENSIONS;
    }
}
