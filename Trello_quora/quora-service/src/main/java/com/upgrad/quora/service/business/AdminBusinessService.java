package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.AdminDao;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

@Service
public class AdminBusinessService {

    @Autowired
    private UserBusinessService userBusinessService;

    @Autowired
    private AdminDao adminDao;

    public boolean confirmAdmin(final String accessToken) throws AuthorizationFailedException {
        UserAuthTokenEntity userByToken = userBusinessService.getUserByToken(accessToken);
        if (userByToken.getUserId().getRole().equals("admin")) {
            return true;
        } else {
            throw new AuthorizationFailedException("ATHR-003", "Unauthorized Access, Entered user is not an admin");
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public String deleteUser(String accessToken, String userId) throws AuthorizationFailedException, UserNotFoundException {
        UserEntity userById = userBusinessService.getUserById(userId);
        if (this.confirmAdmin(accessToken)) {
            adminDao.deleteUserByUuid(userId);
        }
        return userId;
    }
}
