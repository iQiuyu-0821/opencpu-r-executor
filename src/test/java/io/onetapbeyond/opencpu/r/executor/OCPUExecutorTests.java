/*
 * Copyright 2015 David Russell
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.onetapbeyond.opencpu.r.executor;

import org.junit.Test;
import org.junit.BeforeClass;
import static org.junit.Assert.*;
import java.io.*;
import java.util.*;

public class OCPUExecutorTests {

    private static String endpoint;

    @BeforeClass
    public static void setUpClass() {
        endpoint = "http://public.opencpu.org/ocpu";
    }

    @Test
    public void testLibraryFunctionRnormCall() throws OCPUException {

        Map data = new HashMap();
        data.put("n", 10);
        data.put("mean", 5);
        OCPUTask oTask = OCPU.R()
                             .pkg("stats")
                             .function("rnorm")
                             .input(data)
                             .library();
        OCPUResult oResult = oTask.execute(endpoint);
        assertTrue(oResult.success());
        assertNotNull(oResult.input());
        assertEquals(oResult.input().size(), data.size());
        assertNotNull(oResult.output());
        assertNull(oResult.error());
        assertNull(oResult.cause());
    }

    @Test
    public void testLibraryFunctionTvScoreCall() throws OCPUException {

        Map data = new HashMap();
        Map jane = new HashMap();
        jane.put("age", 26);
        jane.put("marital", "MARRIED");
        Map john = new HashMap();
        john.put("age", 56);
        john.put("marital", "DIVORCED");
        List people = Arrays.asList(jane, john);
        data.put("input", people);

        OCPUTask oTask = OCPU.R()
                             .pkg("tvscore")
                             .function("tv")
                             .input(data)
                             .library();
        OCPUResult oResult = oTask.execute(endpoint);
        assertTrue(oResult.success());
        assertNotNull(oResult.input());
        assertEquals(oResult.input().size(), data.size());
        assertNotNull(oResult.output());
        assertNull(oResult.error());
        assertNull(oResult.cause());
    }

    @Test
    public void testGithubFunctionGeodistanceCall() throws OCPUException {

        Map data = new HashMap();
        data.put("long", Arrays.asList(-74.0064,-118.2430,-74.0064));
        data.put("lat", Arrays.asList(40.7142,34.0522,40.7142));
        OCPUTask oTask = OCPU.R()
                             .user("openmhealth")
                             .pkg("dpu.mobility")
                             .function("geodistance")
                             .input(data)
                             .github();
        OCPUResult oResult = oTask.execute(endpoint);
        assertTrue(oResult.success());
        assertNotNull(oResult.input());
        assertEquals(oResult.input().size(), data.size());
        assertNotNull(oResult.output());
        assertNull(oResult.error());
        assertNull(oResult.cause());
    }

    @Test
    public void testLibraryFunctionRnormCallMissingPackage() throws OCPUException {

        OCPUTask oTask = OCPU.R()
                             .function("rnorm")
                             .library();
        OCPUResult oResult = oTask.execute(endpoint);
        assertFalse(oResult.success());
        assertNull(oResult.input());
        assertNull(oResult.output());
        assertNotNull(oResult.error());
        assertNotNull(oResult.cause());
    }

    @Test
    public void testLibraryFunctionMissingCall() throws OCPUException {

        OCPUTask oTask = OCPU.R()
                             .pkg("stats")
                             .library();
        OCPUResult oResult = oTask.execute(endpoint);
        assertFalse(oResult.success());
        assertNull(oResult.input());
        assertNull(oResult.output());
        assertNotNull(oResult.error());
        assertNotNull(oResult.cause());
    }

    @Test
    public void testLibraryFunctionRnormCallMissingParameters() throws OCPUException {

        OCPUTask oTask = OCPU.R()
                             .pkg("stats")
                             .function("rnorm")
                             .library();
        OCPUResult oResult = oTask.execute(endpoint);
        assertFalse(oResult.success());
        assertNull(oResult.input());
        assertNull(oResult.output());
        assertNotNull(oResult.error());
        assertNotNull(oResult.cause());
    }

    @Test
    public void testScriptCh01Call() throws OCPUException {

        Map data = new HashMap();
        OCPUTask oTask = OCPU.R()
                             .pkg("MASS")
                             .script("ch01.R", "dd")
                             .library();
        OCPUResult oResult = oTask.execute(endpoint);
        assertTrue(oResult.success());
        assertNull(oResult.input());
        assertNotNull(oResult.output());
        assertNull(oResult.error());
        assertNull(oResult.cause());
    }

    @Test
    public void testLibraryScriptCh01CallMissingPackage() throws OCPUException {

        OCPUTask oTask = OCPU.R()
                             .script("ch01.R", "dd")
                             .library();
        OCPUResult oResult = oTask.execute(endpoint);
        assertFalse(oResult.success());
        assertNull(oResult.input());
        assertNull(oResult.output());
        assertNotNull(oResult.error());
        assertNotNull(oResult.cause());
    }

    @Test
    public void testLibraryScriptMissingCall() throws OCPUException {

        OCPUTask oTask = OCPU.R()
                             .pkg("stats")
                             .library();
        OCPUResult oResult = oTask.execute(endpoint);
        assertFalse(oResult.success());
        assertNull(oResult.input());
        assertNull(oResult.output());
        assertNotNull(oResult.error());
        assertNotNull(oResult.cause());
    }

}