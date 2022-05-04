package kr.goldenmine.flowerserver;


import java.util.ArrayList;
import java.util.List;

public class SimpleTest {
    public static void main(String[] args) {
        List<Integer> list = new ArrayList<>();
        list.add(0);
        list.add(1);
        list.add(2);

        list.add(3, 3);

        System.out.println(list);
    }
}
