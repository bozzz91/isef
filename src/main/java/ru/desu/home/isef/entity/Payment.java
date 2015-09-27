package ru.desu.home.isef.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Getter @Setter
public class Payment implements Serializable {

    @Id
    @SequenceGenerator(name = "SeqPayment", sequenceName = "SEQ_PAYMENT", allocationSize = 1, initialValue = 1 )
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SeqPayment")
    Long id;
    
    // 0 - оплата нам, 1 - выплата им
    @Column(nullable = false)
    int type = 0;

	// 0 - создан, 1 - оплачен
    @Column(nullable = false)
    int status = 0;
    
    @Column(name = "onpay_id", nullable = false)
    int onpayId;
    
    @Column(name = "order_amount", nullable = false)
    double orderAmount;
    @Column(name = "order_amount_rub", nullable = false)
    double orderAmountRub;
    
    @Column(name = "balance_amount", nullable = false)
    double balanceAmount;
    @Column(name = "balance_amount_rub", nullable = false)
    double balanceAmountRub;

	@Column
	String info;
    
    @ManyToOne(optional = false)
    Person payOwner;
    
    @ManyToOne
    PersonWallet wallet;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "order_date")
    Date orderDate;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "pay_date")
    Date payDate;
}
