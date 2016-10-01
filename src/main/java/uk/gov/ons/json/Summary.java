package uk.gov.ons.json;

public class Summary {

    public String name;
    public String reference;
    public String urn;
    public String refLink;
    public String urnLink;

    public Summary() {
        // Default constructor
    }

    public  Summary(Survey survey) {
        name = survey.description.title;
        reference = name.replaceAll("\\B.|\\P{L}", "");
        urn = "urn:com.herokuapp.sdc-survey-registry-java:id:ru:" + reference;
        refLink = "https://sdc-survey-registry-java.herokuapp.com/" + reference;
        urnLink = "https://sdc-survey-registry.herokuapp.com/urn:com.herokuapp.sdc-survey-registry:id:ru:" + reference;
    }
}
