package net.phoneportal.idea.fuelphp.codeInsight.navigation;

import com.intellij.codeInsight.navigation.actions.GotoDeclarationHandler;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.jetbrains.php.lang.psi.elements.*;
import net.phoneportal.idea.fuelphp.FuelSettings;
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
        VirtualFile projectBaseDir = LocalFileSystem.getInstance().findFileByPath(psiProject.getBasePath());

        FuelSettings settings = FuelSettings.getInstance(psiProject);
        String fuelEnv = settings.getFuelEnv();
        String lang = settings.getMainLanguage();

        PsiElement variableContext = psiElement.getParent().getContext();
        if (!(variableContext instanceof ParameterList)) {
            return PsiElement.EMPTY_ARRAY;
        }

        ParameterList parameterList = (ParameterList) variableContext;
        if (!(parameterList.getContext() instanceof MethodReference)) {
            if (!(parameterList.getContext() instanceof FunctionReference)) {
                return PsiElement.EMPTY_ARRAY;
            }

            FunctionReference functionReference = (FunctionReference) parameterList.getContext();
            String methodName = functionReference.getName();
            PsiElement[] parameters = functionReference.getParameters();

            if (parent instanceof StringLiteralExpression) {
                if (parent.equals(parameters[0])) {
                    if (methodName.equals("__")) {
                        String[] langParams = psiElement.getText().split("\\.");
                        VirtualFile targetFile = projectBaseDir.findFileByRelativePath("fuel/app/lang")
                                .findFileByRelativePath(lang)
                                .findFileByRelativePath(langParams[0] + ".php");
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

        MethodReference methodReference = (MethodReference) parameterList.getContext();
        ClassReference classReference = (ClassReference) methodReference.getClassReference();
        String namespace = classReference.getNamespaceName();
        String className = classReference.getName();
        String methodName = methodReference.getName();
        PsiElement[] parameters = methodReference.getParameters();
        String fullClassName = namespace + className;

        if (parent instanceof StringLiteralExpression) {
            if (parent.equals(parameters[0])) {
                if (fullClassName.equals("\\View") && methodName.equals("forge")) {
                    VirtualFile targetFile = projectBaseDir.findFileByRelativePath("fuel/app/views").findFileByRelativePath(psiElement.getText() + ".php");
                    if (targetFile != null) {
                        PsiElement psiTargetFile = PsiManager.getInstance(psiProject).findFile(targetFile);
                        if (psiTargetFile != null) {
                            psiTargets.add(psiTargetFile);
                        }
                    }
                }

                if (fullClassName.equals("\\ViewModel") && methodName.equals("forge")) {
                    VirtualFile targetFile = projectBaseDir.findFileByRelativePath("fuel/app/classes/view")
                            .findFileByRelativePath(psiElement.getText() + ".php");
                    if (targetFile != null) {
                        PsiElement psiTargetFile = PsiManager.getInstance(psiProject).findFile(targetFile);
                        if (psiTargetFile != null) {
                            psiTargets.add(psiTargetFile);
                        }
                    }
                }

                if (fullClassName.equals("\\Config")) {
                    if (methodName.equals("get") || methodName.equals("load")) {
                        String text = psiElement.getText();
                        String[] configParams = text.split("\\.");
                        String configFileName = configParams[0] + ".php";
                        VirtualFile targetDirectory = projectBaseDir.findFileByRelativePath("fuel/app/config");
                        VirtualFile targetFile = targetDirectory.findFileByRelativePath(configFileName);
                        if (targetFile != null) {
                            PsiElement psiTargetFile = PsiManager.getInstance(psiProject).findFile(targetFile);
                            if (psiTargetFile != null) {
                                psiTargets.add(psiTargetFile);
                            }
                        }

                        if ( ! fuelEnv.isEmpty()) {
                            targetFile = targetDirectory
                                    .findFileByRelativePath(fuelEnv)
                                    .findFileByRelativePath(configFileName);
                            if (targetFile != null) {
                                PsiElement psiTargetFile = PsiManager.getInstance(psiProject).findFile(targetFile);
                                if (psiTargetFile != null) {
                                    psiTargets.add(psiTargetFile);
                                }
                            }
                        }
                    }
                }

                if (fullClassName.equals("\\Lang")) {
                    if (methodName.equals("get") || methodName.equals("load")) {
                        String[] langParams = psiElement.getText().split("\\.");
                        VirtualFile targetFile = projectBaseDir.findFileByRelativePath("fuel/app/lang")
                                .findFileByRelativePath(lang)
                                .findFileByRelativePath(langParams[0] + ".php");
                        if (targetFile != null) {
                            PsiElement psiTargetFile = PsiManager.getInstance(psiProject).findFile(targetFile);
                            if (psiTargetFile != null) {
                                psiTargets.add(psiTargetFile);
                            }
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
