package org.parser_vacancy;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;

public class VacancyParser {
    private static final String QUERY = "Java разработчик not script";

    public static int getVacancyCount(int areaId, String experience, String schedule) {
        AppLogger.logger.info("Getting vacancy count for areaId={}, experience={}, schedule={}", areaId, experience, schedule);
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet request;
        try {
            URIBuilder builder = new URIBuilder("https://api.hh.ru/vacancies");
            builder.addParameter("text", QUERY);
            builder.addParameter("area", String.valueOf(areaId));
            builder.addParameter("per_page", "0");
            if (experience != null) {
                builder.addParameter("experience", experience);
            }
            if (schedule != null) {
                builder.addParameter("schedule", schedule);
            }
            request = new HttpGet(builder.build());
        } catch (URISyntaxException e) {
            AppLogger.logger.warn("Ошибка построения URI: {}", e.getMessage());
            return -1;
        }


        try (httpClient) {
            HttpResponse response = httpClient.execute(request);
            int statusCode = response.getStatusLine().getStatusCode();

            if (statusCode == HttpStatus.SC_OK) {
                HttpEntity entity = response.getEntity();
                String responseBody = EntityUtils.toString(entity);

                try {
                    JSONObject json = new JSONObject(responseBody);
                    int count = json.getInt("found");
                    AppLogger.logger.info("Vacancy count received: {}", count);
                    return count;
                } catch (JSONException e) {
                    AppLogger.logger.warn("Ошибка парсинга JSON: {}", e.getMessage());
                    return -1;
                }
            } else {
                AppLogger.logger.warn("Ошибка запроса к API HH.ru: {}", statusCode);
                return -1;
            }
        } catch (IOException e) {
            AppLogger.logger.warn("Ошибка выполнения запроса: {}", e.getMessage());
            return -1;
        }
    }
}