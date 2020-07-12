package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.UserNotFoundException;
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
    public QuestionEntity postQuestion(final QuestionEntity questionEntity, final String accessToken) throws AuthorizationFailedException {
        UserAuthTokenEntity userAuthToken = userDao.getUserAuthToken(accessToken);
        if (userAuthToken == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }
        ZonedDateTime logoutTime = userAuthToken.getLogoutAt();
        if (logoutTime != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to post a question");
        }
        questionEntity.setUser(userAuthToken.getUser());
        return questionDao.persistQuestion(questionEntity);
    }
    //**getAllQuestions**//
    //This method fetches all the questions that are posted in the application, but before that two checks are done
    //whether a user is signedin or signedout. After successfull validation, a user can view all the list of questions

    @Transactional(propagation = Propagation.REQUIRED)
    public List<QuestionEntity> getAllQuestions(final String accessToken) throws AuthorizationFailedException {
        UserAuthTokenEntity userAuthToken = userDao.getUserAuthToken(accessToken);
        if (userAuthToken == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }
        ZonedDateTime logoutTime = userAuthToken.getLogoutAt();
        if (logoutTime != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get all questions");
        }
        return questionDao.getAllQuestions();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity editQuestion(final QuestionEntity questionEntity, final String accessToken) throws AuthorizationFailedException {
        UserAuthTokenEntity userAuthToken = userDao.getUserAuthToken(accessToken);
        if (userAuthToken == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }
        ZonedDateTime logoutTime = userAuthToken.getLogoutAt();
        if (logoutTime != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to edit the question");
        }
        String questionOwnerUuid = questionEntity.getUser().getUuid();
        String signedInUserUuid = userAuthToken.getUser().getUuid();

        if (questionOwnerUuid.equals(signedInUserUuid)) {
            QuestionEntity updatedQuestion = questionDao.updateQuestion(questionEntity);
            return updatedQuestion;
        } else {
            throw new AuthorizationFailedException("ATHR-003", "Only the question owner can edit the question");
        }
    }

    //This method retrieves the question in the database
    public QuestionEntity getQuestion(final String questionUuid, final String accessToken) throws InvalidQuestionException {

        QuestionEntity questionEntity = questionDao.getQuestionByUuid(questionUuid);
        if (questionEntity == null) {
            throw new InvalidQuestionException("QUES-001", "Entered question uuid does not exist");
        }
        return questionEntity;

    }

    @Transactional(propagation = Propagation.REQUIRED)
    public String deleteQuestion(final String questionIdUuid, final String accessToken) throws AuthorizationFailedException, InvalidQuestionException {
        UserAuthTokenEntity userAuthToken = userDao.getUserAuthToken(accessToken);
        //If the accessToken of admin or QuestionOwner doesnt exist in the database throw following Exception
        //It means that if the user hasnt signedin, then the basic Authentication is not done and the accessToken is not generated
        if (userAuthToken == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }
        //we have logoutAt attribute in the userAuth table, upon successfull signout of the application
        //the user logoutAt attribute will be updated. So if the logoutAt is not null then it means that user has signed out
        ZonedDateTime logoutTime = userAuthToken.getLogoutAt();
        if (logoutTime != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to delete a question");
        }
        //If the questionUuid doesnt exist in the database throw following exception
        QuestionEntity questionEntity = questionDao.getQuestionByUuid(questionIdUuid);
        if (questionEntity == null) {
            throw new InvalidQuestionException("QUES-001", "Entered question uuid does not exist");
        }
        //The owneroftheQuestion or an admin will have privelege of deleting the Question
        //The user with the role non-admin and non-owner of the Question is trying to delete the Question
        //then following exception is thrown
        String role = userAuthToken.getUser().getRole();
        String questionOwnerUuid = questionEntity.getUser().getUuid();
        String signedInUserUuid = userAuthToken.getUser().getUuid();

        if (role.equals("admin") || questionOwnerUuid.equals(signedInUserUuid)) {
            questionDao.deleteQuestion(questionEntity);
        } else {
            throw new AuthorizationFailedException("ATHR-003", "Only the question owner or admin can delete the question");
        }
        return questionIdUuid;
    }

    //** getAllQuestionsByUser **//
    //This method updates the question in the database retrieves all the questions post by a user from the database
    public List<QuestionEntity> getAllQuestionsByUser(final String accessToken, String userUuid) throws AuthorizationFailedException, UserNotFoundException {
        UserAuthTokenEntity userAuthTokenEntity = userDao.getUserAuthToken(accessToken);
        if (userAuthTokenEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }
        ZonedDateTime logoutTime = userAuthTokenEntity.getLogoutAt();
        if (logoutTime != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to edit the question");
        }
       //UserEntity userEntity = userBusinessService.getUser(userUuid, accessToken);
        UserEntity userEntity = userDao.getUserByUuid(userUuid);
        if (userEntity == null) {
            throw new UserNotFoundException("USR-001", "User with entered uuid whose question details are to be seen does not exist");
        }
        return questionDao.getAllQuestionsByUser(userUuid);
    }
}



