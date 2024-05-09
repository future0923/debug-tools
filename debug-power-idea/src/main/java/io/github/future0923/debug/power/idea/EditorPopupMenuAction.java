package io.github.future0923.debug.power.idea;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.UserDataHolder;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.util.PsiTreeUtil;
import com.sun.tools.attach.VirtualMachine;
import io.github.future0923.debug.power.common.dto.RunContentDTO;
import io.github.future0923.debug.power.common.dto.RunDTO;
import io.github.future0923.debug.power.common.utils.DebugPowerFileUtils;
import io.github.future0923.debug.power.common.utils.DebugPowerJsonUtils;
import io.github.future0923.debug.power.idea.context.ClassDataContext;
import io.github.future0923.debug.power.idea.context.DataContext;
import io.github.future0923.debug.power.idea.context.MethodDataContext;
import io.github.future0923.debug.power.idea.model.ParamCache;
import io.github.future0923.debug.power.idea.model.ServerDisplayValue;
import io.github.future0923.debug.power.idea.setting.DebugPowerSettingState;
import io.github.future0923.debug.power.idea.ui.main.MainDialog;
import io.github.future0923.debug.power.idea.utils.DebugPowerActionUtil;
import io.github.future0923.debug.power.idea.utils.DebugPowerIdeaClassUtil;
import io.github.future0923.debug.power.idea.utils.DebugPowerNotifierUtil;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * 右键菜单
 *
 * @author future0923
 */
public class EditorPopupMenuAction extends AnAction {

    private static final Logger log = Logger.getInstance(EditorPopupMenuAction.class);

    private final static Key<PsiMethod> USER_DATE_ELEMENT_KEY = new Key<>("user.psi.Element");

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        final Project project = e.getProject();
        final Editor editor = e.getData(CommonDataKeys.EDITOR);
        if (null == project || editor == null) {
            throw new IllegalArgumentException("idea arg error (project or editor is null)");
        }

