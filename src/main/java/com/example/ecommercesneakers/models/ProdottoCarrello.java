package com.example.ecommercesneakers.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Collection;
import java.util.LinkedList;


@Entity
@Setter
@Getter
@EqualsAndHashCode
@ToString
public class ProdottoCarrello {
    @JsonIgnore
    @GeneratedValue
    @Id
    @EqualsAndHashCode.Exclude  //sennò ogni volta che creo un ProdottoCarrello nuovo da aggiungere al carrello lo crea con id diverso e non si può comparare (dovrei comparare il riferimento a prodotto)
    private Long id;
    private Integer qta;
    private Double prezzo;
    @ManyToOne(optional = false)  //il cascade tipe qui potrebbe essere pericoloso, perchè propaga le modifiche sui prodotti visti da tutti gli utenti
    private Prodotto prodotto;
    @JsonIgnore
    @Version
    private long version;

    public ProdottoCarrello(){}

    public ProdottoCarrello(Prodotto prodotto)
    {
        qta=prodotto.getQta()-(prodotto.getQta()-1); // ==1
        prezzo= prodotto.getPrezzo();
        this.prodotto=prodotto;
    }
}
