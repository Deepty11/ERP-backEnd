package com.example.ERPSpringBootBackEnd.model;

import com.example.ERPSpringBootBackEnd.enums.Gender;
import com.example.ERPSpringBootBackEnd.enums.Religion;
import com.example.ERPSpringBootBackEnd.enums.Role;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Entity
@Table
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Users implements UserDetails, Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String firstName;
    private String lastName;
    private String userName;
    private String password;

    @Enumerated(EnumType.STRING)
    Role role;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date birthDate;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Enumerated(EnumType.STRING)
    private Religion religion;

    @OneToOne
    @JoinColumn(name = "contact_info_id")
    private ContactInfo contactInfo;

    @OneToOne
    @JoinColumn(name = "emergency_contact_info_id")
    private EmergencyContactInfo emergencyContact;

    @OneToMany
    @JoinColumn(name = "user_document_id")
    private List<FileEntity> documents;

    @OneToOne
    private FileEntity profilePicture;

    @OneToOne
    @JoinColumn(name = "job_info_id")
    private JobProfile jobProfile;

    @OneToMany(mappedBy = "users")
    private List<LeaveApplication> leaveApplications;

    @Transient
    private String fullName;

    public String getFullName() {
        return this.firstName + " " + this.lastName;
    }

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private Date updated;

    @Version
    private int version;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return userName;
    }


    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
