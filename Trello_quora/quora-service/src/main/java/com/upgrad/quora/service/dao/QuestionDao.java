package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.QuestionEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class QuestionDao {

    //This annotation PersistenceContext handles a set of entities that hold data to be persisted in the database
    @PersistenceContext
    private EntityManager entityManager;

    //This method persists the newly created question entity in the database
    public QuestionEntity persistQuestion(final QuestionEntity questionEntity) {
        entityManager.persist(questionEntity);
        return questionEntity;
    }

    //This method retrieves all the list of questions from the database
    public List<QuestionEntity> getAllQuestions() {
        try {
            return entityManager.createNamedQuery("ListofAllQuestions", QuestionEntity.class).getResultList();
        } catch (NoResultException nre) {
            return null;
        }
    }
    //This method updates the question in the database
    public QuestionEntity updateQuestion(final QuestionEntity questionEntity) {
        QuestionEntity updatedQ = entityManager.merge(questionEntity);
        return updatedQ;
    }
    //This method retrieves the question based on question uuid, if found returns question else null
    public QuestionEntity getQuestionByUuid(final String uuid) {
        try {
            return entityManager.createNamedQuery("QuestionByUUID", QuestionEntity.class).setParameter("uuid", uuid).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    //This method deletes the question record from database
    public void deleteQuestion(final QuestionEntity questionEntity) {
        entityManager.remove(questionEntity);
    }
}
