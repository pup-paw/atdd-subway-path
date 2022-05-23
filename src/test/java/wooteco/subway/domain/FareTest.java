package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class FareTest {

    @ParameterizedTest(name = "{0}km일 때 요금은 {1}원이다")
    @CsvSource({"9,1250,0", "12,1350,0", "16,1450,0", "58,2150,900", "15.1,1450,0"})
    void calculate(double distance, int expected, int extraFare) {
        Fare fare = Fare.of(distance, extraFare);

        assertThat(fare.getValue()).isEqualTo(expected + extraFare);
    }
}
