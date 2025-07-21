package com.example.partidasdefutebol.tests;

import org.junit.jupiter.api.Test;

import static com.example.partidasdefutebol.util.isValidBrazilianState.isValidBrazilianState;
import static org.assertj.core.api.Assertions.assertThat;

public class isBrazilianState {
    @Test
    public void testReturnsTrueWithValidAcronym() {
        assertThat(isValidBrazilianState("SP")).isEqualTo(true);
        assertThat(isValidBrazilianState("RJ")).isEqualTo(true);
        assertThat(isValidBrazilianState("PR")).isEqualTo(true);
        assertThat(isValidBrazilianState("PA")).isEqualTo(true);
    }

    @Test
    public void testReturnsFalseWithInvalidAcronym() {
        assertThat(isValidBrazilianState("PP")).isEqualTo(false);
        assertThat(isValidBrazilianState("AJ")).isEqualTo(false);
        assertThat(isValidBrazilianState("IS")).isEqualTo(false);
        assertThat(isValidBrazilianState("IZZ")).isEqualTo(false);
    }

    @Test
    public void testReturnsTrueWithValidBrazilianStateAndDifferentCase() {
        assertThat(isValidBrazilianState("sp")).isEqualTo(true);
        assertThat(isValidBrazilianState("rJ")).isEqualTo(true);
        assertThat(isValidBrazilianState("pr")).isEqualTo(true);
    }
}
