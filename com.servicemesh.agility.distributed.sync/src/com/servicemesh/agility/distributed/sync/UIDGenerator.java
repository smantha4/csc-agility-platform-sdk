package com.servicemesh.agility.distributed.sync;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
import com.fasterxml.uuid.impl.RandomBasedGenerator;

/**
 * Class to generate a unique UUID
 */
public final class UIDGenerator
{

    private static RandomBasedGenerator _generator;

    private static void initGenerator()
    {
        if (_generator == null)
        {
            _generator = new RandomBasedGenerator(null);
        }
    }

    /**
     * Generate a general purpose unique ID. The returned value is the hexadecimal representation of a 128 bit value, i.e. it is
     * 16 octets in the form of 8-4-4-4-12 comprising 36 characters. The implementation guarantees a high level of uniqueness, but
     * makes no provisions to guarantee randomness. It is thread safe, but doesn't use synchronization.
     * 
     * @return the generated unique ID
     */
    public static String generateUID()
    {
        initGenerator();
        return _generator.generate().toString();
    }
}