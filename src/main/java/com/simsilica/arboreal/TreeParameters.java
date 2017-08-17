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
import com.simsilica.arboreal.LevelOfDetailParameters.ReductionType;
import org.jetbrains.annotations.NotNull;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * The settings of tree.
 *
 * @author Paul Speed
 */
public class TreeParameters implements Iterable<BranchParameters>, Savable, JmeCloneable {

    private static final int VERSION = 1;

    @NotNull
    private static final String VERSION_KEY = "formatVersion";

    @NotNull
    private static final String BRANCHES_KEY = "branches";

    @NotNull
    private static final String ROOTS_KEY = "roots";

    @NotNull
    private static final String LODS_KEY = "lodLevels";

    private static final int DEFAULT_BASE_SCALE = 1;
    private static final int DEFAULT_U_REPEAT = 4;
    private static final int DEFAULT_LEAF_SCALE = 1;
    private static final int DEFAULT_DEPTH = 4;

    private static final float DEFAULT_TRUNK_RADIUS = 0.5f * 0.3f;
    private static final float DEFAULT_TRUNK_HEIGHT = 6 * 0.3f;
    private static final float DEFAULT_ROOT_HEIGHT = 1 * 0.3f;
    private static final float DEFAULT_Y_OFFSET = 1 * 0.3f;
    private static final float DEFAULT_FLEX_HEIGHT = 2.0f;
    private static final float DEFAULT_TRUNK_FLEXIBILITY = 1.0f;
    private static final float DEFAULT_BRANCH_FLEXIBILITY = 1.0f;
    private static final float DEFAULT_V_SCALE = 0.45f;

    private static final boolean DEFAULT_GENERATE_LEAVES = false;
    public static final BranchParameters[] EMPTY_BRANCHES = new BranchParameters[0];
    public static final LevelOfDetailParameters[] EMPTY_LODS = new LevelOfDetailParameters[0];

    @NotNull
    private BranchParameters[] branches;

    @NotNull
    private BranchParameters[] roots;

    @NotNull
    private LevelOfDetailParameters[] lodLevels;

    private float baseScale = DEFAULT_BASE_SCALE;
    private float trunkRadius = DEFAULT_TRUNK_RADIUS;
    private float trunkHeight = DEFAULT_TRUNK_HEIGHT;
    private float rootHeight = DEFAULT_ROOT_HEIGHT;
    private float yOffset = DEFAULT_Y_OFFSET;
    private float flexHeight = DEFAULT_FLEX_HEIGHT;
    private float trunkFlexibility = DEFAULT_TRUNK_FLEXIBILITY;
    private float branchFlexibility = DEFAULT_BRANCH_FLEXIBILITY;
    private int uRepeat = DEFAULT_U_REPEAT;
    private float vScale = DEFAULT_V_SCALE;
    private float leafScale = DEFAULT_LEAF_SCALE;
    private boolean generateLeaves = DEFAULT_GENERATE_LEAVES;

    private int seed = 0;

    /**
     * Instantiates a new Tree parameters.
     */
    public TreeParameters() {
        this(DEFAULT_DEPTH);
    }

