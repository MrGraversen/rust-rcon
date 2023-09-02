package io.graversen.rust.rcon.event.server;

import io.graversen.rust.rcon.RustServer;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

import javax.annotation.Nullable;

@Getter
@ToString(callSuper = true)
public class ItemDisappearedEvent extends ServerEvent {
    private final @NonNull ItemDisappearTypes itemDisappearType;
    private final @Nullable String description;

    public ItemDisappearedEvent(
            @NonNull RustServer server,
            @NonNull ItemDisappearTypes itemDisappearType,
            @Nullable String description
    ) {
        super(server);
        this.itemDisappearType = itemDisappearType;
        this.description = description;
    }
}
