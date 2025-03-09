package com.example.test.model;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
public class Seat {
    @Id
    private Integer id;
    private String name;
    private String type;
    private boolean isMoreLegRoom;

}
