package com.example.ecommercesneakers.controllers;

import com.example.ecommercesneakers.DTO.ProdottoDTO;
import com.example.ecommercesneakers.Exceptions.*;
import com.example.ecommercesneakers.models.Brand;
import com.example.ecommercesneakers.models.Prodotto;
import com.example.ecommercesneakers.services.CaricaDatiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
@RequestMapping(value= "/admin")
public class AdminController {
    @Autowired
    CaricaDatiService caricaDatiService;

    @PreAuthorize("hasAuthority('admin')")
    @PostMapping(value="prodotto")
    public ResponseEntity<String> caricaProdotto(@RequestBody ProdottoDTO prodotto)  //va sempre prima caricato il brand e poi il prodotto
    {
        Prodotto ret = null;
        try{
            Prodotto tmp = new Prodotto();
            tmp.setQta(prodotto.getQta());
            tmp.setPrezzo(prodotto.getPrezzo());
            tmp.setNome(prodotto.getNome());
            tmp.setTaglia(prodotto.getTaglia());
            tmp.setBrand(new Brand(prodotto.getNomeBrand()));
            tmp.setUrl(prodotto.getUrl());
            ret = caricaDatiService.caricaProdotto(tmp);
        }catch(BrandNotFoundException bnfe)
        {
            return new ResponseEntity<>("Brand not found.", HttpStatus.NOT_FOUND);
        }catch(ProdottoAlreadyExistsException paee){
            return new ResponseEntity<>("Prodotto already exists.", HttpStatus.NOT_ACCEPTABLE);
        }catch(InvalidProductException ipe){
            return new ResponseEntity<>("Quantity or Price out of bound.", HttpStatus.NOT_ACCEPTABLE);
        }catch(OptimisticLockingFailureException olfe)
        {
            return new ResponseEntity<>("L'operazione non è andata a buon fine", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(ret.toString(), HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('admin')")
    @PostMapping(value="brand")
    public ResponseEntity<String> caricaBrand(@RequestBody String nomeBrand)
    {
        Brand ret = null;
        try{
            Brand tmp=new Brand(nomeBrand);
            ret = caricaDatiService.caricaBrand(tmp);
        }catch(BrandNotSupportedException bnse){
            return new ResponseEntity<>("Brand not supported.", HttpStatus.BAD_REQUEST);
            /*
            Non vi possono essere casi in cui questa eccezione
            verrà sollevata perchè creiamo "tmp" su misura in
            modo che non abbia alcun prodotto correlato.
             */
        }catch(BrandAlreadyExistsException baee){
            return new ResponseEntity<>("Brand already exists.", HttpStatus.NOT_ACCEPTABLE);
        }catch(OptimisticLockingFailureException olfe)
        {
            return new ResponseEntity<>("L'operazione non è andata a buon fine", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(ret.getNome(), HttpStatus.OK);
    }


}
