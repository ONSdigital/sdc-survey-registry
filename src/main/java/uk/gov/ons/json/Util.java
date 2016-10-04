package uk.gov.ons.json;

/**
 * A few useful functions shared by {@link Detail} and {@link Summary}.
 */
class Util {

    static String urnPrefix = "urn:uk.gov.ons.surveys:id:survey:";

    /**
     * Makes a sensible abbreviation for the given survey name.
     * @param name The string to be abbreviated.
     * @return The abbreviation.
     */
    static String abbreviate(String name) {

        String stop_words = name;
        for (String stop : new String[]{"and", "of", "in", "the", "by"}) {
            stop_words = stop_words.replace(" " + stop + " ", " ");
        }

        String acronym = stop_words.replaceAll("\\B.|\\P{L}", "");

        return acronym.toLowerCase();
    }

    /**
     * Makes a best-guess as to whether this is a Monthly, Quarterly or Annual survey.
     * @param name The survey name.
     * @return One of "monthly", "quarterly" or "annual".
     */
    static String frequency(String name) {

        if (name.toLowerCase().contains("monthly")) return "monthly";
        if (name.toLowerCase().contains("quarterly")) return "quarterly";
        if (name.toLowerCase().contains("annual")) return "annual";
        // Fall back to a default guess - 1 in 3 ain't bad for now..
        return "quarterly";
    }
}
