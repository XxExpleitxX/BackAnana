package com.example.backAnana.Repositories;

import com.example.backAnana.Entities.Persona;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonaRepository extends BaseRepository<Persona, Long> {

    @Query("SELECT p FROM Persona p WHERE p.user.id = :userId")
    Persona findByUserId(@Param("userId") Long userId);

}
