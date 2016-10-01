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

    @RequestMapping(path="/", method=RequestMethod.GET)
    public @ResponseBody List<Summary> list() throws IOException {
        Surveys surveys = getSurveys();
        List<Summary> result = new ArrayList<>();
        for (Survey survey : surveys.result.results) {
            result.add(new Summary(survey));
        }
        return result;
    }

    // NB path valiable is not just {reference} because Spring truncates the URNs
    // http://stackoverflow.com/questions/16332092/spring-mvc-pathvariable-with-dot-is-getting-truncated
    @RequestMapping(path="/{reference:.+}", method=RequestMethod.GET)
    public @ResponseBody Detail detail(@PathVariable String reference) throws IOException {
        Surveys surveys = getSurveys();
        for (Survey survey : surveys.result.results) {
            Summary summary = new Summary(survey);
            if (summary.reference.equalsIgnoreCase(reference) || summary.urn.equalsIgnoreCase(reference)) {
                return new Detail(survey);
            }
        }

        // Not found;
        throw new ResourceNotFoundException();
    }


    private Surveys getSurveys() throws IOException {
        String json = ResourceUtils.getString("/surveys.json");
        return new Gson().fromJson(ResourceUtils.getString("/surveys.json"), Surveys.class);
    }

}