package nlu.dacn.dacn_backend.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Setter
    @CreatedBy
    private String createBy;
    @Setter
    @CreatedDate
    private Date createdDate;
    @Setter
    @LastModifiedBy
    private String modifiedBy;
    @Setter
    @LastModifiedDate
    private Date modifiedDate;
}
