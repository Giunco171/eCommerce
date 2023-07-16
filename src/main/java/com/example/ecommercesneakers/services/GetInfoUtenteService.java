package com.example.ecommercesneakers.services;

import com.example.ecommercesneakers.Exceptions.CarrelloNotFoundException;
import com.example.ecommercesneakers.Exceptions.EmailNotFoundException;
import com.example.ecommercesneakers.Exceptions.InvalidProductException;
import com.example.ecommercesneakers.Exceptions.InvalidUtenteException;
import com.example.ecommercesneakers.models.*;
import com.example.ecommercesneakers.repositories.CarrelloRepository;
import com.example.ecommercesneakers.repositories.OrdineRepository;
import com.example.ecommercesneakers.repositories.ProdottoCarrelloRepository;
import com.example.ecommercesneakers.repositories.UtenteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class GetInfoUtenteService {
    @Autowired
    private UtenteRepository utenteRepository;
    @Autowired
    private CarrelloRepository carrelloRepository;
    @Autowired
    private ProdottoCarrelloRepository prodottoCarrelloRepository;
    @Autowired
    private OrdineRepository ordineRepository;

    @Transactional(readOnly=true)
    public Utente getUtenteFromEmail(String email) throws EmailNotFoundException
    {
        if(email==null)
            throw new EmailNotFoundException();
        Optional<Utente> utente = utenteRepository.findByEmail(email.toLowerCase());
        if(utente.isPresent())
            return utente.get();
        else
            throw new EmailNotFoundException();
    }

    @Transactional(readOnly=true)
    public Carrello getCarrelloFromUtente(Utente utente) throws InvalidUtenteException
    {
        if(utente==null)
            throw new InvalidUtenteException();
        Optional<Carrello> carrello = carrelloRepository.findByUtente(utente);
        if(carrello.isPresent())
            return carrello.get();
        else
            throw new InvalidUtenteException();
    }

    @Transactional(readOnly=true)
    public List<ProdottoCarrello> getAllProdottiInCarrello(int pageNumber, int pageSize, String sortBy,
                                                            Carrello carrello)
    {
        Optional<Carrello> carrelloManaged = carrelloRepository.findById(carrello.getId());
        LinkedList<ProdottoCarrello> ret = new LinkedList<>();
        if(carrelloManaged.isPresent())
            ret.addAll(carrelloManaged.get().getProdotti());
        return ret;
    }//getAllProdottiInCarrello

    @Transactional(readOnly=true)
    public Optional<ProdottoCarrello> getProdottoFromCarrello(String nomeProdotto,int tagliaProdotto, Carrello carrello) throws InvalidProductException, CarrelloNotFoundException
    {  //se il prodotto è nel carrello lo prendiamo
        if(nomeProdotto==null)
            throw new InvalidProductException();
        if(carrello==null)
            throw new CarrelloNotFoundException();
        Optional<Carrello> carrelloManaged = carrelloRepository.findById(carrello.getId());
        if(! carrelloManaged.isPresent())  //non è la stessa cosa di carrelloManaged.empty(), perchè empty() è unn prototype, restituisce una istanza
            throw new CarrelloNotFoundException();
        for( ProdottoCarrello prodottoCarrello : carrelloManaged.get().getProdotti())
        {
            Optional<ProdottoCarrello> prodottoCarrelloManaged =
                    prodottoCarrelloRepository.findById(prodottoCarrello.getId());
            if(prodottoCarrelloManaged.isPresent())  //se non è isPresent() allora c'è un problema di consistenza del carrello, non è scopo di questo metodo occuparsene
            {
                if(prodottoCarrelloManaged.get().getProdotto().getNome().toLowerCase().equals(nomeProdotto.toLowerCase())
                 && prodottoCarrelloManaged.get().getProdotto().getTaglia() == tagliaProdotto )
                    return Optional.of(prodottoCarrelloManaged.get());
            }//if
        }//for
        return Optional.empty();
    }//getProdottoFromCarrello

    @Transactional(readOnly=true)
    public List<Ordine> getOrdini(Utente utente)
    throws InvalidUtenteException
    {
        if(utente==null)
            throw new InvalidUtenteException();
        List<Ordine> ordiniManaged = ordineRepository.findAllByUtente(utente);
        return ordiniManaged;
    }
}
