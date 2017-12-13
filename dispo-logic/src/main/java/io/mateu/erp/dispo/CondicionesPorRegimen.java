package io.mateu.erp.dispo;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter@Setter
public class CondicionesPorRegimen {

    private List<CondicionesPorDia> dias = new ArrayList<>();

    private double total;

    public CondicionesPorRegimen(int totalNights) {
        for (int i = 0; i < totalNights; i++) dias.add(new CondicionesPorDia());
    }
}