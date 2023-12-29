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
 * Copyright (c) 2017-2023 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.handler.chat;

import io.github.artsok.RepeatedIfExceptionsTest;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SpamCheckAttachmentTest {
    @RepeatedIfExceptionsTest
    void check() throws InterruptedException {
        SpamCheckAttachment attachment = new SpamCheckAttachment.Key(Duration.ofMillis(400), 3).initialize();

        assertTrue(attachment.check());
        assertTrue(attachment.check());
        assertTrue(attachment.check());

        assertFalse(attachment.check());
        assertFalse(attachment.check());

        Thread.sleep(600);
        assertTrue(attachment.check());
    }
}
