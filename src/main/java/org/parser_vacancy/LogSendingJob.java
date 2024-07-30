package org.parser_vacancy;

import org.quartz.Job;
import org.quartz.JobExecutionContext;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogSendingJob implements Job {
    private final Main main = new Main(Main.BOT_TOKEN);

    @Override
    public void execute(JobExecutionContext context) {
        AppLogger.logger.info("Executing LogSendingJob.");
        try {
            File logFile = new File("parser.log");
            if (logFile.exists()) {
                if (logFile.length() > 0) {
                    sendLogFile(logFile);
                } else {
                    AppLogger.logger.info("Log file is empty. Nothing to send.");
                }
            } else {
                AppLogger.logger.warn("Log file not found.");
            }
        } catch (Exception e) {
            AppLogger.logger.error("Error sending log file: {}", e.getMessage(), e);
        }
    }

    private void sendLogFile(File logFile) {
        AppLogger.logger.info("Sending log file: {}", logFile.getAbsolutePath());
        try (BufferedReader reader = new BufferedReader(new FileReader(logFile))) {
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String formattedDate = dateFormat.format(new Date());
            String message = String.format("Лог файл за %s:\n\n%s", formattedDate, content.toString());

            main.sendMessage(message);
            AppLogger.logger.info("Log file sent successfully.");
        } catch (IOException e) {
            AppLogger.logger.error("Error reading log file: {}", e.getMessage(), e);
        }
    }
}