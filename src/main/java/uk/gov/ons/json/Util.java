package uk.gov.ons.json;

/**
 * A few useful functions shared by {@link Detail} and {@link Summary}.
 */
class Util {

    static String urnPrefix = "urn:uk.gov.ons.surveys:id:survey:";

    static String abbreviate(String name) {

        String stop_words = name;
        for (String stop : new String[]{"and", "of", "in", "the", "by"}) {
            stop_words = stop_words.replace(" " + stop + " ", " ");
        }

        String acronym = stop_words.replaceAll("\\B.|\\P{L}", "");

        return acronym.toLowerCase();
    }
}
