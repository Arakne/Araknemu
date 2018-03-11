package fr.quatrevieux.araknemu.game.event.listener.service;

import fr.quatrevieux.araknemu.data.living.entity.player.Player;
import fr.quatrevieux.araknemu.data.living.entity.player.PlayerSpell;
import fr.quatrevieux.araknemu.data.living.repository.player.PlayerSpellRepository;
import fr.quatrevieux.araknemu.game.event.Listener;
import fr.quatrevieux.araknemu.game.event.manage.CharacterCreated;
import fr.quatrevieux.araknemu.game.player.race.PlayerRaceService;
import fr.quatrevieux.araknemu.game.spell.SpellLevels;

/**
 * Set the default positions for start spells
 */
final public class SetDefaultPositionSpellBook implements Listener<CharacterCreated> {
    final private PlayerRaceService service;
    final private PlayerSpellRepository repository;

    public SetDefaultPositionSpellBook(PlayerRaceService service, PlayerSpellRepository repository) {
        this.service = service;
        this.repository = repository;
    }

    @Override
    public void on(CharacterCreated event) {
        Player player = event.character().character();

        char position = 'b';

        for (SpellLevels spell : service.get(player.race()).spells()) {
            if (spell.level(1).minPlayerLevel() > player.level()) {
                break;
            }

            repository.add(
                new PlayerSpell(player.id(), spell.id(), true, 1, position++)
            );
        }
    }

    @Override
    public Class<CharacterCreated> event() {
        return CharacterCreated.class;
    }
}
