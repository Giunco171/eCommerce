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
public class Brand { //sarebbe la categoria
    @JsonIgnore
    @GeneratedValue
    @Id
    private Long id;
    private String nome;
    @OneToMany(mappedBy = "brand", cascade= CascadeType.ALL)
    @JsonIgnore
    @ToString.Exclude
    private Collection<Prodotto> prodotti = new LinkedList<>();
    @JsonIgnore
    @Version
    private long version;

    public Brand(){}
    public Brand(Brand brand)
    {
        nome=brand.nome.toLowerCase();
        prodotti=new LinkedList<Prodotto>();
    }
    public Brand(String nome)
    {
        this.nome=nome;
        prodotti=new LinkedList<Prodotto>();
    }
}
