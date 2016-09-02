package org.eclipse.kapua.service.generator.id.random.mysql;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.generator.id.IdGeneratorService;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class IdGeneratorServiceImplTest extends Assert
{
    @Test
    public void testIdGeneration()
        throws Exception
    {
        KapuaLocator locator = KapuaLocator.getInstance();
        IdGeneratorService idGeneratorService = locator.getService(IdGeneratorService.class);

        KapuaId id = idGeneratorService.generate();

        assertNotNull(id);
        assertNotNull(id.getShortId());
        assertTrue(!id.getShortId().isEmpty());
        assertNotNull(id.getId());
    }

    @Test
    public void testBulkIdGeneration()
        throws Exception
    {
        KapuaLocator locator = KapuaLocator.getInstance();
        IdGeneratorService idGeneratorService = locator.getService(IdGeneratorService.class);

        Set<KapuaId> generatedIds = new HashSet<>();
        for (int i = 0; i < 1000; i++) {
            KapuaId id = idGeneratorService.generate();
            assertFalse(generatedIds.contains(id));
            generatedIds.add(id);
        }
    }

}
