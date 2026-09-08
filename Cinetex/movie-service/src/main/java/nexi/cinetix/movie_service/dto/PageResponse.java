package nexi.cinetix.movie_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> {
    private int page;          // zero-based page index
    private int size;          // page size
    private long totalElements;
    private int totalPages;
    private List<T> content;
}

