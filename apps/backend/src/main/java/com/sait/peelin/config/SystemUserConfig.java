// Contributor(s): Robbie
// Main: Robbie - Well-known UUID for system-owned records and automated actions.

package com.sait.peelin.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;

/**
 * Exposes {@link #SYSTEM_USER_ID} as a Spring bean for services that attribute work to the system user.
 */
@Configuration
public class SystemUserConfig {

    public static final UUID SYSTEM_USER_ID =
            UUID.fromString("00000000-0000-0000-0000-000000000001");

    @Bean(name = "systemUserId")
    public UUID systemUserId() {
        return SYSTEM_USER_ID;
    }
}
