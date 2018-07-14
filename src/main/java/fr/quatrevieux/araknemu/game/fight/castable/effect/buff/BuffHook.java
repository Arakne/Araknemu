package fr.quatrevieux.araknemu.game.fight.castable.effect.buff;

import fr.quatrevieux.araknemu.game.fight.castable.CastScope;

/**
 * Hook action for apply buff effects
 */
public interface BuffHook {
    /**
     * Apply effect when fighter turn is started
     *
     * @return False the the fighter cannot start the turn
     */
    default boolean onStartTurn(Buff buff) {
        return true;
    }

    /**
     * Apply effect on turn ending
     */
    default public void onEndTurn(Buff buff) {}

    /**
     * Start the buff
     */
    default public void onBuffStarted(Buff buff) {}

    /**
     * The buff is terminated (buff expired, debuff...)
     */
    default public void onBuffTerminated(Buff buff) {}

    /**
     * The fighter is a target of a cast
     */
    default public void onCastTarget(Buff buff, CastScope cast) {}
}
