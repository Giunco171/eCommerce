package com.example.ecommercesneakers.repositories;

import com.example.ecommercesneakers.models.Carrello;
import com.example.ecommercesneakers.models.Utente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CarrelloRepository extends JpaRepository<Carrello, Long> {

    Optional<Carrello> findByUtente(Utente utente);
}
