package io.github.future0923.debug.tools.idea.search;

import com.intellij.ide.util.gotoByName.ChooseByNameItemProvider;
import com.intellij.ide.util.gotoByName.ChooseByNameModel;
import com.intellij.ide.util.gotoByName.ChooseByNamePopup;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.text.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 *
 *
 * @author future0923
 */
public class HttpUrlChooseByNamePopup extends ChooseByNamePopup {

    public static final Key<HttpUrlChooseByNamePopup> CHOOSE_BY_NAME_POPUP_IN_PROJECT_KEY = new Key<>("ChooseByNamePopup");

    /**
     * 本地
     */
    private static final String localhostRegex = "(http(s?)://)?(localhost)(:\\d+)?";

    /**
     * 远程
     */
    private static final String hostAndPortRegex = "^((http|https)://)?(([a-zA-Z0-9]([a-zA-Z0-9\\-]{0,61}[a-zA-Z0-9])?\\.)+[a-zA-Z]{2,6}|((25[0-5]|2[0-4]\\d|[0-1]?\\d\\d?)\\.){3}(25[0-5]|2[0-4]\\d|[0-1]?\\d\\d?))(:\\d{1,5})?";;

    protected HttpUrlChooseByNamePopup(@Nullable Project project, @NotNull ChooseByNameModel model, @NotNull ChooseByNameItemProvider provider, @Nullable ChooseByNamePopup oldPopup, @Nullable String predefinedText, boolean mayRequestOpenInCurrentWindow, int initialIndex) {
        super(project, model, provider, oldPopup, predefinedText, mayRequestOpenInCurrentWindow, initialIndex);
    }

    @NotNull
    public static HttpUrlChooseByNamePopup createPopup(final Project project,
                                                       @NotNull final ChooseByNameModel model,
                                                       @NotNull ChooseByNameItemProvider provider,
                                                       @Nullable final String predefinedText,
                                                       boolean mayRequestOpenInCurrentWindow,
                                                       final int initialIndex) {
        if (!StringUtil.isEmptyOrSpaces(predefinedText)) {
            return new HttpUrlChooseByNamePopup(project, model, provider, null, predefinedText, mayRequestOpenInCurrentWindow, initialIndex);
        }
        final HttpUrlChooseByNamePopup oldPopup = project == null ? null : project.getUserData(CHOOSE_BY_NAME_POPUP_IN_PROJECT_KEY);
        if (oldPopup != null) {
            oldPopup.close(false);
        }
        HttpUrlChooseByNamePopup newPopup = new HttpUrlChooseByNamePopup(project, model, provider, oldPopup, predefinedText, mayRequestOpenInCurrentWindow, initialIndex);
        if (project != null) {
            project.putUserData(CHOOSE_BY_NAME_POPUP_IN_PROJECT_KEY, newPopup);
        }
        return newPopup;
    }

    @NotNull
    @Override
    public String transformPattern(@NotNull String pattern) {
        return getTransformedPattern(pattern, getModel());
    }

    @NotNull
    public static String getTransformedPattern(@NotNull String pattern, @NotNull ChooseByNameModel model) {
        if (!(model instanceof HttpUrlFilteringGotoByModel)) {
            return pattern;
        }
        pattern = removeRedundancyMarkup(pattern);
        return pattern;
    }
    /**
     * 移除没有用的字符串
     */
    @NotNull
    public static String removeRedundancyMarkup(@NotNull String pattern) {
        if (pattern.contains("localhost")) {
            pattern = pattern.replaceFirst(localhostRegex, "");
        }
        if (pattern.contains("http:") || pattern.contains("https:")) {
            pattern = pattern.replaceFirst(hostAndPortRegex, "");
        }
        // 包含参数
        if (pattern.contains("?")) {
            pattern = pattern.substring(0, pattern.indexOf("?"));
        }
        return pattern;
    }


    @Override
    @Nullable
    public String getMemberPattern() {
        final String enteredText = getTrimmedText();
        final int index = enteredText.lastIndexOf('#');
        if (index == -1) {
            return null;
        }
        String name = enteredText.substring(index + 1).trim();
        return StringUtil.isEmptyOrSpaces(name) ? null : name;
    }
}
