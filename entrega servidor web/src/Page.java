import java.util.List;

/**
 * Clase genérica para representar una página de resultados
 * Similar a org.springframework.data.domain.Page
 */
public class Page<T> {
    private List<T> content;
    private int number; 
    private int size; 
    private long totalElements; 
    private int totalPages; 

    public Page(List<T> content, int number, int size, long totalElements, int totalPages) {
        this.content = content;
        this.number = number;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
    }


    public List<T> getContent() {
        return content;
    }

    public int getNumber() {
        return number;
    }

    public int getSize() {
        return size;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public boolean isFirst() {
        return number == 0;
    }

    public boolean isLast() {
        return number == totalPages - 1;
    }

    public boolean hasNext() {
        return number < totalPages - 1;
    }

    public boolean hasPrevious() {
        return number > 0;
    }

    @Override
    public String toString() {
        return "Page{" +
                "number=" + number +
                ", size=" + size +
                ", totalElements=" + totalElements +
                ", totalPages=" + totalPages +
                ", content size=" + (content != null ? content.size() : 0) +
                '}';
    }
}
