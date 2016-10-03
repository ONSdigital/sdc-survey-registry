package uk.gov.ons;

import com.github.davidcarboni.ResourceUtils;
import com.google.gson.Gson;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import uk.gov.ons.json.Detail;
import uk.gov.ons.json.Summary;
import uk.gov.ons.json.Survey;
import uk.gov.ons.json.Surveys;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
public class SurveysList {

    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public static class ResourceNotFoundException extends RuntimeException {
    }

    @RequestMapping(path = "/", method = RequestMethod.GET)
    public
    @ResponseBody
    List<Summary> list() throws IOException {
        List<Summary> result = new ArrayList<>();
        Surveys surveys = SurveysJson.getJson();
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
        Surveys surveys = SurveysJson.getJson();
        if (surveys != null) {
            for (Survey survey : surveys.result.results) {
                Summary summary = new Summary(survey);
                if (summary.reference.equalsIgnoreCase(reference) || summary.urn.equalsIgnoreCase(reference)) {
                    return new Detail(survey);
                }
            }
        }

        // Not found;
        throw new ResourceNotFoundException();
    }

}