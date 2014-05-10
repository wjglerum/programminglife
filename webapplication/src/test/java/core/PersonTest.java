package core;
import static org.junit.Assert.assertTrue;
import core.Person;

import org.junit.Test;

/**
 * Some simple tests checking the validation when creating a person object
 * 
 * @author Mathijs Hoogland
 *
 */
public class PersonTest {
    
    Person testPerson = new Person();
    /**
     * Are we able to instantiate a person that has a negative age?
     */
    //TODO: dit kan blijkbaar nog wel, fixen!
//    @Test
//    public void negativeAge() {
//        testPerson.setAge(-4);
//        System.out.print(testPerson.getAge());
//        assertTrue(testPerson.getAge() == null);
//    }
    
    @Test
    public void goodAge() {
        testPerson.setAge(20);

        assertTrue(testPerson.getAge() == 20);
    }

}
