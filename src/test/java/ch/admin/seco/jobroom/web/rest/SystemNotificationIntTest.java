package ch.admin.seco.jobroom.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import ch.admin.seco.jobroom.domain.SystemNotification;
import ch.admin.seco.jobroom.repository.SystemNotificationRepository;
import ch.admin.seco.jobroom.service.SystemNotificationService;
import ch.admin.seco.jobroom.service.dto.SystemNotificationDTO;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SystemNotificationIntTest {

    private static final String DEFAULT_TITLE = "SYSTEM NOTIFICATION";
    private static final String UPDATED_TITLE = "SYSTEM NOTIFICATION";

    private static final String DEFAULT_TEXT_DE = "SYSTEM NOTIFICATION_DE";
    private static final String UPDATED_TEXT_DE = "SYSTEM NOTIFICATION_DE2";

    private static final String DEFAULT_TEXT_FR = "SYSTEM NOTIFICATION_FR";
    private static final String UPDATED_TEXT_FR = "SYSTEM NOTIFICATION_FR2";

    private static final String DEFAULT_TEXT_IT = "SYSTEM NOTIFICATION_IT";
    private static final String UPDATED_TEXT_IT = "SYSTEM NOTIFICATION_IT2";

    private static final String DEFAULT_TEXT_EN = "SYSTEM NOTIFICATION_EN";
    private static final String UPDATED_TEXT_EN = "SYSTEM NOTIFICATION_EN2";

    private static final String DEFAULT_TYPE = "INFO";
    private static final String UPDATED_TYPE = "INFO";

    private static final LocalDateTime DEFAULT_STARTDATE = LocalDateTime.parse("2099-01-01T14:00");
    private static final LocalDateTime UPDATED_STARTDATE = LocalDateTime.parse("2099-01-01T14:00");

    private static final LocalDateTime DEFAULT_ENDDATE = LocalDateTime.parse("2099-01-01T16:00");
    private static final LocalDateTime UPDATED_ENDDATE = LocalDateTime.parse("2099-01-01T16:00");

    private static final Boolean DEFAULT_ACTIVE = false;
    private static final Boolean UPDATED_ACTIVE = true;

    @Autowired
    private SystemNotificationRepository systemNotificationRepository;

    @Autowired
    private SystemNotificationService systemNotificationService;

    private MockMvc restSystemNotificationMockMvc;

    private SystemNotification systemNotification;


    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        SystemNotificationResource systemNotificationResource = new SystemNotificationResource(systemNotificationService);
        this.restSystemNotificationMockMvc = MockMvcBuilders.standaloneSetup(systemNotificationResource).build();
        systemNotificationRepository.deleteAll();
        systemNotification = createEntity();
    }

    public SystemNotification createEntity() {
        SystemNotification systemNotification = new SystemNotification();
        systemNotification.setTitle(DEFAULT_TITLE);
        systemNotification.setText_de(DEFAULT_TEXT_DE);
        systemNotification.setText_fr(DEFAULT_TEXT_FR);
        systemNotification.setText_it(DEFAULT_TEXT_IT);
        systemNotification.setText_en(DEFAULT_TEXT_EN);
        systemNotification.setType(DEFAULT_TYPE);
        systemNotification.setActive(DEFAULT_ACTIVE);
        systemNotification.setStartDate(DEFAULT_STARTDATE);
        systemNotification.setEndDate(DEFAULT_ENDDATE);
        return systemNotification;
    }

    @Test
    @Transactional
    public void getAllSystemNotifications() throws Exception {
        // Initialize the database
        systemNotificationRepository.saveAndFlush(systemNotification);

        // Get all the organizationList
        restSystemNotificationMockMvc.perform(get("/api/system-notifications"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(systemNotification.getId().toString())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].text_de").value(hasItem(DEFAULT_TEXT_DE)))
            .andExpect(jsonPath("$.[*].text_fr").value(hasItem(DEFAULT_TEXT_FR)))
            .andExpect(jsonPath("$.[*].text_it").value(hasItem(DEFAULT_TEXT_IT)))
            .andExpect(jsonPath("$.[*].text_en").value(hasItem(DEFAULT_TEXT_EN)))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE)))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE.booleanValue())));
    }

    @Test
    @Transactional
    public void getSystemNotificationById() throws Exception {
        systemNotificationRepository.saveAndFlush(systemNotification);
        restSystemNotificationMockMvc.perform(get("/api/system-notifications/{id}", systemNotification.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(systemNotification.getId().toString()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.text_de").value(DEFAULT_TEXT_DE))
            .andExpect(jsonPath("$.text_fr").value(DEFAULT_TEXT_FR))
            .andExpect(jsonPath("$.text_it").value(DEFAULT_TEXT_IT))
            .andExpect(jsonPath("$.text_en").value(DEFAULT_TEXT_EN))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE))
            .andExpect(jsonPath("$.active").value(DEFAULT_ACTIVE.booleanValue()));
    }

    @Test
    @Transactional
    public void createSystemNotification() throws Exception {
        int databaseSizeBeforeCreate = Long.valueOf(systemNotificationRepository.count()).intValue();

        SystemNotificationDTO systemNotificationDTO = SystemNotificationDTO.toDto(systemNotification);

        restSystemNotificationMockMvc.perform(post("/api/system-notifications")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(systemNotificationDTO)))
            .andExpect(status().isOk());

        List<SystemNotification> systemNotificationList = systemNotificationRepository.findAll();
        assertThat(systemNotificationList).hasSize(databaseSizeBeforeCreate + 1);
        SystemNotification testSystemNotification = systemNotificationList.get(systemNotificationList.size() - 1);
        assertThat(testSystemNotification.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testSystemNotification.getText_de()).isEqualTo(DEFAULT_TEXT_DE);
        assertThat(testSystemNotification.getText_fr()).isEqualTo(DEFAULT_TEXT_FR);
        assertThat(testSystemNotification.getText_it()).isEqualTo(DEFAULT_TEXT_IT);
        assertThat(testSystemNotification.getText_en()).isEqualTo(DEFAULT_TEXT_EN);
        assertThat(testSystemNotification.getType()).isEqualTo(DEFAULT_TYPE);
        assertThat(testSystemNotification.getStartDate()).isEqualTo(DEFAULT_STARTDATE);
        assertThat(testSystemNotification.getEndDate()).isEqualTo(DEFAULT_ENDDATE);
        assertThat(testSystemNotification.isActive()).isEqualTo(DEFAULT_ACTIVE);
    }

    @Test
    @Transactional
    public void updateSystemNotification() throws Exception {
        systemNotificationRepository.saveAndFlush(systemNotification);
        systemNotificationRepository.save(systemNotification);
        int databaseSizeBeforeUpdate = Long.valueOf(systemNotificationRepository.count()).intValue();

        SystemNotification updatedSystemNotification = systemNotificationRepository.getOne(systemNotification.getId());
        updatedSystemNotification.setTitle(UPDATED_TITLE);
        updatedSystemNotification.setText_de(UPDATED_TEXT_DE);
        updatedSystemNotification.setText_fr(UPDATED_TEXT_FR);
        updatedSystemNotification.setText_it(UPDATED_TEXT_IT);
        updatedSystemNotification.setText_en(UPDATED_TEXT_EN);
        updatedSystemNotification.setType(UPDATED_TYPE);
        updatedSystemNotification.setStartDate(UPDATED_STARTDATE);
        updatedSystemNotification.setEndDate(UPDATED_ENDDATE);
        updatedSystemNotification.setActive(UPDATED_ACTIVE);
        SystemNotificationDTO systemNotificationDTO = SystemNotificationDTO.toDto(updatedSystemNotification);

        restSystemNotificationMockMvc.perform(patch("/api/system-notifications")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(systemNotificationDTO)))
            .andExpect(status().isOk());

        List<SystemNotification> systemNotificationList = systemNotificationRepository.findAll();
        assertThat(systemNotificationList).hasSize(databaseSizeBeforeUpdate);
        SystemNotification testSystemNotification = systemNotificationList.get(systemNotificationList.size() - 1);
        assertThat(testSystemNotification.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testSystemNotification.getText_de()).isEqualTo(UPDATED_TEXT_DE);
        assertThat(testSystemNotification.getText_fr()).isEqualTo(UPDATED_TEXT_FR);
        assertThat(testSystemNotification.getText_it()).isEqualTo(UPDATED_TEXT_IT);
        assertThat(testSystemNotification.getText_en()).isEqualTo(UPDATED_TEXT_EN);
        assertThat(testSystemNotification.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testSystemNotification.getStartDate()).isEqualTo(UPDATED_STARTDATE);
        assertThat(testSystemNotification.getEndDate()).isEqualTo(UPDATED_ENDDATE);
        assertThat(testSystemNotification.isActive()).isEqualTo(UPDATED_ACTIVE);

    }

    @Test
    @Transactional
    public void deleteSystemNotification() throws Exception {
        systemNotificationRepository.saveAndFlush(systemNotification);
        systemNotificationRepository.save(systemNotification);
        int databaseSizeBeforeDelete = systemNotificationRepository.findAll().size();

        restSystemNotificationMockMvc.perform(delete("/api/system-notifications/{id}", systemNotification.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        List<SystemNotification> systemNotificationList = systemNotificationRepository.findAll();
        assertThat(systemNotificationList).hasSize(databaseSizeBeforeDelete - 1);
    }

}
