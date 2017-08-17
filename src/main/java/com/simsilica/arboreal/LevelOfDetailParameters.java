/*
 * $Id$
 *
 * Copyright (c) 2014, Simsilica, LLC
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions 
 * are met:
 * 
 * 1. Redistributions of source code must retain the above copyright 
 *    notice, this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright 
 *    notice, this list of conditions and the following disclaimer in 
 *    the documentation and/or other materials provided with the 
 *    distribution.
 * 
 * 3. Neither the name of the copyright holder nor the names of its 
 *    contributors may be used to endorse or promote products derived 
 *    from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS 
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT 
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS 
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE 
 * COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, 
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES 
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR 
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) 
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, 
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED 
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.simsilica.arboreal;

import com.jme3.export.*;
import com.jme3.util.clone.Cloner;
import com.jme3.util.clone.JmeCloneable;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.TreeMap;


/**
 * Defines the level of detail settings for a particular
 * tree model and a particular level of detail.
 *
 * @author Paul Speed
 */
public class LevelOfDetailParameters implements Savable, JmeCloneable {

    private static final String VERSION_KEY = "formatVersion";
    private static final int VERSION = 1;

    @NotNull
    private static final ReductionType DEFAULT_REDUCTION_TYPE = ReductionType.Normal;

    private static final int DEFAULT_DISTANCE = 0;
    private static final int DEFAULT_BRANCH_DEPTH = Integer.MAX_VALUE;
    private static final int DEFAULT_ROOT_DEPTH = Integer.MAX_VALUE;
    private static final int DEFAULT_MAX_RADIAL_SEGMENTS = 6;

    /**
     * The enum Reduction type.
     */
    public enum ReductionType {
        /**
         * Normal reduction type.
         */
        Normal("Normal"),
        /**
         * Flat poly reduction type.
         */
        FlatPoly("Flat-poly"),
        /**
         * Impostor reduction type.
         */
        Impostor("Impostor");

        @NotNull
        private final String name;

        ReductionType(@NotNull final String name) {
            this.name = name;
        }

        @Override
        public @NotNull String toString() {
            return name;
        }
    }

    /**
     * The distance from the tree in which this
     * level of detail takes affect.
     */
    public float distance;

    /**
     * The type of mesh reduction that will be used at this level of
     * detail.
     */
    @NotNull
    public ReductionType reduction;

    /**
     * The number of branch levels to render at this level
     * of detail.
     */
    public int branchDepth;

    /**
     * The number of root levels to render at this level
     * of detail.
     */
    public int rootDepth;

    /**
     * The maximum number of radials allowed at this level of detail.
     */
    public int maxRadialSegments;

    /**
     * Instantiates a new Level of detail parameters.
     */
    public LevelOfDetailParameters() {
        this(DEFAULT_DISTANCE, DEFAULT_REDUCTION_TYPE, DEFAULT_BRANCH_DEPTH, DEFAULT_ROOT_DEPTH, DEFAULT_MAX_RADIAL_SEGMENTS);
    }

    /**
     * Instantiates a new Level of detail parameters.
     *
     * @param distance          the distance
     * @param reduction         the reduction
     * @param branchDepth       the branch depth
     * @param rootDepth         the root depth
     * @param maxRadialSegments the max radial segments
     */
    public LevelOfDetailParameters(final float distance, @NotNull final ReductionType reduction, final int branchDepth,
                                   final int rootDepth, final int maxRadialSegments) {
        this.distance = distance;
        this.reduction = reduction;
        this.branchDepth = branchDepth;
        this.rootDepth = rootDepth;
        this.maxRadialSegments = maxRadialSegments;
    }

    /**
     * From map.
     *
     * @param map the map
     */
    public void fromMap(@NotNull final Map<String, Object> map) {
        Number version = (Number) map.get(VERSION_KEY);

        Class type = getClass();
        for (Map.Entry<String, Object> e : map.entrySet()) {
            if (VERSION_KEY.equals(e.getKey())) {
                continue;
            }
            try {
                Field f = type.getField(e.getKey());
                if (f.getType() == Boolean.TYPE) {
                    f.set(this, e.getValue());
                } else if (f.getType() == Integer.TYPE) {
                    Number val = (Number) e.getValue();
                    f.set(this, val.intValue());
                } else if (f.getType() == Float.TYPE) {
                    Number val = (Number) e.getValue();
                    f.set(this, val.floatValue());
                } else if (f.getType() == ReductionType.class) {
                    f.set(this, Enum.valueOf(ReductionType.class, (String) e.getValue()));
                } else {
                    throw new RuntimeException("Unhandled type:" + f.getType());
                }
            } catch (Exception ex) {
                throw new RuntimeException("Error processing:" + e, ex);
            }
        }
    }

