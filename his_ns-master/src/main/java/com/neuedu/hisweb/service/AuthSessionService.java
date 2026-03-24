package com.neuedu.hisweb.service;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.neuedu.hisweb.entity.Customer;
import com.neuedu.hisweb.entity.User;

public interface AuthSessionService {
    String issueTokenForUser(User user);
    String issueTokenForCustomer(Customer customer);
    boolean isTokenActive(String token, DecodedJWT decodedJWT);
    void invalidateToken(String token);
    void kickUser(String kind, Integer userId);
    void bumpUserVersion(String kind, Integer userId);
}
