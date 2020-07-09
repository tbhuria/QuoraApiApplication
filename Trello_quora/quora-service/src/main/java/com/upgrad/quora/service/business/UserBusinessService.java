package com.upgrad.quora.service.business;

import com.upgrad.quora.service.Util.QuoraUtil;
import com.upgrad.quora.service.common.GenericErrorCode;
import com.upgrad.quora.service.common.UnexpectedException;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import javafx.animation.ScaleTransition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.Base64;

@Service
public class UserBusinessService {
    @Autowired
    private UserDao userDao;

    @Autowired
    private PasswordCryptographyProvider passwordCryptographyProvider;

    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity signup(UserEntity userEntity) throws SignUpRestrictedException {
        if (userDao.isUsernameExists(userEntity.getUserName())) {
            throw new SignUpRestrictedException("SGR-001", "Try any other Username, this Username has already been taken");
        }
        if (userDao.isEmailExists(userEntity.getEmail())) {
            if (userDao.isEmailExists(userEntity.getEmail())) {
                throw new SignUpRestrictedException("SGR-001", "This user has already been registered, try with any other emailId");
            }
        }

        String[] encryptedText = passwordCryptographyProvider.encrypt(userEntity.getPassword());
        userEntity.setSalt(encryptedText[0]);
        userEntity.setPassword(encryptedText[1]);
        userEntity.setRole(QuoraUtil.NON_ADMIN_ROLE);
        UserEntity signup = userDao.createUser(userEntity);

        return signup;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public UserAuthTokenEntity signin(final String username, final String password) throws AuthenticationFailedException {
        try {
            UserEntity user = userDao.getUserByUserName(username);
            if (user == null) {
                throw new AuthenticationFailedException("ATH-001", "This username does not exist");
            }
            final String encryptedPassword = passwordCryptographyProvider.encrypt(password, user.getSalt());
            if (encryptedPassword.equals(user.getPassword())) {
                JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(encryptedPassword);
                UserAuthTokenEntity userAuthTokenEntity = new UserAuthTokenEntity();
                userAuthTokenEntity.setUser(user);
                final ZonedDateTime now = ZonedDateTime.now();
                final ZonedDateTime expiresAt = now.plusHours(8);
                userAuthTokenEntity.setAccessToken(jwtTokenProvider.generateToken(user.getUuid(), now, expiresAt));
                userAuthTokenEntity.setLoginAt(now);
                userAuthTokenEntity.setLogoutAt(expiresAt);
                userAuthTokenEntity.setUuid(user.getUuid());
                return userDao.createAuthToken(userAuthTokenEntity);
            } else {
                throw new AuthenticationFailedException("ATH-002", "Password failed");
            }
        } catch (ArrayIndexOutOfBoundsException | IllegalArgumentException ex) {
            GenericErrorCode genericErrorCode = GenericErrorCode.GEN_001;
            throw new UnexpectedException(genericErrorCode, ex);
        }
    }
}


