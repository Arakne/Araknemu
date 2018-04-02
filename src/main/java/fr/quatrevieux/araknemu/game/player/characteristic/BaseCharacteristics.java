package fr.quatrevieux.araknemu.game.player.characteristic;

import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.data.living.entity.player.Player;
import fr.quatrevieux.araknemu.core.event.Dispatcher;
import fr.quatrevieux.araknemu.game.player.characteristic.event.CharacteristicsChanged;
import fr.quatrevieux.araknemu.game.player.race.GamePlayerRace;
import fr.quatrevieux.araknemu.game.world.creature.characteristics.MutableCharacteristics;

/**
 * Characteristics wrapper for player base stats
 */
final public class BaseCharacteristics implements MutableCharacteristics {
    final private Dispatcher dispatcher;
    final private GamePlayerRace race;
    final private Player player;

    public BaseCharacteristics(Dispatcher dispatcher, GamePlayerRace race, Player player) {
        this.dispatcher = dispatcher;
        this.race = race;
        this.player = player;
    }

    @Override
    public int get(Characteristic characteristic) {
        return race.baseStats(player.level()).get(characteristic) + player.stats().get(characteristic);
    }

    @Override
    public void set(Characteristic characteristic, int value) {
        player.stats().set(characteristic, value);

        dispatcher.dispatch(new CharacteristicsChanged());
    }

    @Override
    public void add(Characteristic characteristic, int value) {
        player.stats().add(characteristic, value);

        dispatcher.dispatch(new CharacteristicsChanged());
    }
}