    /**
     * Instantiates a new Tree parameters.
     *
     * @param depth the depth
     */
    public TreeParameters(int depth) {
        this.branches = new BranchParameters[depth];
        for (int i = 0; i < depth; i++) {
            branches[i] = new BranchParameters();

            // for testing
            //branches[i].enabled = false;

            // For any branch greater than depth 3, we disable it by default
            if (i > 3) {
                branches[i].enabled = false;
            }
        }
        this.branches[0].inherit = false;
        //this.branches[0].enabled = true;                    
        //this.branches[1].enabled = true;

        this.roots = new BranchParameters[depth];
        for (int i = 0; i < depth; i++) {
            roots[i] = new BranchParameters();
            roots[i].enabled = false;
        }
        this.roots[0].inherit = false;

        // Roots get a specific default setup
        roots[0].lengthSegments = 1;
        roots[0].segmentVariation = 0;
        roots[0].taper = 1;
        roots[0].sideJointCount = 5;
        roots[0].inclination = 0.5f;
        roots[0].lengthScale = 1.1f;
        roots[0].enabled = true;

        roots[1].inherit = false;
        roots[1].taper = 0.5f;
        roots[1].gravity = 0.1f;
        roots[1].lengthScale = 1.0f;
        roots[1].enabled = true;

        roots[2].enabled = true;

        this.lodLevels = new LevelOfDetailParameters[4];
        for (int i = 0; i < lodLevels.length; i++) {
            lodLevels[i] = new LevelOfDetailParameters();
            lodLevels[i].distance = (i + 1) * 20;
            lodLevels[i].branchDepth = 2;
            lodLevels[i].rootDepth = 2;
            lodLevels[i].maxRadialSegments = 3;
            if (i > 0) {
                lodLevels[i].reduction = ReductionType.FlatPoly;
            }
        }
        lodLevels[0].maxRadialSegments = Integer.MAX_VALUE;
        lodLevels[0].branchDepth = depth;
        lodLevels[0].rootDepth = depth;
        lodLevels[1].maxRadialSegments = 4;
        lodLevels[lodLevels.length - 1].distance = Float.MAX_VALUE;
    }

    /**
     * Sets seed.
     *
     * @param seed the seed
     */
    public void setSeed(int seed) {
        this.seed = seed;
    }

    /**
     * Gets seed.
     *
     * @return the seed
     */
    public int getSeed() {
        return seed;
    }

    /**
     * Sets generate leaves.
     *
     * @param b the b
     */
    public void setGenerateLeaves(boolean b) {
        this.generateLeaves = b;
    }

    /**
     * Gets generate leaves.
     *
     * @return the generate leaves
     */
    public boolean getGenerateLeaves() {
        return generateLeaves;
    }

    /**
     * Sets base scale.
     *
     * @param f the f
     */
    public void setBaseScale(float f) {
        this.baseScale = f;
    }

    /**
     * Gets base scale.
     *
     * @return the base scale
     */
    public float getBaseScale() {
        return baseScale;
    }

    /**
     * Sets trunk radius.
     *
     * @param f the f
     */
    public void setTrunkRadius(float f) {
        this.trunkRadius = f;
    }

    /**
     * Gets trunk radius.
     *
     * @return the trunk radius
     */
    public float getTrunkRadius() {
        return trunkRadius;
    }

    /**
     * Sets trunk height.
     *
     * @param f the f
     */
    public void setTrunkHeight(float f) {
        this.trunkHeight = f;
    }

    /**
     * Gets trunk height.
     *
     * @return the trunk height
     */
    public float getTrunkHeight() {
        return trunkHeight;
    }

    /**
     * Sets root height.
     *
     * @param f the f
     */
    public void setRootHeight(float f) {
        this.rootHeight = f;
    }

    /**
     * Gets root height.
     *
     * @return the root height
     */
    public float getRootHeight() {
        return rootHeight;
    }

    /**
     * Sets y offset.
     *
     * @param f the f
     */
    public void setYOffset(float f) {
        this.yOffset = f;
    }

    /**
     * Gets y offset.
     *
     * @return the y offset
     */
    public float getYOffset() {
        return yOffset;
    }

    /**
     * Sets leaf scale.
     *
     * @param f the f
     */
    public void setLeafScale(float f) {
        this.leafScale = f;
    }

    /**
     * Gets leaf scale.
     *
     * @return the leaf scale
     */
    public float getLeafScale() {
        return leafScale;
    }

    /**
     * Sets texture u repeat.
     *
     * @param repeat the repeat
     */
    public void setTextureURepeat(int repeat) {
        this.uRepeat = repeat;
    }

    /**
     * Gets texture u repeat.
     *
     * @return the texture u repeat
     */
    public int getTextureURepeat() {
        return uRepeat;
    }

    /**
     * Sets texture v scale.
     *
     * @param f the f
     */
    public void setTextureVScale(float f) {
        this.vScale = f;
    }

