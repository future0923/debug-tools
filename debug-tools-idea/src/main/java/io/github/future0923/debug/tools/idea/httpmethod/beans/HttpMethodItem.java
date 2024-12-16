package io.github.future0923.debug.tools.idea.httpmethod.beans;

import com.intellij.navigation.ItemPresentation;
import com.intellij.navigation.NavigationItem;
import com.intellij.pom.Navigatable;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import io.github.future0923.debug.tools.idea.httpmethod.enums.HttpMethod;
import lombok.Getter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class HttpMethodItem implements NavigationItem {

    private final PsiElement psiElement;

    @Getter
    private PsiMethod psiMethod;

    @Getter
    private HttpMethod method;

    @Getter
    private final String url;

    private Navigatable navigationElement;

    public HttpMethodItem(PsiElement psiElement, HttpMethod method, String urlPath) {
        this.psiElement = psiElement;
        if (psiElement instanceof PsiMethod) {
            this.psiMethod = (PsiMethod) psiElement;
        }
        if (method != null) {
            this.method = method;
        }
        this.url = urlPath;
        if (psiElement instanceof Navigatable) {
            navigationElement = (Navigatable) psiElement;
        }
    }

    @Nullable
    @Override
    public String getName() {
        return this.url;
    }

    @Nullable
    @Override
    @Contract(" -> new")
    public ItemPresentation getPresentation() {
        return new ItemPresentation() {

            @Nullable
            @Override
            public String getPresentableText() {
                return getUrl();
            }

            @Override
            public String getLocationString() {
                String location = null;

                if (psiMethod != null) {
                    PsiClass psiClass = psiMethod.getContainingClass();
                    if (psiClass != null) {
                        location = psiClass.getName();
                    }
                    location += "#" + psiMethod.getName();
                    location = "Java: (" + location + ")";
                }

                if (psiElement != null) {
                    location += " in " + psiElement.getResolveScope().getDisplayName();
                }
                return location;
            }

            @NotNull
            @Override
            public Icon getIcon(boolean unused) {
                return HttpMethod.getIcon(method);
            }
        };
    }

    @Override
    public void navigate(boolean requestFocus) {
        if (navigationElement != null) {
            navigationElement.navigate(requestFocus);
        }
    }

    @Override
    public boolean canNavigate() {
        return navigationElement.canNavigate();
    }

    @Override
    public boolean canNavigateToSource() {
        return true;
    }
}
