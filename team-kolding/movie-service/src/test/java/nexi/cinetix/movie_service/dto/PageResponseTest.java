package nexi.cinetix.movie_service.dto;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PageResponseTest {

    @Test
    void pageResponse_builderPattern() {
        List<String> content = List.of("item1", "item2", "item3");

        PageResponse<String> response = PageResponse.<String>builder()
                .page(0)
                .size(10)
                .totalElements(25L)
                .totalPages(3)
                .content(content)
                .build();

        assertEquals(0, response.getPage());
        assertEquals(10, response.getSize());
        assertEquals(25L, response.getTotalElements());
        assertEquals(3, response.getTotalPages());
        assertEquals(content, response.getContent());
    }

    @Test
    void pageResponse_settersAndGetters() {
        PageResponse<Integer> response = new PageResponse<>();
        List<Integer> content = List.of(1, 2, 3, 4, 5);

        response.setPage(2);
        response.setSize(5);
        response.setTotalElements(100L);
        response.setTotalPages(20);
        response.setContent(content);

        assertEquals(2, response.getPage());
        assertEquals(5, response.getSize());
        assertEquals(100L, response.getTotalElements());
        assertEquals(20, response.getTotalPages());
        assertEquals(content, response.getContent());
    }

    @Test
    void pageResponse_equalsAndHashCode() {
        List<String> content = List.of("test");

        PageResponse<String> response1 = PageResponse.<String>builder()
                .page(1)
                .size(10)
                .totalElements(50L)
                .totalPages(5)
                .content(content)
                .build();

        PageResponse<String> response2 = PageResponse.<String>builder()
                .page(1)
                .size(10)
                .totalElements(50L)
                .totalPages(5)
                .content(content)
                .build();

        PageResponse<String> response3 = PageResponse.<String>builder()
                .page(2)
                .size(10)
                .totalElements(50L)
                .totalPages(5)
                .content(content)
                .build();

        assertEquals(response1, response2);
        assertNotEquals(response1, response3);
        assertEquals(response1.hashCode(), response2.hashCode());
    }

    @Test
    void pageResponse_toString() {
        PageResponse<String> response = PageResponse.<String>builder()
                .page(0)
                .size(10)
                .build();

        String toString = response.toString();
        assertTrue(toString.contains("page"));
        assertTrue(toString.contains("size"));
    }

    @Test
    void pageResponse_noArgsConstructor() {
        PageResponse<Object> response = new PageResponse<>();
        assertNotNull(response);
        assertEquals(0, response.getPage());
        assertEquals(0, response.getSize());
        assertEquals(0L, response.getTotalElements());
        assertEquals(0, response.getTotalPages());
        assertNull(response.getContent());
    }

    @Test
    void pageResponse_allArgsConstructor() {
        List<String> content = List.of("constructor", "test");
        PageResponse<String> response = new PageResponse<>(1, 20, 100L, 5, content);

        assertEquals(1, response.getPage());
        assertEquals(20, response.getSize());
        assertEquals(100L, response.getTotalElements());
        assertEquals(5, response.getTotalPages());
        assertEquals(content, response.getContent());
    }

    @Test
    void pageResponse_withDifferentGenericTypes() {
        // Test with MovieDto type
        List<MovieDto> movieContent = List.of(
                MovieDto.builder().movieId(1).title("Movie 1").build(),
                MovieDto.builder().movieId(2).title("Movie 2").build()
        );

        PageResponse<MovieDto> movieResponse = PageResponse.<MovieDto>builder()
                .page(0)
                .size(2)
                .totalElements(2L)
                .totalPages(1)
                .content(movieContent)
                .build();

        assertEquals(2, movieResponse.getContent().size());
        assertEquals("Movie 1", movieResponse.getContent().get(0).getTitle());
    }
}
