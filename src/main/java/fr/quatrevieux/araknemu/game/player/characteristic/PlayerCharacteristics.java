package fr.quatrevieux.araknemu.game.player.characteristic;

import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.data.living.entity.player.Player;
import fr.quatrevieux.araknemu.data.value.BoostStatsData;
import fr.quatrevieux.araknemu.data.world.entity.character.PlayerRace;
import fr.quatrevieux.araknemu.game.event.Dispatcher;
import fr.quatrevieux.araknemu.game.event.common.CharacteristicsChanged;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.game.world.creature.characteristics.Characteristics;
import fr.quatrevieux.araknemu.game.world.creature.characteristics.DefaultCharacteristics;
import fr.quatrevieux.araknemu.game.world.creature.characteristics.MutableCharacteristics;
import fr.quatrevieux.araknemu.game.item.effect.SpecialEffect;
import fr.quatrevieux.araknemu.game.item.type.Equipment;

/**
 * Characteristic map for player
 * This class will handle aggregation of stats, and computed stats
 */
final public class PlayerCharacteristics implements Characteristics {
    final private MutableCharacteristics base;
    final private Dispatcher dispatcher;
    final private Player entity;
    final private PlayerRace race;
    final private GamePlayer player;

    final private SpecialEffects specials = new SpecialEffects();

    private Characteristics stuff;

    public PlayerCharacteristics(Dispatcher dispatcher, GamePlayer player, Player entity) {
        this.dispatcher = dispatcher;
        this.player = player;
        this.entity = entity;
        this.race = player.race();

        this.base = new BaseCharacteristics(
            dispatcher,
            race.baseStats(),
            entity.stats()
        );

        this.stuff = computeStuffStats();
    }

    @Override
    public int get(Characteristic characteristic) {
        return base.get(characteristic) + stuff.get(characteristic);
    }

    /**
     * Get the player base stats (i.e. boosted stats + race stats)
     */
    public MutableCharacteristics base() {
        return base;
    }

    /**
     * Get the total stuff stats
     */
    public Characteristics stuff() {
        return stuff;
    }

    /**
     * Get the feat (candy ??) stats
     */
    public Characteristics feats() {
        return new DefaultCharacteristics();
    }

    /**
     * Get the boost (buff) stats
     */
    public Characteristics boost() {
        return new DefaultCharacteristics();
    }

    /**
     * Get the current special effects
     */
    public SpecialEffects specials() {
        return specials;
    }

    /**
     * Boost a characteristic
     */
    public void boostCharacteristic(Characteristic characteristic) {
        BoostStatsData.Interval interval = race.boostStats().get(
            characteristic,
            base.get(characteristic)
        );

        int points = entity.boostPoints() - interval.cost();

        if (points < 0) {
            throw new IllegalArgumentException("Not enough points for boost stats");
        }

        entity.setBoostPoints(points);
        base.add(characteristic, interval.boost());
    }

    /**
     * Get the available boost points
     */
    public int boostPoints() {
        return entity.boostPoints();
    }

    /**
     * @todo DO NOT USE : temporary method !
     */
    @Deprecated
    public void setBoostPoints(int points) {
        entity.setBoostPoints(points);
    }

    /**
     * Get the current player initiative
     */
    public int initiative() {
        int base = player.life().max() / (4 * race.boostStats().get(Characteristic.VITALITY, 0).boost());

        base += get(Characteristic.STRENGTH);
        base += get(Characteristic.LUCK);
        base += get(Characteristic.AGILITY);
        base += get(Characteristic.INTELLIGENCE);
        base += specials.get(SpecialEffects.Type.INITIATIVE);

        int init = base * player.life().current() / player.life().max();

        return init < 1 ? 1 : init;
    }

    /**
     * Get the current player discernment
     */
    public int discernment() {
        return race.startDiscernment()
            + get(Characteristic.LUCK) / 10
            + specials.get(SpecialEffects.Type.DISCERNMENT)
        ;
    }

    /**
     * Rebuild the stuff stats
     */
    public void rebuildStuffStats() {
        stuff = computeStuffStats();

        dispatcher.dispatch(new CharacteristicsChanged());
    }

    /**
     * Rebuild the special effects
     */
    public void rebuildSpecialEffects() {
        specials.clear();

        for (Equipment equipment : player.inventory().equipments()) {
            for (SpecialEffect effect : equipment.specials()) {
                effect.apply(player);
            }
        }

        player.inventory().itemSets().applySpecials(player);
    }

    /**
     * Compute the stuff stats
     */
    private Characteristics computeStuffStats() {
        MutableCharacteristics characteristics = new DefaultCharacteristics();

        for (Equipment equipment : player.inventory().equipments()) {
            equipment.apply(characteristics);
        }

        player.inventory().itemSets().apply(characteristics);

        return characteristics;
    }
}
