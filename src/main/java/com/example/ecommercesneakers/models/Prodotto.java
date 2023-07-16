package com.example.ecommercesneakers.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.lang.Nullable;

@Entity
@Setter
@Getter
@EqualsAndHashCode
@ToString
public class Prodotto {
    @JsonIgnore
    @GeneratedValue
    @Id
    private Long id;
    private String nome; //unique
    private double prezzo;
    private int qta;
    private int taglia;
    @Nullable
    private String url;
    @ManyToOne(optional = false, cascade=CascadeType.ALL)
    private Brand brand;
    @JsonIgnore
    @Version
    private long version;

    public Prodotto(){}
    public Prodotto(Prodotto prodotto) {
        nome = prodotto.nome.toLowerCase();
        prezzo = prodotto.prezzo;
        qta = prodotto.qta;
        taglia = prodotto.taglia;
        url = prodotto.url;
        brand = new Brand(prodotto.brand);  //poi viene sovrascritto nei service
    }
}
