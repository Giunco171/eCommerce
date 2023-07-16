package com.example.ecommercesneakers.DTO;

import com.example.ecommercesneakers.models.DettaglioOrdine;
import com.example.ecommercesneakers.models.Prodotto;
import com.example.ecommercesneakers.models.ProdottoCarrello;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@EqualsAndHashCode
@ToString
public class ProdottoCarrelloDTO {
    private int qta;
    private double prezzo;
    private ProdottoDTO prodotto;

    public ProdottoCarrelloDTO(){

    }

    public ProdottoCarrelloDTO(ProdottoCarrello prodotto){
        prezzo=prodotto.getPrezzo();
        qta=prodotto.getQta();
        this.prodotto=new ProdottoDTO(prodotto.getProdotto());
    }

    public ProdottoCarrelloDTO(DettaglioOrdine prodotto){
        prezzo=prodotto.getPrezzo();
        qta=prodotto.getQta();
        this.prodotto=new ProdottoDTO(prodotto.getProdotto());
    }
}
