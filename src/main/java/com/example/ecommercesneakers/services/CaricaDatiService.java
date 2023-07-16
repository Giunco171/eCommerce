package com.example.ecommercesneakers.services;

import com.example.ecommercesneakers.Exceptions.*;
import com.example.ecommercesneakers.models.Brand;
import com.example.ecommercesneakers.models.Prodotto;
import com.example.ecommercesneakers.repositories.BrandRepository;
import com.example.ecommercesneakers.repositories.ProdottoRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



@Service
@Transactional
public class CaricaDatiService {
    @Autowired
    private ProdottoRepository prodottoRepository;
    @Autowired
    private BrandRepository brandRepository;

    public Prodotto caricaProdotto(Prodotto prodotto) throws BrandNotFoundException, ProdottoAlreadyExistsException, InvalidProductException
    {
        Brand brand = prodotto.getBrand();
        if(brand == null || ! brandRepository.existsByNome(brand.getNome().toLowerCase()))
            throw new BrandNotFoundException();
        brand = brandRepository.findByNome(brand.getNome().toLowerCase());
        if(prodotto.getNome() == null || prodottoRepository.existsByNomeAndTaglia(prodotto.getNome().toLowerCase(), prodotto.getTaglia()))
            throw new ProdottoAlreadyExistsException();
        if(prodotto.getQta() < 1 || prodotto.getPrezzo()<=0.0)
            throw new InvalidProductException();

        Prodotto prodottoToLowerCase=new Prodotto(prodotto);
        prodottoToLowerCase.setBrand(brand);

        brand.getProdotti().add(prodottoToLowerCase);  //lavoriamo sulla versione managed di brand perchè altrimenti lavoreremmo sulla versione passatacci come parametro che creerebbe inconsistenza perchè all'atto della save(), per propagazione, verrebbe fatta la merge e il Brand sul DB verrebbe perso per sovrascrizione
        return prodottoRepository.save(prodottoToLowerCase);
    }

    public Brand caricaBrand(Brand brand) throws BrandNotSupportedException, BrandAlreadyExistsException
    {
        if(brand.getProdotti()!=null)   //vogliamo bloccare l'aggiunta di un brand che abbia dei prodotti correlati ad esso, prima si aggiunge il brand e poi a parte i prodotti
            if(! brand.getProdotti().isEmpty())
                throw new BrandNotSupportedException();
        if(brandRepository.existsByNome(brand.getNome().toLowerCase()))
            throw new BrandAlreadyExistsException();

        Brand brandToLowerCase=new Brand(brand);

        return brandRepository.save(brandToLowerCase);
    }

}
