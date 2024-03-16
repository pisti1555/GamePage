package com.example.szakdoga.service;

import com.example.szakdoga.data.model.game.string.ProbaString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StringService {
    public ProbaString probaString;

    @Autowired
    public StringService(ProbaString probaString) {
        this.probaString = probaString;
    }
}
