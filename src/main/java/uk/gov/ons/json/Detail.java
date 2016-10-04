package uk.gov.ons.json;

public class Detail {

    public String name;
    public String reference;
    public String urn;
    public String frequency;

    public Detail() {
        // Default constructor
    }

    public Detail(Survey survey) {
        name = survey.description.title;
        reference = Util.abbreviate(name);
        urn = Util.urnPrefix + reference;
        frequency = Util.frequency(survey.description.title);
    }

}
