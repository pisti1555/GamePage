package com.example.szakdoga.data.model.game.string;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.io.Serializable;
import java.util.Random;

@SessionScope
@Component
public class ProbaString implements Serializable {
    public String proba;

    public ProbaString() {
        Random random = new Random();
        this.proba = "EZT MODOSITSD" + random.nextInt();
    }

    public String getProba() {
        return proba;
    }

    public void setProba(String proba) {
        this.proba = proba;
    }
}
