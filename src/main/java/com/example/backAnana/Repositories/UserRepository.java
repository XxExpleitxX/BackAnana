package com.example.backAnana.Repositories;

import com.example.backAnana.Entities.User;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;

import static org.hibernate.jpa.HibernateHints.HINT_CACHE_MODE;

@Repository
public interface UserRepository extends BaseRepository<User, Long> {

    @QueryHints(@QueryHint(name = HINT_CACHE_MODE, value = "REFRESH"))
    User findByUsuario(String usuario);

}
