package io.graversen.rust.rcon.event.umod;

import io.graversen.rust.rcon.event.RustEvent;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

import javax.annotation.Nullable;

@Getter
@ToString(callSuper = true)
public class UmodBridgeDiagnosticEvent extends RustEvent {
    private final @NonNull UmodBridgeDiagnosticType diagnosticType;
    private final @NonNull String rawMessage;
    private final @Nullable String details;

    public UmodBridgeDiagnosticEvent(
            @NonNull UmodBridgeDiagnosticType diagnosticType,
            @NonNull String rawMessage,
            @Nullable String details
    ) {
        this.diagnosticType = diagnosticType;
        this.rawMessage = rawMessage;
        this.details = details;
    }
}
