package fr.quatrevieux.araknemu.data.constant;

/**
 * Character sex
 */
public enum Sex {
    MALE,
    FEMALE;

    /**
     * Get Sex from string value
     * @param value String to parse
     */
    static public Sex parse(String value) {
        if (value.equals("0")) {
            return MALE;
        }

        return FEMALE;
    }
}
