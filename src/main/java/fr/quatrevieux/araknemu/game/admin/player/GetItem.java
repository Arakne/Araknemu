/*
 * This file is part of Araknemu.
 *
 * Araknemu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Araknemu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Araknemu.  If not, see <https://www.gnu.org/licenses/>.
 *
 * Copyright (c) 2017-2020 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.admin.player;

import fr.quatrevieux.araknemu.common.account.Permission;
import fr.quatrevieux.araknemu.data.constant.Effect;
import fr.quatrevieux.araknemu.data.value.ItemTemplateEffectEntry;
import fr.quatrevieux.araknemu.data.world.transformer.ItemEffectsTransformer;
import fr.quatrevieux.araknemu.game.admin.AbstractCommand;
import fr.quatrevieux.araknemu.game.admin.AdminPerformer;
import fr.quatrevieux.araknemu.game.admin.exception.CommandException;
import fr.quatrevieux.araknemu.game.admin.formatter.Link;
import fr.quatrevieux.araknemu.game.item.Item;
import fr.quatrevieux.araknemu.game.item.ItemService;
import fr.quatrevieux.araknemu.game.item.effect.ItemEffect;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.util.Splitter;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.index.qual.Positive;
import org.checkerframework.checker.nullness.qual.EnsuresNonNullIf;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.dataflow.qual.Pure;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.OptionDef;
import org.kohsuke.args4j.spi.OptionHandler;
import org.kohsuke.args4j.spi.Parameters;
import org.kohsuke.args4j.spi.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Get an item for the player
 */
public final class GetItem extends AbstractCommand<GetItem.Arguments> {
    private final GamePlayer player;
    private final ItemService service;

    public GetItem(GamePlayer player, ItemService service) {
        this.player = player;
        this.service = service;
    }

    @Override
    protected void build(Builder builder) {
        builder
            .help(
                formatter -> formatter
                    .description("Add an item to the player")
                    .example("getitem 2425", "Generate a random 'Amulette du Bouftou'")
                    .example("!getitem 2425 3", "Generate 3 random 'Amulette du Bouftou', and ensure that the admin user is the target")
                    .example("@Robert getitem 39", "Add to Robert the 'Petite Amulette du Hibou'")
                    .example("getitem --max 2425", "Generate an 'Amulette du Bouftou' with max stats")
                    .example("getitem --effects 5b#1#32#0#,5c#1#32#0#,5d#1#32#0#,5e#1#32#0#,5f#1#32#0# 40", "Cheated 'Petite Epée de Boisaille'")
                    .example("getitem --effects STOLEN_WATER:1:50,STOLEN_EARTH:1:50,STOLEN_WIND:1:50,STOLEN_FIRE:1:50,STOLEN_NEUTRAL:1:50 40", "Same as above")

                    .seeAlso("/ui itemsummoner", "Show the item picker", Link.Type.EXECUTE)
            )
            .requires(Permission.MANAGE_PLAYER)
            .arguments(Arguments::new)
        ;
    }

    @Override
    public String name() {
        return "getitem";
    }

    @Override
    public void execute(AdminPerformer performer, Arguments arguments) throws CommandException {
        for (int j = 0; j < arguments.times(); ++j) {
            final Item item = arguments.hasCustomEffects()
                ? service.retrieve(arguments.itemId(), arguments.effects())
                : service.create(arguments.itemId(), arguments.max())
            ;

            player.inventory().add(item, arguments.quantity());

            performer.success("Generate {} '{}'", arguments.quantity(), item.template().name());

            if (!item.effects().isEmpty()) {
                performer.success("Effects :");

                for (ItemEffect effect : item.effects()) {
                    performer.success("\t{}", effect.toString());
                }
            }
        }
    }

    public static final class Arguments {
        @Option(name = "--max", usage = "Generate item with maximized characteristics")
        private boolean max = false;

        @Option(name = "--each", usage = "Regenerate item stats for each QUANTITY items instead of generate the same item with QUANTITY")
        private boolean each = false;

        @Option(
            name = "--effects", handler = EffectsConverter.class,
            usage = "Set the item effects\n" +
                "The effects should be a list of effects separated with comma ','. Available formats :\n" +
                "Item template format : 64#b#f#0#1d5+10,7d#b#0#0#0d0+11,9a#f#0#0#0d0+15\n" +
                "Simplified format    : INFLICT_DAMAGE_NEUTRAL:11:15,ADD_VITALITY:11,SUB_AGILITY:15\n" +
                "This option is not compatible with --max option.\n" +
                "If a range value is set for a characteristic effect, a random value will be generated"
        )
        private @Nullable List<ItemTemplateEffectEntry> effects = null;

        @Argument(
            required = true, index = 0, metaVar = "ITEM_ID",
            usage = "The id of the item to generate. It can be found using /ui itemsummoner command"
        )
        private int itemId;

        @Argument(
            index = 1, metaVar = "QUANTITY",
            usage = "The quantity of item to generate. By default all generated items will gets the same characteristics unless --each option is used."
        )
        private @Positive int quantity = 1;

        @Pure
        public boolean max() {
            return max;
        }

        public void setMax(boolean max) {
            this.max = max;
        }

        @Pure
        public boolean each() {
            return each;
        }

        public void setEach(boolean each) {
            this.each = each;
        }

        @Pure
        public @Nullable List<ItemTemplateEffectEntry> effects() {
            return effects;
        }

        @EnsuresNonNullIf(expression = "effects()", result = true)
        @SuppressWarnings("contracts.conditional.postcondition")
        public boolean hasCustomEffects() {
            return effects != null;
        }

        @Pure
        public int itemId() {
            return itemId;
        }

        @Pure
        public @Positive int times() {
            return each ? quantity : 1;
        }

        @Pure
        public @Positive int quantity() {
            return each ? 1 : quantity;
        }
    }

    public static class EffectsConverter extends OptionHandler<ItemTemplateEffectEntry> {
        public EffectsConverter(CmdLineParser parser, OptionDef option, Setter<? super ItemTemplateEffectEntry> setter) {
            super(parser, option, setter);
        }

        @Override
        public int parseArguments(Parameters params) throws CmdLineException {
            for (ItemTemplateEffectEntry effect : parseEffects(params.getParameter(0))) {
                setter.addValue(effect);
            }

            return 1;
        }

        @Override
        public String getDefaultMetaVariable() {
            return "effects,...";
        }

        private List<ItemTemplateEffectEntry> parseEffects(String value) throws CmdLineException {
            if (value.contains("#")) {
                return new ItemEffectsTransformer().unserialize(value);
            }

            final List<ItemTemplateEffectEntry> effects = new ArrayList<>();

            for (String strEffect : StringUtils.split(value, ",")) {
                final Splitter parts = new Splitter(strEffect, ':');
                final Effect effect;

                final String effectName = parts.nextPart().toUpperCase();

                try {
                    effect = Effect.valueOf(effectName);
                } catch (IllegalArgumentException e) {
                    throw new CmdLineException(owner, "Undefined effect " + effectName, e);
                }

                effects.add(
                    new ItemTemplateEffectEntry(
                        effect,
                        parts.nextNonNegativeIntOrDefault(0),
                        parts.nextNonNegativeIntOrDefault(0),
                        parts.nextNonNegativeIntOrDefault(0),
                        parts.nextPartOrDefault("")
                    )
                );
            }

            return effects;
        }
    }
}
