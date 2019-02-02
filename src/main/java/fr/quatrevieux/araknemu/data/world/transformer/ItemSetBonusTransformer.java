package fr.quatrevieux.araknemu.data.world.transformer;

import fr.quatrevieux.araknemu.data.constant.Effect;
import fr.quatrevieux.araknemu.data.transformer.Transformer;
import fr.quatrevieux.araknemu.data.transformer.TransformerException;
import fr.quatrevieux.araknemu.data.value.ItemTemplateEffectEntry;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Transformer for item set bonus effects
 */
final public class ItemSetBonusTransformer implements Transformer<List<List<ItemTemplateEffectEntry>>> {
    @Override
    public String serialize(List<List<ItemTemplateEffectEntry>> value) {
        return value.stream()
            .map(
                effects -> effects
                    .stream()
                    .map(effect -> effect.effect().id() + ":" + effect.min())
                    .collect(Collectors.joining(","))
            )
            .collect(Collectors.joining(";"))
        ;
    }

    @Override
    public List<List<ItemTemplateEffectEntry>> unserialize(String serialize) {
        if (serialize == null || serialize.isEmpty()) {
            return Collections.emptyList();
        }

        List<List<ItemTemplateEffectEntry>> bonus = new ArrayList<>();

        try {
            for (String effects : StringUtils.splitByWholeSeparatorPreserveAllTokens(serialize, ";")) {
                if (effects.isEmpty()) {
                    bonus.add(Collections.emptyList());
                    continue;
                }

                String[] aEffects = StringUtils.split(effects, ',');

                if (aEffects.length == 1) {
                    bonus.add(Collections.singletonList(parseEffect(aEffects[0])));
                    continue;
                }

                bonus.add(
                    Arrays.stream(aEffects)
                        .map(this::parseEffect)
                        .collect(Collectors.toList())
                );
            }
        } catch (RuntimeException e) {
            throw new TransformerException("Cannot parse item set bonus '" + serialize + "'", e);
        }

        return bonus;
    }

    private ItemTemplateEffectEntry parseEffect(String effect) {
        String[] parts = StringUtils.split(effect, ":", 2);

        return new ItemTemplateEffectEntry(
            Effect.byId(Integer.parseInt(parts[0])),
            Integer.parseInt(parts[1]),
            0, 0, ""
        );
    }
}
