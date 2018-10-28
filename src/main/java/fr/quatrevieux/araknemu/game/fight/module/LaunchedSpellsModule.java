package fr.quatrevieux.araknemu.game.fight.module;

import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.castable.spell.LaunchedSpells;
import fr.quatrevieux.araknemu.game.fight.fighter.event.FighterInitialized;
import fr.quatrevieux.araknemu.game.fight.turn.action.event.SpellCasted;
import fr.quatrevieux.araknemu.game.fight.turn.event.TurnTerminated;

/**
 * Fight module for handle launch spell validation (cooldown, launch per turn, per target)
 */
final public class LaunchedSpellsModule implements FightModule {
    /**
     * @param fight For compatibility with {@link fr.quatrevieux.araknemu.game.fight.module.FightModule.Factory}
     */
    public LaunchedSpellsModule(Fight fight) {}

    @Override
    public Listener[] listeners() {
        return new Listener[] {
            new Listener<FighterInitialized> () {
                @Override
                public void on(FighterInitialized event) {
                    event.fighter().attach(new LaunchedSpells());
                }

                @Override
                public Class<FighterInitialized> event() {
                    return FighterInitialized.class;
                }
            },
            new Listener<SpellCasted> () {
                @Override
                public void on(SpellCasted event) {
                    event.caster().attachment(LaunchedSpells.class).push(
                        event.spell(),
                        event.target()
                    );
                }

                @Override
                public Class<SpellCasted> event() {
                    return SpellCasted.class;
                }
            },
            new Listener<TurnTerminated> () {
                @Override
                public void on(TurnTerminated event) {
                    event.turn().fighter().attachment(LaunchedSpells.class).refresh();
                }

                @Override
                public Class<TurnTerminated> event() {
                    return TurnTerminated.class;
                }
            },
        };
    }
}
