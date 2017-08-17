/*
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
 * The settings of branch.
 *
 * @author Paul Speed
 */
public class BranchParameters implements Savable, JmeCloneable {

    @NotNull
    private static final String VERSION_KEY = "formatVersion";

    private static final int VERSION = 1;

    private static final float DEFAULT_RADIUS_SCALE = 1f;
    private static final float DEFAULT_LENGTH_SCALE = 0.6f;
    private static final float DEFAULT_TAPER = 0.7f;
    private static final float DEFAULT_SEGMENT_VARIATION = 0.4f;
    private static final float DEFAULT_GRAVITY = 0.1f;
    private static final float DEFAULT_INCLINATION = 0.872f;
    private static final float DEFAULT_TIP_ROTATION = 0F;
    private static final float DEFAULT_SIDE_JOINT_START_ANGLE = 0F;
    private static final float DEFAULT_TWIST = 0F;

    private static final int DEFAULT_RADIAL_SEGMENTS = 6;
    private static final int DEFAULT_LENGTH_SEGMENTS = 4;
    private static final int DEFAULT_SIDE_JOINT_COUNT = 4;

    private static final boolean DEFAULT_INHERIT = true;
    private static final boolean DEFAULT_HAS_END_JOINT = false;

    /**
     * The Enabled.
     */
    public boolean enabled;

    /**
     * The Inherit.
     */
    public boolean inherit;

    /**
     * The Radius scale.
     */
    public float radiusScale;

    /**
     * The Length scale.
     */
    public float lengthScale;

    /**
     * The Radial segments.
     */
    public int radialSegments;

    /**
     * The Length segments.
     */
    public int lengthSegments;

    /**
     * The Taper.
     */
    public float taper;

    /**
     * The Inclination.
     */
    public float inclination;

    /**
     * The Twist.
     */
    public float twist;

    /**
     * The Tip rotation.
     */
    public float tipRotation;

    /**
     * The Segment variation.
     */
    public float segmentVariation;
    /**
     * The Gravity.
     */
    public float gravity;

    /**
     * The Has end joint.
     */
    public boolean hasEndJoint;
    /**
     * The Side joint count.
     */
    public int sideJointCount;
    /**
     * The Side joint start angle.
     */
    public float sideJointStartAngle;

    /**
     * Instantiates a new Branch parameters.
     */
    public BranchParameters() {
        this.enabled = true;
        this.inherit = DEFAULT_INHERIT;
        this.radiusScale = DEFAULT_RADIUS_SCALE;
        this.lengthScale = DEFAULT_LENGTH_SCALE;
        this.radialSegments = DEFAULT_RADIAL_SEGMENTS;
        this.lengthSegments = DEFAULT_LENGTH_SEGMENTS;
        this.taper = DEFAULT_TAPER;
        this.tipRotation = DEFAULT_TIP_ROTATION;
        this.segmentVariation = DEFAULT_SEGMENT_VARIATION;
        this.gravity = DEFAULT_GRAVITY;
        this.sideJointCount = DEFAULT_SIDE_JOINT_COUNT;
        this.inclination = DEFAULT_INCLINATION;
        this.hasEndJoint = DEFAULT_HAS_END_JOINT;
        this.sideJointStartAngle = DEFAULT_SIDE_JOINT_START_ANGLE;
    }

