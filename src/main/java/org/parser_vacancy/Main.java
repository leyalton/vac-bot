package org.parser_vacancy;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

public class Main implements LongPollingSingleThreadUpdateConsumer {
    private final TelegramClient telegramClient;
    public static final String BOT_TOKEN = "token";
    public static final String CHANNEL_ID = "id";

    public Main(String botToken) {
        telegramClient = new OkHttpTelegramClient(botToken);
        AppLogger.logger.info("----- СОЗДАН НОВЫЙ ИНСТАНС БОТА -----");
    }

    public static void main(String[] args) {
        try (TelegramBotsLongPollingApplication botsApplication = new TelegramBotsLongPollingApplication()) {
            botsApplication.registerBot(BOT_TOKEN, new Main(BOT_TOKEN));
            AppLogger.logger.info("\n " +
                            "########     ###    ########   ######  ######## ########    \n " +
                            "##     ##   ## ##   ##     ## ##    ## ##       ##     ##   \n " +
                            "##     ##  ##   ##  ##     ## ##       ##       ##     ##   \n " +
                            "########  ##     ## ########   ######  ######   ########    \n " +
                            "##        ######### ##   ##         ## ##       ##   ##     \n " +
                            "##        ##     ## ##    ##  ##    ## ##       ##    ##    \n " +
                            "##        ##     ## ##     ##  ######  ######## ##     ##   \n "
            );
            initSchedulers();
            Thread.currentThread().join();
        } catch (Exception e) {
            AppLogger.logger.error("Error registering bot: {}", e.getMessage());
        }
    }

    private static void initSchedulers() throws SchedulerException {
        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
        scheduler.start();
        AppLogger.logger.info("Schedulers started.");

        // Vacancy report job
        JobDetail vacancyReportJob = JobBuilder.newJob(VacancyReportJob.class)
                .withIdentity("vacancyReportJob", "group1")
                .build();
        Trigger vacancyReportTrigger = TriggerBuilder.newTrigger()
                .withIdentity("vacancyReportTrigger", "group1")
                .withSchedule(CronScheduleBuilder.cronSchedule("0 0 */6 * * ?"))
                .build();
        scheduler.scheduleJob(vacancyReportJob, vacancyReportTrigger);
        AppLogger.logger.info("Vacancy report job scheduled.");

        // Log sending job
        JobDetail logSendingJob = JobBuilder.newJob(LogSendingJob.class)
                .withIdentity("logSendingJob", "group2")
                .build();
        Trigger logSendingTrigger = TriggerBuilder.newTrigger()
                .withIdentity("logSendingTrigger", "group2")
                .withSchedule(CronScheduleBuilder.cronSchedule("0 0 15 * * ?"))
                .build();
        scheduler.scheduleJob(logSendingJob, logSendingTrigger);
        AppLogger.logger.info("Log sending job scheduled.");
    }

    @Override
    public void consume(Update update) {
        AppLogger.logger.info("New update received: {}", update.toString());
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            AppLogger.logger.info("Received message: {}", messageText);
            if (messageText.equals("/posting_now")) {
                try {
                    AppLogger.logger.info("Executing VacancyReportJob on demand.");
                    new VacancyReportJob().execute(null);
                } catch (JobExecutionException e) {
                    AppLogger.logger.error("Error executing job: {}", e.getMessage(), e);
                }
            }
        }
    }

    public void sendMessage(String text) {
        AppLogger.logger.info("Sending message: {}", text);
        SendMessage sendMessage = new SendMessage(CHANNEL_ID, text);
        sendMessage.setParseMode("HTML");
        try {
            telegramClient.execute(sendMessage);
            AppLogger.logger.info("Message sent successfully.");
        } catch (TelegramApiException e) {
            AppLogger.logger.error("Error sending message: {}", e.getMessage(), e);
        }
    }
}


//TODO сохранять все значения в бд
//TODO подключить эту бд в докер
//TODO выводить еженедельный и ежемесячный график по количеству вакансий