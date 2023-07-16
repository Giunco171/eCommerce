package com.example.ecommercesneakers.repositories;

import com.example.ecommercesneakers.models.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Long> {

    boolean existsByNome(String nome); //se è package come è possibile che venga richiamato dal service? Perchè questa è solo l'interfaccia, poi l'implementazione fatta tramite IoC sarà public

    Brand findByNome(String nome);
}
