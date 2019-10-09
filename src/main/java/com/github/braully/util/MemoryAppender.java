/*
 * https://www.randomnoun.com/src/common-public/src/main/java/com/randomnoun/common/log4j/MemoryAppender.java
 Copyright 2109 Braully Rocha

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 
 
 */
package com.github.braully.util;

/* (c) 2013 randomnoun. All Rights Reserved. This work is licensed under a
 * BSD Simplified License. (http://www.randomnoun.com/bsd-simplified.html)
 */
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.PatternLayout;

/**
 * Log4j appender to capture logging events in an in-memory List. I'm amazed
 * this isn't in the log4j package.
 *
 * <p>
 * The code in this class is roughly based on the WriterAppender class in the
 * log4j source code.
 *
 * <p>
 * This appender can be configured using the property "maximumLogSize" which
 * limits the number of logging events captured by this class (old events are
 * popped off the list when the list becomes full).
 *
 * <p>
 * Use the {@link #getLoggingEvents()} to return the List of events written to
 * this class. This list is a *copy* of the list contained within this class, so
 * it can safely be iterated over even if logging events are still occurring in
 * an application.
 *
 * <p>
 * Example log4j configuration:
 * <pre class="code">
 * log4j.rootLogger=DEBUG, MEMORY
 *
 * log4j.appender.MEMORY=com.randomnoun.common.log4j.MemoryAppender
 * log4j.appender.MEMORY.MaximumLogSize=1000
 * </pre>
 *
 * You can then obtain the list of events via the code:
 * <pre>
 * MemoryAppender memoryAppender = (MemoryAppender) Logger.getRootLogger().getAppender("MEMORY");
 * List events = memoryAppender.getEvents();
 * </pre>
 *
 * @blog http://www.randomnoun.com/wp/2013/01/13/logging/
 * @version $Id: MemoryAppender.java,v 1.3 2013-09-24 02:37:09 knoxg Exp $
 * @author knoxg
 */
@Plugin(name = "MemoryAppender", category = "Core", elementType = "appender", printObject = true)
public class MemoryAppender
        extends AbstractAppender {

    /**
     * A revision marker to be used in exception stack traces.
     */
    public static final String _revision = "$Id: MemoryAppender.java,v 1.3 2013-09-24 02:37:09 knoxg Exp $";

    public final static long DEFAULT_LOG_SIZE = 500;
    private long maximumLogSize = DEFAULT_LOG_SIZE;
    private LinkedList<LoggingEvent> loggingEvents = new LinkedList<>();
    private LinkedList<LogEvent> logEvents = new LinkedList<>();

    public MemoryAppender(String name, Filter filter, Layout<? extends Serializable> layout, boolean ignoreExceptions, Property[] properties) {
        super(name, filter, layout, ignoreExceptions, properties);
    }

    /**
     * Immediate flush is always set to true, regardless of how this logger is
     * configured.
     *
     * @param value ignored
     */
    public void setImmediateFlush(boolean value) {
        // this method does nothing
    }

    /**
     * Returns value of the <b>ImmediateFlush</b> option.
     */
    public boolean getImmediateFlush() {
        return true;
    }

    /**
     * Set the maximum log size
     */
    public void setMaximumLogSize(long logSize) {
        this.maximumLogSize = logSize;
    }

    /**
     * Return the maximum log size
     */
    public long getMaximumLogSize() {
        return maximumLogSize;
    }

    /**
     * This method does nothing.
     */
    public void activateOptions() {
    }

    /**
     * This method is called by the {@link AppenderSkeleton#doAppend} method.
     * <p>
     * If the output stream exists and is writable then write a log statement to
     * the output stream. Otherwise, write a single warning message to
     * <code>System.err</code>.
     * <p>
     * The format of the output will depend on this appender's layout.
     */
    @Override
    public void append(LogEvent event) {
        synchronized (logEvents) {
            if (logEvents.size() >= maximumLogSize) {
                logEvents.removeLast();
            }
            logEvents.addFirst(event);
        }
    }

    public void append(LoggingEvent event) {
        // Reminder: the nesting of calls is:
        //
        //    doAppend()
        //      - check threshold
        //      - filter
        //      - append();
        //        - checkEntryConditions();
        //        - subAppend();
        if (!checkEntryConditions()) {
            return;
        }

        subAppend(event);
    }

    /**
     * This method determines if there is a sense in attempting to append.
     * <p>
     * It checks whether there is a set output target and also if there is a set
     * layout. If these checks fail, then the boolean value <code>false</code>
     * is returned.
     *
     * <p>
     * This method always returns true; which I guess means we can't have
     * thresholds set on our MemoryAppender.
     */
    protected boolean checkEntryConditions() {
        /*
         if (this.layout == null)
         {
         errorHandler.error("No layout set for the appender named [" + name + "].");
         return false;
         } */
        return true;
    }

    /**
     * Close this appender instance. The event log list is cleared by this
     * method. This method is identical to calling clear()
     */
    public synchronized void close() {
        loggingEvents.clear();
    }

    /**
     * Clear all events from this appender.
     */
    public synchronized void clear() {
        synchronized (loggingEvents) {
            loggingEvents.clear();
        }
    }

    /**
     * Actual appending occurs here.
     */
    protected void subAppend(LoggingEvent event) {
        // this.qw.write(this.layout.format(event));
        synchronized (loggingEvents) {
            if (loggingEvents.size() >= maximumLogSize) {
                loggingEvents.removeLast();
            }

            loggingEvents.addFirst(event);
        }
    }

    /**
     * The MemoryAppender does not require a layout. Hence, this method returns
     * <code>false</code>.
     */
    public boolean requiresLayout() {
        return false;
    }

    /**
     * Returns a list of logging events captured by this appender. (This list is
     * cloned in order to prevent ConcurrentModificationExceptions occuring
     * whilst iterating through it)
     */
    public List<LoggingEvent> getLoggingEvents() {
        return new ArrayList<LoggingEvent>(loggingEvents);
    }

    public LinkedList<LogEvent> getLogEvents() {
        return logEvents;
    }

    @PluginFactory
    public static MemoryAppender createAppender(
            @PluginAttribute("name") String name,
            @PluginElement("Layout") Layout<? extends Serializable> layout,
            @PluginElement("Filter") final Filter filter,
            @PluginAttribute("otherAttribute") String otherAttribute) {
        if (name == null) {
            LOGGER.error("No name provided for MemoryAppender");
            return null;
        }
        if (layout == null) {
            layout = PatternLayout.createDefaultLayout();
        }
        return new MemoryAppender(name, filter, layout, true, null);
    }
}
