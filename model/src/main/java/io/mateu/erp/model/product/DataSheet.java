package io.mateu.erp.model.product;

import io.mateu.common.model.common.File;
import io.mateu.common.model.multilanguage.Literal;
import io.mateu.ui.mdd.server.annotations.Ignored;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by miguel on 1/10/16.
 */
@Entity
@Getter
@Setter
public class DataSheet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    private Literal description;

    @ManyToOne
    private File mainImage;

    @OneToMany(mappedBy = "dataSheet")
    @Ignored
    private List<FeatureValue> features = new ArrayList<>();
}
