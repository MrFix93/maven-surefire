package org.apache.maven.surefire.util.internal;

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

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Creates new daemon Thread.
 */
public final class DaemonThreadFactory
    implements ThreadFactory
{
    private static final AtomicInteger POOL_NUMBER = new AtomicInteger( 1 );

    private final AtomicInteger threadNumber = new AtomicInteger( 1 );

    private final ThreadGroup group;

    private final String namePrefix;

    private DaemonThreadFactory()
    {
        SecurityManager s = System.getSecurityManager();
        group = s != null ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
        namePrefix = "pool-" + POOL_NUMBER.getAndIncrement() + "-thread-";
    }

    public Thread newThread( Runnable r )
    {
        Thread t = new Thread( group, r, namePrefix + threadNumber.getAndIncrement() );
        if ( t.getPriority() != Thread.NORM_PRIORITY )
        {
            t.setPriority( Thread.NORM_PRIORITY );
        }
        t.setDaemon( true );
        return t;
    }

    /**
     * Should be used by thread pools.
     */
    public static ThreadFactory newDaemonThreadFactory()
    {
        return new DaemonThreadFactory();
    }

    public static ThreadFactory newDaemonThreadFactory( String name )
    {
        return new NamedThreadFactory( name );
    }

    public static Thread newDaemonThread( Runnable r )
    {
        SecurityManager s = System.getSecurityManager();
        ThreadGroup group = s == null ? Thread.currentThread().getThreadGroup() : s.getThreadGroup();
        Thread t = new Thread( group, r );
        if ( t.getPriority() != Thread.NORM_PRIORITY )
        {
            t.setPriority( Thread.NORM_PRIORITY );
        }
        t.setDaemon( true );
        return t;
    }

    public static Thread newDaemonThread( Runnable r, String name )
    {
        SecurityManager s = System.getSecurityManager();
        ThreadGroup group = s == null ? Thread.currentThread().getThreadGroup() : s.getThreadGroup();
        Thread t = new Thread( group, r, name );
        if ( t.getPriority() != Thread.NORM_PRIORITY )
        {
            t.setPriority( Thread.NORM_PRIORITY );
        }
        t.setDaemon( true );
        return t;
    }

    private static class NamedThreadFactory
        implements ThreadFactory
    {

        private final String name;

        private NamedThreadFactory( String name )
        {
            this.name = name;
        }

        public Thread newThread( Runnable r )
        {
            return newDaemonThread( r, name );
        }
    }
}
