package codeInsight.navigation;

import com.intellij.codeInsight.navigation.actions.GotoDeclarationHandler;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GotoHandler implements GotoDeclarationHandler {
    @Nullable
    @Override
    public PsiElement[] getGotoDeclarationTargets(PsiElement psiElement, int i, Editor editor) {
        Collection<PsiElement> psiTargets = new ArrayList<>();
        PsiElement parent = psiElement.getParent();
        Project psiProject = psiElement.getProject();
        VirtualFile projectFile = LocalFileSystem.getInstance().findFileByPath(psiProject.getBasePath());

        if(parent instanceof StringLiteralExpression) {
            String viewModelLiteral = parent.getParent().getParent().getText();

            Pattern patternView = Pattern.compile("View::forge\\s*\\(\\s*['\"]([^'\"]+)['\"]");
            Matcher matcher = patternView.matcher(viewModelLiteral);
            if(matcher.find()) {
                String matchText = matcher.group(1);
                if (matchText.equals(psiElement.getText())) {
                    VirtualFile targetFile = projectFile.findFileByRelativePath("fuel/app/views").findFileByRelativePath(psiElement.getText() + ".php");
                    if (targetFile != null) {
                        PsiElement psiTargetFile = PsiManager.getInstance(psiProject).findFile(targetFile);
                        if (psiTargetFile != null) {
                            psiTargets.add(psiTargetFile);
                        }
                    }
                }
            }

            Pattern patternViewModel = Pattern.compile("ViewModel::forge\\s*\\(\\s*['\"]([^'\"]+)['\"]");
            Matcher matcherViewModel = patternViewModel.matcher(viewModelLiteral);
            if(matcherViewModel.find()) {
                String matchText = matcherViewModel.group(1);
                if (matchText.equals(psiElement.getText())) {
                    VirtualFile targetFile = projectFile.findFileByRelativePath("fuel/app/classes/view").findFileByRelativePath(psiElement.getText() + ".php");
                    if (targetFile != null) {
                        PsiElement psiTargetFile = PsiManager.getInstance(psiProject).findFile(targetFile);
                        if (psiTargetFile != null) {
                            psiTargets.add(psiTargetFile);
                        }
                    }
                }
            }

        }

        return psiTargets.toArray(new PsiElement[psiTargets.size()]);
    }

    @Nullable
    @Override
    public String getActionText(DataContext dataContext) {
        return null;
    }
}
