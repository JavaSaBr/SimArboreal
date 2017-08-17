package com.simsilica.arboreal.test;

import com.jme3.export.binary.BinaryExporter;
import com.jme3.export.binary.BinaryImporter;
import com.simsilica.arboreal.LevelOfDetailParameters;
import com.simsilica.arboreal.LevelOfDetailParameters.ReductionType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author JavaSaBr
 */
public class LevelOfDetailParametersTest {

    @Test
    public void readWriteTest() {

        final ByteArrayOutputStream bout = new ByteArrayOutputStream();

        final LevelOfDetailParameters test = new LevelOfDetailParameters();
        test.setMaxRadialSegments(10);
        test.setBranchDepth(20);
        test.setReduction(ReductionType.Impostor);
        test.setDistance(100);
        test.setRootDepth(5);

        final BinaryExporter exporter = BinaryExporter.getInstance();
        try {
            exporter.save(test, bout);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }

        final BinaryImporter importer = BinaryImporter.getInstance();
        final LevelOfDetailParameters loaded;
        try {
            loaded = (LevelOfDetailParameters) importer.load(new ByteArrayInputStream(bout.toByteArray()));
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }

        Assertions.assertEquals(10, loaded.getMaxRadialSegments());
        Assertions.assertEquals(20, loaded.getBranchDepth());
        Assertions.assertEquals(100, loaded.getDistance());
        Assertions.assertEquals(5, loaded.getRootDepth());
        Assertions.assertEquals(ReductionType.Impostor, loaded.getReduction());
    }
}
