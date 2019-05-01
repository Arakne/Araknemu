package fr.quatrevieux.araknemu.game.fight.builder;

import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightService;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterFactory;
import fr.quatrevieux.araknemu.game.fight.team.SimpleTeam;
import fr.quatrevieux.araknemu.game.fight.type.ChallengeType;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.util.RandomUtil;
import org.slf4j.Logger;

/**
 * Builder for challenge fight
 */
final public class ChallengeBuilder implements FightBuilder {
    final private BaseBuilder builder;
    final private FighterFactory fighterFactory;

    public ChallengeBuilder(FightService service, FighterFactory fighterFactory, RandomUtil random, Logger logger) {
        this.builder = new BaseBuilder(service, random, new ChallengeType(), logger);
        this.fighterFactory = fighterFactory;
    }

    @Override
    public Fight build(int fightId) {
        return builder.build(fightId);
    }

    /**
     * Set the fight map
     */
    public ChallengeBuilder map(ExplorationMap map) {
        builder.map(map);

        return this;
    }

    /**
     * Add new fighter
     */
    public ChallengeBuilder fighter(GamePlayer player) {
        builder.addTeam((number, startPlaces) -> new SimpleTeam(
            fighterFactory.create(player),
            startPlaces,
            number
        ));

        return this;
    }
}
