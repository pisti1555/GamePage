package com.example.szakdoga.service;

import com.example.szakdoga.data.model.game.string.ProbaString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StringService {
    public ProbaString probaString;
    public ConnectionService connectionService;

    @Autowired
    public StringService(ProbaString probaString, ConnectionService connectionService) {
        this.probaString = probaString;
        this.connectionService = connectionService;
    }
}
