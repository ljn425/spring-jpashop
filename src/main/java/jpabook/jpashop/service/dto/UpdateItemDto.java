package jpabook.jpashop.service.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter
@ToString
public class UpdateItemDto {
    private String name;
    private int price;
    private int stockQuantity;

}
