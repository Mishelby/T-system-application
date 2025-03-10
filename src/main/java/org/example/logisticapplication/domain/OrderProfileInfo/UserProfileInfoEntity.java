package org.example.logisticapplication.domain.OrderProfileInfo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.example.logisticapplication.domain.User.UserEntity;

@Entity
@Table(name = "user_profile_info")
@Getter
@Setter
public class UserProfileInfoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserEntity user;

    @Column(name = "message_for_user")
    private String messageForUser;

    @Column(name = "allow_system_decide")
    private Boolean allowSystemDecide;

    @Column(name = "decided")
    private Boolean decided;

    @Column(name = "drivers_count")
    private Integer driversCount;

    public UserProfileInfoEntity(
            UserEntity user,
            Boolean allowSystemDecide,
            Integer driversCount
    ) {
        this.user = user;
        this.allowSystemDecide = allowSystemDecide;
        this.driversCount = driversCount;
    }

    public UserProfileInfoEntity(
            UserEntity user
    ) {
        this.user = user;
    }

    public UserProfileInfoEntity() {}

}
