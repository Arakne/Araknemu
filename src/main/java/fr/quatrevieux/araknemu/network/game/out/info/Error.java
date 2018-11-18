package fr.quatrevieux.araknemu.network.game.out.info;

import fr.quatrevieux.araknemu.data.value.Interval;

/**
 * Error message
 */
final public class Error extends InformationMessage {
    public Error(Entry... entries) {
        super(Type.ERROR, entries);
    }

    public Error(int id) {
        this(new Entry(id));
    }

    public Error(int id, Object... arguments) {
        this(new Entry(id, arguments));
    }
    /**
     * Get the welcome message
     */
    static public Error welcome() {
        return new Error(89);
    }

    /**
     * Cannot do the action on this server
     */
    static public Error cantDoOnServer() {
        return new Error(226);
    }

    /**
     * Cannot learn the spell
     *
     * @param spellId The spell
     */
    static public Error cantLearnSpell(int spellId) {
        return new Error(7, spellId);
    }

    /**
     * Cannot cast the spell : not in the spell list
     */
    static public Error cantCastNotFound() {
        return new Error(169);
    }

    /**
     * Cannot cast the spell : not enough action points
     *
     * @param available The available action points
     * @param required The required action points for cast the spell
     */
    static public Error cantCastNotEnoughActionPoints(int available, int required) {
        return new Error(170, available, required);
    }

    /**
     * Cannot cast the spell : The target cell is invalid
     */
    static public Error cantCastInvalidCell() {
        return new Error(193);
    }

    /**
     * Cannot cast the spell : The target cell is not available
     */
    static public Error cantCastCellNotAvailable() {
        return new Error(172);
    }

    /**
     * Cannot cast the spell : The target cell is not in line
     */
    static public Error cantCastLineLaunch() {
        return new Error(173);
    }

    /**
     * Cannot cast the spell : The cast is in invalid state
     */
    static public Error cantCastBadState() {
        return new Error(116);
    }

    /**
     * Cannot cast the spell : the cell is out of range
     *
     * @param range The spell range
     * @param distance The cell distance
     */
    public static Error cantCastBadRange(Interval range, int distance) {
        return new Error(171, range.min(), range.max(), distance);
    }

    /**
     * Cannot cast the spell
     */
    static public Error cantCast() {
        return new Error(175);
    }

    /**
     * Cannot perform the action during fight
     */
    static public Error cantDoDuringFight() {
        return new Error(91);
    }
}
