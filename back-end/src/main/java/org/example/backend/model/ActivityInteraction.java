package org.example.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class ActivityInteraction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
//    @JsonManagedReference
    @JoinColumn(name = "child_id")
    private ChildProfile child;
    @ManyToOne private Activity activity;

    private int views;
    private boolean favorited;
    private boolean registered;
}
