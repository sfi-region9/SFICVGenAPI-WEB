import com.google.gson.Gson;
import com.squareup.okhttp.*;
import fr.colin.cvgenweb.objects.Answers;
import fr.colin.cvgenweb.objects.CV;
import fr.colin.cvgenweb.objects.CVAnswer;

import java.io.IOException;
import java.util.Arrays;

public class TestUpload {

    public static void main(String... args) {
        MediaType type = MediaType.parse("application/json");
        CV cv = new CV(1, "Colin THOMAS", "SCC#79341", "USS Versailles R9", "Lieutenant Junior Grade", "Chief Science Officer", "Science", "France\nFrance\nFrance\n");
        OkHttpClient okHttpClient = new OkHttpClient();
        try {
            Response response = okHttpClient.newCall(new Request.Builder().url("https://cv.nwa2coco.fr/upload").post(RequestBody.create(type, new Gson().toJson(cv))).build()).execute();

            String s = response.body().string();
            Answers answer;
            if (s.contains("pdf")) {
                answer = new Gson().fromJson(s, CVAnswer.class);
            } else {
                answer = new Gson().fromJson(response.body().string(), Answers.class);
            }
            if (answer instanceof CVAnswer) {
                CVAnswer cvAnswer = (CVAnswer) answer;
                System.out.println("CVAnswer " + cvAnswer.getPdfDownload());
            }
            System.out.println(answer.getMessage());
            System.out.println(response.code());
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

}
