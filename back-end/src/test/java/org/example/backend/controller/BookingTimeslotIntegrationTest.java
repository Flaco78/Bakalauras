package org.example.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.backend.dto.BookingDTO;
import org.example.backend.dto.TimeslotDTO;
import org.example.backend.enums.*;
import org.example.backend.model.*;
import org.example.backend.repository.*;
import org.example.backend.security.JwtUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class BookingTimeslotIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ProviderRepository providerRepository;
    @Autowired
    private ActivityRepository activityRepository;
    @Autowired
    private TimeslotRepository timeslotRepository;
    @Autowired
    private ChildProfileRepository childProfileRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private ObjectMapper objectMapper;

    private String providerToken;
    private String parentToken;
    private Activity activity;
    private ChildProfile child;
    private Long providerId;
    private Long activityId;
    private Long timeslotId;
    private Long childId;
    private Long parentId;

    @BeforeEach
    void setUp() {
        Role providerRole = roleRepository.findByName("PROVIDER")
                .orElseThrow(() -> new RuntimeException("PROVIDER role not found in DB"));
        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new RuntimeException("USER role not found in DB"));

        // Provider
        Provider provider = new Provider();
        provider.setEmail("provider@example.com");
        provider.setName("Test Provider");
        provider.setDescription("Test Provider");
        provider.setPhone("+37060186472");
        provider.setPassword(passwordEncoder.encode("providerpass"));
        provider.setRoles(Set.of(providerRole));
        provider = providerRepository.save(provider);
        providerToken = jwtUtil.generateToken(provider.getEmail(), provider.getRoles().stream().map(Role::getName).toList());
        providerId = provider.getId();

        // Activity
        activity = new Activity();
        activity.setTitle("Test Activity");
        activity.setDescription("desc");
        activity.setDescriptionChild("desc child");
        activity.setCategory(ActivityCategory.SPORTS);
        activity.setLocation("Test Location");
        activity.setPrice(10.0);
        activity.setPriceType(PriceType.ONE_TIME);
        activity.setDurationMinutes(60);
        activity.setDeliveryMethod(DeliveryMethod.ONSITE);
        activity.setImageUrl("http://img.com/img.jpg");
        activity.setProvider(provider);
        activity = activityRepository.save(activity);
        activityId = activity.getId();

        // Parent & Child
        User parent = new User();
        parent.setEmail("parent@example.com");
        parent.setPassword(passwordEncoder.encode("parentpass"));
        parent.setAddress("Test Address");
        parent.setRoles(Set.of(userRole));
        parent = userRepository.save(parent);
        parentToken = jwtUtil.generateToken(parent.getEmail(), parent.getRoles().stream().map(Role::getName).toList());
        parentId = parent.getId();

        child = new ChildProfile();
        child.setName("Test Child");
        child.setBirthDate(LocalDate.of(2015, 1, 1));
        child.setGender(Gender.FEMALE);
        child.setMaxActivityDuration(60);
        child.setPreferredDeliveryMethod(DeliveryMethod.ONSITE);
        child.setParent(parent);
        child.setInterests(Set.of(ActivityCategory.SPORTS));
        child = childProfileRepository.save(child);
        childId = child.getId();
    }

    @AfterEach
    void tearDown() {
        if (timeslotId != null && timeslotRepository.existsById(timeslotId)) {
            timeslotRepository.deleteById(timeslotId);
        }

        if (activityId != null && activityRepository.existsById(activityId)) {
            activityRepository.deleteById(activityId);
        }

        if (providerId != null && providerRepository.existsById(providerId)) {
            providerRepository.deleteById(providerId);
        }

        if (childId != null && childProfileRepository.existsById(childId)) {
            childProfileRepository.deleteById(childId);
        }

        if (parentId != null && userRepository.existsById(parentId)) {
            userRepository.deleteById(parentId);
        }
    }

    @Test
    void fullBookingFlow() throws Exception {
        // 1. Provider creates a timeslot
        TimeslotDTO timeslotDTO = new TimeslotDTO();
        timeslotDTO.setStartDateTime(LocalDateTime.now().plusDays(1));
        timeslotDTO.setEndDateTime(LocalDateTime.now().plusDays(1).plusHours(1));
        timeslotDTO.setMaxParticipants(10);

        String timeslotJson = objectMapper.writeValueAsString(timeslotDTO);

        String timeslotResponse = mockMvc.perform(post("/api/timeslots/activity/{activityId}", activity.getId())
                        .header("Authorization", "Bearer " + providerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(timeslotJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andReturn().getResponse().getContentAsString();

        TimeslotDTO createdTimeslot = objectMapper.readValue(timeslotResponse, TimeslotDTO.class);
        timeslotId = createdTimeslot.getId();
        System.out.println("Created timeslot: " + timeslotResponse);


        // 2. Parent books the timeslot for the child
        String bookingRequestJson = """
            {
                "childId": %d,
                "timeSlotId": %d
            }
            """.formatted(child.getId(), createdTimeslot.getId());

        System.out.println("STEP 2: Parent books the timeslot...");
        String bookingResponse = mockMvc.perform(post("/api/bookings/create")
                        .header("Authorization", "Bearer " + parentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookingRequestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.childId", is(child.getId().intValue())))
                .andExpect(jsonPath("$.timeSlotId", is(createdTimeslot.getId().intValue())))
                .andReturn().getResponse().getContentAsString();

        BookingDTO booking = objectMapper.readValue(bookingResponse, BookingDTO.class);

        // 3. Check booking exists for child
        System.out.println("STEP 3: Check booking exists for child...");
        mockMvc.perform(get("/api/bookings/child/{childId}", child.getId())
                        .header("Authorization", "Bearer " + parentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(booking.getId().intValue())))
                .andExpect(jsonPath("$[0].status", is("ACTIVE")));

        // 4. Cancel the booking
        System.out.println("STEP 4: Cancel the booking...");
        mockMvc.perform(patch("/api/bookings/{bookingId}/cancel", booking.getId())
                        .header("Authorization", "Bearer " + parentToken))
                .andExpect(status().isOk());

        // 5. Check booking is cancelled
        System.out.println("STEP 5: Check booking is cancelled (should be empty)...");
        mockMvc.perform(get("/api/bookings/child/{childId}", child.getId())
                        .header("Authorization", "Bearer " + parentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0))); // Should be empty as only ACTIVE bookings are returned
    }
}
