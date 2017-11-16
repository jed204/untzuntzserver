package com.untzuntz.ustackserver.util;

import com.untzuntz.ustackserverapi.APIDocumentation;
import org.junit.Test;

public class DocumentationTest {

    @Test
    public void testAPIDocumentation() {

        APIDocumentation.setLogoUrl("testurl");
        APIDocumentation.setSystemName("testname");
        APIDocumentation.setVersion("1.0");
        APIDocumentation docs = new APIDocumentation();

    }

}


