package org.automation.poc;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.restassured.RestAssured;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.FileAppender;

public class Hook{

    private static final Logger log = LoggerFactory.getLogger(Hook.class);

    @Before
    public void setUp(Scenario scenario) {
        // If LOG_FILE is not set (IDE runs), create a unique run-level log file and set LOG_DIR/LOG_FILE
        try {
            String existingLogFile = System.getProperty("LOG_FILE");
            if (existingLogFile == null || existingLogFile.trim().isEmpty()) {
                String projectRoot = System.getProperty("user.dir");
                File logsFolder = new File(projectRoot, "logs");
                if (!logsFolder.exists()) {
                    boolean created = logsFolder.mkdirs();
                    log.info("Created project-root logs directory: {} -> {}", logsFolder.getAbsolutePath(), created);
                }

                String logsDir = logsFolder.getAbsolutePath();
                System.setProperty("LOG_DIR", logsDir);

                String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
                String logFile = logsDir + File.separator + "test." + timestamp + ".log";
                System.setProperty("LOG_FILE", logFile);
                log.info("Set LOG_DIR={} and LOG_FILE={} (IDE run)", logsDir, logFile);

                // Try to update any existing FILE appender to point to our LOG_FILE
                try {
                    LoggerContext ctx = (LoggerContext) LoggerFactory.getILoggerFactory();
                    ch.qos.logback.classic.Logger root = ctx.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
                    Appender app = root.getAppender("FILE");
                    if (app != null && app instanceof FileAppender) {
                        @SuppressWarnings("unchecked")
                        FileAppender fileAppender = (FileAppender) app;
                        fileAppender.stop();
                        fileAppender.setFile(logFile);
                        fileAppender.start();
                        log.info("Updated FILE appender to use log file: {}", logFile);
                    } else {
                        log.debug("No FILE appender found to update (it may be configured to use ${LOG_FILE} at startup). LOG_FILE={}", logFile);
                    }
                } catch (Exception e) {
                    log.warn("Could not programmatically update logback FILE appender: {}", e.getMessage(), e);
                }
            } else {
                log.info("Using provided LOG_FILE for run: {}", existingLogFile);
            }
        } catch (Throwable t) {
            log.error("Error while ensuring run-level log file: {}", t.getMessage(), t);
        }

        // Reset RestAssured configuration for each scenario
        RestAssured.reset();
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @After
    public void tearDown(Scenario scenario) {
        // Clean up if needed
        RestAssured.reset();
    }
}
