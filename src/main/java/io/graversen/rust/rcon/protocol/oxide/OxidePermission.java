package io.graversen.rust.rcon.protocol.oxide;

import lombok.NonNull;

public interface OxidePermission {
    OxidePermissionTypes permissionType();

    String name();

    String permission();

    default OxidePermission user(@NonNull String name, @NonNull String permission) {
        return new OxidePermission() {
            @Override
            public OxidePermissionTypes permissionType() {
                return OxidePermissionTypes.USER;
            }

            @Override
            public String name() {
                return name;
            }

            @Override
            public String permission() {
                return permission;
            }
        };
    }
}
