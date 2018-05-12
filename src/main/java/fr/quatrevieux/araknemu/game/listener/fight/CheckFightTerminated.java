package fr.quatrevieux.araknemu.game.listener.fight;

import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.fighter.event.FighterDie;
import fr.quatrevieux.araknemu.game.fight.state.ActiveState;
import fr.quatrevieux.araknemu.game.fight.team.FightTeam;

/**
 * Check if the fight is terminated when a fighter die
 */
final public class CheckFightTerminated implements Listener<FighterDie> {
    final private Fight fight;

    public CheckFightTerminated(Fight fight) {
        this.fight = fight;
    }

    @Override
    public void on(FighterDie event) {
        if (fight.teams().stream().filter(FightTeam::alive).count() >= 2) {
            return;
        }

        fight.state(ActiveState.class).terminate();
    }

    @Override
    public Class<FighterDie> event() {
        return FighterDie.class;
    }
}
