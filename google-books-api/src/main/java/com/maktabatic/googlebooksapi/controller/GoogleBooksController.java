package com.maktabatic.googlebooksapi.controller;


import com.maktabatic.googlebooksapi.model.Notice;
import com.maktabatic.googlebooksapi.proxy.GoogleBooksApiProxy;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.LinkedHashMap;

@RestController
@RequestMapping("api")
public class GoogleBooksController {


    @Autowired
    GoogleBooksApiProxy googleBooksApiProxy;

    @GetMapping("/book/{isbn}")
    public Notice getBook(@PathVariable("isbn") String isbn){

        String query = "isbn:"+isbn ;
        Notice notice = new Notice();

        JSONObject obj = googleBooksApiProxy.getBook(query);

        notice.setTitle(((LinkedHashMap) ((LinkedHashMap)( (ArrayList) obj.get("items")).get(0)).get("volumeInfo")).get("title").toString());
        notice.setSubtitle(((LinkedHashMap) ((LinkedHashMap)( (ArrayList) obj.get("items")).get(0)).get("volumeInfo")).get("subtitle").toString());
        notice.setDescription(((LinkedHashMap) ((LinkedHashMap)( (ArrayList) obj.get("items")).get(0)).get("volumeInfo")).get("description").toString());
        notice.setPublishedDate(((LinkedHashMap) ((LinkedHashMap)( (ArrayList) obj.get("items")).get(0)).get("volumeInfo")).get("publishedDate").toString());
        notice.setPublisher(((LinkedHashMap) ((LinkedHashMap)( (ArrayList) obj.get("items")).get(0)).get("volumeInfo")).get("publisher").toString());
        notice.setPages(((LinkedHashMap) ((LinkedHashMap)( (ArrayList) obj.get("items")).get(0)).get("volumeInfo")).get("pageCount").toString());
        notice.setLink(((LinkedHashMap) ((LinkedHashMap)( (ArrayList) obj.get("items")).get(0)).get("volumeInfo")).get("infoLink").toString());
        notice.setImgLink(((LinkedHashMap)((LinkedHashMap) ((LinkedHashMap)( (ArrayList) obj.get("items")).get(0)).get("volumeInfo")).get("imageLinks")).get("thumbnail").toString());
        notice.setIsbn13(((LinkedHashMap)((ArrayList)((LinkedHashMap) ((LinkedHashMap)( (ArrayList) obj.get("items")).get(0)).get("volumeInfo")).get("industryIdentifiers")).get(0)).get("identifier").toString());
        notice.setIsbn10(((LinkedHashMap)((ArrayList)((LinkedHashMap) ((LinkedHashMap)( (ArrayList) obj.get("items")).get(0)).get("volumeInfo")).get("industryIdentifiers")).get(1)).get("identifier").toString());
        notice.setAuthors(((ArrayList) ((LinkedHashMap) ((LinkedHashMap)( (ArrayList) obj.get("items")).get(0)).get("volumeInfo")).get("authors")).toString());
        notice.setCategories(((ArrayList) ((LinkedHashMap) ((LinkedHashMap)( (ArrayList) obj.get("items")).get(0)).get("volumeInfo")).get("categories")).toString());

        return notice;
    }


}
