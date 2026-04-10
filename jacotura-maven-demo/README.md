# Jacotura Maven Demo

This demo shows how to run the `jacotura-maven-plugin` after JaCoCo generates its XML report.

## Run locally

First publish the plugin to your local Maven repository from the project root:

```bash
bash ./gradlew :jacotura-maven-plugin:publishToMavenLocal
```

Then run the demo:

```bash
cd jacotura-maven-demo
mvn verify
```

After the build:

- JaCoCo XML report: `target/site/jacoco/jacoco.xml`
- Cobertura XML report: `target/site/cobertura/coverage.xml`
