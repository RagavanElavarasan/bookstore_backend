import java.util.Base64;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

public class KeyGenerator {
    public static void main(String[] args) {
        byte[] keyBytes = Keys.secretKeyFor(SignatureAlgorithm.HS512).getEncoded();
        System.out.println(Base64.getEncoder().encodeToString(keyBytes));
    }
}