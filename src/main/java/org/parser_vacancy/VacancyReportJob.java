package org.parser_vacancy;

import org.hibernate.SessionFactory;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.Collectors;

public class VacancyReportJob implements Job {
    private final Main main = new Main(Main.BOT_TOKEN);
    private static SessionFactory sessionFactory;
    public static final int[] AREA_IDS = {113, 1, 2};
    private final List<String> EXPERIENCE_IDS = Arrays.asList("between1And3", "between3And6", "moreThan6");
    private final List<String> SCHEDULE_IDS = Arrays.asList("fullDay", "remote");

    private final Map<Integer, String> AREA_MAPPING = new HashMap<>() {{
        put(113, "🇷🇺 Россия");
        put(1, "🏙 Москва");
        put(2, "⚓️ СПБ");
    }};

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        AppLogger.logger.info("Executing VacancyReportJob.");
        try {
            StringBuilder messageText = new StringBuilder();
            for (int areaId : AREA_IDS) {
                messageText.append(formatVacancyInfo(areaId)).append("\n");
            }
            main.sendMessage(messageText.toString());
            AppLogger.logger.info("Vacancy report sent successfully.");
        } catch (Exception e) {
            AppLogger.logger.error("Error sending report: {}", e.getMessage(), e);
            main.sendMessage("Ошибка при отправке отчета: " + e.getMessage());
        }
    }

    private String formatVacancyInfo(int areaId) throws IOException, URISyntaxException {
        AppLogger.logger.info("Formatting vacancy info for areaId={}", areaId);
        String areaName = AREA_MAPPING.get(areaId);
        int totalVacancies = VacancyParser.getVacancyCount(areaId, null, null);

        // Используем List и определенный порядок для experienceCounts
        List<Integer> experienceCounts = new ArrayList<>();
        for (String expId : EXPERIENCE_IDS) {
            experienceCounts.add(VacancyParser.getVacancyCount(areaId, expId, null));
        }
        String formattedExperienceCounts = formatCounts(experienceCounts);

        // Используем List и определенный порядок для scheduleCounts
        List<Integer> scheduleCounts = new ArrayList<>();
        for (String schId : SCHEDULE_IDS) {
            scheduleCounts.add(VacancyParser.getVacancyCount(areaId, null, schId));
        }
        String formattedScheduleCounts = formatCounts(scheduleCounts);

        String formattedInfo = String.format("<b>%s</b>: %d %s %s", areaName, totalVacancies, formattedExperienceCounts, formattedScheduleCounts);
        AppLogger.logger.info("Formatted vacancy info: {}", formattedInfo);
        return formattedInfo;
    }

    private String formatCounts(List<Integer> counts) {
        return "[" + counts.stream().map(Object::toString).collect(Collectors.joining("-")) + "]";
    }
}