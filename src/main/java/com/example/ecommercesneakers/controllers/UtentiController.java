package com.example.ecommercesneakers.controllers;


import com.example.ecommercesneakers.DTO.FormRegistrazioneDTO;
import com.example.ecommercesneakers.DTO.OrdineDTO;
import com.example.ecommercesneakers.DTO.ProdottoDTO;
import com.example.ecommercesneakers.Exceptions.EmailAlreadyUsedException;
import com.example.ecommercesneakers.Exceptions.EmailNotFoundException;
import com.example.ecommercesneakers.Exceptions.InvalidUtenteException;
import com.example.ecommercesneakers.Exceptions.ProdottoNotFoundException;
import com.example.ecommercesneakers.keycloak.Utils;
import com.example.ecommercesneakers.models.Carrello;
import com.example.ecommercesneakers.models.Ordine;
import com.example.ecommercesneakers.models.Prodotto;
import com.example.ecommercesneakers.models.Utente;
import com.example.ecommercesneakers.services.CaricaInfoUtentiService;
import com.example.ecommercesneakers.services.GetDatiService;
import com.example.ecommercesneakers.services.GetInfoUtenteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.POST;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin("*")
@RequestMapping(value= "/utenti")
public class UtentiController {
    @Autowired
    CaricaInfoUtentiService caricaInfoUtentiService;
    @Autowired
    GetInfoUtenteService getInfoUtenteService;
    @Autowired
    GetDatiService getDatiService;

    @PostMapping(value="/registrazione")
    public ResponseEntity<String> registrazione(@RequestBody FormRegistrazioneDTO utente)  //usiamo un DTO specifico
    {
        Utente newUtente=null;
        try{
            newUtente = caricaInfoUtentiService.registraUtente(utente.getEmail(), utente.getNome(), utente.getCognome(), utente.getPassword());
        }catch(EmailAlreadyUsedException eaue){
            return new ResponseEntity("Registrazione annullata, utente già esistente.", HttpStatus.NOT_ACCEPTABLE);
        }catch(InvalidUtenteException iue)
        {
            return new ResponseEntity("Email non valida.", HttpStatus.NOT_ACCEPTABLE);
        }catch(OptimisticLockingFailureException olfe)
        {
            return new ResponseEntity<>("L'operazione non è andata a buon fine", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity("Registrazione andata a buon fine. Utente Registrato: " + newUtente, HttpStatus.OK);

    }//registrazione

    @PreAuthorize("hasAuthority('utente')")
    @GetMapping(value="/ordini")
    public ResponseEntity<List<OrdineDTO>> getListaOrdini()
     {
         try{
             Utente utente = getInfoUtenteService.getUtenteFromEmail(Utils.getEmail());
             List<Ordine> listaOrdini = getInfoUtenteService.getOrdini(utente);

             List<OrdineDTO> ret =new LinkedList<>();
             for(Ordine ordine : listaOrdini)
                 ret.add(new OrdineDTO(ordine));

             return new ResponseEntity<>(ret, HttpStatus.OK);
         }catch(InvalidUtenteException iue){
            return new ResponseEntity<>(null, HttpStatus.NOT_ACCEPTABLE);
         }
     }
}
