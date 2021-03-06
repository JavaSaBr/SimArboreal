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
public class TreeParameters extends Parameters implements Iterable<BranchParameters> {

    private static final int VERSION = 1;

    @NotNull
    private static final BranchParameters[] EMPTY_BRANCHES = new BranchParameters[0];

    @NotNull
    private static final LevelOfDetailParameters[] EMPTY_LODS = new LevelOfDetailParameters[0];

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
    private static final boolean DEFAULT_USE_WIND = false;

    /**
     * The list of branches.
     */
    @NotNull
    private BranchParameters[] branches;

    /**
     * The list of roots.
     */
    @NotNull
    private BranchParameters[] roots;

    /**
     * The list of level of details.
     */
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
    private float vScale = DEFAULT_V_SCALE;
    private float leafScale = DEFAULT_LEAF_SCALE;

    private int uRepeat = DEFAULT_U_REPEAT;

    private boolean generateLeaves = DEFAULT_GENERATE_LEAVES;
    private boolean useWind = DEFAULT_USE_WIND;

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
     * @param generateLeaves the generateLeaves
     */
    public void setGenerateLeaves(final boolean generateLeaves) {
        this.generateLeaves = generateLeaves;
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
     * Gets the flag of using wind.
     *
     * @return true if need to use wind.
     */
    public boolean isUseWind() {
        return useWind;
    }

    /**
     * Set the flag of using wind.
     *
     * @param useWind true if need to use wind.
     */
    public void setUseWind(final boolean useWind) {
        this.useWind = useWind;
    }

    /**
     * Sets base scale.
     *
     * @param baseScale the baseScale
     */
    public void setBaseScale(final float baseScale) {
        this.baseScale = baseScale;
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
     * @param trunkRadius the trunkRadius
     */
    public void setTrunkRadius(final float trunkRadius) {
        this.trunkRadius = trunkRadius;
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
     * @param trunkHeight the trunkHeight
     */
    public void setTrunkHeight(final float trunkHeight) {
        this.trunkHeight = trunkHeight;
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
     * @param rootHeight the rootHeight
     */
    public void setRootHeight(final float rootHeight) {
        this.rootHeight = rootHeight;
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
     * @param yOffset the yOffset
     */
    public void setYOffset(final float yOffset) {
        this.yOffset = yOffset;
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
     * @param leafScale the leafScale
     */
    public void setLeafScale(final float leafScale) {
        this.leafScale = leafScale;
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
     * Sets texture u uRepeat.
     *
     * @param uRepeat the uRepeat
     */
    public void setTextureURepeat(final int uRepeat) {
        this.uRepeat = uRepeat;
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
     * @param vScale the vScale
     */
    public void setTextureVScale(final float vScale) {
        this.vScale = vScale;
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
     * @param flexHeight the flexHeight
     */
    public void setFlexHeight(final float flexHeight) {
        this.flexHeight = flexHeight;
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
     * @param trunkFlexibility the trunkFlexibility
     */
    public void setTrunkFlexibility(final float trunkFlexibility) {
        this.trunkFlexibility = trunkFlexibility;
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
     * @param branchFlexibility the branchFlexibility
     */
    public void setBranchFlexibility(final float branchFlexibility) {
        this.branchFlexibility = branchFlexibility;
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
     * Gets branches depth.
     *
     * @return the branches depth
     */
    public int getBranchesDepth() {
        return branches.length;
    }

    /**
     * Gets roots depth.
     *
     * @return the roots depth
     */
    public int getRootsDepth() {
        return roots.length;
    }

    /**
     * @return the list of branches.
     */
    public @NotNull BranchParameters[] getBranches() {
        return branches;
    }

    /**
     * Remove the branch from this tree.
     *
     * @param parameters the branch.
     * @return the index of the branch.
     */
    public int removeBranch(@NotNull final BranchParameters parameters) {

        final BranchParameters[] newBranches = new BranchParameters[branches.length - 1];

        int index = -1;

        for (int i = 0, g = 0; i < branches.length; i++) {
            if (branches[i] == parameters) {
                index = i;
                continue;
            }

            newBranches[g++] = branches[i];
        }

        this.branches = newBranches;

        return index;
    }

    /**
     * Add the new branch by the index.
     *
     * @param parameters the branch.
     * @param index      the index.
     */
    public void addBranch(@NotNull final BranchParameters parameters, final int index) {

        final BranchParameters[] newBranches = new BranchParameters[branches.length + 1];

        for (int i = 0, g = 0; i < branches.length; i++) {

            if (i == index) {
                newBranches[g++] = parameters;
            }

            newBranches[g++] = branches[i];
        }

        if (index == -1) {
            newBranches[branches.length] = parameters;
        }

        this.branches = newBranches;
    }

    /**
     * @return the list of roots.
     */
    public @NotNull BranchParameters[] getRoots() {
        return roots;
    }

    /**
     * Remove the root from this tree.
     *
     * @param parameters the root.
     * @return the index of the root.
     */
    public int removeRoot(@NotNull final BranchParameters parameters) {

        final BranchParameters[] newRoots = new BranchParameters[roots.length - 1];

        int index = -1;

        for (int i = 0, g = 0; i < roots.length; i++) {
            if (roots[i] == parameters) {
                index = i;
                continue;
            }

            newRoots[g++] = roots[i];
        }

        this.roots = newRoots;

        return index;
    }

    /**
     * Add the new root by the index.
     *
     * @param parameters the root.
     * @param index      the index.
     */
    public void addRoot(@NotNull final BranchParameters parameters, final int index) {

        final BranchParameters[] newRoots = new BranchParameters[roots.length + 1];

        for (int i = 0, g = 0; i < roots.length; i++) {

            if (i == index) {
                newRoots[g++] = parameters;
            }

            newRoots[g++] = roots[i];
        }

        if (index == -1) {
            newRoots[roots.length] = parameters;
        }

        this.roots = newRoots;
    }

    /**
     * @return the list of level of details.
     */
    public @NotNull LevelOfDetailParameters[] getLodLevels() {
        return lodLevels;
    }

    /**
     * Remove the lod from this tree.
     *
     * @param parameters the lod.
     * @return the index of the lod.
     */
    public int removeLodLevel(@NotNull final LevelOfDetailParameters parameters) {

        final LevelOfDetailParameters[] newLods = new LevelOfDetailParameters[lodLevels.length - 1];

        int index = -1;

        for (int i = 0, g = 0; i < lodLevels.length; i++) {
            if (lodLevels[i] == parameters) {
                index = i;
                continue;
            }

            newLods[g++] = lodLevels[i];
        }

        this.lodLevels = newLods;

        return index;
    }

    /**
     * Add the new root by the index.
     *
     * @param parameters the root.
     * @param index      the index.
     */
    public void addLodLevel(@NotNull final LevelOfDetailParameters parameters, final int index) {

        final LevelOfDetailParameters[] newLods = new LevelOfDetailParameters[lodLevels.length + 1];

        for (int i = 0, g = 0; i < lodLevels.length; i++) {

            if (i == index) {
                newLods[g++] = parameters;
            }

            newLods[g++] = lodLevels[i];
        }

        if (index == -1) {
            newLods[lodLevels.length] = parameters;
        }

        this.lodLevels = newLods;
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

        final List<BranchParameters> list = new ArrayList<>();

        for (final BranchParameters branchParameters : this) {
            list.add(branchParameters);
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

        final List<BranchParameters> list = new ArrayList<>();
        final Iterator<BranchParameters> it = rootIterator();

        while (it.hasNext()) {
            list.add(it.next());
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
     * @param level the level
     * @return the lod
     */
    public LevelOfDetailParameters getLod(int level) {
        return lodLevels[level];
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

        list = new ArrayList<>(roots.length);
        for (BranchParameters bp : roots) {
            list.add(bp.toMap());
        }
        result.put(ROOTS_KEY, list);

        list = new ArrayList<>(lodLevels.length);
        for (LevelOfDetailParameters lod : lodLevels) {
            list.add(lod.toMap());
        }
        result.put(LODS_KEY, list);

        return result;
    }

    @Override
    public void cloneFields(@NotNull final Cloner cloner, @NotNull final Object original) {
        super.cloneFields(cloner, original);
        this.branches = cloner.clone(branches);
        this.roots = cloner.clone(roots);
        this.lodLevels = cloner.clone(lodLevels);
    }

    @Override
    public void write(@NotNull final JmeExporter ex) throws IOException {
        super.write(ex);

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
        out.write(useWind, "useWind", DEFAULT_USE_WIND);
    }

    @Override
    public void read(@NotNull final JmeImporter im) throws IOException {
        super.read(im);

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
        useWind = in.readBoolean("useWind", DEFAULT_USE_WIND);
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
            return next < getBranchesDepth() && getBranch(next).enabled;
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
            return next < getRootsDepth() && getRoot(next).enabled;
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


