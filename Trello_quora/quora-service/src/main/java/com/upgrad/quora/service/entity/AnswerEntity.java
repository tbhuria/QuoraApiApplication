package com.upgrad.quora.service.entity;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.hibernate.metamodel.internal.SingularAttributeImpl;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;

import static org.hibernate.id.PersistentIdentifierGenerator.SCHEMA;

@Entity
@Table(name = "answer")
@NamedQueries(
        {
                @NamedQuery(name = "answerByUUID", query = "select ans from AnswerEntity ans where ans.uuid = :uuid"),
                @NamedQuery(name = "answerByQuestionId", query = "select ans from AnswerEntity ans where ans.question_id = :questionId")
        }
)
public class AnswerEntity implements Serializable{

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "uuid")
    @NotNull
    private int uuid;

    @Column(name = "ans")
    @NotNull
    @Size(max = 255)
    private String ans;

    @Column(name = "date")
    private ZonedDateTime date;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user_id;

    @ManyToOne
    @JoinColumn(name = "question_id")
    private QuestionEntity question_id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUuid() {
        return uuid;
    }

    public void setUuid(int uuid) {
        this.uuid = uuid;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAns() {
        return ans;
    }

    public void setAns(String ans) {
        this.ans = ans;
    }

    public ZonedDateTime getDate() {
        return date;
    }

    public void setDate(ZonedDateTime date) {
        this.date = date;
    }

    public UserEntity getUser_id() {
        return user_id;
    }

    public void setUser_id(UserEntity user_id) {
        this.user_id = user_id;
    }

    public QuestionEntity getQuestion_id() {
        return question_id;
    }

    public void setQuestion_id(QuestionEntity question_id) {
        this.question_id = question_id;
    }

    /* public RoleEntity(@NotNull int uuid) {
        this.uuid = uuid;
    }*/


}