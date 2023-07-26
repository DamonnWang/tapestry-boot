package org.example.inter.data;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

/**
 * @author Damon
 * @date 2023/6/25
 **/
@Data
@Builder
@AllArgsConstructor
@ToString
public class User implements Serializable {
    private String name;
    private int age;
}
