package core;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Person class, alle patiënten worden in dit object gecreëerd.
 * @author mathijs
 *
 */
public class Person {
    /**
     * Een naam bestaat uit minimaal 2, en maximaal 30 tekens.
     */
    @Size(min = 2, max = 30)
    private String name;

    @NotNull
    @Min(0)
    private Integer age;

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String toString() {
        return "Person(Name: " + this.name + ", Age: " + this.age + ")";
    }
}
