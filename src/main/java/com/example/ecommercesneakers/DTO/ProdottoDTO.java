package com.example.ecommercesneakers.DTO;

import com.example.ecommercesneakers.models.Brand;
import com.example.ecommercesneakers.models.Prodotto;
import jakarta.persistence.CascadeType;
import jakarta.persistence.ManyToOne;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@EqualsAndHashCode
@ToString
public class ProdottoDTO {
    private String nome; //unique
    private double prezzo;
    private int qta;
    private int taglia;
    private String nomeBrand;
    private String url;

    public ProdottoDTO(){

    }

    public ProdottoDTO(Prodotto prodotto){
        nome=prodotto.getNome();
        prezzo=prodotto.getPrezzo();
        qta=prodotto.getQta();
        taglia=prodotto.getTaglia();
        nomeBrand=prodotto.getBrand().getNome();
        url=prodotto.getUrl();
    }
}
