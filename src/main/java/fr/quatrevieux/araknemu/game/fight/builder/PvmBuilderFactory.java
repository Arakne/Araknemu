package fr.quatrevieux.araknemu.game.fight.builder;

import fr.quatrevieux.araknemu.game.fight.FightService;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterFactory;
import fr.quatrevieux.araknemu.util.RandomUtil;

/**
 * Create the pvm builder
 */
final public class PvmBuilderFactory implements FightBuilderFactory<PvmBuilder> {
    final private FighterFactory fighterFactory;
    final private RandomUtil random;

    public PvmBuilderFactory(FighterFactory fighterFactory) {
        this.fighterFactory = fighterFactory;
        this.random = new RandomUtil();
    }

    @Override
    public Class<PvmBuilder> type() {
        return PvmBuilder.class;
    }

    @Override
    public PvmBuilder create(FightService service) {
        return new PvmBuilder(service, fighterFactory, random);
    }
}
