package com.ppossatto.librarymanager.unit.security.service;

import com.ppossatto.librarymanager.security.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

  @InjectMocks
  JwtService service;

  @BeforeEach
  void setUp(){
    ReflectionTestUtils.setField(service, "jwtSecret",
       "dGVzdC1zZWNyZXQta2V5LXRlc3Qtc2VjcmV0LWtleS10ZXN0LXNlY3JldC1rZXk=");
    ReflectionTestUtils.setField(service, "expiration", 86400000L);
    ReflectionTestUtils.invokeMethod(service, "initSecretKey");
  }
}
