package fr.quatrevieux.araknemu.game.player.spell;

import fr.quatrevieux.araknemu.core.event.Dispatcher;
import fr.quatrevieux.araknemu.core.event.EventsSubscriber;
import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.data.living.entity.player.Player;
import fr.quatrevieux.araknemu.data.living.entity.player.PlayerSpell;
import fr.quatrevieux.araknemu.data.living.repository.player.PlayerSpellRepository;
import fr.quatrevieux.araknemu.game.listener.player.spell.*;
import fr.quatrevieux.araknemu.game.player.event.PlayerLoaded;
import fr.quatrevieux.araknemu.game.player.race.PlayerRaceService;
import fr.quatrevieux.araknemu.game.spell.SpellService;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Service for handle player spells
 */
final public class SpellBookService implements EventsSubscriber {
    final private PlayerSpellRepository repository;
    final private SpellService service;
    final private PlayerRaceService playerRaceService;

    public SpellBookService(PlayerSpellRepository repository, SpellService service, PlayerRaceService playerRaceService) {
        this.repository = repository;
        this.service = service;
        this.playerRaceService = playerRaceService;
    }

    @Override
    public Listener[] listeners() {
        return new Listener[] {
            new Listener<PlayerLoaded>() {
                @Override
                public void on(PlayerLoaded event) {
                    event.player().dispatcher().add(new SaveSpellPosition(repository));
                    event.player().dispatcher().add(new SaveLearnedSpell(repository));
                    event.player().dispatcher().add(new SaveUpgradedSpell(event.player(), repository));

                    event.player().dispatcher().add(new SendSpellList(event.player()));
                    event.player().dispatcher().add(new SendAllSpellBoosts(event.player()));
                    event.player().dispatcher().add(new SendLearnedSpell(event.player()));
                    event.player().dispatcher().add(new SendUpgradedSpell(event.player()));
                    event.player().dispatcher().add(new SendSpellBoost(event.player()));
                }

                @Override
                public Class<PlayerLoaded> event() {
                    return PlayerLoaded.class;
                }
            },
            new SetDefaultPositionSpellBook(playerRaceService, repository)
        };
    }

    /**
     * Load the spell book
     */
    public SpellBook load(Dispatcher dispatcher, Player player) {
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

        return new SpellBook(dispatcher, player, entries.values());
    }
}
