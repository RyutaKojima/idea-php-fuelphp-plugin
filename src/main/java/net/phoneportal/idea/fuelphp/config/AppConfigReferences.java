package net.phoneportal.idea.fuelphp.config;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import net.phoneportal.idea.fuelphp.FuelSettings;
import net.phoneportal.idea.fuelphp.util.FilesystemUtil;
import net.phoneportal.idea.fuelphp.util.PsiElementUtils;

import java.util.ArrayList;
import java.util.Collection;

public class AppConfigReferences {

    private boolean match(PsiElement psiElement) {
        if (!(psiElement.getParent() instanceof StringLiteralExpression)) {
            return false;
        }

        if (PsiElementUtils.isMethodReference(psiElement, "\\Config", "get", 0)) {
            return true;
        }

        if (PsiElementUtils.isMethodReference(psiElement, "\\Config", "load", 0)) {
            return true;
        }

        return false;
    }

    public Collection<PsiElement> getPsiTargets(PsiElement psiElement) {
        Collection<PsiElement> psiTargets = new ArrayList<>();

        if (!match(psiElement)) {
            return psiTargets;
        }

        Project project = psiElement.getProject();
        VirtualFile projectBaseDir = FilesystemUtil.getProjectBaseDir(project);
        if (projectBaseDir == null) {
            return psiTargets;
        }

        VirtualFile targetDirectory = projectBaseDir.findFileByRelativePath("fuel/app/config");
        if (targetDirectory == null) {
            return psiTargets;
        }

        String text = psiElement.getText();
        String[] configParams = text.split("\\.");
        String configFileName = configParams[0] + ".php";

        VirtualFile targetFile = targetDirectory.findFileByRelativePath(configFileName);
        if (targetFile != null) {
            PsiElement psiTargetFile = PsiManager.getInstance(project).findFile(targetFile);
            if (psiTargetFile != null) {
                psiTargets.add(psiTargetFile);
            }
        }

        if (!getFuelEnv(psiElement).isEmpty()) {
            targetFile = targetDirectory.findFileByRelativePath(getFuelEnv(psiElement) + "/" + configFileName);
            if (targetFile != null) {
                PsiElement psiTargetFile = PsiManager.getInstance(project).findFile(targetFile);
                if (psiTargetFile != null) {
                    psiTargets.add(psiTargetFile);
                }
            }
        }

        return psiTargets;
    }

    public String getFuelEnv(PsiElement psiElement) {
        return FuelSettings.getInstance(psiElement.getProject()).getFuelEnv();
    }
}
