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

package com.elytradev.concrete.common;

import java.util.List;
import java.util.Locale;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;

public enum EnumAnyRotation implements IStringSerializable {
	NORTH_UP,
	NORTH_EAST,
	NORTH_WEST,
	NORTH_DOWN,
	
	EAST_UP,
	EAST_NORTH,
	EAST_SOUTH,
	EAST_DOWN,
	
	SOUTH_UP,
	SOUTH_WEST,
	SOUTH_EAST,
	SOUTH_DOWN,
	
	WEST_UP,
	WEST_SOUTH,
	WEST_NORTH,
	WEST_DOWN,
	
	UP_NORTH,
	UP_EAST,
	UP_SOUTH,
	UP_WEST,
	
	DOWN_NORTH,
	DOWN_EAST,
	DOWN_SOUTH,
	DOWN_WEST,
	;
	public static final ImmutableList<EnumAnyRotation> VALUES = ImmutableList.copyOf(values());
	
	private final EnumFacing front;
	private final EnumFacing top;
	private final String lowerName;
	
	private EnumAnyRotation() {
		List<String> split = Splitter.on('_').splitToList(name());
		front = EnumFacing.valueOf(split.get(0));
		top = EnumFacing.valueOf(split.get(1));
		lowerName = name().toLowerCase(Locale.ROOT);
	}
	
	@Override
	public String getName() {
		return lowerName;
	}
	
	public EnumFacing getFront() {
		return front;
	}
	
	public EnumFacing getTop() {
		return top;
	}
	
	public static EnumAnyRotation fromFrontTop(EnumFacing front, EnumFacing top) {
		for (EnumAnyRotation ear : VALUES) {
			if (ear.front == front && ear.top == top) return ear;
		}
		throw new IllegalArgumentException(front+"_"+top+" is not a legal facing");
	}
	
	public static EnumAnyRotation fromDispenserFacing(EnumFacing facing) {
		switch (facing) {
			case EAST:
				return EAST_UP;
			case NORTH:
				return NORTH_UP;
			case SOUTH:
				return SOUTH_UP;
			case WEST:
				return WEST_UP;
			case UP:
				return UP_NORTH;
			case DOWN:
				return DOWN_SOUTH;
			default:
				throw new AssertionError("missing case for "+facing);
		}
	}
	
	public static EnumAnyRotation fromHorizontalFacing(EnumFacing facing) {
		switch (facing) {
			case EAST:
				return EAST_UP;
			case NORTH:
				return NORTH_UP;
			case SOUTH:
				return SOUTH_UP;
			case WEST:
				return WEST_UP;
			default:
				throw new IllegalArgumentException(facing+" is not a valid horizontal facing");
		}
	}
}
