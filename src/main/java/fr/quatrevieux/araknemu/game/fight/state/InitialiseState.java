package fr.quatrevieux.araknemu.game.fight.state;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.team.FightTeam;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Initialisation of the fight
 */
final public class InitialiseState implements FightState {
    final private boolean randomize;

    public InitialiseState(boolean randomize) {
        this.randomize = randomize;
    }

    public InitialiseState() {
        this(true);
    }

    @Override
    public void start(Fight fight) {
        for (FightTeam team : fight.teams()) {
            List<Integer> cells = new ArrayList<>(team.startPlaces());

            if (randomize) {
                Collections.shuffle(cells);
            }

            int index = 0;

            for (Fighter fighter : team.fighters()) {
                fighter.move(fight.map().get(cells.get(index++)));
                fighter.join(team);
                fighter.setFight(fight);
            }
        }

        fight.nextState();
    }

    @Override
    public int id() {
        return 1;
    }
}
