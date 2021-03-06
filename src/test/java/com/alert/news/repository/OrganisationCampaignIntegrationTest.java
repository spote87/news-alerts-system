package com.alert.news.repository;

import com.alert.news.TestConfiguration;
import com.alert.news.model.OrganisationCampaign;
import org.junit.*;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Shivaji Pote
 */
public class OrganisationCampaignIntegrationTest extends TestConfiguration {

    /**
     * Organisation_Campaign table constant
     */
    private static final String ORGANISATION_CAMPAIGN_TABLE = "Organisation_Campaign";

    @Autowired
    private OrganisationCampaignRepository campaignRepository;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void createTables() {
        CassandraUtils.executeQuery(getCreateTableQuery());
    }

    private String getCreateTableQuery() {
        final StringBuilder query = new StringBuilder();
        query.append("CREATE TABLE IF NOT EXISTS NewsAlerts.").append(ORGANISATION_CAMPAIGN_TABLE).append("(id bigint primary key,organisation_name text,campaign_title text,campaign_description text,tagged_categories list<text>,status text) WITH comment = 'Organization campaigns';");
        return query.toString();
    }

    @After
    public void dropTable() {
        CassandraUtils.dropTable(ORGANISATION_CAMPAIGN_TABLE);
    }

    @Test
    public void testInsertCampaignDetailsIntoDatabase() {
        final OrganisationCampaign campaign = new OrganisationCampaign("Test Org", "Test campaign",
                "Test campaign description", Arrays.asList("Finance", "Policies"));
        campaignRepository.insert(campaign);

        Assert.assertNotNull(campaignRepository.findById(1L));
    }

    @Test
    public void testFindByStatusNewReturnsListOfNewOrganisations() {
        final OrganisationCampaign campaign = new OrganisationCampaign("Org1", "Ttile 1", "Description 1", Arrays.asList("Finance", "LIC"));
        campaignRepository.insert(campaign);
        // CassandraUtils.executeQuery("CREATE INDEX ON NewsAlerts.Organisation_Campaign (status)");
        final List<OrganisationCampaign> newCampaign = campaignRepository.findByStatus("new");
        assertNotNull(newCampaign);
        assertEquals(1, newCampaign.size());
    }

    @Test
    public void testFindByStatusNotNewShouldReturnEmptyList() {
        final OrganisationCampaign campaign = new OrganisationCampaign("Org1", "Ttile 1", "Description 1", Arrays.asList("finance", "LIC"));
        campaignRepository.insert(campaign);
        // CassandraUtils.executeQuery("CREATE INDEX ON NewsAlerts.Organisation_Campaign (status)");
        final List<OrganisationCampaign> newCampaign = campaignRepository.findByStatus("Not New");
        assertNotNull(newCampaign);
        assertEquals(0, newCampaign.size());
    }
}
