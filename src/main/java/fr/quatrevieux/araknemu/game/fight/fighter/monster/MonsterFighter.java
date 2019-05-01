package fr.quatrevieux.araknemu.game.fight.fighter.monster;

import fr.quatrevieux.araknemu.game.fight.castable.weapon.CastableWeapon;
import fr.quatrevieux.araknemu.game.fight.fighter.AbstractFighter;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterCharacteristics;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterLife;
import fr.quatrevieux.araknemu.game.fight.fighter.BaseFighterLife;
import fr.quatrevieux.araknemu.game.fight.team.FightTeam;
import fr.quatrevieux.araknemu.game.fight.team.MonsterGroupTeam;
import fr.quatrevieux.araknemu.game.monster.Monster;
import fr.quatrevieux.araknemu.game.monster.reward.MonsterReward;
import fr.quatrevieux.araknemu.game.spell.SpellList;
import fr.quatrevieux.araknemu.game.world.creature.Sprite;

/**
 * Fighter for a monster
 */
final public class MonsterFighter extends AbstractFighter {
    final private int id;
    final private Monster monster;
    final private MonsterGroupTeam team;

    final private BaseFighterLife life;
    final private MonsterFighterCharacteristics characteristics;
    final private MonsterFighterSprite sprite;

    public MonsterFighter(int id, Monster monster, MonsterGroupTeam team) {
        this.id = id;
        this.monster = monster;
        this.team = team;

        this.life = new BaseFighterLife(this, monster.life());
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

    /**
     * Get the end fight rewards
     *
     * @see Monster#reward()
     */
    public MonsterReward reward() {
        return monster.reward();
    }

    /**
     * Get the monster data for the fighter
     */
    public Monster monster() {
        return monster;
    }
}
