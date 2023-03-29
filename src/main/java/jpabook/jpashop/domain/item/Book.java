package jpabook.jpashop.domain.item;

import lombok.*;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("B")
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Book extends Item{

    private String author;
    private String isbn;


    @Builder
    public Book(String name, int price, int stockQuantity, String author, String isbn) {
        super(name, price, stockQuantity);
        this.author = author;
        this.isbn = isbn;
    }

}
