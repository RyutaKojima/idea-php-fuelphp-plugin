package net.phoneportal.idea.fuelphp.lang;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import net.phoneportal.idea.fuelphp.FuelSettings;
import net.phoneportal.idea.fuelphp.util.FilesystemUtil;
import net.phoneportal.idea.fuelphp.util.PsiElementUtils;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;

public class LanguageReferences {

    public Collection<PsiElement> getPsiTargets(PsiElement psiElement) {
        Collection<PsiElement> psiTargets = new ArrayList<>();

        if (PsiElementUtils.isFunctionReference(psiElement, "__", 0) ||
                PsiElementUtils.isMethodReference(psiElement, "\\Lang", "load", 0) ||
                PsiElementUtils.isMethodReference(psiElement, "\\Lang", "get", 0)) {

            PsiElement psiTargetFile = getLangFile(psiElement);
            if (psiTargetFile != null) {
                psiTargets.add(psiTargetFile);
            }
        }

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
