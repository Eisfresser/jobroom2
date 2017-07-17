package ch.admin.seco.jobroom.cucumber.stepdefs;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.ResultActions;

import ch.admin.seco.jobroom.JobroomApp;

@WebAppConfiguration
@SpringBootTest
@ContextConfiguration(classes = JobroomApp.class)
public abstract class StepDefs {

    protected ResultActions actions;

}