        try {
            PsiMethod psiMethod = null;
            if (e.getDataContext() instanceof UserDataHolder) {
                psiMethod = ((UserDataHolder) e.getDataContext()).getUserData(USER_DATE_ELEMENT_KEY);
            }
            if (psiMethod == null) {
                PsiFile file = e.getData(CommonDataKeys.PSI_FILE);
                psiMethod = PsiTreeUtil.getParentOfType(getElement(editor, file), PsiMethod.class);
                if (psiMethod == null) {
                    throw new IllegalArgumentException("idea arg error (method is null)");
                }
            }

            DebugPowerSettingState settingState = DebugPowerSettingState.getInstance(project);
            if (settingState == null) {
                DebugPowerNotifierUtil.notifyError(project, "state not exists");
                return;
            }

            if (StringUtils.isBlank(settingState.getAgentPath())) {
                InputStream inputStream = EditorPopupMenuAction.class.getResourceAsStream("/lib/debug-power-agent-jar-with-dependencies.jar");
                if (inputStream == null) {
                    DebugPowerNotifierUtil.notifyError(project, "读取代理Jar失败");
                    return;
                }
                settingState.setAgentPath(DebugPowerFileUtils.getTmpLibFile(inputStream, "agent", ".jar"));
            }

            PsiClass psiClass = (PsiClass) psiMethod.getParent();
            String cacheKey = DebugPowerActionUtil.genCacheKey(psiClass, psiMethod);
            ParamCache cache = settingState.getCache(cacheKey);
            ClassDataContext classDataContext = DataContext.instance(project).getClassDataContext(psiClass.getQualifiedName());
            MethodDataContext methodDataContext = new MethodDataContext(classDataContext, DebugPowerIdeaClassUtil.getMethodQualifiedName(psiMethod), psiMethod, cache.content(), project);
            MainDialog dialog = new MainDialog(methodDataContext, project);
            PsiMethod finalPsiMethod = psiMethod;
            dialog.setOkAction((auth, text) -> {
                ParamCache paramCacheDto = new ParamCache(text);
                settingState.putCache(cacheKey, paramCacheDto);
                Gson gson = new Gson();
                Map<String, String> headers = null;
                if (StringUtil.isNotEmpty(auth)) {
                    headers = new HashMap<>();
                    headers.put("Authorization", auth);
                    settingState.putHeader("Authorization", auth);
                }
                Map<String, RunContentDTO> contentMap = gson.fromJson(text, new TypeToken<Map<String, RunContentDTO>>() {}.getType());
                String jsonDtoStr = getJsonDtoStr(psiClass.getQualifiedName(), finalPsiMethod.getName(), DebugPowerActionUtil.toParamTypeNameList(finalPsiMethod.getParameterList()), contentMap, headers);
                //Messages.showInfoMessage(jsonDtoStr, methodName);
                ServerDisplayValue attach = settingState.getAttach();
                if (attach == null || StringUtil.isEmpty(attach.getKey())) {
                    Messages.showErrorDialog("Run attach first", "执行失败");
                    return;
                }
                CompletableFuture.runAsync(() -> {
                    String agentParam;
                    if (jsonDtoStr.length() > 500) {
                        try {
                            String pathname = project.getBasePath() + "/.idea/DebugPower/agent.json";
                            File file = new File(pathname);
                            if (!file.exists()) {
                                if (!file.createNewFile()) {
                                    log.error("参数过长创建json文件失败");
                                }
                            }
                            FileUtil.writeToFile(file, jsonDtoStr);
                            agentParam = "file://" + URLEncoder.encode(pathname, StandardCharsets.UTF_8);
                        } catch (IOException ex) {
                            log.error("参数过长写入json文件失败", ex);
                            return;
                        }
                    } else {
                        agentParam = jsonDtoStr;
                    }
                    VirtualMachine virtualMachine = null;
                    try {
                        virtualMachine = VirtualMachine.attach(attach.getKey());
                        virtualMachine.loadAgent(settingState.getAgentPath(), agentParam);
                    } catch (Exception ex) {
                        log.error("agent attach失败", ex);
                    } finally {
                        if (virtualMachine != null) {
                            try {
                                virtualMachine.detach();
                            } catch (Exception ignored) {

                            }
                        }
                    }
                });
            });
            dialog.show();
            //Messages.showInfoMessage(className, methodName);
        } catch (Exception exception) {
            log.error("debug power invoke exception", exception);
            DebugPowerNotifierUtil.notifyError(project, "invoke exception " + exception.getMessage());
        }
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        // 当前项目
        Project project = e.getProject();
        // 当前编辑器
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        // 当前文件
        PsiFile file = e.getData(CommonDataKeys.PSI_FILE);
        // 获取光标所在方法
        PsiMethod method = PsiTreeUtil.getParentOfType(getElement(editor, file), PsiMethod.class);
        boolean enabled = project != null && editor != null && method != null;
        // 如果是启用状态，则将光标所在方法保存到数据上下文中
        if (enabled && e.getDataContext() instanceof UserDataHolder) {
            ((UserDataHolder) e.getDataContext()).putUserData(USER_DATE_ELEMENT_KEY, method);
        }
        // 启动禁用按钮
        e.getPresentation().setEnabledAndVisible(enabled);
    }

    @Nullable
    public static PsiElement getElement(Editor editor, PsiFile file) {
        if (editor == null || file == null) {
            return null;
        }
        // 获取光标模型 CaretModel 对象。
        CaretModel caretModel = editor.getCaretModel();
        // 获取光标当前的偏移量（即光标在文件中的位置）
        int position = caretModel.getOffset();
        // 根据光标的位置在文件中查找对应的 PsiElement 对象
        return file.findElementAt(position);
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        // EDT 调用线程。表示更新应该在事件调度线程（Event Dispatch Thread，也称为 UI 线程）中进行。这是默认值，适用于大多数情况下，特别是当更新涉及到 UI 元素时。
        // BGT 后台线程。表示更新应该在后台线程（Background Thread）中进行。适用于一些耗时操作，以避免阻塞 UI 线程。
        return ActionUpdateThread.BGT;
    }

    private static String getJsonDtoStr(String className, String methodName, List<String> paramTypeNameList, Map<String, RunContentDTO> contentMap, Map<String, String> headers) {
        RunDTO runDTO = new RunDTO();
        runDTO.setHeaders(headers);
        runDTO.setTargetClassName(className);
        runDTO.setTargetMethodName(methodName);
        runDTO.setTargetMethodParameterTypes(paramTypeNameList);
        runDTO.setTargetMethodContent(contentMap);
        return DebugPowerJsonUtils.getInstance().toJson(runDTO);
    }
}
