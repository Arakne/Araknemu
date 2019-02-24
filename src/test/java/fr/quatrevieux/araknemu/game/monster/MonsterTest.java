package fr.quatrevieux.araknemu.game.monster;

import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.data.value.Colors;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MonsterTest extends GameBaseCase {
    private Monster monster;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet
            .pushMonsterTemplates()
            .pushMonsterSpells()
        ;

        monster = container.get(MonsterService.class).load(34).all().get(2);
    }

    @Test
    void values() {
        assertEquals(34, monster.id());
        assertEquals(1568, monster.gfxId());
        assertEquals(3, monster.gradeNumber());
        assertEquals(8, monster.level());
        assertEquals(Colors.DEFAULT, monster.colors());
        assertEquals(50, monster.life());
        assertEquals(35, monster.initiative());

        assertEquals(5, monster.characteristics().get(Characteristic.ACTION_POINT));
        assertEquals(3, monster.characteristics().get(Characteristic.MOVEMENT_POINT));
    }
}
