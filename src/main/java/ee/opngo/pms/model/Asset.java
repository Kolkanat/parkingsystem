package ee.opngo.pms.model;

import lombok.Builder;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@Builder
@Entity
@Table(name = "asset")
public class Asset implements Serializable {
    @Id
    @Column(name = "id")
    @GeneratedValue
    private Long id;

    @Column(name = "address")
    @NotNull
    private String address;

    @Column(name = "is_active")
    @NotNull
    private Boolean isActive;
}
