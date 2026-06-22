package com.library.library_management;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@Disabled("Disabled in Jenkins because DB is not available during pipeline execution")
@SpringBootTest
class LibraryManagementApplicationTests {

    @Test
    void contextLoads() {
    }
}
