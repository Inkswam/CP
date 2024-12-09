package org.example;
import com.google.gson.Gson;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

// Address class
class Address implements Serializable {
    private String city;
    private String street;

    // Default constructor for YAML/JSON
    public Address() {
    }

    public Address(String city, String street) {
        this.city = city;
        this.street = street;
    }

    // Getter and setter methods for YAML/JSON serialization
    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    @Override
    public String toString() {
        return "Address{" + "city='" + city + '\'' + ", street='" + street + '\'' + '}';
    }
}

// Student class
class Student implements Serializable {
    private String name;


    @JsonIgnore // Jackson exclusion
    private transient int room; // Will not be serialized

    private double payment;
    private boolean discount;
    private Address address;

    // Default constructor for YAML/JSON
    public Student() {
    }

    public Student(String name, int room, double payment, boolean discount, Address address) {
        this.name = name;
        this.room = room;
        this.payment = payment;
        this.discount = discount;
        this.address = address;
    }

    // Getters and setters (if needed for serialization)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRoom() {
        return room;
    }

    public void setRoom(int room) {
        this.room = room;
    }

    public double getPayment() {
        return payment;
    }

    public void setPayment(double payment) {
        this.payment = payment;
    }

    public boolean isDiscount() {
        return discount;
    }

    public void setDiscount(boolean discount) {
        this.discount = discount;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "Student{" +
                "name='" + name + '\'' +
                ", room=" + room +
                ", payment=" + payment +
                ", discount=" + discount +
                ", address=" + address +
                '}';
    }
}

public class Main {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        List<Student> students = new ArrayList<>();
        students.add(new Student("Alice", 101, 1500.0, false, new Address("Kyiv", "Main St")));
        students.add(new Student("Bob", 99, 1200.0, true, new Address("Lviv", "Central Ave")));

        // 1. File I/O Streams
        writeAndReadStreams(students);

        // 2. Native Serialization
        serializeStudents(students);

        // 3. JSON Serialization
        serializeToJson(students);

        // 4. YAML Serialization
        serializeToYaml(students);
    }

    private static void writeAndReadStreams(List<Student> students) throws IOException {
        try (FileOutputStream fos = new FileOutputStream("students_rooms.dat");
             BufferedWriter bos = new BufferedWriter(new FileWriter("students_payment.dat"))) {
            for (Student student : students) {
                fos.write(student.getRoom());
                bos.write(Double.toString(student.getPayment()));
                bos.newLine();
            }
        }

        try (FileInputStream fis = new FileInputStream("students_rooms.dat");
             BufferedReader bis = new BufferedReader(new FileReader("students_payment.dat"))) {
            System.out.println("Reading student room and payment details:");
            int room;
            while ((room = fis.read()) != -1) {
                System.out.println("Room: " + room);
            }
            bis.lines().forEach(line -> System.out.println("Payment: " + line));
        }
    }

    private static void serializeStudents(List<Student> students) throws IOException, ClassNotFoundException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("students.ser"))) {
            for (Student student : students) {
                if (student.getRoom() <= 100) {
                    oos.writeObject(student);
                }
            }
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("students.ser"))) {
            System.out.println("Deserialized Students:");
            while (true) {
                try {
                    System.out.println(ois.readObject());
                } catch (EOFException e) {
                    break;
                }
            }
        }
    }

    private static void serializeToJson(List<Student> students) throws IOException {
        Gson gson = new Gson();
        String json = gson.toJson(students);
        Files.write(Paths.get("students.json"), json.getBytes());

        String content = Files.readString(Paths.get("students.json"));
        Student[] deserialized = gson.fromJson(content, Student[].class);
        System.out.println("Deserialized JSON Students:");
        for (Student student : deserialized) {
            System.out.println(student);
        }
    }

    private static void serializeToYaml(List<Student> students) throws IOException {
        DumperOptions options = new DumperOptions();
        options.setPrettyFlow(true);
        Yaml yaml = new Yaml(options);

        List<Student> filtered = new ArrayList<>();
        for (Student student : students) {
            if (!student.isDiscount()) {
                filtered.add(student);
            }
        }

        String yamlString = yaml.dump(filtered);
        Files.write(Paths.get("students.yaml"), yamlString.getBytes());

        String content = Files.readString(Paths.get("students.yaml"));
        System.out.println("Deserialized YAML Students:");
        System.out.println(content);
    }
}
