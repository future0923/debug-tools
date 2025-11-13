package io.github.future0923.debug.tools.idea.action;

import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.project.Project;
import git4idea.GitBranch;
import git4idea.GitLocalBranch;
import git4idea.GitReference;
import git4idea.GitTag;
import git4idea.actions.branch.GitBranchActionsDataKeys;
import git4idea.actions.branch.GitBranchActionsUtil;
import git4idea.branch.GitBrancher;
import git4idea.commands.Git;
import git4idea.commands.GitCommand;
import git4idea.commands.GitLineHandler;
import git4idea.config.GitSharedSettings;
import git4idea.i18n.GitBundle;
import git4idea.repo.GitRepository;
import git4idea.ui.branch.GitBranchPopupActions;
import io.github.future0923.debug.tools.base.hutool.core.util.BooleanUtil;
import io.github.future0923.debug.tools.idea.utils.DebugToolsNotifierUtil;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author future0923
 */
public class GitMergeAction extends DumbAwareAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) {
            return;
        }
        List<GitRepository> repositories = GitBranchActionsUtil.getAffectedRepositories(e);
        if (repositories.isEmpty()) {
            return;
        }
        GitLocalBranch currentBranch = repositories.get(0).getCurrentBranch();
        if (currentBranch == null) {
            return;
        }
        GitReference ref = getRef(e, repositories);
        if (ref == null) {
            return;
        }
        ApplicationManager.getApplication().executeOnPooledThread(() -> byGit(project, repositories.get(0), currentBranch, ref));
        //byGitBrancher(project, ref, repositories, currentBranch);
    }

    private void byGit(Project project, GitRepository repository, GitReference currentBranch, GitReference refBranch) {
        Git git = Git.getInstance();
        GitLineHandler checkout = new GitLineHandler(project, repository.getRoot(), GitCommand.CHECKOUT);
        checkout.addParameters(refBranch.getName());
        git.runCommand(checkout);

        GitLineHandler merge = new GitLineHandler(project, repository.getRoot(), GitCommand.MERGE);
        merge.addParameters(currentBranch.getName());
        git.runCommand(merge);

        GitLineHandler checkoutBack = new GitLineHandler(project, repository.getRoot(), GitCommand.CHECKOUT);
        checkoutBack.addParameters(currentBranch.getName());
        git.runCommand(checkoutBack);

        DebugToolsNotifierUtil.notifyInfo(project, GitBundle.message("branches.merge.into",
                currentBranch.getName(),
                refBranch.getName()
        ));
    }

    private void byGitBrancher(Project project, GitReference ref, List<GitRepository> repositories, GitLocalBranch currentBranch) {
        GitBrancher brancher = GitBrancher.getInstance(project);
        brancher.checkout(getName(ref), false, repositories, null);
        brancher.merge(currentBranch, deleteOnMerge(currentBranch, project), repositories);
        brancher.checkout(getName(currentBranch), false, repositories, null);
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) {
            return;
        }
        List<GitRepository> repositories = GitBranchActionsUtil.getAffectedRepositories(e);
        if (repositories.isEmpty()) {
            return;
        }
        GitLocalBranch currentBranch = repositories.get(0).getCurrentBranch();
        if (currentBranch == null) {
            return;
        }
        GitReference ref = getRef(e, repositories);
        if (ref == null) {
            return;
        }
        if (ref.getName().equals(currentBranch.getName())) {
            e.getPresentation().setEnabledAndVisible(false);
            return;
        }
        e.getPresentation().setText(GitBundle.message("branches.merge.into",
                GitBranchPopupActions.getCurrentBranchTruncatedPresentation(project, repositories),
                GitBranchPopupActions.getSelectedBranchTruncatedPresentation(project, ref.getName())
        ));
        e.getPresentation().setDescription(GitBundle.message("branches.merge.into",
                GitBranchPopupActions.getCurrentBranchFullPresentation(project, repositories),
                GitBranchPopupActions.getSelectedBranchFullPresentation(ref.getName())));
        GitBranchPopupActions.addTooltipText(e.getPresentation(), GitBundle.message("branches.merge.into",
                GitBranchPopupActions.getSelectedBranchFullPresentation(ref.getName()),
                GitBranchPopupActions.getCurrentBranchFullPresentation(project,
                        repositories)));
    }

    private String getName(GitReference ref) {
        if (ref instanceof GitBranch) {
            return ref.getName();
        } else {
            return ref.getFullName();
        }
    }

    private static GitBrancher.DeleteOnMergeOption deleteOnMerge(GitReference reference, Project project) {
        if (reference instanceof GitBranch
                && !((GitBranch) reference).isRemote()
                && !GitSharedSettings.getInstance(project).isBranchProtected(reference.getName())) {
            return GitBrancher.DeleteOnMergeOption.PROPOSE;
        } else {
            return GitBrancher.DeleteOnMergeOption.NOTHING;
        }
    }

    public GitReference getRef(AnActionEvent e, List<GitRepository> repositories) {
        List<GitBranch> branches = e.getData(GitBranchActionsDataKeys.BRANCHES);
        if (branches != null && branches.size() == 1) {
            return branches.get(0);
        }
        List<GitTag> tags = e.getData(GitBranchActionsDataKeys.TAGS);
        if (tags != null && tags.size() == 1) {
            return tags.get(0);
        }
        if (BooleanUtil.isTrue(e.getData(GitBranchActionsDataKeys.USE_CURRENT_BRANCH))) {
            if (repositories != null && repositories.size() == 1) {
                return repositories.get(0).getCurrentBranch();
            }
        }
        return null;
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }
}
