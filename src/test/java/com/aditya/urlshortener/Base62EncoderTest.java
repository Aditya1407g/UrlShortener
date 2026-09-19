package com.aditya.urlshortener;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Base62EncoderTest {
    @ParameterizedTest
    @CsvSource({
            "0, '0'",
            "1, '1'",
            "10, a",
            "35, z",
            "36, A",
            "61, Z",
            "62, '10'",
            "3843, ZZ",
            "3844, '100'"
    })
    void encode_returnsExpectedShortCode(long input, String expected) {
        assertEquals(expected, Base62Encoder.encode(input));
    }

    @Test
    void encode_isDeterministic_forSameInput(){
        String first = Base62Encoder.encode(500L);
        String second = Base62Encoder.encode(500L);
        assertEquals(first,second);
    }

}