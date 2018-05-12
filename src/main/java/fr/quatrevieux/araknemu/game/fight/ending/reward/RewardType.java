package fr.quatrevieux.araknemu.game.fight.ending.reward;

/**
 * Type of reward
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Game.as#L1364
 */
public enum RewardType {
    LOOSER(0),
    WINNER(2),
    COLLECTOR(5),
    FIGHT_DROP(6); // Contains only kamas and items

    final private int id;

    RewardType(int id) {
        this.id = id;
    }

    public int id() {
        return id;
    }
}
