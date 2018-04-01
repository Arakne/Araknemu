package fr.quatrevieux.araknemu.game.fight.builder;

import fr.quatrevieux.araknemu.game.fight.FightService;

/**
 * Create the challenge builder
 */
final public class ChallengeBuilderFactory implements FightBuilderFactory<ChallengeBuilder> {
    @Override
    public Class<ChallengeBuilder> type() {
        return ChallengeBuilder.class;
    }

    @Override
    public ChallengeBuilder create(FightService service) {
        return new ChallengeBuilder(service);
    }
}
