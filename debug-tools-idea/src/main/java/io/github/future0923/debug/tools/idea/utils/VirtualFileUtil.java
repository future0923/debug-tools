package io.github.future0923.debug.tools.idea.utils;

import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;

/**
 * @author future0923
 */
public class VirtualFileUtil {

    public static VirtualFile getVirtualFileByPath(String path) {
        return LocalFileSystem.getInstance().findFileByPath(path);
    }
}
