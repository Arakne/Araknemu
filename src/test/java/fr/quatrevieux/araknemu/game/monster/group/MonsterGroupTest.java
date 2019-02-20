package fr.quatrevieux.araknemu.game.monster.group;

import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.monster.MonsterService;
import fr.quatrevieux.araknemu.game.world.map.Direction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class MonsterGroupTest extends GameBaseCase {
    private MonsterGroup group;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet
            .pushMonsterSpells()
            .pushMonsterTemplates()
        ;

        MonsterService service = container.get(MonsterService.class);

        group = new MonsterGroup(
            5,
            Arrays.asList(
                service.load(31).all().get(2),
                service.load(34).all().get(3),
                service.load(36).all().get(1),
                service.load(36).all().get(5)
            ),
            Direction.WEST,
            123
        );
    }

    @Test
    void values() {
        assertEquals(123, group.cell());
        assertEquals(Direction.WEST, group.orientation());
        assertCount(4, group.monsters());
    }

    @Test
    void sprite() {
        assertSame(group.sprite(), group.sprite());
        assertInstanceOf(MonsterGroupSprite.class, group.sprite());
        assertEquals("123;4;;-503;31,34,36,36;-3;1563^100,1568^100,1566^100,1566^100;4,9,2,6;-1,-1,-1;0,0,0,0;-1,-1,-1;0,0,0,0;-1,-1,-1;0,0,0,0;-1,-1,-1;0,0,0,0;", group.sprite().toString());
    }

    @Test
    void id() {
        assertEquals(-503, group.id());
    }
}
