package org.ec;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "test_jwt")
public class JwtDB extends PanacheEntity {
    @Column(columnDefinition = "text")
    public String jwtToken;

    public static void addJwt(String jwtToken) {
        JwtDB jwtDB = new JwtDB();

        jwtDB.jwtToken = jwtToken;
        jwtDB.persist();
    }

    public static JwtDB getJwt(String jwtToken) {
        return find("jwtToken", jwtToken).firstResult();
    }

    public static void deleteJwt(String jwtToken) {
        delete("jwtToken", jwtToken);
    }
}
