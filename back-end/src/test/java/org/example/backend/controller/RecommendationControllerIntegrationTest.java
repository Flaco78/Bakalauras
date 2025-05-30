package org.example.backend.controller;

import jakarta.transaction.Transactional;
import org.example.backend.enums.*;
import org.example.backend.model.*;
import org.example.backend.repository.*;
import org.example.backend.service.ActivityInteractionService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@Transactional
class RecommendationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProviderRepository providerRepository;

    @Autowired
    private ChildProfileRepository childProfileRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private ActivityInteractionService activityInteractionService;

    @Autowired
    private ActivityInteractionRepository activityInteractionRepository;

    private ChildProfile testChild;
    private User parent;
    private Provider provider1;
    private ActivityInteraction activityInteraction;
    private final Activity activity1 = new Activity();
    private final Activity activity2 = new Activity();
    private final Activity activity3 = new Activity();
    private final List<Long> createdInteractionIds = new ArrayList<>();

    private Role getOrCreateRole(String roleName) {
        return roleRepository.findByName(roleName)
                .orElseGet(() -> roleRepository.save(new Role(roleName)));
    }

    @BeforeEach
    void setUp() {
        Role userRole = getOrCreateRole("USER");
        parent = new User();
        parent.setEmail("parent@example.com");
        parent.setPassword("password123");
        parent.setAddress("Gedimino pr. Vilnius");
        parent.setRoles(Set.of(userRole));
        parent = userRepository.save(parent);

        provider1 = new Provider();
        provider1.setName("Vilniaus IT Mokykla");
        provider1.setEmail("provider@example.com");
        provider1.setPassword("password123");
        provider1.setPhone("+37061234567");
        provider1.setWebsite("http://vilniausitmokykla.com");
        provider1.setDescription("Teikiame profesionalias IT pamokas vaikams ir suaugusiems.");
        provider1.setProviderType(ProviderType.COMPANY);
        provider1.setCompanyName("Vilniaus IT UAB");
        provider1.setCompanyCode("123456789");
        Role providerRole = getOrCreateRole("PROVIDER");
        provider1.setRoles(Set.of(providerRole));

        testChild = new ChildProfile();
        testChild.setName("Test Child");
        testChild.setBirthDate(LocalDate.of(2018, 5, 10));
        testChild.setGender(Gender.FEMALE);
        testChild.setMaxActivityDuration(5);
        testChild.setParent(parent);
        testChild.setPreferredDeliveryMethod(DeliveryMethod.ONSITE);
        testChild.setInterests(Set.of(ActivityCategory.MUSIC));
        testChild = childProfileRepository.save(testChild);

        provider1 = providerRepository.save(provider1);

        activity1.setTitle("Programavimo pamokos vaikams");
        activity1.setDescription("Išmokite programavimą per žaidimus ir interaktyvias užduotis.");
        activity1.setDescriptionChild("Vaikams skirta programavimo pamoka.");
        activity1.setCategory(ActivityCategory.CODING);
        activity1.setLocation("Vilniaus g. 1, Vilnius, 01100");
        activity1.setPrice(15.0);
        activity1.setPriceType(PriceType.MONTHLY);
        activity1.setDurationMinutes(60);
        activity1.setDeliveryMethod(DeliveryMethod.ONSITE);
        activity1.setImageUrl("http://example.com/image1.jpg");
        activity1.setProvider(provider1);
        activityRepository.save(activity1);

        activity2.setTitle("Gitaros pamokos");
        activity2.setDescription("Išmokite groti gitara su profesionaliu mokytoju.");
        activity2.setDescriptionChild("Vaikams skirta gitaros pamoka.");
        activity2.setCategory(ActivityCategory.MUSIC);
        activity2.setLocation("Gedimino pr. 10, Vilnius, 01100");
        activity2.setPrice(20.0);
        activity2.setPriceType(PriceType.MONTHLY);
        activity2.setDurationMinutes(90);
        activity2.setDeliveryMethod(DeliveryMethod.ONSITE);
        activity2.setImageUrl("http://example.com/image2.jpg");
        activity2.setProvider(provider1);
        activityRepository.save(activity2);

        activity3.setTitle("Šokių pamokos");
        activity3.setDescription("Šokite kartu su draugais ir mokytojais pagal šiuolaikinius ritmus.");
        activity3.setDescriptionChild("Vaikams skirta šokių pamoka.");
        activity3.setCategory(ActivityCategory.DANCE);
        activity3.setLocation("Maironio g. 5, Vilnius, 01110");
        activity3.setPrice(25.0);
        activity3.setPriceType(PriceType.MONTHLY);
        activity3.setDurationMinutes(5);
        activity3.setDeliveryMethod(DeliveryMethod.ONSITE);
        activity3.setImageUrl("http://example.com/image3.jpg");
        activity3.setProvider(provider1);
        activityRepository.save(activity3);

        activityInteraction = new ActivityInteraction();
        activityInteraction.setChild(testChild);
        activityInteraction.setActivity(activity1);
        activityInteraction = activityInteractionRepository.save(activityInteraction);
        createdInteractionIds.add(activityInteraction.getId());
    }

    @AfterEach
    void tearDown() {
        for (Activity activity : List.of(activity1, activity2, activity3)) {
            List<ActivityInteraction> interactions = activityInteractionRepository.findAllByActivityId(activity.getId());
            activityInteractionRepository.deleteAll(interactions);
        }
        activityRepository.deleteAll(List.of(activity1, activity2, activity3));
        if (provider1 != null && providerRepository.existsById(provider1.getId())) {
            providerRepository.deleteById(provider1.getId());
        }
        if (testChild != null && childProfileRepository.existsById(testChild.getId())) {
            childProfileRepository.deleteById(testChild.getId());
        }
        if (parent != null && userRepository.existsById(parent.getId())) {
            userRepository.deleteById(parent.getId());
        }
    }

    @Test
    void testContentBasedRecommendations() throws Exception {
        mockMvc.perform(get("/api/recommendations/content-based/{childId}", testChild.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", not(empty())))
                .andExpect(jsonPath("$[*].title", hasItem("Gitaros pamokos")));
    }

    @Test
    void testNearbyRecommendations() throws Exception {
        mockMvc.perform(get("/api/recommendations/nearby/{childId}", testChild.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", not(empty())));
    }

    @Test
    void testCollaborativeRecommendations_noInteractions() throws Exception {
        mockMvc.perform(get("/api/recommendations/collaboration-filtering/{childId}", testChild.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void testCollaborativeRecommendations_withInteractions() throws Exception {
        activityInteractionService.recordInteraction(testChild.getId(), activity1.getId(), InteractionType.VIEW);
        ActivityInteraction ai1 = activityInteractionRepository.findByChildAndActivity(testChild, activity1).orElse(null);
        if (ai1 != null) createdInteractionIds.add(ai1.getId());

        activityInteractionService.recordInteraction(testChild.getId(), activity2.getId(), InteractionType.FAVORITE);
        ActivityInteraction ai2 = activityInteractionRepository.findByChildAndActivity(testChild, activity2).orElse(null);
        if (ai2 != null) createdInteractionIds.add(ai2.getId());

        mockMvc.perform(get("/api/recommendations/collaboration-filtering/{childId}", testChild.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void testFavoriteAndUnfavoriteInteraction() throws Exception {
        activityInteractionService.recordInteraction(testChild.getId(), activity3.getId(), InteractionType.FAVORITE);
        ActivityInteraction interaction = activityInteractionRepository.findByChildAndActivity(testChild, activity3).orElse(null);
        assertNotNull(interaction);
        assertTrue(interaction.isFavorited());

        activityInteractionService.toggleFavorite(testChild.getId(), activity3.getId());
        ActivityInteraction toggled = activityInteractionRepository.findByChildAndActivity(testChild, activity3).orElse(null);
        assertNotNull(toggled);
        assertFalse(toggled.isFavorited());
    }
}
