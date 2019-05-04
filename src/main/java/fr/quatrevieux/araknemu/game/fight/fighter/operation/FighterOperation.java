package fr.quatrevieux.araknemu.game.fight.fighter.operation;

import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.monster.MonsterFighter;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;

/**
 * Operation to apply on a fighter creature
 * Implements the visitor pattern
 *
 * @see Fighter#apply(FighterOperation)
 */
public interface FighterOperation {
    /**
     * Apply the operation to a PlayerFighter
     */
    default public void onPlayer(PlayerFighter fighter) {
        onGenericFighter(fighter);
    }

    /**
     * Apply the operation to a MonsterFighter
     */
    default public void onMonster(MonsterFighter fighter) {
        onGenericFighter(fighter);
    }

    /**
     * Apply the operation to a generic fighter type
     */
    default public void onGenericFighter(Fighter fighter) {}
}
