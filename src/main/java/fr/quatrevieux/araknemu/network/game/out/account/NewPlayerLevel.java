package fr.quatrevieux.araknemu.network.game.out.account;

/**
 * Player has reach a new level
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Account.as#L1101
 */
final public class NewPlayerLevel {
    final private int level;

    public NewPlayerLevel(int level) {
        this.level = level;
    }

    @Override
    public String toString() {
        return "AN" + level;
    }
}
