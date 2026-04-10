package com.game.aichat;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class SearchService {

    public List<SearchResult> callSearchAPI(String query) throws IOException {
        if ("YOUR_APPBUILDER_API_KEY".equals(Constants.API_KEY)) {
            throw new RuntimeException("请先在代码中配置您的 AppBuilder API Key!");
        }

        String jsonInputString = String.format(
                "{\"messages\":[{\"role\":\"user\",\"content\":\"%s\"}],\"search_source\":\"baidu_search_v2\",\"resource_type_filter\":[{\"type\":\"web\",\"top_k\":5}]}",
                query.replace("\"", "\\\"")
        );

        URL url = new URL(Constants.API_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Authorization", "Bearer " + Constants.API_KEY);
        conn.setDoOutput(true);

        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        int responseCode = conn.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                throw new RuntimeException("API请求失败，状态码: " + responseCode + ", 错误信息: " + response.toString());
            }
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }

            JSONObject jsonResponse = new JSONObject(response.toString());
            List<SearchResult> results = new ArrayList<>();
            if (jsonResponse.has("references")) {
                JSONArray references = jsonResponse.getJSONArray("references");
                for (int i = 0; i < references.length(); i++) {
                    JSONObject ref = references.getJSONObject(i);
                    SearchResult result = new SearchResult(
                            ref.optString("title", "无标题"),
                            ref.optString("url", ""),
                            ref.optString("content", "无内容摘要"),
                            ref.optString("date", "未知日期")
                    );
                    results.add(result);
                }
            }
            return results;
        }
    }
}
