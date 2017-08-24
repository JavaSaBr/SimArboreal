package com.simsilica.arboreal.test;

import com.jme3.export.binary.BinaryExporter;
import com.jme3.export.binary.BinaryImporter;
import com.simsilica.arboreal.TreeParameters;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author JavaSaBr
 */
public class TreeParametersTest {

    @Test
    public void readWriteTest() {

        final ByteArrayOutputStream bout = new ByteArrayOutputStream();

        final TreeParameters test = new TreeParameters();
        test.setBaseScale(2.3F);

        final BinaryExporter exporter = BinaryExporter.getInstance();
        try {
            exporter.save(test, bout);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }

        final BinaryImporter importer = BinaryImporter.getInstance();
        final TreeParameters loaded;
        try {
            loaded = (TreeParameters) importer.load(new ByteArrayInputStream(bout.toByteArray()));
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }

        Assertions.assertEquals(2.3F, loaded.getBaseScale());
    }
}
