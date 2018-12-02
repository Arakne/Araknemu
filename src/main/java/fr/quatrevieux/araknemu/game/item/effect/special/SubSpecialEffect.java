package fr.quatrevieux.araknemu.game.item.effect.special;

import fr.quatrevieux.araknemu.data.value.ItemTemplateEffectEntry;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.game.player.characteristic.SpecialEffects;
import fr.quatrevieux.araknemu.game.item.effect.SpecialEffect;
import fr.quatrevieux.araknemu.util.RandomUtil;

/**
 * Sub special effect to player {@link SpecialEffects}
 */
final public class SubSpecialEffect implements SpecialEffectHandler {
    final static private RandomUtil RANDOM = new RandomUtil();

    final private SpecialEffects.Type type;

    public SubSpecialEffect(SpecialEffects.Type type) {
        this.type = type;
    }

    @Override
    public void apply(SpecialEffect effect, GamePlayer player) {
        player.properties().characteristics().specials().sub(type, effect.arguments()[0]);
    }

    @Override
    public void relieve(SpecialEffect effect, GamePlayer player) {
        player.properties().characteristics().specials().add(type, effect.arguments()[0]);
    }

    @Override
    public SpecialEffect create(ItemTemplateEffectEntry entry, boolean maximize) {
        int value = maximize ? entry.min() : RANDOM.rand(entry.min(), entry.max());

        return new SpecialEffect(this, entry.effect(), new int[] {value, 0, entry.special()}, "0d0+" + value);
    }
}
