package fr.quatrevieux.araknemu.data.world.entity.monster;

/**
 * Store Pvm item drop by a monster
 */
final public class MonsterRewardItem {
    final private int monsterId;
    final private int itemTemplateId;
    final private int quantity;
    final private int discernment;
    final private double rate;

    public MonsterRewardItem(int monsterId, int itemTemplateId, int quantity, int discernment, double rate) {
        this.monsterId = monsterId;
        this.itemTemplateId = itemTemplateId;
        this.quantity = quantity;
        this.discernment = discernment;
        this.rate = rate;
    }

    /**
     * The monster template id (part of the primary key)
     *
     * @see MonsterTemplate#id()
     */
    public int monsterId() {
        return monsterId;
    }

    /**
     * The drop item id (part of the primary key)
     *
     * @see fr.quatrevieux.araknemu.data.world.entity.item.ItemTemplate#id()
     */
    public int itemTemplateId() {
        return itemTemplateId;
    }

    /**
     * Maximum item quantity dropped by the monster
     */
    public int quantity() {
        return quantity;
    }

    /**
     * Minimal discernment value for drop the item
     */
    public int discernment() {
        return discernment;
    }

    /**
     * The drop chance in percent
     *
     * The rate is a double value in interval ]0, 100], where :
     * - 100 means always dropped
     * - 0 means never dropped (cannot be reached)
     */
    public double rate() {
        return rate;
    }
}
