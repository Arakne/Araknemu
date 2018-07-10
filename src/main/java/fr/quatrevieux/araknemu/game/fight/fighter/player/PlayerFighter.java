package fr.quatrevieux.araknemu.game.fight.fighter.player;

import fr.quatrevieux.araknemu.core.event.DefaultListenerAggregate;
import fr.quatrevieux.araknemu.core.event.ListenerAggregate;
import fr.quatrevieux.araknemu.game.fight.castable.weapon.CastableWeapon;
import fr.quatrevieux.araknemu.game.fight.event.FighterReadyStateChanged;
import fr.quatrevieux.araknemu.game.fight.exception.FightException;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterCharacteristics;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterLife;
import fr.quatrevieux.araknemu.game.fight.turn.FightTurn;
import fr.quatrevieux.araknemu.game.item.inventory.exception.InventoryException;
import fr.quatrevieux.araknemu.game.item.type.Weapon;
import fr.quatrevieux.araknemu.game.listener.fight.SendFightJoined;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.fight.team.FightTeam;
import fr.quatrevieux.araknemu.game.listener.fight.fighter.*;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.game.player.inventory.slot.WeaponSlot;
import fr.quatrevieux.araknemu.game.spell.SpellList;
import fr.quatrevieux.araknemu.game.world.creature.Sprite;
import fr.quatrevieux.araknemu.game.world.util.Sender;

/**
 * Fighter for a player
 */
final public class PlayerFighter implements Fighter, Sender {
    final private GamePlayer player;

    final private ListenerAggregate dispatcher = new DefaultListenerAggregate();
    final private PlayerFighterCharacteristics characteristics;
    final private PlayerFighterLife life;

    private FightCell cell;
    private FightTeam team;
    private Fight fight;
    private FightTurn turn;

    private boolean ready = false;
    private CastableWeapon weapon;

    public PlayerFighter(GamePlayer player) {
        this.player = player;
        this.characteristics = new PlayerFighterCharacteristics(player.characteristics());
        this.life = new PlayerFighterLife(player.life(), this);

        dispatcher.add(new SendFightJoined(this));
        dispatcher.add(new ApplyEndFightReward(this));
        dispatcher.add(new StopFightSession(this));
        dispatcher.add(new SendFightLeaved(this));
        dispatcher.add(new LeaveOnDisconnect(this));
        dispatcher.add(new ApplyLeaveReward(this));
    }

    @Override
    public void init() {
        life.init();
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

        cell.set(this);
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
        return life;
    }

    @Override
    public boolean dead() {
        return life.dead();
    }

    @Override
    public FighterCharacteristics characteristics() {
        return characteristics;
    }

    @Override
    public SpellList spells() {
        return player.spells();
    }

    @Override
    public CastableWeapon weapon() {
        if (weapon != null) {
            return weapon;
        }

        try {
            return weapon = player.inventory()
                .bySlot(WeaponSlot.SLOT_ID)
                .map(Weapon.class::cast)
                .map(CastableWeapon::new)
                .orElseThrow(() -> new FightException("The fighter do not have any weapon"))
            ;
        } catch (InventoryException e) {
            throw new FightException(e);
        }
    }

    @Override
    public int level() {
        return player.experience().level();
    }

    @Override
    public void dispatch(Object event) {
        player.dispatch(event);
    }

    public ListenerAggregate dispatcher() {
        return dispatcher;
    }

    @Override
    public FightTeam team() {
        return team;
    }

    @Override
    public void join(FightTeam team) {
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
    public void setFight(Fight fight) {
        this.fight = fight;
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
}
