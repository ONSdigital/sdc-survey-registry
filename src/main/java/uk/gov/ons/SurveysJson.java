package uk.gov.ons;

import com.google.gson.Gson;
import org.apache.commons.io.IOUtils;
import uk.gov.ons.json.Surveys;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;

public class SurveysJson {

    static String URL = "https://www.ons.gov.uk/surveys/informationforbusinesses/businesssurveys/staticlist/data?size=60";
    static Surveys surveys;

    static Surveys getJson() {

        if (surveys == null) {


            try {
                URL url = new URL(URL);
                URLConnection urlConnection = url.openConnection();
                // We need to add a valid user agent, or we get a 403 from the server
                urlConnection.addRequestProperty("User-Agent", "Mozilla/4.0");
                try (InputStream inputStream = urlConnection.getInputStream()) {
                    String json = IOUtils.toString(inputStream, "UTF-8");
                    surveys = new Gson().fromJson(json, Surveys.class);
                } catch (IOException e) {
                    System.out.println("Error getting json data from " + url + ". Will try again on the next request.");
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return surveys;
    }
}
