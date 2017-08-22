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
    String SOURCE_PATH = "src/com/naoki/sample/";

    @Override
    protected Detector getDetector() {
        return new HttpCheckDetector();
    }

    @Override
    protected List<Issue> getIssues() {
        return Collections.singletonList(HttpCheckDetector.ISSUE);
    }


//    @Override
//    protected Detector getDetector() {
//        return new PsiClassStructureDetector();
//    }
//
//    @Override
//    protected List<Issue> getIssues() {
//        return Collections.singletonList(PsiClassStructureDetector.ISSUE);
//    }

    @Override
    protected boolean allowCompilationErrors() {
        return true;
    }

    @Test
    public void testFooClassDetect() throws Exception {
        String source = "package com.naoki.sample;\n" +
                "\n" +
                "/**\n" +
                " * java class for test\n" +
                " * <p>see http://www.foo.jp/sample.</p>\n" +
                " */\n" +
                "public class Foo {\n" +
                "\n" +
                "    private String fieldUrl = \"http://www.foo.jp/field\";\n" +
                "\n" +
                "    public Foo() {\n" +
                "\n" +
                "    }\n" +
                "\n" +
                "    // class comment http://sample.co.jp\n" +
                "\n" +
                "    /**\n" +
                "     * javadoc\n" +
                "     * <p>http://sample.co.jp</p>\n" +
                "     */\n" +
                "    public void startSampleRequest() {\n" +
                "        String localUrl = \"http://www.foo.jp/local\";\n" +
                "\n" +
                "        // method comment http://sample.co.jp\n" +
                "        request(fieldUrl);\n" +
                "\n" +
                "        request(\"http://www.foo.jp/direct\");\n" +
                "    }\n" +
                "\n" +
                "    private void request(String url) {\n" +
                "\n" +
                "    }\n" +
                "}";
        String result = lintProject(java(SOURCE_PATH + "Foo.java", source));
        Assert.assertThat(result,
                CoreMatchers.is(
                        "src/com/naoki/sample/Foo.java:9: Error: Don't write http:// code direct!!! [WriteHttpDirect]\n" +
                                "    private String fieldUrl = \"http://www.foo.jp/field\";\n" +
                                "                              ~~~~~~~~~~~~~~~~~~~~~~~~~\n" +
                                "src/com/naoki/sample/Foo.java:22: Error: Don't write http:// code direct!!! [WriteHttpDirect]\n" +
                                "        String localUrl = \"http://www.foo.jp/local\";\n" +
                                "                          ~~~~~~~~~~~~~~~~~~~~~~~~~\n" +
                                "src/com/naoki/sample/Foo.java:27: Error: Don't write http:// code direct!!! [WriteHttpDirect]\n" +
                                "        request(\"http://www.foo.jp/direct\");\n" +
                                "                ~~~~~~~~~~~~~~~~~~~~~~~~~~\n" +
                                "3 errors, 0 warnings\n"
                ));
    }

    @Test
    public void testFooNoWarningClassDetect() throws Exception {
        String source = "package com.naoki.sample;\n" +
                "\n" +
                "/**\n" +
                " * java class for test\n" +
                " * <p>see http://www.foo.jp/sample.</p>\n" +
                " */\n" +
                "public class FooNoWarning {\n" +
                "\n" +
                "    private String fieldUrl = \"No warning url\";\n" +
                "\n" +
                "    public FooNoWarning() {\n" +
                "\n" +
                "    }\n" +
                "\n" +
                "    // class comment http://sample.co.jp\n" +
                "\n" +
                "    /**\n" +
                "     * javadoc\n" +
                "     * <p>http://sample.co.jp</p>\n" +
                "     */\n" +
                "    public void startSampleRequest() {\n" +
                "        String localUrl = \"No warning url\";\n" +
                "\n" +
                "        // method comment http://sample.co.jp\n" +
                "        request(fieldUrl);\n" +
                "\n" +
                "        request(\"No warning url\");\n" +
                "    }\n" +
                "\n" +
                "    private void request(String url) {\n" +
                "\n" +
                "    }\n" +
                "}";
        String result = lintProject(java(SOURCE_PATH + "Foo.java", source));
        Assert.assertThat(result,
                CoreMatchers.is(
                        "No warnings."
                ));
    }

    @Test
    public void testSimpleClass() throws Exception {
        String source = "package com.naoki.sample;\n" +
                "\n" +
                "public class SimpleClass {\n" +
                "    \n" +
                "    int getNumber(int i){\n" +
                "        return 1;\n" +
                "    }\n" +
                "}";
        String result = lintProject(java(SOURCE_PATH + "SimpleClass.java", source));
    }
}