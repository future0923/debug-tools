package io.github.future0923.debug.tools.idea.utils;

import com.intellij.openapi.util.IconLoader;

import javax.swing.*;

/**
 * @author future0923
 */
public class DebugToolsIcons {

    public static final Icon DebugTools = IconLoader.getIcon("/icon/plugin_icon.svg", DebugToolsIcons.class);
    public static final Icon ExampleSimple = IconLoader.getIcon("/icon/example_simple.svg", DebugToolsIcons.class);
    public static final Icon ExampleCurrent = IconLoader.getIcon("/icon/example_current.svg", DebugToolsIcons.class);
    public static final Icon ExampleAll = IconLoader.getIcon("/icon/example_all.svg", DebugToolsIcons.class);
    public static final Icon Pretty = IconLoader.getIcon("/icon/pretty.svg", DebugToolsIcons.class);
    public static final Icon Compress = IconLoader.getIcon("/icon/compress.svg", DebugToolsIcons.class);
    public static final Icon Import = IconLoader.getIcon("/icon/import.svg", DebugToolsIcons.class);
    public static final Icon Export = IconLoader.getIcon("/icon/export.svg", DebugToolsIcons.class);
    public static final Icon Add = IconLoader.getIcon("/icon/add.svg", DebugToolsIcons.class);
    public static final Icon Clear = IconLoader.getIcon("/icon/clear.svg", DebugToolsIcons.class);
    public static final Icon Groovy = IconLoader.getIcon("/icon/groovy.svg", DebugToolsIcons.class);
    public static final Icon Request = IconLoader.getIcon("/icon/request.svg", DebugToolsIcons.class);
    public static final Icon Last = IconLoader.getIcon("/icon/last.svg", DebugToolsIcons.class);
    public static final Icon Last_ClassLoader = IconLoader.getIcon("/icon/last_classloader.svg", DebugToolsIcons.class);
    public static final Icon Connect = IconLoader.getIcon("/icon/connect.svg", DebugToolsIcons.class);
    public static final Icon Search = IconLoader.getIcon("/icon/search.svg", DebugToolsIcons.class);

    public static final class HttpMethod {
        public static final Icon Delete = IconLoader.getIcon("/icon/request/delete.png", DebugToolsIcons.class);
        public static final Icon Get = IconLoader.getIcon("/icon/request/get.png", DebugToolsIcons.class);
        public static final Icon Head = IconLoader.getIcon("/icon/request/head.png", DebugToolsIcons.class);
        public static final Icon Options = IconLoader.getIcon("/icon/request/options.png", DebugToolsIcons.class);
        public static final Icon Patch = IconLoader.getIcon("/icon/request/patch.png", DebugToolsIcons.class);
        public static final Icon Post = IconLoader.getIcon("/icon/request/post.png", DebugToolsIcons.class);
        public static final Icon Put = IconLoader.getIcon("/icon/request/put.png", DebugToolsIcons.class);
        public static final Icon Request = IconLoader.getIcon("/icon/request/request.png", DebugToolsIcons.class);
        public static final Icon Trace = IconLoader.getIcon("/icon/request/trace.png", DebugToolsIcons.class);
    }

    public static final class Hotswap {
        public static final Icon Off = IconLoader.getIcon("/icon/hotswap/hotswap.svg", DebugToolsIcons.class);
        public static final Icon On = IconLoader.getIcon("/icon/hotswap/hotswap_on.svg", DebugToolsIcons.class);
    }
}
