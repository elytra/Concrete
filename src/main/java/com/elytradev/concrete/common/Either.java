package com.elytradev.concrete.common;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class Either<L,R> {
    public static <L,R> Either<L,R> left(L value) {return new Left<>(value);}
    public static <L,R> Either<L,R> right(R value) {return new Right<>(value);}

    public Optional<L> getLeft() {return Optional.ofNullable(getLeftNullable());}
    public Optional<R> getRight() {return Optional.ofNullable(getRightNullable());}
    public @Nullable L getLeftNullable() {return null;}
    public @Nullable R getRightNullable() {return null;}
    public abstract boolean isLeft();
    public boolean isRight() {return !isLeft();}

    static class Left<L, R> extends Either<L, R> {
        L val;
        public Left(@Nonnull L val) {this.val = val;}
        @Override public L getLeftNullable() {return val;}
        @Override public boolean isLeft() {return true;}
    }

    static class Right<L, R> extends Either<L, R> {
        R val;
        public Right(@Nonnull R val) {this.val = val;}
        @Override public R getRightNullable() {return val;}
        @Override public boolean isLeft() {return false;}
    }

    public final <T> T map(
            Function<? super L, ? extends T> lFunc,
            Function<? super R, ? extends T> rFunc) {
        return isLeft() ? lFunc.apply(getLeftNullable()) : rFunc.apply(getRightNullable());
    }

    public <T> Either<T,R> mapLeft(Function<? super L, ? extends T> lFunc) {
        if (isLeft()) return new Left<>(lFunc.apply(getLeftNullable()));
        else          return new Right<>(getRightNullable());
    }
    public <T> Either<L,T> mapRight(Function<? super R, ? extends T> rFunc) {
        if (isLeft()) return new Left<>(getLeftNullable());
        else          return new Right<>(rFunc.apply(getRightNullable()));
    }
    public void apply(Consumer<? super L> lFunc, Consumer<? super R> rFunc) {
        if (isLeft()) lFunc.accept(getLeftNullable()); else rFunc.accept(getRightNullable());
    }
}
