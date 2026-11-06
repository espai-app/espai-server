
package app.espai.businesslogic;

import java.io.IOException;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpJdkSolrClient;

/**
 *
 * @author rborowski
 */
public class ProductionSearchIndex {

  private static final String COLLECTION = "productions";

  public void reindex() throws IOException, SolrServerException {

    String urlString = "http://localhost:8983/solr/bigboxstore";
    HttpJdkSolrClient solr = new HttpJdkSolrClient.Builder(urlString).build();

    // clear collection
    solr.deleteByQuery(COLLECTION, "*:*");

    // get production meta fields

    // query all productions

    // index all productions

  }

}
