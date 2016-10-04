package uk.gov.ons;

import com.google.gson.Gson;
import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import uk.gov.ons.json.Detail;
import uk.gov.ons.json.Summary;
import uk.gov.ons.json.Survey;
import uk.gov.ons.json.Surveys;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

@RestController
public class SurveysList {

    static String URL = "https://www.ons.gov.uk/surveys/informationforbusinesses/businesssurveys/staticlist/data?size=60";
    static Surveys surveys;

    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public static class ResourceNotFoundException extends RuntimeException {
    }

    @RequestMapping(path = "/", method = RequestMethod.GET)
    public
    @ResponseBody
    List<Summary> list() throws IOException {
        List<Summary> result = new ArrayList<>();
        Surveys surveys = getJson();
        if (surveys != null) {
            for (Survey survey : surveys.result.results) {
                result.add(new Summary(survey));
            }
        }
        return result;
    }

    // NB path valiable is not just {reference} because Spring truncates the URN format:
    // http://stackoverflow.com/questions/16332092/spring-mvc-pathvariable-with-dot-is-getting-truncated
    @RequestMapping(path = "/{reference:.+}", method = RequestMethod.GET)
    public
    @ResponseBody
    Detail detail(@PathVariable String reference) throws IOException {
        Surveys surveys = getJson();
        if (surveys != null) {
            for (Survey survey : surveys.result.results) {
                Summary summary = new Summary(survey);
                if (summary.reference.equalsIgnoreCase(reference)) {
                    return new Detail(survey);
                }
            }
        }

        // Not found;
        throw new ResourceNotFoundException();
    }

    static Surveys getJson() {

        if (surveys == null) {


            try {
                java.net.URL url = new URL(URL);
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