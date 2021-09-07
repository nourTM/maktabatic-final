package com.maktabatic.msqueryloanreturn.entities;

import com.maktabatic.coreapi.model.Book;
import com.maktabatic.coreapi.model.BookState;
import com.maktabatic.coreapi.model.Reader;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data @AllArgsConstructor @NoArgsConstructor
public class LoanReturn {
    @EmbeddedId
    private KeyLoanReturn id;
    private String title;
    private String name;
    private String firstName;
    @Enumerated(EnumType.STRING)
    private BookState state;
    private Date dateReturn;
    private Long idNotice;
}
