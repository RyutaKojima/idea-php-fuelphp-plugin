package net.phoneportal.idea.fuelphp.views;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import net.phoneportal.idea.fuelphp.util.FilesystemUtil;
import net.phoneportal.idea.fuelphp.util.PsiElementUtils;

import java.util.ArrayList;
import java.util.Collection;

public class ViewsReferences {

    public Collection<PsiElement> getPsiTargets(PsiElement psiElement) {
        Collection<PsiElement> psiTargets = new ArrayList<>();

        Project project = psiElement.getProject();
        VirtualFile projectBaseDir = FilesystemUtil.getProjectBaseDir(project);
        if (projectBaseDir == null) {
            return psiTargets;
        }

        if (PsiElementUtils.isMethodReference(psiElement, "\\View", "forge", 0)) {
            VirtualFile targetFile = projectBaseDir.findFileByRelativePath("fuel/app/views").findFileByRelativePath(psiElement.getText() + ".php");
            if (targetFile != null) {
                PsiElement psiTargetFile = PsiManager.getInstance(project).findFile(targetFile);
                if (psiTargetFile != null) {
                    psiTargets.add(psiTargetFile);
                }
            }
        }

        return psiTargets;
    }
}
