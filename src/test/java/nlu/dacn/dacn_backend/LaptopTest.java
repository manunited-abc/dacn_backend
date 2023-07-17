package nlu.dacn.dacn_backend;

import nlu.dacn.dacn_backend.dto.request.LaptopDTO;
import nlu.dacn.dacn_backend.service.impl.LaptopService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@SpringBootTest(classes = LaptopTest.class)
@ComponentScan({"nlu.dacn.dacn_backend"})
public class LaptopTest {
    @Autowired
    LaptopService laptopService;
    @Test
    void search(){
        List<LaptopDTO> result = laptopService.search("8gb");
    }

    void test(){
        // Creating an empty array list
        HashSet<String> bags = new HashSet<String>();

        // Add values in the bags Set.
        bags.add("pen");
        bags.add("ink");
        bags.add("paper");

        // Creating another empty array list
        List<String> boxes = new ArrayList<String>();

        // Add values in the boxes list.
        boxes.add("pen");
        boxes.add("paper");
        boxes.add("books");
        boxes.add("rubber");
        boxes.add("ink");

        // Before Applying method print both list and set.
        System.out.println("Bags Contains :" + bags);
        System.out.println("Boxes Contains :" + boxes);

        // Apply retainAll() method to boxes passing bags as parameter
        boxes.retainAll(bags);

        // Displaying both the lists after operation
        System.out.println("\nAfter Applying retainAll()" +
                " method to Boxes\n");
        System.out.println("Bags Contains :" + bags);
        System.out.println("Boxes Contains :" + boxes);
    }
}
