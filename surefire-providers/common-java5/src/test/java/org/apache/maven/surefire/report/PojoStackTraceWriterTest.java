package org.apache.maven.surefire.report;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import junit.framework.TestCase;

import static org.assertj.core.api.Assertions.assertThat;

/**
 *
 */
public class PojoStackTraceWriterTest
    extends TestCase
{

    public void testTrimmedThrowableReal()
    {
        PojoStackTraceWriter w =
            new PojoStackTraceWriter( ATestClass.AnotherTestClass.class.getName(), "testQuote", getAThrowAble() );
        String out = w.writeTrimmedTraceToString();
        String expected = "org.apache.maven.surefire.report.PojoStackTraceWriterTest$ATestClass$AnotherTestClass"
                + ".getAThrowable(PojoStackTraceWriterTest.java";
        assertTrue( out.contains( expected ) );
    }

    public void testMultiLineMessage()
    {
        String msg =
            "assert \"foo\" == \"bar\"\n"
            + "             |\n"
            + "             false";
        try
        {
            throw new RuntimeException( msg );
        }
        catch ( Throwable t )
        {
            PojoStackTraceWriter writer = new PojoStackTraceWriter( null, null, t );
            String stackTrace = writer.writeTraceToString();
            assertTrue( stackTrace.startsWith( "java.lang.RuntimeException: \n" + msg ) );
        }
    }

    public void testTrimmedStackTrace( )
    {
        Throwable throwable = new Exception( "" );

        try
        {
            throwExceptionFromTestClass( );
        }
        catch ( Throwable t )
        {
            throwable = t;
        }
        PojoStackTraceWriter a = new PojoStackTraceWriter(
            "org.apache.maven.surefire.report.PojoStackTraceWriterTest", null, throwable
        );
        String result = a.trimmedStackTrace();

        assertThat( result ).contains( "org.apache.maven.surefire.report.PojoStackTraceWriterTest."
            + "throwExceptionFromTestClass(PojoStackTraceWriterTest.java" );
        assertThat( result ).contains( "org.apache.maven.surefire.report.PojoStackTraceWriterTest."
            + "testTrimmedStackTrace(PojoStackTraceWriterTest.java" );
        assertThat( result ).hasLineCount( 8 );
        assertThat( result ).contains( "Caused by: java.lang.Exception: Hey ho, hey ho, a throwable we throw!" );
    }

    public void throwExceptionFromTestClass() throws Exception
    {
        Object call = new RunnableTestClass1().call();
        throw new IllegalStateException( "illegal state", (Throwable) call );
    }

    static class ATestClass
    {
        static class AnotherTestClass
        {
            public static Throwable getAThrowable()
            {
                try
                {
                    throw new Exception( "Hey ho, hey ho, a throwable we throw!" );
                }
                catch ( Exception e )
                {
                    return e;
                }
            }
        }
    }

    private Throwable getAThrowAble()
    {
        return ATestClass.AnotherTestClass.getAThrowable();
    }

}
