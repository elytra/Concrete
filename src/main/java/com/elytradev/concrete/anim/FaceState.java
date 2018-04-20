/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2018:
 * 	Una Thompson (unascribed),
 * 	Isaac Ellingson (Falkreon),
 * 	Jamie Mansfield (jamierocks),
 * 	Alex Ponebshek (capitalthree),
 * 	and contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.elytradev.concrete.anim;

import java.util.Locale;
import java.util.Map;

import javax.annotation.Nonnull;

import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import com.google.common.base.Joiner.MapJoiner;
import com.google.common.collect.ImmutableMap;

public final class FaceState {
	private static final MapJoiner COMMA_EQUALS_JOINER = Joiner.on(',').withKeyValueSeparator('=');
	
	public final ImmutableMap<String, String> blockstate;
	public final Face face;
	
	public FaceState(ImmutableMap<String, String> blockstate, Face face) {
		if (blockstate == null) throw new IllegalArgumentException("blockstate cannot be null");
		if (face == null) throw new IllegalArgumentException("face cannot be null");
		if (face.isPsuedoface()) throw new IllegalArgumentException("face cannot be a psuedoface");
		this.blockstate = blockstate;
		this.face = face;
	}

	@Override
	public String toString() {
		return "["+COMMA_EQUALS_JOINER.join(blockstate)+"]#"+face.name().toLowerCase(Locale.ROOT);
	}
	
	@Override
	public int hashCode() {
		return face.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj == null) return false;
		if (!(obj instanceof FaceState)) return false;
		return isEquivalent((FaceState)obj);
	}

	public boolean isEquivalent(@Nonnull FaceState that) {
		if (this == that) {
			return true;
		}
		for (Map.Entry<String, String> en : this.blockstate.entrySet()) {
			if ("*".equals(en.getValue())) continue;
			String otherValue = that.blockstate.get(en.getKey());
			if ("*".equals(otherValue)) continue;
			if (!Objects.equal(en.getValue(), otherValue)) return false;
		}
		if (face != that.face) {
			return false;
		}
		return true;
	}
	
	
	
}
 