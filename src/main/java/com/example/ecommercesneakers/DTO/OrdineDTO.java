package com.example.ecommercesneakers.DTO;

import com.example.ecommercesneakers.models.DettaglioOrdine;
import com.example.ecommercesneakers.models.Ordine;
import com.example.ecommercesneakers.models.Utente;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;

@Setter
@Getter
@EqualsAndHashCode
@ToString
public class OrdineDTO {
    private Date data;
    private Collection<ProdottoCarrelloDTO> dettagli = new LinkedList<>();

    public OrdineDTO(Ordine ordine)
    {
        data=ordine.getData();
        for(DettaglioOrdine dettaglioOrdine : ordine.getDettagli())
            dettagli.add(new ProdottoCarrelloDTO(dettaglioOrdine));
    }
}
