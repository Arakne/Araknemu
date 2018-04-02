package fr.quatrevieux.araknemu.game.fight.fighter;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.fight.FightService;
import fr.quatrevieux.araknemu.game.fight.map.FightMap;
import fr.quatrevieux.araknemu.game.fight.team.SimpleTeam;
import fr.quatrevieux.araknemu.game.world.creature.Sprite;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class PlayerFighterSpriteTest extends GameBaseCase {
    private PlayerFighterSprite sprite;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushMaps();

        FightMap map = container.get(FightService.class).map(
            container.get(ExplorationMapService.class).load(10340)
        );

        PlayerFighter fighter = new PlayerFighter(gamePlayer(true));
        sprite = new PlayerFighterSprite(fighter, gamePlayer().spriteInfo());

        fighter.join(new SimpleTeam(fighter, new ArrayList<>(), 0));
        fighter.move(map.get(222));
    }

    @Test
    void generate() {
        assertEquals(
            "222;1;0;1;Bob;1;10^100x100;0;50;0,0,0,0;7b;1c8;315;,,,,;295;6;3;0;0;0;0;0;0;0;0;;",
            sprite.toString()
        );
    }

    @Test
    void getters() throws SQLException, ContainerException {
        assertEquals(gamePlayer().id(), sprite.id());
        assertEquals(222, sprite.cell());
        assertEquals(Sprite.Type.PLAYER, sprite.type());
        assertEquals("Bob", sprite.name());
    }
}
