package io.github.rmcdouga.fgumcpserver;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;

class ApplicationTests {

    // Instantiates your structural blueprint
    ApplicationModules modules = ApplicationModules.of(FguMcpServerApplication.class);

    @Test
    void verifyModularity() {
        // This will fail the test build if any module boundary rules are violated
        modules.verify();
    }

    @Test
    void writeDocumentation() {
        // Automatically generates PlantUML diagrams of your application architecture
        new Documenter(modules).writeModulesAsPlantUml();
    }
}
