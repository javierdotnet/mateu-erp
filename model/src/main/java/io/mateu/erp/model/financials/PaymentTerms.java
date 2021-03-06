package io.mateu.erp.model.financials;

import io.mateu.ui.mdd.server.annotations.OwnedList;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class PaymentTerms {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "terms")
    @OwnedList
    private List<PaymentTermsLine> lines = new ArrayList<>();
}
