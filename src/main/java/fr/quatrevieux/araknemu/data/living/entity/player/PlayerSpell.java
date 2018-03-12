package fr.quatrevieux.araknemu.data.living.entity.player;

/**
 * Save player spell data
 */
final public class PlayerSpell {
    final static public int DEFAULT_POSITION = 63;

    final private int playerId;
    final private int spellId;
    final private boolean classSpell;
    private int level;
    private int position;

    public PlayerSpell(int playerId, int spellId, boolean classSpell, int level, int position) {
        this.playerId = playerId;
        this.spellId = spellId;
        this.classSpell = classSpell;
        this.level = level;
        this.position = position;
    }

    public PlayerSpell(int playerId, int spellId, boolean classSpell) {
        this(playerId, spellId, classSpell, 1, DEFAULT_POSITION);
    }

    public int playerId() {
        return playerId;
    }

    public int spellId() {
        return spellId;
    }

    public boolean classSpell() {
        return classSpell;
    }

    public int level() {
        return level;
    }

    public int position() {
        return position;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
