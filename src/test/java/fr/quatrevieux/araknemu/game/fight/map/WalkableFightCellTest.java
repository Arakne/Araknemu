package fr.quatrevieux.araknemu.game.fight.map;

import fr.quatrevieux.araknemu.data.world.repository.environment.MapTemplateRepository;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.fight.exception.FightMapException;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

class WalkableFightCellTest extends GameBaseCase {
    private WalkableFightCell cell;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushMaps();

        cell = new WalkableFightCell(
            container.get(MapTemplateRepository.class).get(10340).cells().get(123),
            123
        );
    }

    @Test
    void initialState() {
        assertEquals(123, cell.id());
        assertTrue(cell.walkable());
        assertFalse(cell.sightBlocking());
        assertFalse(cell.fighter().isPresent());
    }

    @Test
    void withFighter() {
        Fighter fighter = Mockito.mock(Fighter.class);

        cell.set(fighter);

        assertSame(fighter, cell.fighter().get());
        assertFalse(cell.walkable());
        assertTrue(cell.sightBlocking());
    }

    @Test
    void setAlreadySet() {
        Fighter fighter = Mockito.mock(Fighter.class);

        cell.set(fighter);

        assertThrows(FightMapException.class, () -> cell.set(fighter));
    }

    @Test
    void removeFighterNotSet() {
        assertThrows(FightMapException.class, () -> cell.removeFighter());
    }

    @Test
    void removeFighterSuccess() {
        Fighter fighter = Mockito.mock(Fighter.class);
        cell.set(fighter);

        cell.removeFighter();

        assertFalse(cell.fighter().isPresent());
    }
}
