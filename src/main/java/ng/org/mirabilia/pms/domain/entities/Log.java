package ng.org.mirabilia.pms.domain.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import ng.org.mirabilia.pms.domain.enums.Action;
import ng.org.mirabilia.pms.domain.enums.Module;

import java.sql.Timestamp;
import java.util.UUID;

@Getter
@Setter
@Entity
public class Log {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    UUID uuid;
    String initiator;
    Action action;
    Module moduleOfAction;
    Timestamp timestamp;
}
