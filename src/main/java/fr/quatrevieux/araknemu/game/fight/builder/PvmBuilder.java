package fr.quatrevieux.araknemu.game.fight.builder;

import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightService;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterFactory;
import fr.quatrevieux.araknemu.game.fight.team.MonsterGroupTeam;
import fr.quatrevieux.araknemu.game.fight.team.SimpleTeam;
import fr.quatrevieux.araknemu.game.fight.type.PvmType;
import fr.quatrevieux.araknemu.game.monster.group.MonsterGroup;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.util.RandomUtil;
import org.slf4j.Logger;

/**
 * Builder for pvm fight
 *
 * @see PvmBuilder
 * @see PvmType
 */
final public class PvmBuilder implements FightBuilder {
    final private BaseBuilder builder;
    final private FighterFactory fighterFactory;

    public PvmBuilder(FightService service, FighterFactory fighterFactory, RandomUtil random, PvmType type, Logger logger) {
        this.builder = new BaseBuilder(service, random, type, logger);
        this.fighterFactory = fighterFactory;
    }

    @Override
    public Fight build(int fightId) {
        return builder.build(fightId);
    }

    /**
     * Set the fight map
     */
    public PvmBuilder map(ExplorationMap map) {
        builder.map(map);

        return this;
    }

    /**
     * Set the initiator fighter
     */
    public PvmBuilder initiator(GamePlayer initiator) {
        builder.addTeam((number, startPlaces) -> new SimpleTeam(
            fighterFactory.create(initiator),
            startPlaces,
            number
        ));

        return this;
    }

    /**
     * Set the attacked group
     */
    public PvmBuilder monsterGroup(MonsterGroup monsterGroup) {
        builder.addTeam((number, startPlaces) -> new MonsterGroupTeam(monsterGroup, startPlaces, number));

        return this;
    }
}
