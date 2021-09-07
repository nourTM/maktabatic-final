package com.maktabatic.msqueryloanreturn.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Date;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class KeyLoanReturn implements Serializable {
    private String rr;
    private String rb;
    private Date dateLoan;
}
