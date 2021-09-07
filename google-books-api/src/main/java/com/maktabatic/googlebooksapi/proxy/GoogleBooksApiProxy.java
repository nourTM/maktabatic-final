package com.maktabatic.googlebooksapi.proxy;

import net.minidev.json.JSONObject;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name="google-api", url = "https://www.googleapis.com")
public interface GoogleBooksApiProxy {

    @GetMapping("/books/v1/volumes")
    public JSONObject getBook(@RequestParam("q") String query);
}
