package fr.quatrevieux.araknemu.game.listener.map.monster;

import fr.quatrevieux.araknemu.data.value.Position;
import fr.quatrevieux.araknemu.data.world.entity.monster.MonsterGroupPosition;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.interaction.event.PlayerMoveFinished;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.fight.type.PvmType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LaunchMonsterFightTest extends GameBaseCase {
    private LaunchMonsterFight listener;
    private ExplorationPlayer player;
    private ExplorationMap map;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet
            .pushMonsterTemplates()
            .pushMonsterSpells()
            .pushMonsterGroups()
            .pushMaps()
        ;

        dataSet.pushMonsterGroupPosition(new MonsterGroupPosition(new Position(10340, 123), 1));
        map = container.get(ExplorationMapService.class).load(10340);

        listener = new LaunchMonsterFight();
        player = explorationPlayer();
        player.join(map);
    }

    @Test
    void onMoveFinishedWithoutMonsters() {
        listener.on(new PlayerMoveFinished(player, map.get(185)));

        assertTrue(map.creatures().contains(player));
    }

    @Test
    void onMoveFinishedWithMonsterGroupShouldStartsFight() {
        listener.on(new PlayerMoveFinished(player, map.get(123)));

        assertFalse(map.creatures().contains(player));
        assertTrue(player.player().isFighting());
        assertInstanceOf(PvmType.class, player.player().fighter().fight().type());
    }
}
