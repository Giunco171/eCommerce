package com.example.ecommercesneakers.controllers;

import com.example.ecommercesneakers.DTO.ProdottoCarrelloDTO;
import com.example.ecommercesneakers.DTO.ProdottoDTO;
import com.example.ecommercesneakers.Exceptions.*;
import com.example.ecommercesneakers.keycloak.Utils;
import com.example.ecommercesneakers.models.Carrello;
import com.example.ecommercesneakers.models.Prodotto;
import com.example.ecommercesneakers.models.ProdottoCarrello;
import com.example.ecommercesneakers.models.Utente;
import com.example.ecommercesneakers.services.AcquistoService;
import com.example.ecommercesneakers.services.CaricaInfoUtentiService;
import com.example.ecommercesneakers.services.GetDatiService;
import com.example.ecommercesneakers.services.GetInfoUtenteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


import java.util.LinkedList;
import java.util.List;
import java.util.Optional;


@RestController
@CrossOrigin("*")
@RequestMapping(value= "/carrello")
public class CarrelloController {
    @Autowired
    GetInfoUtenteService getInfoUtenteService;
    @Autowired
    GetDatiService getDatiService;
    @Autowired
    CaricaInfoUtentiService caricaInfoUtentiService;
    @Autowired
    AcquistoService acquistoService;


    @PreAuthorize("hasAuthority('utente')")
    @GetMapping
    public ResponseEntity<List<ProdottoCarrelloDTO>> getProdottiInCarrello(@RequestParam(value= "pageNumber", defaultValue="0") int pageNumber,
                                                                        @RequestParam(value= "pageSize", defaultValue="10") int pageSize,
                                                                        @RequestParam(value= "sortBy", defaultValue="prezzo") String sortBy)
    {
        Carrello carrello =null;
        try {
            Utente utente = getInfoUtenteService.getUtenteFromEmail(Utils.getEmail());
            carrello = getInfoUtenteService.getCarrelloFromUtente(utente);
        }catch(EmailNotFoundException enfe)  //non sarebbe necessario fare queste verifiche perchè l'utente lo prendiamo dal jwt, però non si sa mai
        {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }catch(InvalidUtenteException iue)
        {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND); //non si verifica mai questo caso, però non si sa mai
        }//try catch
        List<ProdottoCarrello> result = getInfoUtenteService.getAllProdottiInCarrello(pageNumber, pageSize, sortBy, carrello);

        List<ProdottoCarrelloDTO> ret =new LinkedList<>();
        for(ProdottoCarrello prod : result)
            ret.add(new ProdottoCarrelloDTO(prod));

