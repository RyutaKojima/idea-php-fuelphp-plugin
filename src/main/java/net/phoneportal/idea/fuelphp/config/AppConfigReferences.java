package net.phoneportal.idea.fuelphp.config;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.psi.util.PsiTreeUtil;
import com.jetbrains.php.lang.psi.elements.*;
import net.phoneportal.idea.fuelphp.FuelSettings;
import net.phoneportal.idea.fuelphp.util.FilesystemUtil;
import net.phoneportal.idea.fuelphp.util.PsiElementUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.ArrayDeque;

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
        ArrayDeque<String> paramQue = new ArrayDeque<String>();
        for (String s : configParams) {
            paramQue.add(s);
        }

        String configFileName = paramQue.poll() + ".php";
        VirtualFile targetFile;
        PsiElement psiConfigFileForEnvironment = null;
        PsiElement psiConfigFileForRoute = null;
        PhpPsiElement foundNearlyLine = null;

        // find config file
        if (!getFuelEnv(psiElement).isEmpty()) {
            targetFile = targetDirectory.findFileByRelativePath(getFuelEnv(psiElement) + "/" + configFileName);
            if (targetFile != null) {
                psiConfigFileForEnvironment = PsiManager.getInstance(project).findFile(targetFile);
            }
        }

        targetFile = targetDirectory.findFileByRelativePath(configFileName);
        if (targetFile != null) {
            psiConfigFileForRoute = PsiManager.getInstance(project).findFile(targetFile);
        }

        // config for specific environment
        if (psiConfigFileForEnvironment != null) {
            PhpReturn phpReturn = PsiTreeUtil.findChildOfType(psiConfigFileForEnvironment, PhpReturn.class);
            if (phpReturn.getFirstPsiChild() instanceof ArrayCreationExpression) {
                PhpPsiElement found = PsiElementUtils.findArrayRecursive((ArrayCreationExpression)phpReturn.getFirstPsiChild(), paramQue);
                if (found instanceof PhpPsiElement) {
                    String foundKeyStr = found.getText().replaceFirst("^[\"']", "").replaceFirst("[\"']$", "");
                    if (paramQue.getLast().equals(foundKeyStr)) {
                        psiTargets.add(found);
                        return psiTargets;
                    } else {
                        foundNearlyLine = found;
                        // continue search fallback
                    }
                }
            }
        }

        // fallback to config for route
        if (psiConfigFileForRoute != null) {
            PhpReturn phpReturn = PsiTreeUtil.findChildOfType(psiConfigFileForRoute, PhpReturn.class);
            if (phpReturn.getFirstPsiChild() instanceof ArrayCreationExpression) {
                PhpPsiElement found = PsiElementUtils.findArrayRecursive((ArrayCreationExpression)phpReturn.getFirstPsiChild(), paramQue);
                if (found instanceof PhpPsiElement) {
                    String foundKeyStr = found.getText().replaceFirst("^[\"']", "").replaceFirst("[\"']$", "");
                    if (paramQue.getLast().equals(foundKeyStr)) {
                        psiTargets.add(found);
                        return psiTargets;
                    } else {
                        if (foundNearlyLine == null) {
                            foundNearlyLine = found;
                        }

                        psiTargets.add(foundNearlyLine);
                        return psiTargets;
                    }
                }
            }
        }

        if (psiTargets.isEmpty()) {
            if (psiConfigFileForEnvironment != null) {
                psiTargets.add(psiConfigFileForEnvironment);
                return psiTargets;
            }

            if (psiConfigFileForRoute != null) {
                psiTargets.add(psiConfigFileForRoute);
                return psiTargets;
            }
        }

        return psiTargets;
    }

    public String getFuelEnv(PsiElement psiElement) {
        return FuelSettings.getInstance(psiElement.getProject()).getFuelEnv();
    }
}
