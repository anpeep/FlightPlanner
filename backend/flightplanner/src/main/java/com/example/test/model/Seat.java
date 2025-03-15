package com.example.test.model;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "seat")
public class Seat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // Add this line to auto-generate the ID
    private Integer id;
    @ManyToOne
    @JoinColumn(name = "plane_id")
    private Plane plane;
    private Boolean available;
    private Integer row;
    private Boolean recommended;
    private String seat_column;

    @Override
    public String toString() {
        return "Seat{" +
            "id=" + id +
            ", plane=" + plane +
            ", available=" + available +
            ", row=" + row +
            ", recommended=" + recommended +
            ", seat_column='" + seat_column + '\'' +
            '}';
    }
}
