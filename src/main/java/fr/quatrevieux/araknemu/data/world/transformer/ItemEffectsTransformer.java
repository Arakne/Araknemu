package fr.quatrevieux.araknemu.data.world.transformer;

import fr.quatrevieux.araknemu.data.constant.Effect;
import fr.quatrevieux.araknemu.data.transformer.Transformer;
import fr.quatrevieux.araknemu.data.value.ItemTemplateEffectEntry;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Transform item effects
 */
final public class ItemEffectsTransformer implements Transformer<List<ItemTemplateEffectEntry>> {
    @Override
    public String serialize(List<ItemTemplateEffectEntry> value) {
        if (value == null || value.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder(value.size() * 8);

        for (ItemTemplateEffectEntry entry : value) {
            if (sb.length() > 0) {
                sb.append(',');
            }

            sb
                .append(Integer.toString(entry.effect().id(), 16)).append('#')
                .append(Integer.toString(entry.min(), 16)).append('#')
                .append(Integer.toString(entry.max(), 16)).append('#')
                .append(Integer.toString(entry.special(), 16)).append('#')
                .append(entry.text())
            ;
        }

        return sb.toString();
    }

    @Override
    public List<ItemTemplateEffectEntry> unserialize(String serialize) {
        if (serialize == null || serialize.isEmpty()) {
            return new ArrayList<>();
        }

        List<ItemTemplateEffectEntry> effects = new ArrayList<>();

        for (String part : StringUtils.split(serialize, ",")) {
            String[] args = StringUtils.split(part, "#", 5);

            if (args.length < 4) {
                throw new IllegalArgumentException("Cannot unserialize effect " + serialize);
            }

            effects.add(
                new ItemTemplateEffectEntry(
                    Effect.byId(Integer.parseInt(args[0], 16)),
                    Integer.parseInt(args[1], 16),
                    Integer.parseInt(args[2], 16),
                    Integer.parseInt(args[3], 16),
                    args.length > 4 ? args[4] : ""
                )
            );
        }

        return effects;
    }
}
