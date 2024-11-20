package com.tribia.application.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "vehicles")
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true)
    private String licensePlate;

    @Column(name = "inspection_deadline")
    private LocalDate inspectionDeadline;

    @Column(name = "is_subscribed")
    private boolean subscribed;

    @Column(name = "is_notification_sent")
    private boolean notificationSent = false;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;
}
