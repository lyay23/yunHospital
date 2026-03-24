package com.neuedu.hisweb.security;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class RoleMapper {
    private RoleMapper() {}

    public static Set<String> rolesForUserType(Integer userType) {
        if (userType == null) {
            return Collections.emptySet();
        }
        Set<String> roles = new HashSet<>();
        switch (userType) {
            case 1 -> roles.add("ROLE_ADMIN");
            case 2 -> roles.add("ROLE_REGISTRAR");
            case 3, 4 -> roles.add("ROLE_DOCTOR");
            case 5 -> roles.add("ROLE_PHARMACIST");
            case 6 -> roles.add("ROLE_FINANCE");
            default -> {}
        }
        return roles;
    }

    public static Set<String> rolesForCustomer() {
        Set<String> roles = new HashSet<>();
        roles.add("ROLE_CUSTOMER");
        return roles;
    }
}
