package com.example.ecommercesneakers.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Setter
@Getter
@EqualsAndHashCode
@ToString
public class DettaglioOrdine {
    @JsonIgnore
    @GeneratedValue
    @Id
    private Long id;
    private Integer qta;
    private Double prezzo;
    @ManyToOne(optional = false)
    private Prodotto prodotto;
    @JsonIgnore
    @Version
    private long version;

    public DettaglioOrdine(){}
}
