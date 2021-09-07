package com.example.mscmd.proxy;

import com.maktabatic.coreapi.model.Book;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "books-management")
public interface BooksProxy {
    @GetMapping("/exemplaireApi/idnotice/{rb}")
    Long getIdNotice(@PathVariable("rb") String rb);

    @GetMapping("/exemplaires/search/findExemplaireByRfid")
    Book getBook(@RequestParam("rb") String rb, @RequestParam("projection") String projection);

    @GetMapping("/exemplaireApi/bookexist/{rb}")
    Book verifyBook(@PathVariable("rb") String rb);
}
