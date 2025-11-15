package Olivier;

import okhttp3.*;
import java.io.IOException;
import org.json.JSONArray;
import org.json.JSONObject;

public class OptimizeDataAccess {
    private static final String API_KEY = System.getenv("GEMINI_API_KEY");
    private static final String MODEL = "gemini-2.5-flash";
    private static final OkHttpClient client = new OkHttpClient();

    public static void main(String[] args) {
        String p = "Could you give me a cookie recipe";

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
                .post(body)
                .build();

        System.out.println("KEY RAW: [" + API_KEY + "]");
        System.out.println("POST BODY: " + postData);
        System.out.println("URL: " + request.url());

        try (Response response = client.newCall(request).execute()) {

            if (!response.isSuccessful()) {
                System.out.println(response.code());
            }
            else{
                if (response.body() != null) {
                    return response.body().string();
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
