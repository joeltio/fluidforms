package io.joelt.texttemplate.models

sealed class Either<L, R> private constructor(left: L?, right: R?) {
    class Left<L, R>(val value: L): Either<L, R>(value, null)
    class Right<L, R>(val value: R): Either<L, R>(null, value)
}