package com.maktabatic.coreapi.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Reader {
    private String lastname;
    private String firstname;
    private String rfid;
    private String email;
}
