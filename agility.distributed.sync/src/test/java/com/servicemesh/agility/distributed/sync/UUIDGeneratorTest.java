package com.servicemesh.agility.distributed.sync;

import java.util.HashSet;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.uuid.UUIDType;
import com.fasterxml.uuid.impl.UUIDUtil;
import com.servicemesh.agility.distributed.sync.UIDGenerator;

public class UUIDGeneratorTest
{

    @Test
    public void testGenerateRandomBasedUUIDs()
    {
        // check that all uuids were unique
        // NOTE: technically, this test 'could' fail, but statistically
        // speaking it should be extremely unlikely unless the implementation
        // of (Secure)Random is bad
        HashSet<String> hash_set = new HashSet<String>();
        for (int i = 0; i < 100000; i++)
        {
            String uuid = UIDGenerator.generateUID();
            Assert.assertNotNull(uuid);
            Assert.assertTrue("Uniqueness test failed on insert into HashSet value " + uuid, hash_set.add(uuid));
        }
    }

    @Test
    public void checkUUIDForCorrectVariantAndVersion()
    {
        String suuid = UIDGenerator.generateUID();
        Assert.assertTrue("Lenght of uuid is not 36", suuid.length() == 36);
        UUID uuid = UUIDUtil.uuid(suuid);
        UUIDType expectedType = UUIDType.RANDOM_BASED;
        UUIDType actual = UUIDUtil.typeOf(uuid);

        if (actual != expectedType)
        {
            Assert.fail(
                    "Expected version (type) did not match for UUID '" + uuid + "' expected " + expectedType + ", got " + actual);
        }
        byte[] temp_uuid = UUIDUtil.asByteArray(uuid);

        // extract type from the UUID and check for correct type
        int type = (temp_uuid[UUIDUtil.BYTE_OFFSET_TYPE] & 0xFF) >> 4;
        Assert.assertEquals("Expected type did not match", UUIDType.RANDOM_BASED.raw(), type);
        // extract variant from the UUID and check for correct variant
        int variant = (temp_uuid[UUIDUtil.BYTE_OFFSET_VARIATION] & 0xFF) >> 6;
        Assert.assertEquals("Expected variant did not match", 2, variant);
    }
}
