package fr.quatrevieux.araknemu.network.game.out.fight.exploration;

/**
 * Fights count on current map
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Fights.as#L43
 */
final public class FightsCount {
    final private int count;

    public FightsCount(int count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "fC" + count;
    }
}
