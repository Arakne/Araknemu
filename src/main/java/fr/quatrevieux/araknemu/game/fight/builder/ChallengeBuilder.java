package fr.quatrevieux.araknemu.game.fight.builder;

import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightService;
import fr.quatrevieux.araknemu.game.fight.fighter.PlayerFighter;
import fr.quatrevieux.araknemu.game.fight.map.FightMap;
import fr.quatrevieux.araknemu.game.fight.team.FightTeam;
import fr.quatrevieux.araknemu.game.fight.team.SimpleTeam;
import fr.quatrevieux.araknemu.game.fight.type.ChallengeType;
import fr.quatrevieux.araknemu.game.player.GamePlayer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Builder for challenge fight
 */
final public class ChallengeBuilder implements FightBuilder {
    final private FightService service;

    final private List<PlayerFighter> challengers = new ArrayList<>();
    private FightMap map;

    public ChallengeBuilder(FightService service) {
        this.service = service;
    }

    @Override
    public Fight build() {
        return new Fight(
            new ChallengeType(),
            map,
            buildTeams()
        );
    }

    /**
     * Set the fight map
     */
    public ChallengeBuilder map(ExplorationMap map) {
        this.map = service.map(map);

        return this;
    }

    /**
     * Add new fighter
     */
    public ChallengeBuilder fighter(GamePlayer player) {
        challengers.add(new PlayerFighter(player));

        return this;
    }

    private List<FightTeam> buildTeams() {
        List<FightTeam> teams = new ArrayList<>(challengers.size());

        Collections.shuffle(challengers);

        for (int number = 0; number < challengers.size(); ++number) {
            teams.add(
                new SimpleTeam(
                    challengers.get(number),
                    map.startPlaces(number),
                    number
                )
            );
        }

        return teams;
    }
}
