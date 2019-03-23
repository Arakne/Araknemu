package fr.quatrevieux.araknemu.game.fight.fighter.monster;

import fr.quatrevieux.araknemu.game.fight.castable.weapon.CastableWeapon;
import fr.quatrevieux.araknemu.game.fight.fighter.AbstractFighter;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterCharacteristics;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterLife;
import fr.quatrevieux.araknemu.game.fight.team.FightTeam;
import fr.quatrevieux.araknemu.game.fight.team.MonsterGroupTeam;
import fr.quatrevieux.araknemu.game.fight.turn.FightTurn;
import fr.quatrevieux.araknemu.game.monster.Monster;
import fr.quatrevieux.araknemu.game.monster.reward.MonsterReward;
import fr.quatrevieux.araknemu.game.spell.SpellList;
import fr.quatrevieux.araknemu.game.world.creature.Sprite;

import java.time.Duration;

/**
 * Fighter for a monster
 */
final public class MonsterFighter extends AbstractFighter {
    final private int id;
    final private Monster monster;
    final private MonsterGroupTeam team;

    final private MonsterFighterLife life;
    final private MonsterFighterCharacteristics characteristics;
    final private MonsterFighterSprite sprite;

    public MonsterFighter(int id, Monster monster, MonsterGroupTeam team) {
        this.id = id;
        this.monster = monster;
        this.team = team;

        this.life = new MonsterFighterLife(this, monster.life());
        this.characteristics = new MonsterFighterCharacteristics(monster, this);
        this.sprite = new MonsterFighterSprite(this, monster);
    }

    @Override
    public int id() {
        return id;
    }

    @Override
    public Sprite sprite() {
        return sprite;
    }

    @Override
    public FighterLife life() {
        return life;
    }

    @Override
    public FighterCharacteristics characteristics() {
        return characteristics;
    }

    @Override
    public SpellList spells() {
        return monster.spells();
    }

    @Override
    public CastableWeapon weapon() {
        return null;
    }

    @Override
    public int level() {
        return monster.level();
    }

    @Override
    public FightTeam team() {
        return team;
    }

    @Override
    public boolean ready() {
        return true;
    }

    @Override
    public void play(FightTurn turn) {
        super.play(turn);

        // @todo Start AI
        fight().schedule(turn::stop, Duration.ofMillis(500));
    }

    /**
     * Get the end fight rewards
     *
     * @see Monster#reward()
     */
    public MonsterReward reward() {
        return monster.reward();
    }
}