    /**
     * Gets texture v scale.
     *
     * @return the texture v scale
     */
    public float getTextureVScale() {
        return vScale;
    }

    /**
     * Sets flex height.
     *
     * @param f the f
     */
    public void setFlexHeight(float f) {
        this.flexHeight = f;
    }

    /**
     * Gets flex height.
     *
     * @return the flex height
     */
    public float getFlexHeight() {
        return flexHeight;
    }

    /**
     * Sets trunk flexibility.
     *
     * @param f the f
     */
    public void setTrunkFlexibility(float f) {
        this.trunkFlexibility = f;
    }

    /**
     * Gets trunk flexibility.
     *
     * @return the trunk flexibility
     */
    public float getTrunkFlexibility() {
        return trunkFlexibility;
    }

    /**
     * Sets branch flexibility.
     *
     * @param f the f
     */
    public void setBranchFlexibility(float f) {
        this.branchFlexibility = f;
    }

    /**
     * Gets branch flexibility.
     *
     * @return the branch flexibility
     */
    public float getBranchFlexibility() {
        return branchFlexibility;
    }

    /**
     * Gets depth.
     *
     * @return the depth
     */
    public int getDepth() {
        return branches.length;
    }

    /**
     * Gets branch.
     *
     * @param index the index
     * @return the branch
     */
    public BranchParameters getBranch(int index) {
        return branches[index];
    }

    /**
     * Gets effective branches.
     *
     * @return the effective branches
     */
    public List<BranchParameters> getEffectiveBranches() {
        List<BranchParameters> list = new ArrayList<>();
        for (BranchParameters p : this) {
            list.add(p);
        }
        return list;
    }

    @Override
    public Iterator<BranchParameters> iterator() {
        return new BranchIterator();
    }

    /**
     * Gets root.
     *
     * @param index the index
     * @return the root
     */
    public BranchParameters getRoot(int index) {
        return roots[index];
    }

    /**
     * Gets effective roots.
     *
     * @return the effective roots
     */
    public List<BranchParameters> getEffectiveRoots() {
        List<BranchParameters> list = new ArrayList<BranchParameters>();
        for (Iterator<BranchParameters> it = rootIterator(); it.hasNext(); ) {
            BranchParameters p = it.next();
            list.add(p);
        }
        return list;
    }

    /**
     * Root iterator iterator.
     *
     * @return the iterator
     */
    public Iterator<BranchParameters> rootIterator() {
        return new RootIterator();
    }

    /**
     * Gets lod count.
     *
     * @return the lod count
     */
    public int getLodCount() {
        return lodLevels.length;
    }

    /**
     * Gets lod.
     *
     * @param i the
     * @return the lod
     */
    public LevelOfDetailParameters getLod(int i) {
        return lodLevels[i];
    }

    /**
     * Gets lods.
     *
     * @return the lods
     */
    public List<LevelOfDetailParameters> getLods() {
        return Arrays.asList(lodLevels);
    }

