package com.example.ecommercesneakers.repositories;

import com.example.ecommercesneakers.models.ProdottoCarrello;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProdottoCarrelloRepository extends JpaRepository<ProdottoCarrello, Long> {
}
