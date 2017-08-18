package com.naoki.sample;

import com.android.annotations.NonNull;
import com.android.tools.lint.detector.api.*;
import com.intellij.psi.*;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

public class HttpCheckDetector extends Detector implements Detector.JavaPsiScanner {

    public static Issue ISSUE =
            Issue.create("WriteHttpDirect",
                    "Write http url direct in code",
                    "Don't write http/https url direct in code.",
                    Category.CORRECTNESS,
                    7,
                    Severity.ERROR,
                    new Implementation(HttpCheckDetector.class,
                            EnumSet.of(Scope.JAVA_FILE),
                            Scope.JAVA_FILE_SCOPE));

    @Override
    public JavaElementVisitor createPsiVisitor(JavaContext context) {
        return new JavaCustomVisitor(context);
    }

    @Override
    public List<Class<? extends PsiElement>> getApplicablePsiTypes() {
//        return Arrays.asList(
//                PsiJavaFile.class);
        return Arrays.asList(
                PsiMethod.class);
    }

    private static class JavaCustomVisitor extends JavaElementVisitor {
        private JavaContext mContext;
        private HttpDirectChecker mChecker;

        JavaCustomVisitor(JavaContext context) {
            mContext = context;
            mChecker = new HttpDirectChecker(context);
        }

        @Override
        public void visitJavaFile(PsiJavaFile file) {
            mChecker.check(file);

        }

        @Override
        public void visitComment(PsiComment comment) {
            super.visitComment(comment);
        }

        @Override
        public void visitMethod(PsiMethod method) {
            super.visitMethod(method);
        }
    }

    private static class HttpDirectChecker {
        private JavaContext mContext;

        HttpDirectChecker(JavaContext context) {
            mContext = context;
        }

        void check(PsiJavaFile file) {
            String source = file.getText();
            if (source.contains("http://") || source.contains("https://")) {
                int startOffset = source.indexOf("http");
                int endOffset = source.contains("https://") ? source.indexOf("http") + 8 : source.indexOf("http") + 7;
                Location location = createLocation(source, startOffset, endOffset);
                mContext.report(ISSUE, location,
                        "Don't write http:// code direct!!!");
            }
        }

        private Location createLocation(String contents, int startOffset, int endOffset) {
            DefaultPosition startPosition = new DefaultPosition(
                    getLineNumber(contents, startOffset), getColumnNumber(contents, startOffset), startOffset);

            DefaultPosition endPosition = new DefaultPosition(
                    getLineNumber(contents, endOffset), getColumnNumber(contents, endOffset), endOffset);

            return Location.create(mContext.file, startPosition, endPosition);
        }

        private int getLineNumber(@NonNull String contents, int offset) {
            // this li1ne number is 0 base.
            String preContents = contents.substring(0, offset);
            String remContents = preContents.replaceAll("\n", "");
            return preContents.length() - remContents.length();
        }

        private int getColumnNumber(@NonNull String contents, int offset) {
            // this column number is 0 base.
            String preContents = contents.substring(0, offset);
            String[] preLines = preContents.split("\n");
            int lastIndex = preLines.length - 1;
            return preContents.endsWith("\n") ? 0 : preLines[lastIndex].length();
        }

    }
}
