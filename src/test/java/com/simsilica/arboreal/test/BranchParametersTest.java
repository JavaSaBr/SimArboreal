package com.simsilica.arboreal.test;

import com.jme3.export.binary.BinaryExporter;
import com.jme3.export.binary.BinaryImporter;
import com.simsilica.arboreal.BranchParameters;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author JavaSaBr
 */
public class BranchParametersTest {

    @Test
    public void readWriteTest() {

        final ByteArrayOutputStream bout = new ByteArrayOutputStream();

        final BranchParameters test = new BranchParameters();
        test.setEnabled(false);
        test.setGravity(3);
        test.setHasEndJoint(false);
        test.setInclination(2);
        test.setInherit(false);
        test.setLengthScale(4);
        test.setLengthSegments(5);
        test.setRadialSegments(6);
        test.setRadiusScale(2.5F);
        test.setSegmentVariation(5.5F);
        test.setSideJointCount(7);
        test.setSideJointStartAngle(1.5F);
        test.setTwist(6.5F);
        test.setTipRotation(7.1F);
        test.setTaper(3.3F);

        final BinaryExporter exporter = BinaryExporter.getInstance();
        try {
            exporter.save(test, bout);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }

        final BinaryImporter importer = BinaryImporter.getInstance();
        final BranchParameters loaded;
        try {
            loaded = (BranchParameters) importer.load(new ByteArrayInputStream(bout.toByteArray()));
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }

        Assertions.assertEquals(false, loaded.isEnabled());
        Assertions.assertEquals(3, loaded.getGravity());
        Assertions.assertEquals(false, loaded.isHasEndJoint());
        Assertions.assertEquals(2, loaded.getInclination());
        Assertions.assertEquals(false, loaded.isInherit());
        Assertions.assertEquals(4, loaded.getLengthScale());
        Assertions.assertEquals(5, loaded.getLengthSegments());
        Assertions.assertEquals(6, loaded.getRadialSegments());
        Assertions.assertEquals(2.5F, loaded.getRadiusScale());
        Assertions.assertEquals(5.5F, loaded.getSegmentVariation());
        Assertions.assertEquals(7, loaded.getSideJointCount());
        Assertions.assertEquals(1.5F, loaded.getSideJointStartAngle());
        Assertions.assertEquals(6.5F, loaded.getTwist());
        Assertions.assertEquals(7.1F, loaded.getTipRotation());
        Assertions.assertEquals(3.3F, loaded.getTaper());
    }
}
