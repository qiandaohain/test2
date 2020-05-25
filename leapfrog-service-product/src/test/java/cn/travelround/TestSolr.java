package cn.travelround;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by travelround on 2019/4/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:application-context.xml"})
public class TestSolr {

    @Autowired
    private SolrServer solrServer;
    @Test
    public void testSolrJSpring() throws Exception {
        SolrInputDocument doc = new SolrInputDocument();
        doc.setField("id", 4);
        doc.setField("name", "钢铁侠");
        solrServer.add(doc);
        solrServer.commit();
    }

    @Test
    public void testSolrJ() throws Exception {
        String baseUrl = "http://172.16.52.214:8080/solr";
        HttpSolrServer solrServer = new HttpSolrServer(baseUrl);

        SolrInputDocument doc = new SolrInputDocument();
        doc.setField("id", 3);
        doc.setField("name", "范冰冰");
        solrServer.add(doc);
        solrServer.commit();
    }

}
