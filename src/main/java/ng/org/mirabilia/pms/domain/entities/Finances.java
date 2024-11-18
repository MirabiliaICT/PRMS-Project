package ng.org.mirabilia.pms.domain.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import ng.org.mirabilia.pms.domain.enums.PropertyType;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Setter
@Data
@Entity
@Getter
public class Finances {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private Phase phase;

    @ManyToOne
    private User owner;

    private PropertyType type;

    private double price;

    private String paidBy;

    private LocalDate date;

    private LocalDateTime dateTime;

    private double amountPaid;

    private double outstandingAmount;

    public Finances(){}

    public Finances(Long id, State state, City city, Phase phase,
                    User owner, PropertyType type,
                    double price, String paidBy,
                    LocalDate date, double amountPaid,
                    double outstandingAmount, LocalDateTime dateTime) {
        this.id = id;
        this.phase = phase;
        this.owner = owner;
        this.type = type;
        this.price = price;
        this.paidBy = paidBy;
        this.date = date;
        this.amountPaid = amountPaid;
        this.outstandingAmount = outstandingAmount;
        this.dateTime = dateTime;
    }

    public String getAmountPaidFormattedToString(){
        String nairaSymbol = "₦";
        DecimalFormat decimalFormat = new DecimalFormat("#,###");
        String formattedAmount = decimalFormat.format(this.amountPaid);
        return nairaSymbol + formattedAmount;
    }

    public void updateOutstandingAmount() {
        outstandingAmount = this.price - this.amountPaid;
    }

    public String getOutstandingFormattedToString(){
        String nairaSymbol = "₦";
        DecimalFormat decimalFormat = new DecimalFormat("#,###");
        updateOutstandingAmount();
        String formattedAmount = decimalFormat.format(outstandingAmount);
        return nairaSymbol + formattedAmount;
    }


}
