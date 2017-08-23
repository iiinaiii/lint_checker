package com.naoki.sample;

import com.android.annotations.NonNull;
import com.android.tools.lint.detector.api.*;
import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLiteralExpression;

import java.util.Collections;
import java.util.List;

public class HttpCheckDetector extends Detector implements Detector.JavaPsiScanner {

    static Issue ISSUE =
            Issue.create("WriteHttpDirect",
                    "Write http url direct in code",
                    "Don't write http/https url direct in code.",
                    Category.CORRECTNESS,
                    7,
                    Severity.ERROR,
                    new Implementation(HttpCheckDetector.class,
                            Scope.JAVA_FILE_SCOPE));

    @Override
    public JavaElementVisitor createPsiVisitor(JavaContext context) {
        return new JavaCustomVisitor(context);
    }

    @Override
    public List<Class<? extends PsiElement>> getApplicablePsiTypes() {
        return Collections.singletonList(
                PsiLiteralExpression.class);
    }

    private static class JavaCustomVisitor extends JavaElementVisitor {
        private HttpDirectChecker mChecker;

        JavaCustomVisitor(JavaContext context) {
            mChecker = new HttpDirectChecker(context);
        }

        @Override
        public void visitLiteralExpression(PsiLiteralExpression expression) {
            mChecker.check(expression);
        }
    }

    private static class HttpDirectChecker {
        private JavaContext mContext;

        HttpDirectChecker(JavaContext context) {
            mContext = context;
        }

        void check(PsiElement element) {
            String source = element.getText();
            if (source.contains("http://") || source.contains("https://")) {
                int startOffset = element.getTextRange().getStartOffset();
                int endOffset = element.getTextRange().getEndOffset();
                Location location = createLocation(mContext.getJavaFile().getText(), startOffset, endOffset);
                mContext.report(ISSUE,
                        element,
                        location,
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
