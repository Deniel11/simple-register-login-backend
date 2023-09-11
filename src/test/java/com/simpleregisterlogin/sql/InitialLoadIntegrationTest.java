package com.simpleregisterlogin.sql;

import com.simpleregisterlogin.repositories.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.NoSuchElementException;

@SpringBootTest
@ActiveProfiles("test")
public class InitialLoadIntegrationTest {

    private UserRepository userRepository;

    @Autowired
    public InitialLoadIntegrationTest(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Test
    @Sql("/db/test/clear_tables.sql")
    @Sql("/db/test/insert_users.sql")
    public void whenLoadInsertPlayersSQLData_AllUsersInserted() {
        Assertions.assertEquals(5, userRepository.findAll().size());
        Assertions.assertEquals("Sanyi", userRepository.findById(1L).get().getUsername());
        Assertions.assertTrue(userRepository.findById(4L).get().getValid());
        Assertions.assertTrue(userRepository.findById(5L).get().getAdmin());
        Assertions.assertThrows(NoSuchElementException.class, () -> userRepository.findById(9898L).get().getUsername());
    }
}