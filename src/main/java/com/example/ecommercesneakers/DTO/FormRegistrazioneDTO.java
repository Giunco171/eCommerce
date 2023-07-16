package com.example.ecommercesneakers.DTO;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@EqualsAndHashCode
@ToString
public class FormRegistrazioneDTO {
    private String nome=null;
    private String password=null;
    private String email=null;
    private String cognome=null;

    public FormRegistrazioneDTO(){}
}
