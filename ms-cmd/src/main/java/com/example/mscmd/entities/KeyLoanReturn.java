package com.example.mscmd.entities;

import com.maktabatic.coreapi.model.BookState;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class KeyLoanReturn implements Serializable {

    private String rr;
    private String rb;
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateLoan;
    @Enumerated(EnumType.STRING)
    private BookState state;

}
