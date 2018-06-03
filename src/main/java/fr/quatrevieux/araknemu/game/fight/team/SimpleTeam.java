package fr.quatrevieux.araknemu.game.fight.team;

import fr.quatrevieux.araknemu.data.constant.Alignment;
import fr.quatrevieux.araknemu.game.fight.JoinFightError;
import fr.quatrevieux.araknemu.game.fight.exception.JoinFightException;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Simple fight team for player fighters
 */
final public class SimpleTeam implements FightTeam {
    final private List<PlayerFighter> fighters;
    final private List<Integer> startPlaces;
    final private int number;

    public SimpleTeam(PlayerFighter leader, List<Integer> startPlaces, int number) {
        this.fighters = new ArrayList<>();
        this.fighters.add(leader);
        this.startPlaces = startPlaces;
        this.number = number;
    }

    @Override
    public int id() {
        return fighters.get(0).id();
    }

    @Override
    public int cell() {
        return fighters.get(0).player().position().cell();
    }

    @Override
    public int type() {
        return 0;
    }

    @Override
    public Alignment alignment() {
        return Alignment.NONE;
    }

    @Override
    public int number() {
        return number;
    }

    @Override
    public List<Integer> startPlaces() {
        return startPlaces;
    }

    @Override
    public Collection<? extends Fighter> fighters() {
        return fighters;
    }

    @Override
    public void send(Object packet) {
        fighters.forEach(fighter -> fighter.send(packet));
    }

    @Override
    public boolean alive() {
        return fighters.stream().anyMatch(fighter -> !fighter.dead());
    }

    @Override
    public void join(Fighter fighter) throws JoinFightException {
        if (!(fighter instanceof PlayerFighter)) {
            throw new JoinFightException(JoinFightError.TEAM_CLOSED);
        }

        PlayerFighter playerFighter = (PlayerFighter) fighter;

        if (fighters.size() >= startPlaces.size()) {
            throw new JoinFightException(JoinFightError.TEAM_FULL);
        }

        fighters.add(playerFighter);
    }
}
