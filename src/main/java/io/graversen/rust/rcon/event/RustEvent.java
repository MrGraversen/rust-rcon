package io.graversen.rust.rcon.event;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class RustEvent extends Event {

}
