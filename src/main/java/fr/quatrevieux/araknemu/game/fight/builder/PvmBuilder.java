package fr.quatrevieux.araknemu.game.fight.builder;

import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightService;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterFactory;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.game.fight.map.FightMap;
import fr.quatrevieux.araknemu.game.fight.team.FightTeam;
import fr.quatrevieux.araknemu.game.fight.team.MonsterGroupTeam;
import fr.quatrevieux.araknemu.game.fight.team.SimpleTeam;
import fr.quatrevieux.araknemu.game.fight.type.PvmType;
import fr.quatrevieux.araknemu.game.monster.group.MonsterGroup;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.util.RandomUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Builder for pvm fight
 *
 * @see PvmBuilder
 * @see PvmType
 */
final public class PvmBuilder implements FightBuilder {
    final private FightService service;
    final private FighterFactory fighterFactory;
    final private RandomUtil random;
    final private PvmType type;

    private PlayerFighter initiator;
    private MonsterGroup monsterGroup;
    private FightMap map;

    public PvmBuilder(FightService service, FighterFactory fighterFactory, RandomUtil random, PvmType type) {
        this.service = service;
        this.fighterFactory = fighterFactory;
        this.random = random;
        this.type = type;
    }

    @Override
    public Fight build(int fightId) {
        return new Fight(fightId, type, map, buildTeams());
    }

    /**
     * Set the fight map
     */
    public PvmBuilder map(ExplorationMap map) {
        this.map = service.map(map);

        return this;
    }

    /**
     * Set the initiator fighter
     */
    public PvmBuilder initiator(GamePlayer initiator) {
        this.initiator = fighterFactory.create(initiator);

        return this;
    }

    /**
     * Set the attacked group
     */
    public PvmBuilder monsterGroup(MonsterGroup monsterGroup) {
        this.monsterGroup = monsterGroup;

        return this;
    }

    private List<FightTeam> buildTeams() {
        int playerTeamNumber = random.integer(2);
        int monstersTeamNumber = 1 - playerTeamNumber;

        FightTeam[] teams = new FightTeam[2];

        teams[playerTeamNumber] = new SimpleTeam(initiator, map.startPlaces(playerTeamNumber), playerTeamNumber);
        teams[monstersTeamNumber] = new MonsterGroupTeam(monsterGroup, map.startPlaces(monstersTeamNumber), monstersTeamNumber);

        return new ArrayList<>(Arrays.asList(teams));
    }
}
