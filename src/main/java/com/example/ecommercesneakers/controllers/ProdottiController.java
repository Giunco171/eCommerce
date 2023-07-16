package com.example.ecommercesneakers.controllers;

import com.example.ecommercesneakers.DTO.ProdottoDTO;
import com.example.ecommercesneakers.models.Prodotto;
import com.example.ecommercesneakers.services.GetDatiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;


@RestController
@CrossOrigin("*")
@RequestMapping(value= "/prodotti")
public class ProdottiController {

    @Autowired
    GetDatiService getDatiService;

    @GetMapping("/prova")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<List<Prodotto>> getProdottiProva(){
        List<Prodotto> a = getDatiService.getProdottiAll();
        return new ResponseEntity<>(a, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<ProdottoDTO>> getAllProdotti(@RequestParam(value= "pageNumber", defaultValue="0") int pageNumber,
                                                   @RequestParam(value= "pageSize", defaultValue="10") int pageSize,
                                                   @RequestParam(value= "sortBy", defaultValue="id") String sortBy)
    {
        List<Prodotto> result= getDatiService.getAllProdottiPaginati(pageNumber, pageSize, sortBy);
        List<ProdottoDTO> ret =new LinkedList<>();
        for(Prodotto prod : result)
            ret.add(new ProdottoDTO(prod));

        if(ret.size()<=0)
            return new ResponseEntity<>(ret, HttpStatus.OK); //caso lista vuota, introduciamo ridondanza per un'anticipazione del cambiamento
        else
            return new ResponseEntity<>(ret, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProdottoDTO> getProdottoById(@PathVariable("id") long id)
    {
       Optional<Prodotto> ret = getDatiService.getProdottoById(id);
       if(ret.isPresent())
           return new ResponseEntity<>(new ProdottoDTO(ret.get()), HttpStatus.OK);
       else
           return new ResponseEntity<>(null, HttpStatus.OK);
    }

    @GetMapping("/filtrati")
    public ResponseEntity<List<ProdottoDTO>> getProdottiFiltrati(@RequestParam(value= "pageNumber", defaultValue="0") int pageNumber,
                                                              @RequestParam(value= "pageSize", defaultValue="10") int pageSize,
                                                              @RequestParam(value= "sortBy", defaultValue="prezzo") String sortBy,
                                                              @RequestParam(value= "nome", defaultValue="") String nome,
                                                              @RequestParam(value= "prezzoMax", defaultValue="5000") double prezzoMax,
                                                              @RequestParam(value= "prezzoMin", defaultValue="0") double prezzoMin,
                                                              @RequestParam(value= "brand", defaultValue="") String brand,
                                                              @RequestParam(value= "taglia", defaultValue="-1") int taglia)
    {
        List<Prodotto> result= getDatiService.getProdottiFiltrati(pageNumber, pageSize, sortBy, nome, prezzoMax, prezzoMin, brand, taglia);
        List<ProdottoDTO> ret =new LinkedList<>();
        for(Prodotto prod : result)
            ret.add(new ProdottoDTO(prod));

        if(ret.size()<=0)
            return new ResponseEntity<>(ret, HttpStatus.OK); //caso lista vuota, introduciamo ridondanza per un'anticipazione del cambiamento
        else
            return new ResponseEntity<>(ret, HttpStatus.OK);
    }

}
