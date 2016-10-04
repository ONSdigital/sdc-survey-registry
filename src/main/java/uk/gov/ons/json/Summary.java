package uk.gov.ons.json;

public class Summary {

    public String name;
    public String reference;
    public String urn;
    public String link;

    public Summary() {
        // Default constructor
    }

    public  Summary(Survey survey) {
        name = survey.description.title;
        reference = Util.abbreviate(name);
        urn = Util.urnPrefix + reference;
        link = "/" + reference;
    }
}
