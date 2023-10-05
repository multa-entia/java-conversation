package ru.multa.entia.conversion.impl.message;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.multa.entia.conversion.api.content.Content;
import ru.multa.entia.results.api.result.Result;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultContentCheckerTest {

    @Test
    void shouldCheckChecking_ifContentNull() {
        Result<Content> result = new DefaultContentChecker().apply(null);

        assertThat(result.ok()).isFalse();
        assertThat(result.value()).isNull();
        assertThat(result.seed().code()).isEqualTo(DefaultContentChecker.CODE);
        assertThat(result.seed().args()).isEmpty();
    }

    @Test
    void shouldCheckChecking_ifContentNotNull() {
        Content expectedContent = createContent();
        Result<Content> result = new DefaultContentChecker().apply(expectedContent);

        assertThat(result.ok()).isTrue();
        assertThat(result.value()).isEqualTo(expectedContent);
        assertThat(result.seed()).isNull();
    }

    private Content createContent(){
        return Mockito.mock(Content.class);
    }
}