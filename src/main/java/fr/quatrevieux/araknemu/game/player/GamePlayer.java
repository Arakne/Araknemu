package fr.quatrevieux.araknemu.game.player;

import fr.quatrevieux.araknemu.core.event.DefaultListenerAggregate;
import fr.quatrevieux.araknemu.core.event.ListenerAggregate;
import fr.quatrevieux.araknemu.data.living.entity.player.Player;
import fr.quatrevieux.araknemu.data.value.Position;
import fr.quatrevieux.araknemu.game.account.GameAccount;
import fr.quatrevieux.araknemu.game.chat.ChannelSet;
import fr.quatrevieux.araknemu.game.chat.ChannelType;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.game.player.characteristic.CharacterCharacteristics;
import fr.quatrevieux.araknemu.game.player.experience.GamePlayerExperience;
import fr.quatrevieux.araknemu.game.player.inventory.PlayerInventory;
import fr.quatrevieux.araknemu.game.player.race.GamePlayerRace;
import fr.quatrevieux.araknemu.game.player.spell.SpellBook;
import fr.quatrevieux.araknemu.game.player.sprite.GamePlayerSpriteInfo;
import fr.quatrevieux.araknemu.game.player.sprite.SpriteInfo;
import fr.quatrevieux.araknemu.game.world.creature.Life;
import fr.quatrevieux.araknemu.network.game.GameSession;

import java.util.Set;

/**
 * GamePlayer object
 * A player is a logged character, with associated game session
 */
final public class GamePlayer implements PlayerSessionScope {
    final private GameAccount account;
    final private Player entity;
    final private PlayerService service;
    final private GameSession session;
    final private GamePlayerRace race;
    final private Set<ChannelType> channels;
    final private PlayerInventory inventory;
    final private SpriteInfo spriteInfo;
    final private PlayerData data;

    final private ListenerAggregate dispatcher = new DefaultListenerAggregate();

    private PlayerSessionScope scope = this;

    public GamePlayer(GameAccount account, Player entity, GamePlayerRace race, GameSession session, PlayerService service, PlayerInventory inventory, SpellBook spells, GamePlayerExperience experience) {
        this.account = account;
        this.entity = entity;
        this.race = race;
        this.session = session;
        this.service = service;
        this.channels = new ChannelSet(entity.channels(), dispatcher);
        this.inventory = inventory.attach(this);
        this.data = new PlayerData(dispatcher, this, entity, spells, experience);
        this.spriteInfo = new GamePlayerSpriteInfo(entity, inventory);

        this.data.build();
    }

    @Override
    public void dispatch(Object event) {
        session.dispatch(event);
    }

    @Override
    public ListenerAggregate dispatcher() {
        return dispatcher;
    }

    /**
     * Get the base player data
     * The data should be used for change the player data
     * This value do not depends of the current player state (exploring / fighting) and should be modified carefully
     */
    @Override
    public PlayerData properties() {
        return data;
    }

    @Override
    public void send(Object packet) {
        session.write(packet);
    }

    public int id() {
        return entity.id();
    }

    public GameAccount account() {
        return account;
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
     * Get the current session scope
     */
    public PlayerSessionScope scope() {
        return scope;
    }

    /**
     * Check if the player is exploring
     */
    public boolean isExploring() {
        return session.exploration() != null;
    }

    /**
     * Attach the exploration session to the current game
     *
     * @todo attachScope
     */
    @Deprecated
    public void attachExploration(ExplorationPlayer exploration) {
        if (isExploring()) {
            throw new IllegalStateException("The current player is already exploring");
        }

        session.setExploration(exploration);
        scope = exploration;
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
     *
     * @todo stopScope
     */
    @Deprecated
    public void stopExploring() {
        if (!isExploring()) {
            throw new IllegalStateException("The current player is not an exploration state");
        }

        if (exploration() == scope) {
            scope = this;
        }

        session.setExploration(null);
    }

    /**
     * Attach a fighter to the player
     *
     * @todo attachScope
     */
    @Deprecated
    public void attachFighter(PlayerFighter fighter) {
        if (isFighting()) {
            throw new IllegalStateException("The current player is already in fight");
        }

        session.setFighter(fighter);
        scope = fighter;
    }

    /**
     * Remove the attached fighter
     *
     * @todo stopScope
     */
    @Deprecated
    public void stopFighting() {
        if (!isFighting()) {
            throw new IllegalStateException("The current player is not in fight");
        }

        if (fighter() == scope) {
            scope = this;
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
