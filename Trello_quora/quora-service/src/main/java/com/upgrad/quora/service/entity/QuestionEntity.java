package com.upgrad.quora.service.entity;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Date;

@Entity
@Table(name = "question", schema = "quora")
public class QuestionEntity implements Serializable {

    @Id
    //@Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ManyToOne
    @JoinColumn(name = "ID")
    private AnswerEntity id;

    @Column(name = "UUID")
    @NotNull
    private int uuid;

    @Column(name = "CONTENT")
    @NotNull
    @Size(max = 500)
    private String content;
    @Column(name = "DATE")
    private Date date;


    @Column(name = "USER_ID")
    private Integer user_id;

    public AnswerEntity getId() {
        return id;
    }

    public void setId(AnswerEntity id) {
        this.id = id;
    }

    public int getUuid() {
        return uuid;
    }

    public void setUuid(int uuid) {
        this.uuid = uuid;
    }

        public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Integer getUser_id() {
        return user_id;
    }

    public void setUser_id(Integer user_id) {
        this.user_id = user_id;
    }
   /* public RoleEntity(@NotNull int uuid) {
        this.uuid = uuid;
    }*/

    @Override
    public boolean equals(Object obj) {
        return new EqualsBuilder().append(this, obj).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(this).hashCode();
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}

