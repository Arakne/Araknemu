package fr.quatrevieux.araknemu.game.fight.fighter.player;

import fr.quatrevieux.araknemu.core.event.DefaultListenerAggregate;
import fr.quatrevieux.araknemu.core.event.ListenerAggregate;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.BuffList;
import fr.quatrevieux.araknemu.game.fight.castable.weapon.CastableWeapon;
import fr.quatrevieux.araknemu.game.fight.event.FighterReadyStateChanged;
import fr.quatrevieux.araknemu.game.fight.exception.FightException;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterCharacteristics;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterLife;
import fr.quatrevieux.araknemu.game.fight.fighter.States;
import fr.quatrevieux.araknemu.game.fight.fighter.event.FighterInitialized;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.fight.team.FightTeam;
import fr.quatrevieux.araknemu.game.fight.turn.FightTurn;
import fr.quatrevieux.araknemu.game.item.type.Weapon;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.game.player.PlayerSessionScope;
import fr.quatrevieux.araknemu.game.player.inventory.slot.WeaponSlot;
import fr.quatrevieux.araknemu.game.player.spell.SpellBook;
import fr.quatrevieux.araknemu.game.spell.SpellList;
import fr.quatrevieux.araknemu.game.world.creature.Sprite;
import fr.quatrevieux.araknemu.game.world.util.Sender;

import java.util.HashMap;
import java.util.Map;

/**
 * Fighter for a player
 */
final public class PlayerFighter implements Fighter, PlayerSessionScope {
    final private GamePlayer player;

    final private ListenerAggregate dispatcher = new DefaultListenerAggregate();
    final private PlayerFighterProperties properties;
    final private BuffList buffs = new BuffList(this);
    final private States states = new States(this);
    final private Map<Object, Object> attachments = new HashMap<>();

    private FightCell cell;
    private FightTeam team;
    private Fight fight;
    private FightTurn turn;

    private boolean ready = false;
    private CastableWeapon weapon;

    public PlayerFighter(GamePlayer player) {
        this.player = player;
        this.properties = new PlayerFighterProperties(this, player.properties());
    }

    @Override
    public void init() {
        properties.life().init();

        fight.dispatch(new FighterInitialized(this));
    }

    @Override
    public FightCell cell() {
        return cell;
    }

    @Override
    public void move(FightCell cell) {
        if (this.cell != null) {
            this.cell.removeFighter();
        }

        if (cell != null) {
            cell.set(this);
        }

        this.cell = cell;
    }

    /**
     * Get the base player data
     */
    public GamePlayer player() {
        return player;
    }

    @Override
    public int id() {
        return player.id();
    }

    @Override
    public Sprite sprite() {
        return new PlayerFighterSprite(this, player.spriteInfo());
    }

    @Override
    public FighterLife life() {
        return properties.life();
    }

    @Override
    public boolean dead() {
        return properties.life().dead();
    }

    @Override
    public FighterCharacteristics characteristics() {
        return properties.characteristics();
    }

    /**
     * Get the properties of the current character
     */
    @Override
    public PlayerFighterProperties properties() {
        return properties;
    }

    @Override
    public SpellList spells() {
        return properties.spells();
    }

    @Override
    public void dispatch(Object event) {
        player.dispatch(event);
    }

    @Override
    public ListenerAggregate dispatcher() {
        return dispatcher;
    }

    @Override
    public CastableWeapon weapon() {
        if (weapon != null) {
            return weapon;
        }

        return weapon = player.inventory()
            .bySlot(WeaponSlot.SLOT_ID)
            .map(Weapon.class::cast)
            .map(CastableWeapon::new)
            .orElseThrow(() -> new FightException("The fighter do not have any weapon"))
        ;
    }

    @Override
    public BuffList buffs() {
        return buffs;
    }

    @Override
    public States states() {
        return states;
    }

    @Override
    public int level() {
        return player.properties().experience().level();
    }

    @Override
    public FightTeam team() {
        return team;
    }

    /**
     * Set the fighter team
     */
    public void setTeam(FightTeam team) {
        this.team = team;
    }

    @Override
    public void send(Object packet) {
        player.send(packet);
    }

    @Override
    public Fight fight() {
        return fight;
    }

    @Override
    public void joinFight(Fight fight, FightCell startCell) {
        if (this.fight != null) {
            throw new IllegalStateException("A fight is already defined");
        }

        this.fight = fight;
        this.cell = startCell;
        startCell.set(this);
    }

    @Override
    public boolean ready() {
        return ready;
    }

    @Override
    public void play(FightTurn turn) {
        this.turn = turn;
    }

    @Override
    public void stop() {
        turn = null;
    }

    /**
     * Get the current fighter turn
     */
    public FightTurn turn() {
        if (turn == null) {
            throw new FightException("It's not your turn");
        }

        return turn;
    }

    /**
     * Change the ready flag
     */
    public void setReady(boolean ready) {
        this.ready = ready;
        fight.dispatch(new FighterReadyStateChanged(this));
    }

    @Override
    public void attach(Object key, Object value) {
        attachments.put(key, value);
    }

    @Override
    public Object attachment(Object key) {
        return attachments.get(key);
    }

    @Override
    public boolean isOnFight() {
        return fight != null && cell != null;
    }

    @Override
    public boolean equals(Object obj) {
        return getClass().equals(obj.getClass()) && id() == ((PlayerFighter) obj).id();
    }

    @Override
    public int hashCode() {
        return id();
    }

    /**
     * Clear fighter data
     */
    public void destroy() {
        this.fight = null;
        this.attachments.clear();
    }
}
