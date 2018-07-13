package fr.quatrevieux.araknemu.game.fight.castable.effect.buff;

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
}
