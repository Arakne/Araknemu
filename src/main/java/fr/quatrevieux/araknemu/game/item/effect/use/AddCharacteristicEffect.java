package fr.quatrevieux.araknemu.game.item.effect.use;

import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.item.effect.UseEffect;
import fr.quatrevieux.araknemu.network.game.out.info.Information;
import fr.quatrevieux.araknemu.util.RandomUtil;

/**
 * Effect handler for add characteristic
 */
final public class AddCharacteristicEffect implements UseEffectHandler {
    final private Characteristic characteristic;

    final private RandomUtil random = new RandomUtil();

    public AddCharacteristicEffect(Characteristic characteristic) {
        this.characteristic = characteristic;
    }

    @Override
    public void apply(UseEffect effect, ExplorationPlayer caster) {
        int value = random.rand(effect.arguments());

        caster.player().properties().characteristics().base().add(characteristic, value);

        Information info = Information.characteristicBoosted(characteristic, value);

        if (info != null) {
            caster.send(Information.characteristicBoosted(characteristic, value));
        }
    }

    @Override
    public void applyToTarget(UseEffect effect, ExplorationPlayer caster, ExplorationPlayer target, int cell) {}

    @Override
    public boolean check(UseEffect effect, ExplorationPlayer caster) {
        return true;
    }

    @Override
    public boolean checkTarget(UseEffect effect, ExplorationPlayer caster, ExplorationPlayer target, int cell) {
        return false;
    }
}
