package Olivier;

import okhttp3.*;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;
import org.json.JSONObject;

public class OptimizeDataAccess {
    private static final String API_KEY = System.getenv("GEMINI_API_KEY");
    private static final String MODEL = "gemini-2.5-flash";
    private static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build();

    public static void main(String[] args) {
        String p = "Give two sentences of financial advice, respond only with the advice";

        OptimizeDataAccess test = new OptimizeDataAccess();
        System.out.println(test.generateText(p));
    }

    public String generateText(String prompt){

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

        String postData = root.toString();

        RequestBody body = RequestBody.create(
                postData,
                MediaType.parse("application/json")
        );

        Request request = new Request.Builder()
                .url("https://generativelanguage.googleapis.com/v1/models/" + MODEL +
                        ":generateContent?key=" + API_KEY)
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {

            if (!response.isSuccessful()) {
                System.out.println(response.code());
            }
            else{
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
            return "failed";
        }
        catch (IOException e) {
            e.printStackTrace();
            return "failed";
        }
    }

}
