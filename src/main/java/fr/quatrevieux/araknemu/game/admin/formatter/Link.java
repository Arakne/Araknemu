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

package fr.quatrevieux.araknemu.game.admin.formatter;

/**
 * Build a link for the console output
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/graphics/gapi/ui/Debug.as#L245
 */
final public class Link {
    public enum Type {
        WRITE {
            @Override
            public void configure(Link link, String target) {
                link.command(target);
            }
        },
        EXECUTE {
            @Override
            public void configure(Link link, String target) {
                link.execute(target);
            }
        },
        HELP {
            @Override
            public void configure(Link link, String target) {
                link.execute("help " + target);
            }
        },
        PLAYER {
            @Override
            public void configure(Link link, String target) {
                link.playerMenu(target);
            }
        };

        /**
         * Create a link to the given target, and use this target as inner text
         *
         * Ex: {@code Link.Type.PLAYER.create("Bob")} will create a link for open player menu of "Bob" player with text "Bob"
         */
        final public Link create(String target) {
            final Link link = new Link().text(target);

            configure(link, target);

            return link;
        }

        abstract protected void configure(Link link, String target);
    }

    private String text;
    private String[] arguments;

    /**
     * Define the link text
     */
    public Link text(String text) {
        this.text = text;

        return this;
    }

    /**
     * Define the link arguments
     */
    public Link arguments(String... arguments) {
        this.arguments = arguments;

        return this;
    }

    /**
     * Write the command to the console
     *
     * @param command The command line
     */
    public Link command(String command) {
        return command(command, false);
    }

    /**
     * Execute the command on the console
     *
     * @param command The command line
     */
    public Link execute(String command) {
        return command(command, true);
    }

    /**
     * Command link
     *
     * @param command The command line
     * @param execute True to immediately execute the command on click
     */
    public Link command(String command, boolean execute) {
        return arguments("ExecCmd", command, execute ? "true" : "false");
    }

    /**
     * Show the player popup menu
     * https://github.com/Emudofus/Dofus/blob/1.29/dofus/managers/GameManager.as#L803
     *
     * @param name The player name
     */
    public Link playerMenu(String name) {
        return arguments("ShowPlayerPopupMenu", name);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("<u><a href='asfunction:onHref");

        for (String argument : arguments) {
            sb.append(',').append(argument.replace(",", "%2C"));
        }

        sb.append("'>").append(text).append("</a></u>");

        return sb.toString();
    }
}
