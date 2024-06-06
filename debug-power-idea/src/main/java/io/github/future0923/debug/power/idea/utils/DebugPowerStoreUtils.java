package io.github.future0923.debug.power.idea.utils;

import cn.hutool.core.lang.TypeReference;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.io.FileUtil;
import io.github.future0923.debug.power.common.utils.DebugPowerJsonUtils;
import io.github.future0923.debug.power.idea.constant.IdeaPluginProjectConstants;
import io.github.future0923.debug.power.idea.ui.main.MainDialog;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author future0923
 */
public class DebugPowerStoreUtils {

    private static final Logger log = Logger.getInstance(MainDialog.class);

    private static final Map<String, String> globalHeader = new ConcurrentHashMap<>();

    public static void save(Project project) {
        try {
            String filePath = project.getBasePath() + IdeaPluginProjectConstants.GLOBAL_HEADER_FILE;
            File file = new File(filePath);
            if (!file.exists()) {
                FileUtils.touch(file);
            }
            FileUtil.writeToFile(file, DebugPowerJsonUtils.compress(DebugPowerJsonUtils.toJsonStr(globalHeader)), StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("保存全局header文件失败", e);
        }
    }

    public static void putGlobalHeader(String key, String value) {
        if (StringUtils.isNotBlank(key)) {
            globalHeader.put(key, value);
        }
    }

    public static Map<String, String> getAll(Project project) {
        if (globalHeader.isEmpty()) {
            try {
                String filePath = project.getBasePath() + IdeaPluginProjectConstants.GLOBAL_HEADER_FILE;
                File file = new File(filePath);
                if (file.exists()) {
                    String content = FileUtil.loadFile(file, StandardCharsets.UTF_8);
                    Map<String, String> map = DebugPowerJsonUtils.toBean(content, new TypeReference<>() {
                    }, true);
                    globalHeader.putAll(map);
                }
            } catch (IOException e) {
                log.error("读取全局header文件失败", e);
            }
        }
        return globalHeader;
    }

}
