package nl.vu.dynamicplugins.stocksactions.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SecurityHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityHandler.class);

    public String getAuthorizedUserEmailFromToken(String bearerToken) {
        String email = null;
        try {
            Algorithm algorithm = Algorithm.HMAC256("secret");
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer("micro-components-application")
                    .acceptLeeway(30)
                    .build(); //Reusable verifier instance
            DecodedJWT jwt = verifier.verify(bearerToken);
            email = jwt.getSubject();
        } catch (JWTVerificationException exception){
            LOGGER.error("Could not verify user token", exception);
        }
        return email;
    }
}