    private PropertyDescriptor findProperty(BeanInfo info, String name) {
        for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
            if (name.equals(pd.getName())) {
                return pd;
            }
        }
        return null;
    }

    private BranchParameters[] listToBranches(List<Map<String, Object>> list, BranchParameters[] result) {
        if (result.length != list.size()) {
            BranchParameters[] newArray = new BranchParameters[list.size()];
            for (int i = 0; i < newArray.length; i++) {
                if (i < result.length) {
                    newArray[i] = result[i];
                }
            }
        }
        for (int i = 0; i < result.length; i++) {
            Map<String, Object> value = list.get(i);
            if (result[i] == null) {
                result[i] = new BranchParameters();
            }
            result[i].fromMap(value);
        }
        return result;
    }

    private LevelOfDetailParameters[] listToLods(List<Map<String, Object>> list, LevelOfDetailParameters[] result) {
        if (result.length != list.size()) {
            LevelOfDetailParameters[] newArray = new LevelOfDetailParameters[list.size()];
            for (int i = 0; i < newArray.length; i++) {
                if (i < result.length) {
                    newArray[i] = result[i];
                }
            }
        }
        for (int i = 0; i < result.length; i++) {
            Map<String, Object> value = list.get(i);
            if (result[i] == null) {
                result[i] = new LevelOfDetailParameters();
            }
            result[i].fromMap(value);
        }
        return result;
    }

    /**
     * From map.
     *
     * @param map the map
     */
    public void fromMap(@NotNull final Map<String, Object> map) {
        Number version = (Number) map.get(VERSION_KEY);

        BeanInfo info = null;
        try {
            info = Introspector.getBeanInfo(getClass());
        } catch (Exception e) {
            throw new RuntimeException("Error introspecting property descriptors", e);
        }

        for (Map.Entry<String, Object> e : map.entrySet()) {
            if (VERSION_KEY.equals(e.getKey())) {
                continue;
            }
            if (BRANCHES_KEY.equals(e.getKey())) {
                branches = listToBranches((List<Map<String, Object>>) e.getValue(), branches);
            } else if (ROOTS_KEY.equals(e.getKey())) {
                roots = listToBranches((List<Map<String, Object>>) e.getValue(), roots);
            } else if (LODS_KEY.equals(e.getKey())) {
                lodLevels = listToLods((List<Map<String, Object>>) e.getValue(), lodLevels);
            } else {
                PropertyDescriptor pd = findProperty(info, e.getKey());
                Method m = pd.getWriteMethod();
                try {
                    if (pd.getPropertyType() == Integer.TYPE) {
                        Number value = (Number) e.getValue();
                        m.invoke(this, value.intValue());
                    } else if (pd.getPropertyType() == Float.TYPE) {
                        Number value = (Number) e.getValue();
                        m.invoke(this, value.floatValue());
                    } else if (pd.getPropertyType() == Boolean.TYPE) {
                        m.invoke(this, e.getValue());
                    } else {
                        throw new RuntimeException("Unhandled property type:" + pd.getPropertyType());
                    }
                } catch (Exception ex) {
                    throw new RuntimeException("Error inspecting property:" + pd.getName(), ex);
                }
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

        PropertyDescriptor[] props = null;
        try {
            BeanInfo info = Introspector.getBeanInfo(getClass());
            props = info.getPropertyDescriptors();
        } catch (Exception e) {
            throw new RuntimeException("Error introspecting property descriptors", e);
        }

        for (PropertyDescriptor pd : props) {
            if (pd.getWriteMethod() == null) {
                continue;
            }
            Method m = pd.getReadMethod();
            if (m == null) {
                continue;
            }
            try {
                result.put(pd.getName(), m.invoke(this));
            } catch (Exception e) {
                throw new RuntimeException("Error inspecting property:" + pd.getName(), e);
            }
        }

        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>(branches.length);
        for (BranchParameters bp : branches) {
            list.add(bp.toMap());
        }
        result.put(BRANCHES_KEY, list);

        list = new ArrayList<Map<String, Object>>(roots.length);
        for (BranchParameters bp : roots) {
            list.add(bp.toMap());
        }
        result.put(ROOTS_KEY, list);

        list = new ArrayList<Map<String, Object>>(lodLevels.length);
        for (LevelOfDetailParameters lod : lodLevels) {
            list.add(lod.toMap());
        }
        result.put(LODS_KEY, list);

        return result;
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
        this.branches = cloner.clone(branches);
        this.roots = cloner.clone(roots);
        this.lodLevels = cloner.clone(lodLevels);
    }

    @Override
    public void write(@NotNull final JmeExporter ex) throws IOException {
        final OutputCapsule out = ex.getCapsule(this);
        out.write(branches, "branches", EMPTY_BRANCHES);
        out.write(roots, "roots", EMPTY_BRANCHES);
        out.write(lodLevels, "lodLevels", EMPTY_LODS);
        out.write(baseScale, "baseScale", DEFAULT_BASE_SCALE);
        out.write(trunkRadius, "trunkRadius", DEFAULT_TRUNK_RADIUS);
        out.write(trunkHeight, "trunkHeight", DEFAULT_TRUNK_HEIGHT);
        out.write(rootHeight, "rootHeight", DEFAULT_ROOT_HEIGHT);
        out.write(yOffset, "yOffset", DEFAULT_Y_OFFSET);
        out.write(flexHeight, "flexHeight", DEFAULT_FLEX_HEIGHT);
        out.write(trunkFlexibility, "trunkFlexibility", DEFAULT_TRUNK_FLEXIBILITY);
        out.write(branchFlexibility, "branchFlexibility", DEFAULT_BRANCH_FLEXIBILITY);
        out.write(uRepeat, "uRepeat", DEFAULT_U_REPEAT);
        out.write(vScale, "vScale", DEFAULT_V_SCALE);
        out.write(leafScale, "leafScale", DEFAULT_LEAF_SCALE);
        out.write(generateLeaves, "generateLeaves", DEFAULT_GENERATE_LEAVES);
    }

    @Override
    public void read(@NotNull final JmeImporter im) throws IOException {
        final InputCapsule in = im.getCapsule(this);

        Savable[] array = in.readSavableArray("branches", EMPTY_BRANCHES);

        branches = Arrays.copyOf(array, array.length, BranchParameters[].class);

        array = in.readSavableArray("roots", EMPTY_BRANCHES);

        roots = Arrays.copyOf(array, array.length, BranchParameters[].class);

        array = in.readSavableArray("lodLevels", EMPTY_LODS);

        lodLevels = Arrays.copyOf(array, array.length, LevelOfDetailParameters[].class);

        baseScale = in.readFloat("baseScale", DEFAULT_BASE_SCALE);
        trunkRadius = in.readFloat("trunkRadius", DEFAULT_TRUNK_RADIUS);
        trunkHeight = in.readFloat("trunkHeight", DEFAULT_TRUNK_HEIGHT);
        rootHeight = in.readFloat("rootHeight", DEFAULT_ROOT_HEIGHT);
        yOffset = in.readFloat("yOffset", DEFAULT_Y_OFFSET);
        flexHeight = in.readFloat("flexHeight", DEFAULT_FLEX_HEIGHT);
        trunkFlexibility = in.readFloat("trunkFlexibility", DEFAULT_TRUNK_FLEXIBILITY);
        branchFlexibility = in.readFloat("branchFlexibility", DEFAULT_BRANCH_FLEXIBILITY);
        uRepeat = in.readInt("uRepeat", DEFAULT_U_REPEAT);
        vScale = in.readFloat("vScale", DEFAULT_V_SCALE);
        leafScale = in.readFloat("leafScale", DEFAULT_LEAF_SCALE);
        generateLeaves = in.readBoolean("generateLeaves", DEFAULT_GENERATE_LEAVES);
    }

    private class BranchIterator implements Iterator<BranchParameters> {

        private BranchParameters last;
        private int next = 0;

        /**
         * Instantiates a new Branch iterator.
         */
        public BranchIterator() {
            this.last = getBranch(0);
        }

        @Override
        public boolean hasNext() {
            return next < getDepth() && getBranch(next).enabled;
        }

        @Override
        public BranchParameters next() {
            BranchParameters result = getBranch(next++);
            if (!result.enabled) {
                throw new NoSuchElementException();
            }
            if (result.inherit) {
                result = last;
            } else {
                last = result;
            }
            return result;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Cannot remove branch parameters.");
        }
    }

    private class RootIterator implements Iterator<BranchParameters> {

        private BranchParameters last;
        private int next = 0;

        /**
         * Instantiates a new Root iterator.
         */
        public RootIterator() {
            this.last = getRoot(0);
        }

        @Override
        public boolean hasNext() {
            return next < getDepth() && getRoot(next).enabled;
        }

        @Override
        public BranchParameters next() {
            BranchParameters result = getRoot(next++);
            if (!result.enabled) {
                throw new NoSuchElementException();
            }
            if (result.inherit) {
                result = last;
            } else {
                last = result;
            }
            return result;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Cannot remove branch parameters.");
        }
    }
}


