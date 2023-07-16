package com.example.ecommercesneakers.services;

import com.example.ecommercesneakers.Exceptions.ProdottoNotFoundException;
import com.example.ecommercesneakers.models.Prodotto;
import com.example.ecommercesneakers.repositories.BrandRepository;
import com.example.ecommercesneakers.repositories.ProdottoRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;


@Service
@Transactional
public class GetDatiService {
    @Autowired
    private ProdottoRepository prodottoRepository;
    @Autowired
    private BrandRepository brandRepository;


    @Transactional(readOnly=true)
    public List<Prodotto> getAllProdottiPaginati(int pageNumber, int pageSize, String sortBy)
    {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy.toLowerCase()));
        Page<Prodotto> pages= prodottoRepository.findAll(pageable);
        if( pages.hasContent())
            return pages.getContent();
        else
            return new LinkedList<>();
    }

    @Transactional(readOnly=true)
    public List<Prodotto> getProdottiAll()
    {
        return prodottoRepository.findAll();
    }

    @Transactional(readOnly=true)
    public Optional<Prodotto> getProdottoById(long id)
    {
        return prodottoRepository.findById(id);
    }

    @Transactional(readOnly=true)
    public Optional<Prodotto> getProdottoByNomeAndTaglia(String nome, int taglia) throws ProdottoNotFoundException
    {
        if(nome==null)
            throw new ProdottoNotFoundException();
        return prodottoRepository.findProdottoByNomeAndTaglia(nome.toLowerCase(), taglia);
    }

    @Transactional(readOnly=true)
    public List<Prodotto> getProdottiFiltrati(int pageNumber, int pageSize, String sortBy,
                                                String nome, double prezzoMax, double prezzoMin,
                                              String brand, int taglia) //brand lo passiamo come String per comodità nel Controller
    {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy.toLowerCase()));
        Page<Prodotto> pages= prodottoRepository.ricercaAvanzataPaginata(pageable, nome.toLowerCase(), prezzoMax, prezzoMin, brand.toLowerCase(), taglia);
        if( pages.hasContent())
            return pages.getContent();
        else
            return new LinkedList<>();
    }
}
