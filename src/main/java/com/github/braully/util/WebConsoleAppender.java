package com.github.braully.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
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

@Plugin(name = "WebConsoleAppender", category = "Core", elementType = "appender", printObject = true)
public class WebConsoleAppender extends AbstractAppender {

    public static final String _revision = "$Id: WebConsoleAppender.java,v 1.3 2013-09-24 02:37:09 knoxg Exp $";

    public final static long DEFAULT_LOG_SIZE = 500;
    private static long maximumLogSize = DEFAULT_LOG_SIZE;
    private static final LinkedList<LogEvent> loggingEvents = new LinkedList<LogEvent>();

    public WebConsoleAppender(String name, Filter filter,
            Layout<? extends Serializable> layout,
            boolean ignoreExceptions, Property[] properties) {
        super(name, filter, layout, ignoreExceptions, properties);
    }

    public void setMaximumLogSize(long logSize) {
        maximumLogSize = logSize;
    }

    public long getMaximumLogSize() {
        return maximumLogSize;
    }

    @Override
    public synchronized void append(LogEvent event) {
        synchronized (loggingEvents) {
            if (loggingEvents.size() >= maximumLogSize) {
                loggingEvents.removeLast();
            }

            loggingEvents.addFirst(event);
        }
    }

    protected boolean checkEntryConditions() {
        return true;
    }

    public synchronized void close() {
        loggingEvents.clear();
    }

    public static synchronized void clear() {
        synchronized (loggingEvents) {
            loggingEvents.clear();
        }
    }

    public boolean requiresLayout() {
        return false;
    }

    public static List<LogEvent> getLoggingEvents() {
        return new ArrayList<LogEvent>(loggingEvents);
    }

    public static List<LogEvent> getLoggingEvents(long timestamp) {
        List<LogEvent> tmpLoggs = new ArrayList<LogEvent>();
        for (LogEvent e : loggingEvents) {
            if (timestamp < e.getTimeMillis()) {
                tmpLoggs.add(e);
            }
        }
        return tmpLoggs;
    }

    static List<String> getLines() {
        List<String> lines = new ArrayList<>();
        if (loggingEvents != null) {
            for (LogEvent e : loggingEvents) {
                Object message = e.getMessage();
                lines.add("" + message);
                lines.add("" + e);
            }
        }
        return lines;
    }

    @PluginFactory
    public static WebConsoleAppender createAppender(
            @PluginAttribute("name") String name,
            @PluginElement("Layout") Layout<? extends Serializable> layout,
            @PluginElement("Filter") final Filter filter,
            @PluginAttribute("otherAttribute") String otherAttribute) {
        if (name == null) {
            LOGGER.error("No name provided for WebConsoleAppender");
            return null;
        }
        if (layout == null) {
            layout = PatternLayout.createDefaultLayout();
        }
        return new WebConsoleAppender(name, filter, layout, true, null);
    }
}
