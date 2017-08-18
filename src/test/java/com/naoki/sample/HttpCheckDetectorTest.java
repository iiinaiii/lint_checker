package com.naoki.sample;

import com.android.tools.lint.checks.infrastructure.LintDetectorTest;
import com.android.tools.lint.detector.api.Detector;
import com.android.tools.lint.detector.api.Issue;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

public class HttpCheckDetectorTest extends LintDetectorTest {
    private String PACKAGE = "package com.naoki.lint;\n";
    String SOURCE_PATH = "src/com/naoki/lint/";

//    @Override
//    protected Detector getDetector() {
//        return new HttpCheckDetector();
//    }
//
//    @Override
//    protected List<Issue> getIssues() {
//        return Collections.singletonList(HttpCheckDetector.ISSUE);
//    }


    @Override
    protected Detector getDetector() {
        return new PsiClassStructureDetector();
    }

    @Override
    protected List<Issue> getIssues() {
        return Collections.singletonList(PsiClassStructureDetector.ISSUE);
    }

    @Override
    protected boolean allowCompilationErrors() {
        return true;
    }

    @Test
    public void testWriteHttpDirect() throws Exception {
        String foo = "" +
                PACKAGE +
                "public class Foo {\n" +
                "public void test() {\n" +
                "// this is comment\n" +
                "// this is comment2. see http://sample.co.jp\n" +
                "String url = \"ttps://www.sample.co.jp/\";" +
                "}\n" +
                "}";
        String result = lintProject(java(SOURCE_PATH + "Foo.java", foo));
        Assert.assertThat(result, CoreMatchers.is("src/com/sample/lint/Foo.java:5: Error: Don't write http:// code direct!!! [WriteHttpDirect]\n" +
                "String url = \"https://www.sample.co.jp/\";" +
                "}\n" +
                "              ~~~~~~~~\n" +
                "1 errors, 0 warnings\n"));
    }
}