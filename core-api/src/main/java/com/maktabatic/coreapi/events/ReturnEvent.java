package com.maktabatic.coreapi.events;

import com.maktabatic.coreapi.model.Book;
import com.maktabatic.coreapi.model.BookState;
import com.maktabatic.coreapi.model.KeyLoanReturn;
import com.maktabatic.coreapi.model.Reader;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReturnEvent {
    private KeyLoanReturn id;
    private Date dateReturn;
}
