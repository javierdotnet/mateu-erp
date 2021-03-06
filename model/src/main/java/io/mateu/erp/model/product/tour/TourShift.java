package io.mateu.erp.model.product.tour;

import io.mateu.ui.mdd.server.annotations.OwnedList;
import io.mateu.ui.mdd.server.annotations.Tab;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class TourShift {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Tab("Info")
    private String name;

    @ManyToOne
    @NotNull
    private Tour tour;

    @Column(name = "_start")
    private LocalDate start;

    @Column(name = "_end")
    private LocalDate end;

    /**
     * hora de inicio
     */
    private int startTime;

    /**
     * en días
     */
    private int release;

    /**
     * lista de idiomas separados por coma
     */
    private String languages;


    @Tab("Allotment")
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "shift")
    @OwnedList
    private List<TourShiftCalendar> allotments = new ArrayList<>();


    @Tab("Pickup times")
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "shift")
    @OwnedList
    private List<TourPickupTime> pickupTimes = new ArrayList<>();


}
