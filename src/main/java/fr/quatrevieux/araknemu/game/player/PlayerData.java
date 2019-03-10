package fr.quatrevieux.araknemu.game.player;

import fr.quatrevieux.araknemu.core.event.Dispatcher;
import fr.quatrevieux.araknemu.data.living.entity.player.Player;
import fr.quatrevieux.araknemu.game.player.characteristic.PlayerCharacteristics;
import fr.quatrevieux.araknemu.game.player.characteristic.PlayerLife;
import fr.quatrevieux.araknemu.game.player.experience.GamePlayerExperience;
import fr.quatrevieux.araknemu.game.player.inventory.PlayerInventory;
import fr.quatrevieux.araknemu.game.player.spell.SpellBook;

/**
 * Store the current player data
 */
final public class PlayerData implements CharacterProperties {
    final private PlayerCharacteristics characteristics;
    final private PlayerLife life;
    final private SpellBook spells;
    final private GamePlayerExperience experience;
    final private PlayerInventory inventory;

    public PlayerData(Dispatcher dispatcher, GamePlayer player, Player entity, SpellBook spells, GamePlayerExperience experience) {
        this.spells = spells;
        this.experience = experience;
        this.characteristics = new PlayerCharacteristics(dispatcher, player, entity);
        this.life = new PlayerLife(player, entity);
        this.inventory = player.inventory();
    }

    @Override
    public PlayerCharacteristics characteristics() {
        return characteristics;
    }

    @Override
    public PlayerLife life() {
        return life;
    }

    @Override
    public SpellBook spells() {
        return spells;
    }

    @Override
    public GamePlayerExperience experience() {
        return experience;
    }

    @Override
    public long kamas() {
        return inventory.kamas();
    }

    void build() {
        characteristics.rebuildSpecialEffects();
        life.init();
    }
}
