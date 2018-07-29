package fr.quatrevieux.araknemu.game.player;

import fr.quatrevieux.araknemu.data.living.entity.player.Player;
import fr.quatrevieux.araknemu.data.value.Position;
import fr.quatrevieux.araknemu.game.account.GameAccount;
import fr.quatrevieux.araknemu.game.chat.ChannelSet;
import fr.quatrevieux.araknemu.game.chat.ChannelType;
import fr.quatrevieux.araknemu.core.event.DefaultListenerAggregate;
import fr.quatrevieux.araknemu.core.event.Dispatcher;
import fr.quatrevieux.araknemu.core.event.ListenerAggregate;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.game.player.characteristic.PlayerLife;
import fr.quatrevieux.araknemu.game.player.characteristic.PlayerCharacteristics;
import fr.quatrevieux.araknemu.game.player.experience.GamePlayerExperience;
import fr.quatrevieux.araknemu.game.player.inventory.PlayerInventory;
import fr.quatrevieux.araknemu.game.player.race.GamePlayerRace;
import fr.quatrevieux.araknemu.game.player.spell.SpellBook;
import fr.quatrevieux.araknemu.game.player.sprite.GamePlayerSpriteInfo;
import fr.quatrevieux.araknemu.game.player.sprite.SpriteInfo;
import fr.quatrevieux.araknemu.game.world.util.Sender;
import fr.quatrevieux.araknemu.network.game.GameSession;

import java.util.Set;

/**
 * GamePlayer object
 * A player is a logged character, with associated game session
 */
final public class GamePlayer implements Dispatcher, CharacterProperties, Sender {
    final private GameAccount account;
    final private Player entity;
    final private PlayerService service;
    final private GameSession session;
    final private GamePlayerRace race;
    final private PlayerCharacteristics characteristics;
    final private Set<ChannelType> channels;
    final private PlayerInventory inventory;
    final private PlayerLife life;
    final private SpellBook spells;
    final private GamePlayerExperience experience;
    final private SpriteInfo spriteInfo;

    final private ListenerAggregate dispatcher = new DefaultListenerAggregate();

    public GamePlayer(GameAccount account, Player entity, GamePlayerRace race, GameSession session, PlayerService service, PlayerInventory inventory, SpellBook spells, GamePlayerExperience experience) {
        this.account = account;
        this.entity = entity;
        this.race = race;
        this.session = session;
        this.service = service;
        this.spells = spells;
        this.experience = experience;
        this.channels = new ChannelSet(entity.channels(), dispatcher);
        this.inventory = inventory.attach(this);

        characteristics = new PlayerCharacteristics(dispatcher, this, entity);
        characteristics.rebuildSpecialEffects();

        life = new PlayerLife(this, entity);
        spriteInfo = new GamePlayerSpriteInfo(entity, inventory);
    }

    public int id() {
        return entity.id();
    }

    public GameAccount account() {
        return account;
    }

    @Override
    public void dispatch(Object event) {
        session.dispatch(event);
    }

    public ListenerAggregate dispatcher() {
        return dispatcher;
    }

    @Override
    public PlayerCharacteristics characteristics() {
        return characteristics;
    }

    @Override
    public void send(Object packet) {
        session.write(packet);
    }

    /**
     * Get the current player position
     */
    public Position position() {
        return entity.position();
    }

    /**
     * Set new position
     */
    public void setPosition(Position position) {
        entity.setPosition(position);
    }

    /**
     * Get the player race
     */
    public GamePlayerRace race() {
        return race;
    }

    public String name() {
        return entity.name();
    }

    /**
     * Get channels subscriptions
     */
    public Set<ChannelType> subscriptions() {
        return channels;
    }

    /**
     * Check if the player is exploring
     */
    public boolean isExploring() {
        return session.exploration() != null;
    }

    /**
     * Get the exploration player
     *
     * @throws IllegalStateException When the player is not on exploration state
     */
    public ExplorationPlayer exploration() {
        if (!isExploring()) {
            throw new IllegalStateException("The current player is not an exploration state");
        }

        return session.exploration();
    }

    /**
     * Remove the exploration player
     */
    public void stopExploring() {
        if (!isExploring()) {
            throw new IllegalStateException("The current player is not an exploration state");
        }

        session.setExploration(null);
    }

    /**
     * Attach a fighter to the player
     */
    public void attachFighter(PlayerFighter fighter) {
        if (isFighting()) {
            throw new IllegalStateException("The current player is already in fight");
        }

        session.setFighter(fighter);
    }

    /**
     * Remove the attached fighter
     */
    public void stopFighting() {
        if (!isFighting()) {
            throw new IllegalStateException("The current player is not in fight");
        }

        session.setFighter(null);
    }

    /**
     * Get the attached fighter
     */
    public PlayerFighter fighter() {
        if (!isFighting()) {
            throw new IllegalStateException("The current player is not in fight");
        }

        return session.fighter();
    }

    /**
     * Check if the current player is in fight
     */
    public boolean isFighting() {
        return session.fighter() != null;
    }

    /**
     * Save the player
     */
    public void save() {
        service.save(this);
    }

    public PlayerInventory inventory() {
        return inventory;
    }

    @Override
    public PlayerLife life() {
        return life;
    }

    @Override
    public GamePlayerExperience experience() {
        return experience;
    }

    @Override
    public SpellBook spells() {
        return spells;
    }

    /**
     * Get the player sprite info
     */
    public SpriteInfo spriteInfo() {
        return spriteInfo;
    }

    Player entity() {
        return entity;
    }
}
