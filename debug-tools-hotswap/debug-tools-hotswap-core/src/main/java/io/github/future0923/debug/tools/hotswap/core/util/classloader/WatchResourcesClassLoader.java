/*
 * Copyright (C) 2024-2025 the original author or authors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package io.github.future0923.debug.tools.hotswap.core.util.classloader;

import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.hotswap.core.watch.WatchEventListener;
import io.github.future0923.debug.tools.hotswap.core.watch.WatchFileEvent;
import io.github.future0923.debug.tools.hotswap.core.watch.Watcher;
import lombok.Setter;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

/**
 * Special URL classloader to get only changed resources from URL.
 *
 * Use this classloader to support watchResources property.
 *
 * This classloader checks if the resource was modified after application startup and in that case
 * delegates getResource()/getResources() to custom URL classloader. Otherwise returns null or resource
 * from paren classloader (depending on searchParent property).
 */
public class WatchResourcesClassLoader extends URLClassLoader {
    private static final Logger LOGGER = Logger.getLogger(WatchResourcesClassLoader.class);

    /**
     * URLs of changed resources. Use this set to check if the resource was changed and hence should
     * be returned by this classloader.
     */
    Set<URL> changedUrls = new HashSet<>();

    /**
     * Watch for requested resource in parent classloader in case it is not found by this classloader?
     * Note that there is child first precedence anyway.
     */
    @Setter
    boolean searchParent = true;

    /**
     * URL classloader configured to get resources only from exact set of URL's (no parent delegation)
     */
    ClassLoader watchResourcesClassLoader;

    public WatchResourcesClassLoader() {
        this(false);
    }

    public WatchResourcesClassLoader(boolean searchParent) {
        super(new URL[]{}, searchParent ? WatchResourcesClassLoader.class.getClassLoader() : null);
        this.searchParent = searchParent;
    }

    public WatchResourcesClassLoader(ClassLoader classLoader) {
        super(new URL[] {}, classLoader);
        this.searchParent = false;
    }

    /**
     * Configure new instance with urls and watcher service.
     *
     * @param extraPath the URLs from which to load resources
     */
    public void initExtraPath(URL[] extraPath) {
        for (URL url : extraPath)
            addURL(url);
    }

    /**
     * Configure new instance with urls and watcher service.
     *
     * @param watchResources the URLs from which to load resources
     * @param watcher        watcher service to register watch events
     */
    public void initWatchResources(URL[] watchResources, Watcher watcher) {
        // create classloader to serve resources only from watchResources URL's
        this.watchResourcesClassLoader = new UrlOnlyClassLoader(watchResources);

        // register watch resources - on change event each modified resource will be added to changedUrls.
        for (URL resource : watchResources) {
            try {
                URI uri = resource.toURI();
                LOGGER.debug("Watching directory '{}' for changes.", uri);
                watcher.addEventListener(this, uri, new WatchEventListener() {
                    @Override
                    public void onEvent(WatchFileEvent event) {
                        try {
                            if (event.isFile() || event.isDirectory()) {
                                changedUrls.add(event.getURI().toURL());
                                LOGGER.trace("File '{}' changed and will be returned instead of original classloader equivalent.", event.getURI().toURL());
                            }
                        } catch (MalformedURLException e) {
                            LOGGER.error("Unexpected - cannot convert URI {} to URL.", e, event.getURI());
                        }
                    }
                });
            } catch (URISyntaxException e) {
                LOGGER.warning("Unable to convert watchResources URL '{}' to URI. URL is skipped.", e, resource);
            }
        }
    }

    /**
     * Check if the resource was changed after this classloader instantiaton.
     *
     * @param url full URL of the file
     * @return true if was changed after instantiation
     */
    public boolean isResourceChanged(URL url) {
        return changedUrls.contains(url);
    }

    /**
     * Returns URL only if the resource is found in changedURL and was actually changed after
     * instantiation of this classloader.
     */
    @Override
    public URL getResource(String name) {
        if (watchResourcesClassLoader != null) {
            URL resource = watchResourcesClassLoader.getResource(name);
            if (resource != null && isResourceChanged(resource)) {
                LOGGER.trace("watchResources - using changed resource {}", name);
                return resource;
            }
        }

        // child first (extra classpath)
        URL resource = findResource(name);
        if (resource != null)
            return resource;

        // without parent do not call super (ignore even bootstrapResources)
        if (searchParent)
            return super.getResource(name);
        else
            return null;
    }

    @Override
    public InputStream getResourceAsStream(String name) {
        URL url = getResource(name);
        try {
            return url != null ? url.openStream() : null;
        } catch (IOException e) {
        }
        return null;
    }

    /**
     * Returns only a single instance of the changed resource.
     * There are conflicting requirements for other resources inclusion. This class
     * should "hide" the original resource, hence it should not be included in the resoult.
     * On the other hand, there may be resource with the same name in other JAR which
     * should be included and now is hidden (for example multiple persistence.xml).
     * Maybe a new property to influence this behaviour?
     */
    @Override
    public Enumeration<URL> getResources(String name) throws IOException {
        if (watchResourcesClassLoader != null) {
            URL resource = watchResourcesClassLoader.getResource(name);
            if (resource != null && isResourceChanged(resource)) {
                LOGGER.trace("watchResources - using changed resource {}", name);
                Vector<URL> res = new Vector<>();
                res.add(resource);
                return res.elements();
            }
        }

        // if extraClasspath contains at least one element, return only extraClasspath
        if (findResources(name).hasMoreElements())
            return findResources(name);

        return super.getResources(name);
    }

    /**
     * Support for classpath builder on Tomcat.
     */
    public String getClasspath() {
        ClassLoader parent = getParent();

        if (parent == null)
            return null;

        try {
            Method m = parent.getClass().getMethod("getClasspath", new Class[] {});
            if( m==null ) return null;
            Object o = m.invoke( parent, new Object[] {} );
            if( o instanceof String )
                return (String)o;
            return null;
        } catch( Exception ex ) {
            LOGGER.debug("getClasspath not supported on parent classloader.");
        }
        return null;

    }

    /**
     * Helper classloader to get resources from list of urls only.
     */
    public static class UrlOnlyClassLoader extends URLClassLoader {
        public UrlOnlyClassLoader(URL[] urls) {
            super(urls);
        }

        // do not use parent resource (may introduce infinite loop)
        @Override
        public URL getResource(String name) {
            return findResource(name);
        }
    };
}
