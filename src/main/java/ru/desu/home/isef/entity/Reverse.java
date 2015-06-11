/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.desu.home.isef.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@lombok.extern.java.Log
@Entity
@Getter @Setter
@NoArgsConstructor
public class Reverse implements Serializable {
    @Id
    @Column(name = "reverse_id")
    @SequenceGenerator(name = "SeqReverse", sequenceName = "SEQ_REVERSE", allocationSize = 1, initialValue = 6 )
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SeqReverse")
    Long id;
    
    @Column(nullable = false)
    double coefficient = 0.0;
}
