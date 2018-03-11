package fr.quatrevieux.araknemu.game.player.spell;

import fr.quatrevieux.araknemu.data.living.entity.player.Player;
import fr.quatrevieux.araknemu.data.living.entity.player.PlayerSpell;
import fr.quatrevieux.araknemu.data.living.repository.player.PlayerSpellRepository;
import fr.quatrevieux.araknemu.game.PreloadableService;
import fr.quatrevieux.araknemu.game.event.ListenerAggregate;
import fr.quatrevieux.araknemu.game.event.listener.service.AddSpellListeners;
import fr.quatrevieux.araknemu.game.event.listener.service.SetDefaultPositionSpellBook;
import fr.quatrevieux.araknemu.game.player.race.PlayerRaceService;
import fr.quatrevieux.araknemu.game.spell.SpellService;
import org.slf4j.Logger;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Service for handle player spells
 */
final public class SpellBookService implements PreloadableService {
    final private PlayerSpellRepository repository;
    final private SpellService service;
    final private PlayerRaceService playerRaceService;
    final private ListenerAggregate dispatcher;

    public SpellBookService(PlayerSpellRepository repository, SpellService service, PlayerRaceService playerRaceService, ListenerAggregate dispatcher) {
        this.repository = repository;
        this.service = service;
        this.playerRaceService = playerRaceService;
        this.dispatcher = dispatcher;
    }

    /**
     * Load the spell book
     */
    public SpellBook load(Player player) {
        Map<Integer, SpellBookEntry> entries = repository.byPlayer(player)
            .stream()
            .map(entity -> new SpellBookEntry(entity, service.get(entity.spellId())))
            .collect(
                Collectors.toMap(
                    entry -> entry.spell().id(),
                    Function.identity()
                )
            )
        ;

        playerRaceService.get(player.race())
            .spells()
            .stream()
            .filter(spell -> !entries.containsKey(spell.id()))
            .map(spell -> new SpellBookEntry(
                new PlayerSpell(player.id(), spell.id(), true),
                spell
            ))
            .forEach(entry -> entries.put(entry.spell().id(), entry))
        ;

        return new SpellBook(entries.values());
    }

    @Override
    public void preload(Logger logger) {
        dispatcher.add(new AddSpellListeners());
        dispatcher.add(new SetDefaultPositionSpellBook(playerRaceService, repository));
    }
}
