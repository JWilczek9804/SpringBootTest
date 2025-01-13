package com.wilczek;
import com.github.javafaker.Faker;
import com.github.javafaker.Name;
import com.wilczek.customer.Customer;
import com.wilczek.customer.CustomerRepository;
import com.wilczek.customer.Gender;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import java.util.*;

@SpringBootApplication
public class Main {

    public static void main(String[] args) {
       SpringApplication.run(Main.class, args);
    }

    @Bean
    CommandLineRunner runner(CustomerRepository customerRepository){
        return args -> {
            var faker = new Faker();
            Name name = faker.name();
            String firstName = name.firstName();
            String lastName = name.lastName();
            int age = new Random().nextInt(20, 50);
            Gender gender = age % 2 == 0 ? Gender.MALE : Gender.FEMALE;
            Customer customer = new Customer(
                    firstName + " " + lastName,
                    "%s.%s@gmail.com".formatted(firstName.toLowerCase(), lastName.toLowerCase()),
                    "password", age, gender);
            customerRepository.save(customer);
        };
    }

//    @Bean
//    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON) // Domyślna wartość zakresu, w której używana jest pojedyńcza instancja na cały kontener
//    public Foo getFoo(){
//        return new Foo("bar");
//    }
//    record Foo(String name){}
//
//    public static void printBeans(ConfigurableApplicationContext ctx) {
//
//        String[] beanDefinitionNames = ctx.getBeanDefinitionNames();
//        Arrays.stream(beanDefinitionNames).forEach(System.out::println);
//    }


//    @RequestMapping(method = RequestMethod.GET,value = "/test")
//    @GetMapping("/greet")
//    public GreetResponse greet(
//            @RequestParam(value = "name", required = false) String mess,
//            @RequestParam(value = "surname", required = false) String sur)
//    {
//        String getMessage = mess == null || mess.isBlank() ? "Hello" : "Hello " + mess;
//        String surname = sur == null || sur.isBlank() ? getMessage : getMessage + " " + sur;
//        return new GreetResponse(surname,
//                List.of("Java","JS","Python"),
//                List.of(new Person("Bartek","Zawada"),
//                        new Person("Karol","Buta"),
//                        new Person("Jarek","Baniak")),
//                new Person("Jakub", "Wilczek"));
//    }
//
//    record Person(String name,String surname){}
//    record GreetResponse(String greet, List<String> favProgrammingLang, List<Person> allCoworkers, Person person){}

//class GreetResponse{ // poniżej napisana klasa jest 1 do 1 utworzonym recordem.
//        private final String greet;
//
//    GreetResponse(String greet) {
//        this.greet = greet;
//    }
//
//    public String getGreet() { // bez tej metody na stronie nie wyświetli się prawidłowo JSON
//        return greet;
//    }
//
//    @Override
//    public String toString() {
//        return "GreetResponse{" +
//                "greet='" + greet + '\'' +
//                '}';
//    }
//
//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//        GreetResponse that = (GreetResponse) o;
//        return Objects.equals(greet, that.greet);
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(greet);
//    }
//}
}
