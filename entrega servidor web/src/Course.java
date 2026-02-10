/**
 * Modelo de datos para un Curso
 */
public class Course {
    private int id;
    private String name;
    private String professor;
    private int credits;

    public Course(int id, String name, String professor, int credits) {
        this.id = id;
        this.name = name;
        this.professor = professor;
        this.credits = credits;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfessor() {
        return professor;
    }

    public void setProfessor(String professor) {
        this.professor = professor;
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    @Override
    public String toString() {
        return "Course{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", professor='" + professor + '\'' +
                ", credits=" + credits +
                '}';
    }
}