    /**
     * To map map.
     *
     * @return the map
     */
    public @NotNull Map<String, Object> toMap() {

        Map<String, Object> result = new TreeMap<String, Object>();
        result.put(VERSION_KEY, VERSION);
        // Easy for this one
        for (Field f : getClass().getFields()) {
            try {
                if (f.getType() == ReductionType.class) {
                    result.put(f.getName(), ((ReductionType) f.get(this)).name());
                } else {
                    result.put(f.getName(), f.get(this));
                }
            } catch (Exception e) {
                throw new RuntimeException("Error getting field:" + f, e);
            }
        }
        return result;
    }

    /**
     * Gets distance.
     *
     * @return the distance
     */
    public float getDistance() {
        return distance;
    }

    /**
     * Sets distance.
     *
     * @param distance the distance
     */
    public void setDistance(final float distance) {
        this.distance = distance;
    }

    /**
     * Gets reduction.
     *
     * @return the reduction
     */
    public @NotNull ReductionType getReduction() {
        return reduction;
    }

    /**
     * Sets reduction.
     *
     * @param reduction the reduction
     */
    public void setReduction(@NotNull final ReductionType reduction) {
        this.reduction = reduction;
    }

    /**
     * Gets branch depth.
     *
     * @return the branch depth
     */
    public int getBranchDepth() {
        return branchDepth;
    }

    /**
     * Sets branch depth.
     *
     * @param branchDepth the branch depth
     */
    public void setBranchDepth(final int branchDepth) {
        this.branchDepth = branchDepth;
    }

    /**
     * Gets root depth.
     *
     * @return the root depth
     */
    public int getRootDepth() {
        return rootDepth;
    }

    /**
     * Sets root depth.
     *
     * @param rootDepth the root depth
     */
    public void setRootDepth(final int rootDepth) {
        this.rootDepth = rootDepth;
    }

    /**
     * Gets max radial segments.
     *
     * @return the max radial segments
     */
    public int getMaxRadialSegments() {
        return maxRadialSegments;
    }

    /**
     * Sets max radial segments.
     *
     * @param maxRadialSegments the max radial segments
     */
    public void setMaxRadialSegments(final int maxRadialSegments) {
        this.maxRadialSegments = maxRadialSegments;
    }

    @Override
    public @NotNull Object jmeClone() {
        try {
            return super.clone();
        } catch (final CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void cloneFields(@NotNull final Cloner cloner, @NotNull final Object original) {
    }

    @Override
    public void write(@NotNull final JmeExporter ex) throws IOException {
        final OutputCapsule out = ex.getCapsule(this);
        out.write(distance, "distance", DEFAULT_DISTANCE);
        out.write(reduction, "reduction", DEFAULT_REDUCTION_TYPE);
        out.write(branchDepth, "branchDepth", DEFAULT_BRANCH_DEPTH);
        out.write(rootDepth, "rootDepth", DEFAULT_ROOT_DEPTH);
        out.write(maxRadialSegments, "maxRadialSegments", DEFAULT_MAX_RADIAL_SEGMENTS);
    }

    @Override
    public void read(@NotNull final JmeImporter im) throws IOException {
        final InputCapsule in = im.getCapsule(this);
        distance = in.readFloat("distance", DEFAULT_DISTANCE);
        reduction = in.readEnum("reduction", ReductionType.class, DEFAULT_REDUCTION_TYPE);
        branchDepth = in.readInt("branchDepth", DEFAULT_BRANCH_DEPTH);
        rootDepth = in.readInt("rootDepth", DEFAULT_ROOT_DEPTH);
        maxRadialSegments = in.readInt("maxRadialSegments", DEFAULT_MAX_RADIAL_SEGMENTS);
    }

    @Override
    public String toString() {
        return "LOD[distance=" + distance + ", reduction=" + reduction + ", branchDepth=" + branchDepth +
                ", rootDepth=" + rootDepth + "]";
    }
}
