package io.mateu.erp.model.product.hotel;

import io.mateu.erp.model.financials.Actor;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "HotelStopSales")
@Getter
@Setter
public class StopSales {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    private Hotel hotel;

    @OneToMany(mappedBy = "stopSales", cascade = CascadeType.ALL)
    private List<StopSalesLine> lines = new ArrayList<>();


    @OneToMany(mappedBy = "stopSales")
    private List<StopSalesOperation> operations = new ArrayList<>();


}
