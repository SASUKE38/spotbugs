/*
 * FindBugs - Find Bugs in Java programs
 * Copyright (C) 2003-2008 University of Maryland
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package edu.umd.cs.findbugs.io;

import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author pugh
 */
class IOTest {

    Random r = new Random();

    private byte[] randomBytes(int size) {
        byte[] result = new byte[size];
        r.nextBytes(result);
        return result;
    }

    @Test
    void testSkipBytesWithNegativeNumber() throws IOException {
        byte[] input = randomBytes(500);
        Assertions.assertThrows(IllegalArgumentException.class, () -> IO.skipFully(new ByteArrayInputStream(input), r.nextInt(400) * -1));
    }

    @Test
    void testSkipBytesWithZeroSkipped() throws IOException {
        byte[] input = { 1, 2, 3 };
        InputStream stream = new ByteArrayInputStream(input);
        IO.skipFully(stream, 0);
        Assertions.assertEquals(1, stream.read());
    }

    @Test
    void testSkipBytesWithSkipTargetEqualtoArrayLength() throws IOException {
        byte[] input = randomBytes(500);
        InputStream stream = new ByteArrayInputStream(input);
        IO.skipFully(stream, 500);
        Assertions.assertThrows(EOFException.class, () -> IO.skipFully(stream, 1));
    }

    @Test
    void testSkipBytesWithValidNumber() throws IOException {
        byte[] input = { 5, 6, 2, 4, 9, 10, 100, 43 };
        ByteArrayInputStream stream = new ByteArrayInputStream(input);
        System.out.println(input);
        IO.skipFully(stream, input.length - 1);
        Assertions.assertEquals(input[input.length - 1], stream.read());
    }

    @Test
    void testSkipBytesWithChainCalls() throws IOException {
        byte[] input = { 5, 6, 2, 4, 9, 10, 100, 43 };
        ByteArrayInputStream stream = new ByteArrayInputStream(input);
        System.out.println(input);
        IO.skipFully(stream, 4);
        IO.skipFully(stream, 2);
        Assertions.assertEquals(100, stream.read());
    }

    @Test
    void testSkipBytesWithTooBigNumber() throws IOException {
        byte[] input = randomBytes(500);
        Assertions.assertThrows(EOFException.class, () -> IO.skipFully(new ByteArrayInputStream(input), r.nextInt(600, 900)));
    }

    @Test
    void testSkipBytesWithEmptyArray() throws IOException {
        byte[] input = new byte[0];
        Assertions.assertThrows(EOFException.class, () -> IO.skipFully(new ByteArrayInputStream(input), r.nextInt(900)));
    }

    @Test
    void testSkipBytesWithExhaustedArray() throws IOException {
        byte[] input = randomBytes(40);
        InputStream stream = new ByteArrayInputStream(input);
        IO.skipFully(stream, 40);
        Assertions.assertThrows(EOFException.class, () -> IO.skipFully(stream, 1));
    }

    @Test
    void testReadAllWithCorrectSize() throws IOException {

        for (int i = 10; i <= 10000; i *= 10) {
            byte[] input = randomBytes(i);
            byte[] output = IO.readAll(new ByteArrayInputStream(input), i);
            Assertions.assertArrayEquals(input, output);
        }
    }

    @Test
    void testReadAllWithSmallSize() throws IOException {

        for (int i = 10; i <= 10000; i *= 10) {
            byte[] input = randomBytes(i);
            byte[] output = IO.readAll(new ByteArrayInputStream(input), i - 9);
            Assertions.assertArrayEquals(input, output);
        }
    }

    @Test
    void testReadAllWithLargeSize() throws IOException {

        for (int i = 10; i <= 10000; i *= 10) {
            byte[] input = randomBytes(i);
            byte[] output = IO.readAll(new ByteArrayInputStream(input), i + 29);
            Assertions.assertArrayEquals(input, output);
        }
    }

    @Test
    void testReadAllWithoutSize() throws IOException {

        for (int i = 10; i <= 10000; i *= 10) {
            byte[] input = randomBytes(i);
            byte[] output = IO.readAll(new ByteArrayInputStream(input));
            Assertions.assertArrayEquals(input, output);
        }
    }
}
