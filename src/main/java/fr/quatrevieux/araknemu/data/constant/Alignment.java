package fr.quatrevieux.araknemu.data.constant;

/**
 * List of available alignments
 */
public enum Alignment {
    NONE,
    NEUTRAL,
    BONTARIAN,
    BRAKMARIAN,
    MERCENARY;

    /**
     * Get the alignment id
     */
    public int id() {
        return ordinal() - 1;
    }

    /**
     * Get an alignment by its id
     */
    static public Alignment byId(int id) {
        return values()[id + 1];
    }
}
