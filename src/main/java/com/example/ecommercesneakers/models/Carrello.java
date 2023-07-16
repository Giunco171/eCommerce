package com.example.ecommercesneakers.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.Collection;
import java.util.LinkedList;


@Entity
@Setter
@Getter
@EqualsAndHashCode
@ToString
public class Carrello {
    @GeneratedValue
    @Id
    private Long id;
    @OneToOne(optional = false)
    @JsonIgnore
    @ToString.Exclude  //sennò va in overflow
    private Utente utente;
    @OneToMany(fetch = FetchType.EAGER, cascade=CascadeType.ALL)
    private Collection<ProdottoCarrello> prodotti = new LinkedList<>();
    @Version
    private long version; //sul carrello vi possono essere accessi concorrenti da parte dello stesso utente da due dispositivi diversi

    public Carrello(){}
}
