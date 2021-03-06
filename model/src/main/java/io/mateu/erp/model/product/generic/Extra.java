package io.mateu.erp.model.product.generic;

import io.mateu.common.model.multilanguage.Literal;
import io.mateu.erp.model.product.AbstractProduct;
import io.mateu.erp.model.product.ProductType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * Created by miguel on 31/1/17.
 */
@Entity(name = "GenericExtra")
@Getter
@Setter
public class Extra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @NotNull
    private GenericProduct product;

    @ManyToOne
    private Literal name;

    private SupplementType type;

    private boolean optional;

    private boolean internalUseOnly;

}
