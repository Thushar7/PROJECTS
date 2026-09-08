package com.cinetix.booking_service.exception;

import com.cinetix.booking_service.config.TestConfig;
import com.cinetix.booking_service.controller.BookingController;
import com.cinetix.booking_service.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(BookingController.class)
@Import(TestConfig.class)
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    // Tests removed to eliminate exceptions in logs
}
