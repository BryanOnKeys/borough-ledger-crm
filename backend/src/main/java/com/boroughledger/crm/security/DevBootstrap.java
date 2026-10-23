package com.boroughledger.crm.security;

import com.boroughledger.crm.organizations.Organization;
import com.boroughledger.crm.organizations.OrganizationRepository;
import com.boroughledger.crm.users.AppUser;
import com.boroughledger.crm.users.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration @Profile({"dev", "bootstrap"})
public class DevBootstrap {
    private static final Logger log = LoggerFactory.getLogger(DevBootstrap.class);
    @Bean CommandLineRunner createFirstAdmin(Environment env, UserRepository users,
                                              OrganizationRepository organizations, PasswordEncoder encoder) {
        return args -> {
            String email = env.getProperty("CRM_BOOTSTRAP_EMAIL");
            String password = env.getProperty("CRM_BOOTSTRAP_PASSWORD");
            if (email == null || email.isBlank() || password == null || password.length() < 12) {
                log.warn("Bootstrap omitido: configura CRM_BOOTSTRAP_EMAIL y CRM_BOOTSTRAP_PASSWORD (mínimo 12 caracteres)");
                return;
            }
            if (users.count() != 0) return;
            Organization org = organizations.save(new Organization(env.getProperty("CRM_BOOTSTRAP_ORGANIZATION", "Organización principal")));
            users.save(new AppUser(org.getId(), email.trim(), encoder.encode(password), "Administrador local", "ADMIN"));
            log.info("Se creó el administrador inicial. No se muestran credenciales.");
        };
    }
}
