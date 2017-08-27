package com.simsilica.arboreal;

import com.jme3.export.*;
import com.jme3.util.clone.Cloner;
import com.jme3.util.clone.JmeCloneable;
import com.simsilica.arboreal.LevelOfDetailParameters.ReductionType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.TreeMap;

/**
 * The base parameters class.
 *
 * @author JavaSaBr
 */
public class Parameters implements Savable, JmeCloneable {

    @NotNull
    protected static final String VERSION_KEY = "formatVersion";

    @NotNull
    protected static final String BRANCHES_KEY = "branches";

    @NotNull
    protected static final String ROOTS_KEY = "roots";

    @NotNull
    protected static final String LODS_KEY = "lodLevels";

    @Nullable
    private Parameters parent;

    /**
     * Set the parent parameters.
     *
     * @param parent the parent parameters.
     */
    public void setParent(@Nullable final Parameters parent) {
        this.parent = parent;
    }

    /**
     * Get the parent parameters.
     *
     * @return the parent parameters.
     */
    public @Nullable Parameters getParent() {
        return parent;
    }

    /**
     * From map.
     *
     * @param map the map
     */
    public void fromMap(@NotNull final Map<String, Object> map) {

        final Number version = (Number) map.get(VERSION_KEY);
        final Class type = getClass();

        for (final Map.Entry<String, Object> entry : map.entrySet()) {

            if (VERSION_KEY.equals(entry.getKey())) {
                continue;
            }

            try {

                final Field field = type.getDeclaredField(entry.getKey());
                field.setAccessible(true);

                if (field.getType() == Boolean.TYPE) {
                    field.set(this, entry.getValue());
                } else if (field.getType() == Integer.TYPE) {
                    Number val = (Number) entry.getValue();
                    field.set(this, val.intValue());
                } else if (field.getType() == Float.TYPE) {
                    Number val = (Number) entry.getValue();
                    field.set(this, val.floatValue());
                } else if (field.getType() == ReductionType.class) {
                    field.set(this, Enum.valueOf(ReductionType.class, (String) entry.getValue()));
                } else {
                    throw new RuntimeException("Unhandled type:" + field.getType());
                }

            } catch (final Exception ex) {
                throw new RuntimeException("Error processing:" + entry, ex);
            }
        }
    }

    /**
     * To map map.
     *
     * @return the map
     */
    public @NotNull Map<String, Object> toMap() {

        final Number version;
        try {
            final Field versionField = getClass().getField("VERSION");
            version = (Number) versionField.get(null);
        } catch (final IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }

        final Map<String, Object> result = new TreeMap<>();
        result.put(VERSION_KEY, version);

        // Easy for this one
        for (final Field field : getClass().getDeclaredFields()) {

            final int modifiers = field.getModifiers();

            if (Modifier.isStatic(modifiers) || Modifier.isFinal(modifiers)) {
                continue;
            }

            field.setAccessible(true);
            try {

                if (field.getType() == ReductionType.class) {
                    result.put(field.getName(), ((ReductionType) field.get(this)).name());
                } else {
                    result.put(field.getName(), field.get(this));
                }

            } catch (final Exception e) {
                throw new RuntimeException("Error getting field:" + field, e);
            }
        }

        return result;
    }

    @Override
    public Object jmeClone() {
        try {
            return super.clone();
        } catch (final CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void cloneFields(@NotNull final Cloner cloner, @NotNull final Object original) {
        parent = cloner.clone(parent);
    }

    @Override
    public void write(@NotNull final JmeExporter ex) throws IOException {
        final OutputCapsule out = ex.getCapsule(this);
        out.write(parent, "parent", null);
    }

    @Override
    public void read(@NotNull final JmeImporter im) throws IOException {
        final InputCapsule in = im.getCapsule(this);
        parent = (Parameters) in.readSavable("parent", null);
    }
}
