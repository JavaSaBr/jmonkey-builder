package com.ss.builder.file.converter.impl;

import com.ss.builder.FileExtensions;
import com.ss.builder.Messages;
import com.ss.builder.FileExtensions;
import com.ss.builder.Messages;
import com.ss.builder.file.converter.FileConverterDescription;

import org.jetbrains.annotations.NotNull;

import com.ss.rlib.common.util.array.Array;
import com.ss.rlib.common.util.array.ArrayFactory;

/**
 * The implementation of {@link AbstractFileConverter} to convert .blend file to .j3o.
 *
 * @author JavaSaBr
 */
public class BlendToJ3oFileConverter extends AbstractModelFileConverter {

    @NotNull
    private static final Array<String> EXTENSIONS = ArrayFactory.newArray(String.class);

    static {
        EXTENSIONS.add(FileExtensions.MODEL_BLENDER);
        EXTENSIONS.asUnsafe().trimToSize();
    }

    /**
     * The description.
     */
    @NotNull
    public static final FileConverterDescription DESCRIPTION = new FileConverterDescription();

    static {
        DESCRIPTION.setDescription(Messages.BLEND_TO_J3O_FILE_CONVERTER_DESCRIPTION);
        DESCRIPTION.setConstructor(BlendToJ3oFileConverter::new);
        DESCRIPTION.setExtensions(EXTENSIONS);
    }

    private BlendToJ3oFileConverter() {
    }

    @Override
    protected @NotNull Array<String> getAvailableExtensions() {
        return EXTENSIONS;
    }

    @Override
    public @NotNull String getTargetExtension() {
        return FileExtensions.JME_OBJECT;
    }
}