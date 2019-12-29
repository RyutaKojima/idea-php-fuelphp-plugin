package net.phoneportal.idea.fuelphp.util;

import com.intellij.psi.PsiElement;
import com.jetbrains.php.lang.psi.elements.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PsiElementUtils {

    @Nullable
    public static String trimQuote(@Nullable String text) {

        if(text == null) return null;

        return text.replaceAll("^\"|\"$|\'|\'$", "");
    }

    public static boolean isFunctionReference(@NotNull PsiElement psiElement, @NotNull String functionName, int parameterIndex) {

        PsiElement parent = psiElement.getParent();
        if (!(parent instanceof StringLiteralExpression)) {
            return false;
        }

        if (!(parent.getContext() instanceof ParameterList)) {
            return false;
        }
        ParameterList parameterList = (ParameterList) parent.getContext();

        if (!(parameterList.getContext() instanceof FunctionReference)) {
            return false;
        }
        FunctionReference functionReference = (FunctionReference) parameterList.getContext();

        PsiElement[] parameters = functionReference.getParameters();
        if (!parent.equals(parameters[parameterIndex])) {
            return false;
        }

        return functionName.equals(functionReference.getName());
    }

    public static boolean isMethodReference(@NotNull PsiElement psiElement, @NotNull String fullClassName, @NotNull String methodName, int parameterIndex) {

        PsiElement parent = psiElement.getParent();
        PsiElement variableContext = parent.getContext();
        if (!(variableContext instanceof ParameterList)) {
            return false;
        }

        ParameterList parameterList = (ParameterList) variableContext;

        if (!(parameterList.getContext() instanceof MethodReference)) {
            return false;
        }
        MethodReference methodReference = (MethodReference) parameterList.getContext();
        if (!(methodReference.getClassReference() instanceof ClassReference)) {
            return false;
        }
        ClassReference classReference = (ClassReference) methodReference.getClassReference();

        String namespace = classReference.getNamespaceName();
        String className = classReference.getName();
        PsiElement[] parameters = methodReference.getParameters();

        if (!parent.equals(parameters[parameterIndex])) {
            return false;
        }

        return fullClassName.equals(namespace + className) && methodName.equals(methodReference.getName());
    }
}
