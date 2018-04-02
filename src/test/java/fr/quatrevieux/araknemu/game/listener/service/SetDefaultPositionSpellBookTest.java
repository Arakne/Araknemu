package fr.quatrevieux.araknemu.game.listener.service;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.data.living.entity.account.Account;
import fr.quatrevieux.araknemu.data.living.entity.player.Player;
import fr.quatrevieux.araknemu.data.living.entity.player.PlayerSpell;
import fr.quatrevieux.araknemu.data.living.repository.player.PlayerSpellRepository;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.account.AccountCharacter;
import fr.quatrevieux.araknemu.game.account.AccountService;
import fr.quatrevieux.araknemu.game.account.GameAccount;
import fr.quatrevieux.araknemu.game.account.event.CharacterCreated;
import fr.quatrevieux.araknemu.game.player.race.PlayerRaceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SetDefaultPositionSpellBookTest extends GameBaseCase {
    private SetDefaultPositionSpellBook listener;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet
            .pushSpells()
            .pushRaces()
            .use(PlayerSpell.class)
        ;

        listener = new SetDefaultPositionSpellBook(
            container.get(PlayerRaceService.class),
            container.get(PlayerSpellRepository.class)
        );
    }

    @Test
    void onCharacterCreated() throws ContainerException {
        Player player;

        listener.on(
            new CharacterCreated(
                new AccountCharacter(
                    new GameAccount(
                        new Account(1),
                        container.get(AccountService.class),
                        2
                    ),
                    player = dataSet.pushPlayer("Robert", 1, 2)
                )
            )
        );

        List<PlayerSpell> spells = new ArrayList<>(container.get(PlayerSpellRepository.class).byPlayer(player));

        assertCount(3, spells);

        assertEquals(3, spells.get(0).spellId());
        assertEquals(1, spells.get(0).position());
        assertEquals(6, spells.get(1).spellId());
        assertEquals(2, spells.get(1).position());
        assertEquals(17, spells.get(2).spellId());
        assertEquals(3, spells.get(2).position());
    }
}