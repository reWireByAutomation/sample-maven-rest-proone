# RestMavenProPrompt

Purpose
-------
A reusable prompt template to instruct an assistant (or a developer) how to create, configure, and validate a small RestAssured + Cucumber + TestNG Maven POC project. Use this document as the canonical instructions for future projects so the same conventions, checks, and artifacts are produced consistently.

How to use this prompt template
--------------------------------
- Save this file under `src/test/resources/referencedocs/` in any new repository.
- When you need a new POC or to replicate this setup, paste the contents into your request to the assistant (or reference this file) so the assistant follows the same structure and verification steps.
- The template expects the project to be a Maven-based Java project and that the assistant can edit files under the repository root.

High-level goals (one-liner for the assistant)
-----------------------------------------------
Create a small, runnable RestAssured + Cucumber (Gherkin) + TestNG Maven project with modern dependencies, clear step definitions for all feature steps, a TestNG+Cucumber runner, SLF4J logging to a single unique run-level file under the project root `logs/`, and robust verification (mvn test passes and reports/logs are produced).

Prompt checklist (what to perform)
----------------------------------
- [ ] Ensure `pom.xml` contains modern, test-scoped dependencies for RestAssured, Cucumber (java/testng/picocontainer/gherkin), TestNG, SLF4J, and Logback.
- [ ] Add/validate `src/test/resources/logback.xml` that writes to `${LOG_FILE}` (fallback to `${LOG_DIR}` / `${user.dir}/logs`).
- [ ] Provide a `TestRunner` class that extends `AbstractTestNGCucumberTests` and uses TestNG DataProvider.
- [ ] Create `Hook.java` (Cucumber hooks) that:
  - resets RestAssured per scenario,
  - for IDE runs (when `LOG_FILE` not provided) creates project-root `logs/`, sets `LOG_DIR` and `LOG_FILE` to a timestamped file, and updates the Logback FILE appender programmatically if needed.
- [ ] Create `RestApiSteps.java` with step definitions covering all steps in the feature(s): Background Given (base URI), When (GET/POST/PUT/DELETE with docstring bodies), Then (status code, content-type, body contains), And (JSON field with type coercion). Use TestNG asserts for final checks.
- [ ] Feature files under `src/test/resources/features/` should define Background and scenarios (use small public API e.g. jsonplaceholder for sample).
- [ ] Update `pom.xml` to generate `run.properties` at validate phase placing it under project root and set Surefire `LOG_FILE=${project.basedir}/logs/test.${run.timestamp}.log` so Maven runs have unique log files.
- [ ] Provide a README.md describing how to run (Maven & IDE), logging behavior, and common command lines (include examples like `mvn clean compile test "-Dsurefire.suiteXmlFiles=src/test/testsuites/testng.xml"` and `mvn clean compile test -Dcucumber.filter.tags=@smoke`).
- [ ] Run `mvn validate` and `mvn -DskipTests=false test` to verify everything passes locally.

Assumptions (state when using prompt)
--------------------------------------
- Project is a Maven Java project (Java 11+).
- The assistant can read and write files under the repository root.
- Network access is allowed for hitting public sample APIs during POC runs.
- Tests run only in the test scope; no production code changes required.

Edge cases & checks
-------------------
- Duplicate step definitions: search the codebase for duplicate Cucumber patterns and remove/merge duplicates.
- Logback duplicate config: ensure only test `logback.xml` is used during tests; exclude `src/main/resources/logback.xml` if present.
- Type mismatches on JSON fields: implement expected-value coercion to Integer/Double/Boolean before assertion.
- Ensure feature steps map 1:1 to stepdef patterns; prefer explicit Cucumber expressions.

Quality gates (must pass)
--------------------------
1) Build: `mvn -DskipTests=false test` must compile and run tests. (PASS/FAIL)
2) Logging: After running, `./logs/test.<timestamp>.log` must exist and contain step-level logs. (PASS/FAIL)
3) Reports: `target/cucumber-reports/cucumber.html` should be present. (PASS/FAIL)
4) No DuplicateStepDefinitionException or missing-step errors in Cucumber runtime. (PASS/FAIL)

Verification commands (recommended)
------------------------------------
- Generate timestamp and prepare run properties:
  - mvn validate
- Run tests (default):
  - mvn -DskipTests=false test
- Run specific TestNG suite file:
  - mvn clean compile test "-Dsurefire.suiteXmlFiles=src/test/testsuites/testng.xml"
- Run specific cucumber tag:
  - mvn clean compile test -Dcucumber.filter.tags=@smoke
- Run a single TestNG runner class via Maven:
  - mvn -DskipTests=false -Dtest=org.automation.poc.TestRunner test

What to include in the generated repository (deliverables)
--------------------------------------------------------
- `pom.xml` with dependencies and build-time steps.
- `src/test/resources/logback.xml` (test-only logback config).
- `src/test/resources/features/*.feature` sample features.
- `src/test/java/.../stepdefinitions/RestApiSteps.java` (full stepdefs).
- `src/test/java/.../TestRunner.java` (TestNG+Cucumber runner).
- `src/test/java/.../Hook.java` (hooks and IDE fallback logging logic).
- `README.md` with run instructions, commands, and notes.
- `src/test/resources/referencedocs/RestMavenProPrompt.md` (this file) for future reuse.

Developer contract for the assistant
------------------------------------
When you receive this template and a target repo, follow these steps exactly:
1. Inspect repo: list `pom.xml`, `src/test` and `src/main` for existing artifacts.
2. If `pom.xml` missing required dependencies, add them in test scope.
3. Create or update `logback.xml` under test resources and ensure test logback is used.
4. Implement `TestRunner`, `Hook`, `RestApiSteps`, and features if missing.
5. Run `mvn validate` then `mvn -DskipTests=false test` and fix errors up to three iterations.
6. Report status and attach the run-level log sample and failing output if any failures remain.

Minimal template for messages to include back to user after running
------------------------------------------------------------------
- Files changed/created list.
- The results of the Quality gates (PASS/FAIL) with concise evidence (mvn output snippets / target files present).
- If failures: the failing test names, stack traces (trimmed), and suggested fixes.
- Next recommended steps (CI integration, report automation, cleanup policy).

Good practices and extras (optional but recommended)
---------------------------------------------------
- Keep logback & SLF4J test-only by scoping in `pom.xml` and excluding main logback resources.
- Use Cucumber `@smoke` tag for quick smoke runs and include tag filtering in README.
- Keep step definitions short and composable; use helper methods for HTTP request/response handling.
- Add JSON schema validation for important endpoints.
- Add a simple `scripts/` folder with helper scripts for common tasks (open report, archive logs).

Template metadata
-----------------
- Template name: `RestMavenProPrompt.md`
- Location (recommended): `src/test/resources/referencedocs/RestMavenProPrompt.md`
- Created by: automation assistant
- Date: 2026-01-25

---

Usage example
-------------
When you need the assistant to create a new POC repo, paste the content of this file in your request and say: "Use RestMavenProPrompt.md to scaffold and validate a RestAssured+Cucumber+TestNG Maven POC in this repository." The assistant should follow the checklist and quality gates above.

---

Happy reusing this template!
