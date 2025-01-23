package org.example.logisticapplication.domain.Driver;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
public class DriverDefaultValues {
    private Long averageSpeed;
    private Integer numberOfHoursWorkedLimit;
    private static Integer defaultNumberOfHoursWorked;

    public DriverDefaultValues(
            @Value("${driver.defaultAverageSpeed}")
            Long averageSpeed,
            @Value("${driver.defaultNumberOfHoursWorkedLimit}")
            Integer numberOfHoursWorkedLimit,
            @Value("${default.number-of-hours-worked}")
            Integer defaultNumberOfHoursWorked

    ) {
        this.averageSpeed = averageSpeed;
        this.numberOfHoursWorkedLimit = numberOfHoursWorkedLimit;
    }
}
