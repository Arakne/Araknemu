package fr.quatrevieux.araknemu.game.fight.builder;

import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightService;
import fr.quatrevieux.araknemu.game.fight.map.FightMap;
import fr.quatrevieux.araknemu.game.fight.state.*;
import fr.quatrevieux.araknemu.game.fight.team.FightTeam;
import fr.quatrevieux.araknemu.game.fight.type.FightType;
import fr.quatrevieux.araknemu.game.fight.type.PvmType;
import fr.quatrevieux.araknemu.util.RandomUtil;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Base builder for fight
 */
final public class BaseBuilder implements FightBuilder {
    public interface TeamFactory {
        /**
         * Creates the fight team
         *
         * @param number The team number
         * @param startPlaces The available start places
         */
        public FightTeam create(int number, List<Integer> startPlaces);
    }

    final private FightService service;
    final private RandomUtil random;
    final private FightType type;
    final private Logger logger;

    private FightMap map;
    final private List<TeamFactory> teamFactories = new ArrayList<>();

    public BaseBuilder(FightService service, RandomUtil random, FightType type, Logger logger) {
        this.service = service;
        this.random = random;
        this.type = type;
        this.logger = logger;
    }

    @Override
    public Fight build(int fightId) {
        return new Fight(fightId, type, map, buildTeams(), statesFlow(), logger);
    }

    /**
     * Set the fight map
     */
    public void map(ExplorationMap map) {
        this.map = service.map(map);
    }

    public void addTeam(TeamFactory factory) {
        teamFactories.add(factory);
    }

    private List<FightTeam> buildTeams() {
        List<TeamFactory> factories = random.shuffle(teamFactories);
        List<FightTeam> teams = new ArrayList<>(factories.size());

        for (int number = 0; number < factories.size(); ++number) {
            teams.add(factories.get(number).create(number, map.startPlaces(number)));
        }

        return teams;
    }

    private StatesFlow statesFlow() {
        return new StatesFlow(
            new NullState(),
            new InitialiseState(),
            new PlacementState(),
            new ActiveState(),
            new FinishState()
        );
    }
}
