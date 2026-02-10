import java.util.*;
import java.util.stream.Collectors;

/**
 * Gestor de cursos con soporte para paginación y ordenamiento
 * Implementa funcionalidad similar a Spring Data JPA
 */
public class CourseManager {

    private List<Course> courses;

    public CourseManager() {
        this.courses = new ArrayList<>();
        initializeData();
    }

    /**
     * Inicializa los datos de prueba
     */
    private void initializeData() {
        courses.add(new Course(1, "Anatomía Humana", "Juan Pérez", 5));
        courses.add(new Course(2, "Fisiología", "Juan Pérez", 5));
        courses.add(new Course(3, "Derecho Penal", "María López", 4));
        courses.add(new Course(4, "Derecho Civil", "María López", 4));
        courses.add(new Course(5, "Historia del Arte", "Carlos García", 3));
        courses.add(new Course(6, "Introducción a la Programación", "Carlos García", 3));
    }

    /**
     * Obtiene una página de cursos con soporte para ordenamiento
     * 
     * @param page Número de página (base 0)
     * @param size Tamaño de la página
     * @param sort Campo por el que ordenar (name, credits, professor)
     * @param direction Dirección del ordenamiento (asc, desc)
     * @return Page<Course> con los resultados paginados
     */
    public Page<Course> getPaginatedCourses(int page, int size, String sort, String direction) {
        if (page < 0) page = 0;
        if (size <= 0) size = 3;

        List<Course> sortedCourses = sortCourses(courses, sort, direction);

        int totalElements = sortedCourses.size();
        int totalPages = (int) Math.ceil((double) totalElements / size);

        if (page >= totalPages && totalPages > 0) {
            page = totalPages - 1;
        }

        int startIndex = page * size;
        int endIndex = Math.min(startIndex + size, totalElements);

        List<Course> pageContent;
        if (startIndex < totalElements) {
            pageContent = sortedCourses.subList(startIndex, endIndex);
        } else {
            pageContent = new ArrayList<>();
        }

        return new Page<>(pageContent, page, size, totalElements, totalPages);
    }

    /**
     * Ordena una lista de cursos
     */
    private List<Course> sortCourses(List<Course> courseList, String sort, String direction) {
        List<Course> result = new ArrayList<>(courseList);
        boolean ascending = "asc".equalsIgnoreCase(direction);

        switch (sort.toLowerCase()) {
            case "name":
                result.sort((c1, c2) -> {
                    int comparison = c1.getName().compareToIgnoreCase(c2.getName());
                    return ascending ? comparison : -comparison;
                });
                break;

            case "credits":
                result.sort((c1, c2) -> {
                    int comparison = Integer.compare(c1.getCredits(), c2.getCredits());
                    return ascending ? comparison : -comparison;
                });
                break;

            case "professor":
                result.sort((c1, c2) -> {
                    int comparison = c1.getProfessor().compareToIgnoreCase(c2.getProfessor());
                    return ascending ? comparison : -comparison;
                });
                break;

            default:
                result.sort((c1, c2) -> {
                    int comparison = c1.getName().compareToIgnoreCase(c2.getName());
                    return ascending ? comparison : -comparison;
                });
        }

        return result;
    }

    /**
     * Obtiene todos los cursos
     */
    public List<Course> getAllCourses() {
        return new ArrayList<>(courses);
    }

    /**
     * Obtiene un curso por ID
     */
    public Course getCourseById(int id) {
        return courses.stream()
                .filter(c -> c.getId() == id)
                .findFirst()
                .orElse(null);
    }

    /**
     * Obtiene cursos de un profesor específico
     */
    public List<Course> getCoursesByProfessor(String professor) {
        return courses.stream()
                .filter(c -> c.getProfessor().equalsIgnoreCase(professor))
                .collect(Collectors.toList());
    }

    /**
     * Obtiene cursos con un número de créditos específico
     */
    public List<Course> getCoursesByCredits(int credits) {
        return courses.stream()
                .filter(c -> c.getCredits() == credits)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene cursos con créditos en un rango (Between)
     */
    public List<Course> getCoursesByCreditsRange(int minCredits, int maxCredits) {
        return courses.stream()
                .filter(c -> c.getCredits() >= minCredits && c.getCredits() <= maxCredits)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene el primer curso ordenado por nombre ascendente
     */
    public Course findFirstByOrderByNameAsc() {
        return courses.stream()
                .sorted(Comparator.comparing(Course::getName))
                .findFirst()
                .orElse(null);
    }

    /**
     * Obtiene los Top N cursos por créditos
     */
    public List<Course> findTopByCreditsDesc(int limit) {
        return courses.stream()
                .sorted((c1, c2) -> Integer.compare(c2.getCredits(), c1.getCredits()))
                .limit(limit)
                .collect(Collectors.toList());
    }
}
