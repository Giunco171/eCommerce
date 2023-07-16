package com.example.ecommercesneakers.services;

import com.example.ecommercesneakers.Exceptions.*;
import com.example.ecommercesneakers.keycloak.ComunicazioneKeycloak;
import com.example.ecommercesneakers.models.Carrello;
import com.example.ecommercesneakers.models.Prodotto;
import com.example.ecommercesneakers.models.ProdottoCarrello;
import com.example.ecommercesneakers.models.Utente;
import com.example.ecommercesneakers.repositories.CarrelloRepository;
import com.example.ecommercesneakers.repositories.ProdottoCarrelloRepository;
import com.example.ecommercesneakers.repositories.ProdottoRepository;
import com.example.ecommercesneakers.repositories.UtenteRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.Optional;

@Service
@Transactional
public class CaricaInfoUtentiService {

    /*Angular invia la richiesta di registrazione
      a Spring che la rigira a keycloak.
     */

    @Autowired
    private UtenteRepository utenteRepository;
    @Autowired
    private ProdottoRepository prodottoRepository;
    @Autowired
    private CarrelloRepository carrelloRepository;
    @Autowired
    private ProdottoCarrelloRepository prodottoCarrelloRepository;

    public Utente registraUtente(String email, String nome, String cognome, String password) throws EmailAlreadyUsedException, InvalidUtenteException
    {
        if(email == null || email.toLowerCase().equals("admin@ecommercesneakers.com") || email.toLowerCase().equals("admin"))
            throw new InvalidUtenteException();
        if(utenteRepository.existsByEmail(email)) //la mail è identificante, due utenti potrebbero avere nome e cognome uguali
            throw new EmailAlreadyUsedException();
        Utente newUtente = new Utente(email.toLowerCase(), cognome.toLowerCase(), nome.toLowerCase());
        Carrello carrelloDiNewUtente = new Carrello();
        carrelloDiNewUtente.setUtente(newUtente);
        newUtente.setCarrello(carrelloDiNewUtente);
        utenteRepository.save(newUtente);

        //integrazione user in keycloak
        ComunicazioneKeycloak.addUser(newUtente,password); //la password non la passiamo toLowerCase perchè il problema del lower case è dovuto più al nostro db per i nomi dei prodotti e dei brand, ma sulle password deve essere preciso

        return newUtente;
    }

    public Prodotto aggiungiProdottoAlCarrello(Prodotto prodotto, Carrello carrello, int qta) throws InvalidProductException, ProdottoEsauritoException, CarrelloNotFoundException, NotEnoughtProductsException, ProdottoAlreadyInCarrelloException
    {
        if(prodotto == null || prodotto.getPrezzo()<0.0)
            throw new InvalidProductException();
        if(carrello == null)
            throw new CarrelloNotFoundException();

        Optional<Prodotto> prodottoManaged = prodottoRepository.findById(prodotto.getId()); //lo riprendiamo perchè ora prodotto non è più managed

        if(! prodottoManaged.isPresent())
            throw new InvalidProductException();

        if(prodottoManaged.get().getQta()<1)
            throw new ProdottoEsauritoException();
        if(prodottoManaged.get().getQta()<qta)      //non possiamo fare questo test su "prodotto" perchè magari nel frattempo al sua quantità è diminuita, meglio farlo sul nuovo managed
            throw new NotEnoughtProductsException();

        ProdottoCarrello prodottoCarrello = new ProdottoCarrello(prodottoManaged.get()); //Primo wiring su prodottoCarrello.prodotto
        prodottoCarrello.setQta(qta);

        Optional<Carrello> carrelloManaged = carrelloRepository.findById(carrello.getId());

        if(! carrelloManaged.isPresent())
            throw new CarrelloNotFoundException();

        if(carrelloManaged.get().getProdotti()== null)
            carrelloManaged.get().setProdotti(new LinkedList<>());
        else if (carrelloManaged.get().getProdotti().contains(prodottoCarrello)) { //dunque non possiamo incrementare la quantità di prodotti nel carrello se quel tipo di prodotto è già presente
            throw new ProdottoAlreadyInCarrelloException();
        }
        carrelloManaged.get().getProdotti().add(prodottoCarrello);

        carrelloRepository.save(carrelloManaged.get()); //tramite cascade si ripercuote sui prodotti carrello

        return prodotto;
    }//aggiungiProdottoAlCarrello

    public Carrello rimuoviProdottoDalCarrello(ProdottoCarrello prodottoDaRimuovere, Carrello carrello, int qta)
    throws ProdottoNotFoundException, CarrelloNotFoundException, InvalidProductException, OptimisticLockingFailureException
    {//per ipotesi il prodotto appartiene al carrello
        if(prodottoDaRimuovere == null)
            throw new ProdottoNotFoundException();
        if(carrello == null)
            throw new CarrelloNotFoundException();

        Optional<ProdottoCarrello> prodottoCarrelloManaged = prodottoCarrelloRepository.findById(prodottoDaRimuovere.getId());
        if(! prodottoCarrelloManaged.isPresent())
            throw new ProdottoNotFoundException();
        if(prodottoCarrelloManaged.get().getQta()<qta)
            throw new InvalidProductException();

        Optional<Carrello> carrelloManaged = carrelloRepository.findById(carrello.getId());
        if(! carrelloManaged.isPresent())
            throw new CarrelloNotFoundException();

        for(ProdottoCarrello prodotto : carrelloManaged.get().getProdotti())
        {
            if(prodotto.getProdotto().getNome().toLowerCase().equals(prodottoCarrelloManaged.get().getProdotto().getNome().toLowerCase())
            && prodotto.getProdotto().getTaglia() == prodottoCarrelloManaged.get().getProdotto().getTaglia())
            {
                if(prodottoCarrelloManaged.get().getQta()==qta) //se la quantità da rimuovere è pari alla quantità totale si rimuove
                    carrelloManaged.get().getProdotti().remove(prodottoCarrelloManaged.get());
                else //altrimenti si scala la quantità
                    prodotto.setQta(prodotto.getQta()-qta);
                break;
            }
        }


        Carrello ret = carrelloRepository.save(carrelloManaged.get());
        prodottoCarrelloRepository.delete(prodottoCarrelloManaged.get());
        return ret;
    } //rimuoviProdottoDalCarrello
}
