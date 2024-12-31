package org.example.logisticapplication.domain.Cargo;


public record CargoForOrderDto(
        String name,
        Long weight
) {
    public CargoForOrderDto(
            String name,
            Long weight
    ) {
        this.name = name;
        this.weight = weight;
    }
}
