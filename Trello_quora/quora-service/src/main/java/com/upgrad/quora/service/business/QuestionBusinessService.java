package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;

@Service
public class QuestionBusinessService {
    @Autowired
    private UserDao userDao;

    @Autowired
    private QuestionDao questionDao;

    @Autowired
    private UserBusinessService userBusinessService;

    //**postQuestion**//
    //This method receives questionEntity and accessToken as input parameters for creating the question, but before that
    // two checks are done whether a user is signedin or signedout. After successfull validation the question will be persisted
    //in the database by calling persistQuestion() method of QuestionDao

    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity postQuestion(final QuestionEntity questionEntity, final String accessToken) throws AuthorizationFailedException{
        UserAuthTokenEntity userAuthToken = userDao.getUserAuthToken(accessToken);
        if(userAuthToken == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }
        ZonedDateTime logoutTime = userAuthToken.getLogoutAt();
        if(logoutTime != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to post a question");
        }
        questionEntity.setUser(userAuthToken.getUser());
        return questionDao.persistQuestion(questionEntity);
    }

}


