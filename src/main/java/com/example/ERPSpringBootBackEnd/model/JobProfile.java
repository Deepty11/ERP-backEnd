package com.example.ERPSpringBootBackEnd.model;

import com.example.ERPSpringBootBackEnd.enums.Currency;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

@Table
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobProfile implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @SequenceGenerator(name = "jobInfoSeq",
            sequenceName = "jobInfoSeq",
            allocationSize = 1)
    @GeneratedValue(generator = "jobInfoSeq")
    private long id;

    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date joiningDate;

    @NotNull
    private double basicSalary;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Currency currency;

    private double conveyanceAllowance;
    private double medicalReimbursement;
    private double houseRent;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "designation_id")
    private Designation designation;

    @OneToOne
    private User user;

    @Transient
    private String joinningDateString;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private Date updated;

    @Version
    private int version = 0;

    public String getJoiningDateString() {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        return format.format(joiningDate);
    }

    private String getFormattedAllowance(double amount) {
        return this.getCurrency().toString() + amount;
    }

    public String getBasicSalaryString() {
        return getFormattedAllowance(this.basicSalary);
    }

    public String getConveyanceAllowanceString() {
        return getFormattedAllowance(this.conveyanceAllowance);
    }


    public String getMedicalReimbursementString() {
        return getFormattedAllowance(this.medicalReimbursement);
    }

    public String getHouseRentString() {
        return getFormattedAllowance(this.houseRent);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JobProfile that = (JobProfile) o;
        return id == that.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}