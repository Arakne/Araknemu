package fr.quatrevieux.araknemu.game.player;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.data.constant.Race;
import fr.quatrevieux.araknemu.data.constant.Sex;
import fr.quatrevieux.araknemu.data.living.entity.account.Account;
import fr.quatrevieux.araknemu.data.living.entity.player.Player;
import fr.quatrevieux.araknemu.data.living.entity.player.PlayerItem;
import fr.quatrevieux.araknemu.data.living.entity.player.PlayerSpell;
import fr.quatrevieux.araknemu.data.value.Colors;
import fr.quatrevieux.araknemu.data.value.Position;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.account.AccountService;
import fr.quatrevieux.araknemu.game.account.GameAccount;
import fr.quatrevieux.araknemu.game.chat.ChannelType;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.game.player.characteristic.PlayerLife;
import fr.quatrevieux.araknemu.game.player.experience.PlayerExperienceService;
import fr.quatrevieux.araknemu.game.player.inventory.InventoryService;
import fr.quatrevieux.araknemu.game.player.race.PlayerRaceService;
import fr.quatrevieux.araknemu.game.player.spell.SpellBookService;
import fr.quatrevieux.araknemu.game.world.creature.characteristics.DefaultCharacteristics;
import fr.quatrevieux.araknemu.game.world.creature.characteristics.MutableCharacteristics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.*;

class GamePlayerTest extends GameBaseCase {
    private GamePlayer player;
    private Player entity;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet
            .pushMaps()
            .pushRaces()
            .pushSpells()
            .use(PlayerItem.class)
            .use(PlayerSpell.class)
        ;

        MutableCharacteristics characteristics = new DefaultCharacteristics();

        characteristics.set(Characteristic.INTELLIGENCE, 150);
        characteristics.set(Characteristic.VITALITY, 50);

        entity = dataSet.push(new Player(5, 2, 1, "Other", Race.CRA, Sex.MALE, new Colors(-1, -1, -1), 50, characteristics, new Position(10300, 308), EnumSet.allOf(ChannelType.class), 0, 0, -1, 0));

        player = new GamePlayer(
            new GameAccount(
                new Account(2),
                container.get(AccountService.class),
                1
            ),
            entity,
            container.get(PlayerRaceService.class).get(Race.CRA),
            session,
            container.get(PlayerService.class),
            container.get(InventoryService.class).load(entity),
            container.get(SpellBookService.class).load(session, entity),
            container.get(PlayerExperienceService.class).load(session, entity)
        );
    }

    @Test
    void send() {
        player.send("test");

        requestStack.assertLast("test");
    }

    @Test
    void characteristics() {
        assertEquals(150, player.characteristics().get(Characteristic.INTELLIGENCE));
        assertEquals(3, player.characteristics().get(Characteristic.MOVEMENT_POINT));
        assertEquals(6, player.characteristics().get(Characteristic.ACTION_POINT));
    }

    @Test
    void position() {
        assertEquals(new Position(10300, 308), player.position());
    }

    @Test
    void isExploring() {
        assertFalse(player.isExploring());

        session.setExploration(
            new ExplorationPlayer(player)
        );

        assertTrue(player.isExploring());
    }

    @Test
    void stopExploringNotExploring() {
        assertThrows(IllegalStateException.class, () -> player.stopExploring());
    }

    @Test
    void stopExploringSuccess() {
        session.setExploration(new ExplorationPlayer(player));

        player.stopExploring();

        assertNull(session.exploration());
        assertFalse(player.isExploring());
    }

    @Test
    void explorationNotExploring() {
        assertThrows(IllegalStateException.class, () -> player.exploration(), "The current player is not an exploration state");
    }

    @Test
    void exploration() {
        session.setExploration(
            new ExplorationPlayer(player)
        );

        assertSame(session.exploration(), player.exploration());
    }

    @Test
    void isFighting() {
        assertFalse(player.isFighting());

        player.attachFighter(new PlayerFighter(player));

        assertTrue(player.isFighting());
    }

    @Test
    void attachFighter() {
        PlayerFighter fighter = new PlayerFighter(player);
        player.attachFighter(fighter);

        assertSame(fighter, session.fighter());
        assertSame(fighter, session.fighter());
    }

    @Test
    void fighterNotInFight() {
        assertThrows(IllegalStateException.class, () -> player.fighter());
    }

    @Test
    void stopFighting() {
        PlayerFighter fighter = new PlayerFighter(player);
        player.attachFighter(fighter);
        player.stopFighting();

        assertNull(session.fighter());
        assertFalse(player.isFighting());
    }

    @Test
    void stopFightingNotInFight() {
        assertThrows(IllegalStateException.class, () -> player.stopFighting());
    }

    @Test
    void save() throws ContainerException {
        player.setPosition(
            new Position(7894, 12)
        );

        player.entity().stats().set(Characteristic.AGILITY, 123);

        player.save();

        assertEquals(new Position(7894, 12), dataSet.refresh(entity).position());
        assertEquals(123, player.characteristics().get(Characteristic.AGILITY));
    }

    @Test
    void life() {
        assertInstanceOf(PlayerLife.class, player.life());
        assertEquals(345, player.life().current());
        assertEquals(345, player.life().max());

        player.entity().setLife(10);
        assertEquals(10, player.life().current());
    }
}
