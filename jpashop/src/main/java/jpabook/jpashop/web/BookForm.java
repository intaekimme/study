package jpabook.jpashop.web;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class BookForm {

    private Long id;

    private String Name;
    private int price;
    private int stockQuantity;
    private String Author;
    private String Isbn;
}
