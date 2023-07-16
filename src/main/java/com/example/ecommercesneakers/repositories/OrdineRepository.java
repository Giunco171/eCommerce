package com.example.ecommercesneakers.repositories;

import com.example.ecommercesneakers.models.Ordine;
import com.example.ecommercesneakers.models.Utente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrdineRepository extends JpaRepository<Ordine, Long> {

    Optional<Ordine> findByUtente(Utente utente);

    List<Ordine> findAllByUtente(Utente utente);
}
