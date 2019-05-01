package fr.quatrevieux.araknemu.game.fight.builder;

import fr.quatrevieux.araknemu.game.fight.FightService;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterFactory;
import fr.quatrevieux.araknemu.util.RandomUtil;
import org.slf4j.Logger;

/**
 * Create the challenge builder
 */
final public class ChallengeBuilderFactory implements FightBuilderFactory<ChallengeBuilder> {
    final private FighterFactory fighterFactory;
    final private Logger logger;
    final private RandomUtil random = new RandomUtil();

    public ChallengeBuilderFactory(FighterFactory fighterFactory, Logger logger) {
        this.fighterFactory = fighterFactory;
        this.logger = logger;
    }

    @Override
    public Class<ChallengeBuilder> type() {
        return ChallengeBuilder.class;
    }

    @Override
    public ChallengeBuilder create(FightService service) {
        return new ChallengeBuilder(service, fighterFactory, random, logger);
    }
}
