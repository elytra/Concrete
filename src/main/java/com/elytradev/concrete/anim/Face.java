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

import com.google.common.collect.ImmutableList;

import net.minecraft.util.EnumFacing;

public enum Face {
	// absolute
	DOWN(EnumFacing.DOWN),
	UP(EnumFacing.UP),
	NORTH(EnumFacing.NORTH),
	SOUTH(EnumFacing.SOUTH),
	WEST(EnumFacing.WEST),
	EAST(EnumFacing.EAST),
	
	// relative
	BOTTOM(Anchor.TOP, Operation.OPPOSITE),
	TOP(Anchor.TOP, Operation.NO_CHANGE),
	
	FRONT(Anchor.FRONT, Operation.NO_CHANGE),
	BACK(Anchor.FRONT, Operation.OPPOSITE),
	LEFT(Anchor.FRONT, Operation.ROTATE_CCW),
	RIGHT(Anchor.FRONT, Operation.ROTATE_CW),
	
	// psuedo
	SIDE(NORTH, SOUTH, EAST, WEST),
	POLE(TOP, BOTTOM);
	
	public static final ImmutableList<Face> VALUES = ImmutableList.copyOf(values());
	public static final ImmutableList<Face> RELATIVE_FACES = ImmutableList.of(BOTTOM, TOP, FRONT, BACK, LEFT, RIGHT);
	public static final ImmutableList<Face> ABSOLUTE_FACES = ImmutableList.of(DOWN, UP, NORTH, SOUTH, WEST, EAST);
	public static final ImmutableList<Face> PSUEDO_FACES = ImmutableList.of(SIDE, POLE);
	
	private enum Anchor {
		FRONT,
		TOP
	}
	
	private enum Operation {
		NO_CHANGE,
		ROTATE_CW,
		ROTATE_CCW,
		OPPOSITE
	}
	
	public final Face[] realFaces;
	
	private final EnumFacing facing;
	
	private final Anchor anchor;
	private final Operation operation;
	
	private Face(EnumFacing facing) {
		this.realFaces = new Face[] { this };
		this.facing = facing;
		this.anchor = null;
		this.operation = null;
	}
	
	private Face(Face... realFaces) {
		this.realFaces = realFaces;
		this.facing = null;
		this.anchor = null;
		this.operation = null;
	}
	
	private Face(Anchor anchor, Operation operation) {
		this.realFaces = new Face[] { this };
		this.facing = null;
		this.anchor = anchor;
		this.operation = operation;
	}
	
	public EnumFacing getFacing() {
		return getFacing(EnumFacing.NORTH, EnumFacing.UP);
	}
	
	public EnumFacing getFacing(EnumFacing front, EnumFacing top) {
		if (facing != null) return facing;
		if (anchor == null) throw new IllegalArgumentException(this+" is not an absolute or relative face");
		EnumFacing anchorFace = (anchor == Anchor.FRONT ? front : top);
		switch (operation) {
			case NO_CHANGE: return anchorFace;
			case ROTATE_CW: return anchorFace.rotateAround(top.getAxis());
			case ROTATE_CCW: return anchorFace.rotateAround(top.getAxis()).rotateAround(top.getAxis()).rotateAround(top.getAxis());
			case OPPOSITE: return anchorFace.getOpposite();
			default: throw new AssertionError("missing case for "+operation);
		}
	}
	
	public boolean isAbsolute() {
		return facing != null;
	}
	
	public boolean isRelative() {
		return anchor != null;
	}
	
	public boolean isPsuedoface() {
		return realFaces.length != 1;
	}

	public static Face relativeFaceFrom(EnumFacing front, EnumFacing top, EnumFacing face) {
		for (Face f : RELATIVE_FACES) {
			if (f.getFacing(front, top) == face) return f;
		}
		throw new AssertionError("can't figure out relative face for "+front+"_"+top+" @ "+face);
	}
	
	public static Face absoluteFaceFrom(EnumFacing facing) {
		return VALUES.get(facing.ordinal());
	}
}