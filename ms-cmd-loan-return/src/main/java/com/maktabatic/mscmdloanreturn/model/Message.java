package com.maktabatic.mscmdloanreturn.model;

import lombok.Data;

@Data
public class Message {
    private  String msg;

    public Message(String msg) {
        this.msg = msg;
    }
}
