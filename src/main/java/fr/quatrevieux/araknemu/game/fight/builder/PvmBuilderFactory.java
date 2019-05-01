package fr.quatrevieux.araknemu.game.fight.builder;

import fr.quatrevieux.araknemu.game.fight.FightService;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterFactory;
import fr.quatrevieux.araknemu.game.fight.type.PvmType;
import fr.quatrevieux.araknemu.util.RandomUtil;
import org.slf4j.Logger;

/**
 * Create the pvm builder
 */
final public class PvmBuilderFactory implements FightBuilderFactory<PvmBuilder> {
    final private FighterFactory fighterFactory;
    final private RandomUtil random;
    final private PvmType type;
    final private Logger logger;

    public PvmBuilderFactory(FighterFactory fighterFactory, PvmType type, Logger logger) {
        this.fighterFactory = fighterFactory;
        this.random = new RandomUtil();
        this.type = type;
        this.logger = logger;
    }

    @Override
    public Class<PvmBuilder> type() {
        return PvmBuilder.class;
    }

    @Override
    public PvmBuilder create(FightService service) {
        return new PvmBuilder(service, fighterFactory, random, type, logger);
    }
}
