package com.example.ecommercesneakers.services;

import com.example.ecommercesneakers.DTO.ProdottoDTO;
import com.example.ecommercesneakers.Exceptions.*;
import com.example.ecommercesneakers.models.*;
import com.example.ecommercesneakers.repositories.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AcquistoService {
    @Autowired
    private CarrelloRepository carrelloRepository;
    @Autowired
    private ProdottoRepository prodottoRepository;
    @Autowired
    private UtenteRepository utenteRepository;
    @Autowired
    private OrdineRepository ordineRepository;
    @Autowired
    private DettaglioOrdineRepository dettaglioOrdineRepository;
    @Autowired
    private ProdottoCarrelloRepository prodottoCarrelloRepository;

    public boolean acquista(Carrello carrello,Utente utente, List<ProdottoDTO> listaProdotti)
            throws CarrelloNotFoundException, InvalidUtenteException, ProdottoEsauritoException, PrezzoCambiatoException, NotEnoughtProductsException
    {
        if(listaProdotti.isEmpty())
            return true; //acquistiamo il niente
        if(carrello == null)
            throw new CarrelloNotFoundException();

        Optional<Carrello> carrelloManaged = carrelloRepository.findById(carrello.getId());

        if(! carrelloManaged.isPresent())
            throw new CarrelloNotFoundException();

        Ordine ordine = new Ordine();
        Optional<Utente> utenteManaged = utenteRepository.findById(utente.getId());
        if(! utenteManaged.isPresent())
            throw new InvalidUtenteException();
        ordine.setUtente(utenteManaged.get());
        //la data è autogenerata sull'ordine
        ordineRepository.save(ordine); //rendiamo l'oggetto Owner managed
        for(ProdottoDTO prodottoDTO : listaProdotti)
        {
            Optional<Prodotto> prodottoNelDB = prodottoRepository.findProdottoByNomeAndTaglia(prodottoDTO.getNome().toLowerCase(), prodottoDTO.getTaglia());
            if(! prodottoNelDB.isPresent())
                throw new ProdottoEsauritoException("Articolo: "+prodottoDTO.getNome().toLowerCase()+" esaurito.");
            if(prodottoDTO.getPrezzo() != prodottoNelDB.get().getPrezzo())
                throw new PrezzoCambiatoException("Vecchio prezzo: "+prodottoDTO.getPrezzo()+
                        " cambiato in nuovo prezzo: "+prodottoNelDB.get().getPrezzo());
            if(prodottoDTO.getQta()>prodottoNelDB.get().getQta())
                throw new NotEnoughtProductsException(prodottoDTO.getNome());
            DettaglioOrdine dettaglioOrdine = new DettaglioOrdine();
            dettaglioOrdine.setProdotto(prodottoNelDB.get());
            dettaglioOrdine.setPrezzo(prodottoDTO.getPrezzo());
            dettaglioOrdine.setQta(prodottoDTO.getQta());
            dettaglioOrdineRepository.save(dettaglioOrdine);
            ordine.getDettagli().add(dettaglioOrdine);
            prodottoNelDB.get().setQta(prodottoNelDB.get().getQta()-prodottoDTO.getQta());
        }
        return true;
    }//acquista

    public void pulisciCarrello(Carrello carrello)
    throws CarrelloNotFoundException, ProdottoNotFoundException
    {
        if(carrello == null)
            throw new CarrelloNotFoundException();
        Optional<Carrello> carrelloManaged = carrelloRepository.findById(carrello.getId());
        if(! carrelloManaged.isPresent())
            throw new CarrelloNotFoundException();

        LinkedList<ProdottoCarrello> listaDiSupporto = new LinkedList<>(carrelloManaged.get().getProdotti());
        //usiamo la lista di supporto per evitare una concurrent modification exception
        for(ProdottoCarrello prodottoCarrello : listaDiSupporto)
        {
            Optional<ProdottoCarrello> prodottoManaged =
                    prodottoCarrelloRepository.findById(prodottoCarrello.getId());
            if(! prodottoManaged.isPresent())
                throw new ProdottoNotFoundException();
            carrelloManaged.get().getProdotti().remove((prodottoManaged.get()));
        }
        carrelloRepository.save(carrelloManaged.get());

        //ora eliminiamo gli owning, RIGOROSAMENTE DOPO l'OWNER
        prodottoCarrelloRepository.deleteAll(listaDiSupporto);

    }
}
