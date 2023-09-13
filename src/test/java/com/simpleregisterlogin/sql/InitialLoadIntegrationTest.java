package com.simpleregisterlogin.sql;

import com.simpleregisterlogin.repositories.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

@SpringBootTest
@ActiveProfiles("test")
public class InitialLoadIntegrationTest {

    private final UserRepository userRepository;

    @Autowired
    public InitialLoadIntegrationTest(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Test
    @Sql({"/db/test/clear_tables.sql", "/db/test/insert_users.sql"})
    public void whenLoadInsertPlayersSQLData_AllUsersInserted() {
        Assertions.assertEquals(5, userRepository.findAll().size());
        Assertions.assertTrue(userRepository.findById(1L).isPresent());
        Assertions.assertTrue(userRepository.findById(4L).isPresent());
        Assertions.assertTrue(userRepository.findById(5L).isPresent());
        Assertions.assertFalse(userRepository.findById(9898L).isPresent());
    }
}