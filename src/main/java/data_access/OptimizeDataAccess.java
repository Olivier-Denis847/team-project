package data_access;

import use_case.optimize.OptimizeDataAccessInterface;

import okhttp3.*;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;
import org.json.JSONObject;

public class OptimizeDataAccess implements OptimizeDataAccessInterface {
    private static OptimizeDataAccess instance = null;

    private final String apiKey;
    private final String model;
    private final OkHttpClient client;
    private final String format;
    private OptimizeDataAccess() {
        apiKey = System.getenv("GEMINI_API_KEY");
        model = "gemini-2.5-flash";
        client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();
        format = "\nPlease give advice on how to optimize spending."
                + "Respond only with the advice.";
    }

    public static OptimizeDataAccess getInstance() {
        if (instance == null){
            instance = new OptimizeDataAccess();
        }
        return instance;
    }

    public String generateText(String expenses){
        String postData = getPrompt(expenses);

        RequestBody body = RequestBody.create(
                postData,
                MediaType.parse("application/json")
        );

        Request request = new Request.Builder()
                .url("https://generativelanguage.googleapis.com/v1/models/" + model +
                        ":generateContent?key=" + apiKey)
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            System.out.println(response.code());
            if (!response.isSuccessful()) {
                return "failed";
            }
            if (response.body() != null) {
                String raw = response.body().string();

                JSONObject obj = new JSONObject(raw);

                return obj.getJSONArray("candidates")
                        .getJSONObject(0)
                        .getJSONObject("content")
                        .getJSONArray("parts")
                        .getJSONObject(0)
                        .getString("text");
            }

        }
        catch (IOException e) {
            return "IOException";
        }
        return "Something went wrong";
    }

    private String getPrompt(String expenses) {
        String prompt = expenses + format;

        JSONObject textObj = new JSONObject();
        textObj.put("text", prompt);

        JSONArray partsArr = new JSONArray();
        partsArr.put(textObj);

        JSONObject contentObj = new JSONObject();
        contentObj.put("parts", partsArr);

        JSONArray contentsArr = new JSONArray();
        contentsArr.put(contentObj);

        JSONObject root = new JSONObject();
        root.put("contents", contentsArr);

        return root.toString();
    }

}
