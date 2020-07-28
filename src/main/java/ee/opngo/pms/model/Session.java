package ee.opngo.pms.model;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Table(name = "session")
public class Session implements Serializable {
    @Id
    @Column(name = "id")
    @GeneratedValue
    private Long id;

    @Column(name = "start_time")
    @NotNull
    private LocalDateTime startTime;

    @Column(name = "last_charge_time")
    @NotNull
    private LocalDateTime lastChargeTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id")
    @NotNull
    @ToString.Exclude
    private Vehicle vehicle;

    @Column(name = "price")
    private BigDecimal price;

    @Column(name = "invoice_sent", columnDefinition = "boolean default false", nullable = false)
    private Boolean invoiceSent = false;

    @Column(name = "license")
    private String license;

    @Column(name = "state")
    private String state;
}
