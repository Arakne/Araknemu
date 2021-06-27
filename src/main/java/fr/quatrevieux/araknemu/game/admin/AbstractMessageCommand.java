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
 * Copyright (c) 2017-2021 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.admin;

import fr.quatrevieux.araknemu.game.admin.exception.AdminException;
import fr.quatrevieux.araknemu.game.admin.executor.argument.handler.ConcatRestOfArgumentsHandler;
import fr.quatrevieux.araknemu.game.admin.help.CommandHelp;
import fr.quatrevieux.araknemu.network.game.out.chat.ServerMessage;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.Option;

/**
 * Command for send a server message
 */
public abstract class AbstractMessageCommand extends AbstractCommand<AbstractMessageCommand.Arguments> {
    /**
     * Configure the help page
     */
    protected abstract void configureHelp(CommandHelp.Builder builder);

    /**
     * Send the message packet
     */
    protected abstract void send(AdminPerformer performer, ServerMessage packet);

    @Override
    protected final void build(Builder builder) {
        builder
            .help(help -> {
                configureHelp(help);
                help.with("colors.enum", Colors.class);
            })
            .arguments(Arguments::new)
        ;
    }

    @Override
    public final String name() {
        return "msg";
    }

    @Override
    public final void execute(AdminPerformer performer, Arguments arguments) throws AdminException {
        final ServerMessage message = new ServerMessage(buildMessage(arguments));

        send(performer, message);
    }

    private String buildMessage(Arguments arguments) {
        String message = arguments.message;

        if (!arguments.noMarkdown) {
            message = applyMarkdown(message);
        }

        if (arguments.color != null) {
            message = "<font color=\"" + Colors.parse(arguments.color) + "\">" + message + "</font>";
        }

        return message;
    }

    private String applyMarkdown(String message) {
        return message
            .replaceAll("\\*\\*([^*]+?)\\*\\*", "<b>$1</b>")
            .replaceAll("\\*([^*]+?)\\*", "<i>$1</i>")
            .replaceAll("__([^_]+?)__", "<u>$1</u>")
            .replaceAll("_([^_]+?)_", "<i>$1</i>")
            .replaceAll("\\[(.+?)\\]\\((.+?)\\)", "<u><a href=\"$2\">$1</a></u>")
        ;
    }

    public static final class Arguments {
        @Argument(
            metaVar = "MESSAGE", handler = ConcatRestOfArgumentsHandler.class, required = true,
            usage = "The message to send.\n" +
                "If --no-md option is not set, the message will be parsed as markdown format.\n" +
                "Available formats :\n" +
                "- *message* or _message_ for <i>italic</i>\n" +
                "- **message** for <i>bold</i>\n" +
                "- __message__ for <u>underline</u>\n" +
                "- [test](http://link) for display a clickable link\n" +
                "- Multiple formats can be applied like : ___**message**___ for <i><u><b>italic + underline + bold</b></u></i>\n" +
                "<font color=\"#CC0000\">Note: The message must be the last argument, <b>after all options</b></font>"
        )
        private String message;

        @Option(
            name = "--color", metaVar = "COLOR",
            usage = "Define a color. The value can be an hexadecimal value, without #, in form RRGGBB, or one of the defined color : {{colors.enum}}."
        )
        private String color;

        @Option(name = "--no-md", usage = "Disable markdown parsing on the message")
        private boolean noMarkdown;
    }

    public static enum Colors {
        RED("C10000"),
        GREEN("009900"),
        BLUE("0066FF"),
        ORANGE("DD7700"),
        ;

        private final String color;

        Colors(String color) {
            this.color = color;
        }

        /**
         * Get the hexadecimal color from a color name, if defined
         */
        public static String parse(String color) {
            try {
                return "#" + valueOf(color.toUpperCase()).color;
            } catch (IllegalArgumentException e) {
                return "#" + color;
            }
        }
    }
}
