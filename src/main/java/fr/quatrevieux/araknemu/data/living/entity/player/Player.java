package fr.quatrevieux.araknemu.data.living.entity.player;

import fr.quatrevieux.araknemu.data.constant.Race;
import fr.quatrevieux.araknemu.data.constant.Sex;
import fr.quatrevieux.araknemu.data.value.Colors;
import fr.quatrevieux.araknemu.data.value.Position;
import fr.quatrevieux.araknemu.game.chat.ChannelType;
import fr.quatrevieux.araknemu.game.world.creature.characteristics.DefaultCharacteristics;
import fr.quatrevieux.araknemu.game.world.creature.characteristics.MutableCharacteristics;

import java.util.EnumSet;
import java.util.Set;

/**
 * Entity class for player
 */
final public class Player {
    final private int id;
    final private int accountId;
    final private int serverId;
    private String name;
    private Race race;
    private Sex sex;
    private Colors colors;
    private int level;
    private MutableCharacteristics stats;
    private Position position;
    private Set<ChannelType> channels;
    private int boostPoints;
    private int spellPoints;
    private int life;
    private long experience;
    private Position savedPosition;
    private long kamas;

    public Player(int id, int accountId, int serverId, String name, Race race, Sex sex, Colors colors, int level, MutableCharacteristics stats, Position position, Set<ChannelType> channels, int boostPoints, int spellPoints, int life, long experience, Position savedPosition, long kamas) {
        this.id = id;
        this.accountId = accountId;
        this.serverId = serverId;
        this.name = name;
        this.race = race;
        this.sex = sex;
        this.colors = colors;
        this.level = level;
        this.stats = stats;
        this.position = position;
        this.channels = channels;
        this.boostPoints = boostPoints;
        this.spellPoints = spellPoints;
        this.life = life;
        this.experience = experience;
        this.savedPosition = savedPosition;
        this.kamas = kamas;
    }

    public Player(int id, int accountId, int serverId, String name, Race race, Sex sex, Colors colors, int level, MutableCharacteristics characteristics) {
        this(id, accountId, serverId, name, race, sex, colors, level, characteristics, new Position(0, 0), EnumSet.noneOf(ChannelType.class), 0, 0, -1, 0, new Position(0, 0), 0);
    }

    public Player(int id) {
        this(id, 0, 0, null, null, null, null, 1, new DefaultCharacteristics());
    }

    public int id() {
        return id;
    }

    public int accountId() {
        return accountId;
    }

    public int serverId() {
        return serverId;
    }

    public String name() {
        return name;
    }

    public Race race() {
        return race;
    }

    public Sex sex() {
        return sex;
    }

    public Colors colors() {
        return colors;
    }

    public int level() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public MutableCharacteristics stats() {
        return stats;
    }

    public Position position() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public Set<ChannelType> channels() {
        return channels;
    }

    public void setChannels(Set<ChannelType> channels) {
        this.channels = channels;
    }

    public int boostPoints() {
        return boostPoints;
    }

    public void setBoostPoints(int boostPoints) {
        this.boostPoints = boostPoints;
    }

    public int spellPoints() {
        return spellPoints;
    }

    public void setSpellPoints(int spellPoints) {
        this.spellPoints = spellPoints;
    }

    public int life() {
        return life;
    }

    public void setLife(int life) {
        this.life = life;
    }

    public long experience() {
        return experience;
    }

    public void setExperience(long experience) {
        this.experience = experience;
    }

    public Position savedPosition() {
        return savedPosition;
    }

    public void setSavedPosition(Position savedPosition) {
        this.savedPosition = savedPosition;
    }

    public long kamas() {
        return kamas;
    }

    public void setKamas(long kamas) {
        this.kamas = kamas;
    }

    /**
     * Create a new player with new race
     *
     * @param newId The new race
     */
    public Player withId(int newId) {
        return new Player(
            newId,
            accountId,
            serverId,
            name,
            race,
            sex,
            colors,
            level,
            stats,
            position,
            channels,
            boostPoints,
            spellPoints,
            life,
            experience,
            savedPosition,
            kamas
        );
    }

    /**
     * Constructor for character creation
     * The player race will be set to -1
     */
    static public Player forCreation(int accountId, int serverId, String name, Race race, Sex sex, Colors colors) {
        return new Player(-1, accountId, serverId, name, race, sex, colors, 1, new DefaultCharacteristics());
    }

    /**
     * Get a player for load for entering game
     *
     * @see fr.quatrevieux.araknemu.data.living.repository.player.PlayerRepository#getForGame(Player)
     */
    static public Player forGame(int playerId, int accountId, int serverId) {
        return new Player(playerId, accountId, serverId, null, null, null, null, 1, null, null, null, 0, 0, -1, 0, null, 0);
    }
}
