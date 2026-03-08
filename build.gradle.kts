
plugins {
    id("com.gtnewhorizons.gtnhconvention")
}

// This has to be here, because otherwise 2.0.7 GTNH Gradle script pulls GTNHLib version
// incompatible with dependencies from 2.8.4.
configurations.all {
    resolutionStrategy {
        force("com.github.GTNewHorizons:GTNHLib:0.7.10")
    }
}
