package fr.quatrevieux.araknemu.game.fight.builder;

import fr.quatrevieux.araknemu.game.fight.FightService;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterFactory;

/**
 * Create the challenge builder
 */
final public class ChallengeBuilderFactory implements FightBuilderFactory<ChallengeBuilder> {
    final private FighterFactory fighterFactory;

    public ChallengeBuilderFactory(FighterFactory fighterFactory) {
        this.fighterFactory = fighterFactory;
    }

    @Override
    public Class<ChallengeBuilder> type() {
        return ChallengeBuilder.class;
    }

    @Override
    public ChallengeBuilder create(FightService service) {
        return new ChallengeBuilder(service, fighterFactory);
    }
}
