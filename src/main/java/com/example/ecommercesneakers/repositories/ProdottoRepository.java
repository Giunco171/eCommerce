package com.example.ecommercesneakers.repositories;

import com.example.ecommercesneakers.models.Brand;
import com.example.ecommercesneakers.models.Prodotto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProdottoRepository extends JpaRepository<Prodotto, Long> {

    boolean existsByNomeAndTaglia(String nome, int taglia);

    Optional<Prodotto> findProdottoByNomeAndTaglia(String nome, int taglia);

    @Query(value = "SELECT p FROM Prodotto p " +
            "WHERE (:nome IS NULL OR p.nome LIKE %:nome%) " +
            "AND (:prezzoMax IS NULL OR p.prezzo <= :prezzoMax) " +
            "AND (:prezzoMin IS NULL OR p.prezzo >= :prezzoMin) " +
            "AND (:brand IS NULL OR p.brand.nome LIKE %:brand%)" +
            "AND (:taglia = -1 OR p.taglia = :taglia)",
            countQuery = "SELECT COUNT(*) FROM Prodotto p " +
                    "WHERE (:nome IS NULL OR p.nome LIKE %:nome%) " +
                    "AND (:prezzoMax IS NULL OR p.prezzo <= :prezzoMax) " +
                    "AND (:prezzoMin IS NULL OR p.prezzo >= :prezzoMin) " +
                    "AND (:brand IS NULL OR p.brand.nome LIKE %:brand%)" +
                    "AND (:taglia = -1 OR p.taglia = :taglia)")  //perchè di default la taglia è -1
    Page<Prodotto> ricercaAvanzataPaginata(Pageable pageable,
                                           @Param("nome") String nome,
                                           @Param("prezzoMax") double prezzoMax,
                                           @Param("prezzoMin") double prezzoMin,
                                           @Param("brand") String brand,
                                           @Param("taglia") int taglia);       //Query avanzata paginata
}
