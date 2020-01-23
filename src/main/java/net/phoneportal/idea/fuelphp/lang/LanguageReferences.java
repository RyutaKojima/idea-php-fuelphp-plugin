package net.phoneportal.idea.fuelphp.lang;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.psi.util.PsiTreeUtil;
import com.jetbrains.php.lang.psi.elements.ArrayCreationExpression;
import com.jetbrains.php.lang.psi.elements.PhpPsiElement;
import com.jetbrains.php.lang.psi.elements.PhpReturn;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import net.phoneportal.idea.fuelphp.FuelSettings;
import net.phoneportal.idea.fuelphp.util.FilesystemUtil;
import net.phoneportal.idea.fuelphp.util.PsiElementUtils;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.ArrayDeque;

public class LanguageReferences {

    private boolean match(PsiElement psiElement) {
        if (!(psiElement.getParent() instanceof StringLiteralExpression)) {
            return false;
        }

        if (PsiElementUtils.isFunctionReference(psiElement, "__", 0)) {
            return true;
        }

        if (PsiElementUtils.isMethodReference(psiElement, "\\Lang", "load", 0)) {
            return true;
        }

        if (PsiElementUtils.isMethodReference(psiElement, "\\Lang", "get", 0)) {
            return true;
        }

        return false;
    }

    public Collection<PsiElement> getPsiTargets(PsiElement psiElement) {
        Collection<PsiElement> psiTargets = new ArrayList<>();

        if (!match(psiElement)) {
            return psiTargets;
        }

        String text = psiElement.getText();
        String[] configParams = text.split("\\.");
        ArrayDeque<String> paramQue = new ArrayDeque<String>();
        for (String s : configParams) {
            paramQue.add(s);
        }
        String langFileName = paramQue.poll() + ".php";

        PsiElement psiLangFile = getLangFile(psiElement);

        if (psiLangFile != null) {
            PhpReturn phpReturn = PsiTreeUtil.findChildOfType(psiLangFile, PhpReturn.class);
            if (phpReturn.getFirstPsiChild() instanceof ArrayCreationExpression) {
                PhpPsiElement found = PsiElementUtils.findArrayRecursive((ArrayCreationExpression)phpReturn.getFirstPsiChild(), paramQue);
                if (found instanceof PhpPsiElement) {
                    psiTargets.add(found);
                    return psiTargets;
                }
            }
        }

        psiTargets.add(psiLangFile);
        return psiTargets;
    }

    @Nullable
    private PsiElement getLangFile(PsiElement psiElement) {

        Project project = psiElement.getProject();
        VirtualFile projectBaseDir = FilesystemUtil.getProjectBaseDir(project);
        if (projectBaseDir == null) {
            return null;
        }

        String[] langParams = psiElement.getText().split("\\.");
        VirtualFile targetFile = projectBaseDir.findFileByRelativePath("fuel/app/lang")
                .findFileByRelativePath(getMainLanguage(project))
                .findFileByRelativePath(langParams[0] + ".php");

        if (targetFile != null) {
            PsiElement psiTargetFile = PsiManager.getInstance(project).findFile(targetFile);
            return psiTargetFile;
        }

        return null;
    }

    private String getMainLanguage(Project project) {
        FuelSettings settings = FuelSettings.getInstance(project);
        return settings.getMainLanguage();
    }
}