    /**
     * Sets enabled.
     *
     * @param enabled the enabled
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Is enabled boolean.
     *
     * @return the boolean
     */
    public boolean isEnabled() {
        return enabled;
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
                result.put(f.getName(), f.get(this));
            } catch (Exception e) {
                throw new RuntimeException("Error getting field:" + f, e);
            }
        }
        return result;
    }

    /**
     * Is inherit boolean.
     *
     * @return the boolean
     */
    public boolean isInherit() {
        return inherit;
    }

    /**
     * Sets inherit.
     *
     * @param inherit the inherit
     */
    public void setInherit(final boolean inherit) {
        this.inherit = inherit;
    }

    /**
     * Gets radius scale.
     *
     * @return the radius scale
     */
    public float getRadiusScale() {
        return radiusScale;
    }

    /**
     * Sets radius scale.
     *
     * @param radiusScale the radius scale
     */
    public void setRadiusScale(final float radiusScale) {
        this.radiusScale = radiusScale;
    }

    /**
     * Gets length scale.
     *
     * @return the length scale
     */
    public float getLengthScale() {
        return lengthScale;
    }

    /**
     * Sets length scale.
     *
     * @param lengthScale the length scale
     */
    public void setLengthScale(final float lengthScale) {
        this.lengthScale = lengthScale;
    }

    /**
     * Gets radial segments.
     *
     * @return the radial segments
     */
    public int getRadialSegments() {
        return radialSegments;
    }

    /**
     * Sets radial segments.
     *
     * @param radialSegments the radial segments
     */
    public void setRadialSegments(final int radialSegments) {
        this.radialSegments = radialSegments;
    }

    /**
     * Gets length segments.
     *
     * @return the length segments
     */
    public int getLengthSegments() {
        return lengthSegments;
    }

    /**
     * Sets length segments.
     *
     * @param lengthSegments the length segments
     */
    public void setLengthSegments(final int lengthSegments) {
        this.lengthSegments = lengthSegments;
    }

    /**
     * Gets taper.
     *
     * @return the taper
     */
    public float getTaper() {
        return taper;
    }

    /**
     * Sets taper.
     *
     * @param taper the taper
     */
    public void setTaper(final float taper) {
        this.taper = taper;
    }

    /**
     * Gets inclination.
     *
     * @return the inclination
     */
    public float getInclination() {
        return inclination;
    }

    /**
     * Sets inclination.
     *
     * @param inclination the inclination
     */
    public void setInclination(final float inclination) {
        this.inclination = inclination;
    }

    /**
     * Gets twist.
     *
     * @return the twist
     */
    public float getTwist() {
        return twist;
    }

    /**
     * Sets twist.
     *
     * @param twist the twist
     */
    public void setTwist(final float twist) {
        this.twist = twist;
    }

    /**
     * Gets tip rotation.
     *
     * @return the tip rotation
     */
    public float getTipRotation() {
        return tipRotation;
    }

    /**
     * Sets tip rotation.
     *
     * @param tipRotation the tip rotation
     */
    public void setTipRotation(final float tipRotation) {
        this.tipRotation = tipRotation;
    }

    /**
     * Gets segment variation.
     *
     * @return the segment variation
     */
    public float getSegmentVariation() {
        return segmentVariation;
    }

    /**
     * Sets segment variation.
     *
     * @param segmentVariation the segment variation
     */
    public void setSegmentVariation(final float segmentVariation) {
        this.segmentVariation = segmentVariation;
    }

    /**
     * Gets gravity.
     *
     * @return the gravity
     */
    public float getGravity() {
        return gravity;
    }

    /**
     * Sets gravity.
     *
     * @param gravity the gravity
     */
    public void setGravity(final float gravity) {
        this.gravity = gravity;
    }

    /**
     * Is has end joint boolean.
     *
     * @return the boolean
     */
    public boolean isHasEndJoint() {
        return hasEndJoint;
    }

    /**
     * Sets has end joint.
     *
     * @param hasEndJoint the has end joint
     */
    public void setHasEndJoint(final boolean hasEndJoint) {
        this.hasEndJoint = hasEndJoint;
    }

    /**
     * Gets side joint count.
     *
     * @return the side joint count
     */
    public int getSideJointCount() {
        return sideJointCount;
    }

    /**
     * Sets side joint count.
     *
     * @param sideJointCount the side joint count
     */
    public void setSideJointCount(final int sideJointCount) {
        this.sideJointCount = sideJointCount;
    }

    /**
     * Gets side joint start angle.
     *
     * @return the side joint start angle
     */
    public float getSideJointStartAngle() {
        return sideJointStartAngle;
    }

    /**
     * Sets side joint start angle.
     *
     * @param sideJointStartAngle the side joint start angle
     */
    public void setSideJointStartAngle(final float sideJointStartAngle) {
        this.sideJointStartAngle = sideJointStartAngle;
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
        out.write(enabled, "enabled", false);
        out.write(inherit, "inherit", DEFAULT_INHERIT);
        out.write(radiusScale, "radiusScale", DEFAULT_RADIUS_SCALE);
        out.write(lengthScale, "lengthScale", DEFAULT_LENGTH_SCALE);
        out.write(radialSegments, "radialSegments", DEFAULT_RADIAL_SEGMENTS);
        out.write(lengthSegments, "lengthSegments", DEFAULT_LENGTH_SEGMENTS);
        out.write(taper, "taper", DEFAULT_TAPER);
        out.write(inclination, "inclination", DEFAULT_INCLINATION);
        out.write(tipRotation, "tipRotation", DEFAULT_TIP_ROTATION);
        out.write(segmentVariation, "segmentVariation", DEFAULT_SEGMENT_VARIATION);
        out.write(twist, "twist", DEFAULT_TWIST);
        out.write(gravity, "gravity", DEFAULT_GRAVITY);
        out.write(hasEndJoint, "hasEndJoint", DEFAULT_HAS_END_JOINT);
        out.write(sideJointCount, "sideJointCount", DEFAULT_SIDE_JOINT_COUNT);
        out.write(sideJointStartAngle, "sideJointStartAngle", DEFAULT_SIDE_JOINT_START_ANGLE);
    }

    @Override
    public void read(@NotNull final JmeImporter im) throws IOException {
        final InputCapsule in = im.getCapsule(this);
        enabled = in.readBoolean("enabled", false);
        inherit = in.readBoolean("inherit", DEFAULT_INHERIT);
        radiusScale = in.readFloat("radiusScale", DEFAULT_RADIUS_SCALE);
        lengthScale = in.readFloat("lengthScale", DEFAULT_LENGTH_SCALE);
        radialSegments = in.readInt("radialSegments", DEFAULT_RADIAL_SEGMENTS);
        lengthSegments = in.readInt("lengthSegments", DEFAULT_LENGTH_SEGMENTS);
        taper = in.readFloat("taper", DEFAULT_TAPER);
        inclination = in.readFloat("inclination", DEFAULT_INCLINATION);
        tipRotation = in.readFloat("tipRotation", DEFAULT_TIP_ROTATION);
        segmentVariation = in.readFloat("segmentVariation", DEFAULT_SEGMENT_VARIATION);
        gravity = in.readFloat("gravity", DEFAULT_GRAVITY);
        hasEndJoint = in.readBoolean("hasEndJoint", DEFAULT_HAS_END_JOINT);
        sideJointCount = in.readInt("sideJointCount", DEFAULT_SIDE_JOINT_COUNT);
        sideJointStartAngle = in.readFloat("sideJointStartAngle", DEFAULT_SIDE_JOINT_START_ANGLE);
        twist = in.readFloat("twist", DEFAULT_TWIST);
    }
}