        if(result.size()<=0)
            return new ResponseEntity<>(ret, HttpStatus.OK); //caso lista vuota, introduciamo ridondanza per un'anticipazione del cambiamento
        else
            return new ResponseEntity<>(ret, HttpStatus.OK);
    }//getProdottiInCarrello

    @PreAuthorize("hasAuthority('utente')")
    @PostMapping
    public ResponseEntity<String> addProdottoInCarrello(@RequestBody ProdottoDTO prodotto) //il ProdottoDTO ha la quantità da voler acqusitare e non quella totale!!!! LA totale sta sul DB
    {
        Carrello carrello =null;
        Prodotto prodottoDaAggiungere=null;
        try {
            Utente utente = getInfoUtenteService.getUtenteFromEmail(Utils.getEmail());
            carrello = getInfoUtenteService.getCarrelloFromUtente(utente);
            Optional<Prodotto> prodottoTMP = getDatiService.getProdottoByNomeAndTaglia(prodotto.getNome(), prodotto.getTaglia());
            if(! prodottoTMP.isPresent())
                return new ResponseEntity<>("Prodotto non presente nel negozio", HttpStatus.NOT_FOUND);
            prodottoDaAggiungere = prodottoTMP.get();
        }catch(EmailNotFoundException enfe)  //non sarebbe necessario fare queste verifiche perchè l'utente lo prendiamo dal jwt, però non si sa mai
        {
            return new ResponseEntity<>("Email non corretta ", HttpStatus.NOT_FOUND);
        }catch(InvalidUtenteException iue)
        {
            return new ResponseEntity<>("Utente non trovato", HttpStatus.NOT_FOUND); //non si verifica mai questo caso, però non si sa mai
        }catch(ProdottoNotFoundException pnfe)
        {
            return new ResponseEntity<>("Prodotto non presente nel negozio", HttpStatus.NOT_FOUND);
        }//try catch


        try{
            Prodotto ret = caricaInfoUtentiService.aggiungiProdottoAlCarrello(prodottoDaAggiungere, carrello, prodotto.getQta());
            return new ResponseEntity<>(ret.toString(), HttpStatus.OK);
        }catch(InvalidProductException ipe){
            return new ResponseEntity<>("Prodotto non valido", HttpStatus.NOT_ACCEPTABLE);
        }catch(ProdottoEsauritoException pee){
            return new ResponseEntity<>("Prodotto esaurito", HttpStatus.MOVED_PERMANENTLY);
        }catch(CarrelloNotFoundException cnfe){
            return new ResponseEntity<>("Carrello not found", HttpStatus.NOT_FOUND);
        }catch(NotEnoughtProductsException nepe){
            return new ResponseEntity<>("Selezionare una quantità minore", HttpStatus.PAYLOAD_TOO_LARGE);
        }catch(ProdottoAlreadyInCarrelloException paice)
        {
            return new ResponseEntity<>("Il prodotto è già presente nel carrello", HttpStatus.NOT_ACCEPTABLE);
        }catch(OptimisticLockingFailureException olfe)
        {
            return new ResponseEntity<>("L'operazione non è andata a buon fine", HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }//addProdottoInCarrello

    @PreAuthorize("hasAuthority('utente')")
    @PostMapping("/remove")  //prima era delete, ma da angular è più comodo usare il post
    public ResponseEntity<String> removeProdottoInCarrello(@RequestBody ProdottoDTO prodotto) //il ProdottoDTO ha la quantità da voler rimuovere
    {
        Carrello carrello =null;
        ProdottoCarrello prodottoDaRimuovere=null;
        try {
            Utente utente = getInfoUtenteService.getUtenteFromEmail(Utils.getEmail());
            carrello = getInfoUtenteService.getCarrelloFromUtente(utente);
            Optional<ProdottoCarrello> prodottoTMP = getInfoUtenteService.getProdottoFromCarrello(prodotto.getNome(), prodotto.getTaglia(), carrello);
            if(! prodottoTMP.isPresent())
                return new ResponseEntity<>("Prodotto non presente nel carrello", HttpStatus.NOT_FOUND);
            prodottoDaRimuovere = prodottoTMP.get();
        }catch(EmailNotFoundException enfe)  //non sarebbe necessario fare queste verifiche perchè l'utente lo prendiamo dal jwt, però non si sa mai
        {
            return new ResponseEntity<>("Email non corretta ", HttpStatus.NOT_FOUND);
        }catch(InvalidUtenteException iue)
        {
            return new ResponseEntity<>("Utente non trovato", HttpStatus.NOT_FOUND); //non si verifica mai questo caso, però non si sa mai
        }catch(InvalidProductException ipe)
        {
            return new ResponseEntity<>("Prodotto non presente nel carrello", HttpStatus.NOT_FOUND);
        }catch(CarrelloNotFoundException cnfe)
        {
            return new ResponseEntity<>("Carrello non trovato", HttpStatus.NOT_FOUND);
        }catch(OptimisticLockingFailureException olfe)
        {
            return new ResponseEntity<>("L'operazione non è andata a buon fine", HttpStatus.INTERNAL_SERVER_ERROR);
        }//try catch

        try{
            Carrello ret = caricaInfoUtentiService.rimuoviProdottoDalCarrello(prodottoDaRimuovere, carrello, prodotto.getQta());
            return new ResponseEntity<>("Carrello: "+ret.toString(), HttpStatus.OK);
        }catch(ProdottoNotFoundException pnfe){
            return new ResponseEntity<>("Il prodotto è già stato eliminato", HttpStatus.NOT_FOUND);
        }catch(CarrelloNotFoundException cnfe){
            return new ResponseEntity<>("Carrello non trovato", HttpStatus.NOT_FOUND);
        }catch(InvalidProductException ipe){
            return new ResponseEntity<>("Prodotto non presente nel carrello", HttpStatus.NOT_FOUND);
        }catch(OptimisticLockingFailureException olfe)
        {
            return new ResponseEntity<>("L'operazione non è andata a buon fine", HttpStatus.INTERNAL_SERVER_ERROR);
        }//try catch
    }//removeProdottoInCarrello

    @PreAuthorize("hasAuthority('utente')")
    @PostMapping(value="/acquista")
    public ResponseEntity<String> acquista(@RequestBody List<ProdottoDTO> listaProdotti)
    {
        try{
            Utente utente = getInfoUtenteService.getUtenteFromEmail(Utils.getEmail());
            Carrello carrello = getInfoUtenteService.getCarrelloFromUtente(utente);
            boolean esito = acquistoService.acquista(carrello, utente, listaProdotti);
            acquistoService.pulisciCarrello(carrello);
            if(esito)
                return new ResponseEntity<>("Acquisto andato a buon fine. Riepilogo acquisto: "+listaProdotti, HttpStatus.OK);
        }catch(EmailNotFoundException enfe)
        {
            return new ResponseEntity<>("Email non corretta ", HttpStatus.NOT_FOUND);
        }catch(InvalidUtenteException iue)
        {
            return new ResponseEntity<>("Utente non trovato", HttpStatus.NOT_FOUND); //non si verifica mai questo caso, però non si sa mai
        }catch(CarrelloNotFoundException cnfe)
        {
            return new ResponseEntity<>("I prodotti selezionati non appartengono al carrello utente.", HttpStatus.NOT_FOUND);
        }catch(ProdottoEsauritoException pee){
            return new ResponseEntity<>(pee.getMessage(), HttpStatus.NOT_FOUND);
        }catch(PrezzoCambiatoException pce){
            return new ResponseEntity<>(pce.getMessage(), HttpStatus.NOT_FOUND);
        }catch(NotEnoughtProductsException nep){
            return new ResponseEntity<>("Non ci sono abbastanza articoli di tipo: "+nep.getMessage(), HttpStatus.NOT_FOUND);
        }catch(OptimisticLockingFailureException olfe)
        {
            return new ResponseEntity<>("L'operazione non è andata a buon fine", HttpStatus.INTERNAL_SERVER_ERROR);
        }//try catch
    return new ResponseEntity<>("Acquisto dei prodotti: "+listaProdotti+" \n fallito. Riprovare", HttpStatus.PERMANENT_REDIRECT);
    }//acquista
}
