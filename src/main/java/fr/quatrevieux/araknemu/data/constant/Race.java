package fr.quatrevieux.araknemu.data.constant;

/**
 * List of available character races
 */
public enum Race {
    NO_CLASS,
    FECA,
    OSAMODAS,
    ENUTROF,
    SRAM,
    XELOR,
    ECAFLIP,
    ENIRIPSA,
    IOP,
    CRA,
    SADIDA,
    SACRIEUR,
    PANDAWA;

    /**
     * Get a character race by its race
     * @param i The race race
     */
    static public Race byId(int i) {
        if (i >= values().length || i == 0) {
            throw new IndexOutOfBoundsException();
        }

        return values()[i];
    }
}
