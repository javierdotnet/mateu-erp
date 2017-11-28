package io.mateu.erp.dispo;

import io.mateu.erp.dispo.interfaces.product.IHotelOffer;
import lombok.Getter;
import lombok.Setter;
import org.easytravelapi.hotel.Occupancy;

import java.util.HashMap;
import java.util.Map;

@Getter@Setter
public class ImportePorDia {

    private double habitacion;

    private double alojamiento;

    private double desayuno;

    private double almuerzo;

    private double cena;

    private double extrasRegimen;

    private double extrasAlojamiento;

    private double descuentoPax;



    public double getTotal() {

        double total = 0;

        total += habitacion;
        total += alojamiento;
        total += desayuno;
        total += almuerzo;
        total += cena;
        total += extrasAlojamiento;
        total += extrasRegimen;
        total += descuentoPax;

        return total;

    }
}
