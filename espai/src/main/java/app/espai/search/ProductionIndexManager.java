package app.espai.search;

import app.espai.dao.ProductionMetaFields;
import app.espai.dao.ProductionMetaValues;
import app.espai.dao.Productions;
import app.espai.events.ItemChangeEvent;
import app.espai.filter.ProductionFilter;
import app.espai.filter.ProductionMetaFieldFilter;
import app.espai.filter.ProductionMetaValueFilter;
import app.espai.model.FieldType;
import app.espai.model.Production;
import app.espai.model.ProductionMetaField;
import app.espai.model.ProductionMetaValue;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.enterprise.event.Observes;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpJdkSolrClient;
import org.apache.solr.client.solrj.request.SolrQuery;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.CommonParams;

/**
 *
 * @author rborowski
 */
@Stateless
public class ProductionIndexManager {

  private List<FieldType> FULL_TEXT_TYPES = List.of(
          FieldType.MULTILINE, FieldType.RICHTEXT, FieldType.TEXT);

  @EJB
  private Productions productions;

  @EJB
  private ProductionMetaFields metaFields;

  @EJB
  private ProductionMetaValues metaValues;

  private String collectionName = "productions";
  private String allFields = "allFields_txt";

  public SearchResult<Production> search(SearchQuery searchQuery) throws IOException {
    try (SolrClient client = connect()) {

      ProductionMetaFieldFilter facetFilter = new ProductionMetaFieldFilter();
      facetFilter.setFacet(Boolean.TRUE);
      List<ProductionMetaField> facetList = metaFields.list(facetFilter).getItems();

      SolrQuery solrQuery = new SolrQuery();
      solrQuery.setFacet(true);

      solrQuery.addField("id");
      solrQuery.addField("title_s");
      solrQuery.addField("subtitle_s");
      solrQuery.addField("durationInMinutes_i");
      solrQuery.addField("rating_i");
      solrQuery.addField("recommendedAge_i");

      for (ProductionMetaField f : facetList) {
        solrQuery.addFacetField(toFacetName(f));
      }

      List<String> qBuilder = new LinkedList<>();
      if (searchQuery.getKeyword() != null && !searchQuery.getKeyword().isBlank()) {
        qBuilder.add("allFields_txt:" + ClientUtils.escapeQueryChars(searchQuery.getKeyword()));
      }

      for (SearchFacet f : searchQuery.getFacets()) {
        for (String v : f.getSelected()) {
          qBuilder.add(f.getName() + ":\"" + ClientUtils.escapeQueryChars(v) + "\"");
        }
      }

      String q = String.join(" AND ", qBuilder.toArray(new String[qBuilder.size()]));
      if (q.isBlank()) {
        q = "*:*";
      }
      solrQuery.set(CommonParams.Q, q);
      solrQuery.setStart(searchQuery.getOffset());
      solrQuery.setRows(searchQuery.getLimit());
      solrQuery.setSort("title_s", SolrQuery.ORDER.asc);

      QueryResponse response = client.query(collectionName, solrQuery);
      return convert(response, searchQuery);

    } catch (SolrServerException ex) {
      throw new IOException(ex);
    }
  }

  public void reindex() throws IOException {
    try (SolrClient client = connect()) {

      client.deleteByQuery(collectionName, "*:*");
      client.commit(collectionName);

      ProductionFilter productionFilter = new ProductionFilter();
      List<Production> productionList = productions.list(productionFilter).getItems();

      List<SolrInputDocument> request = new LinkedList<>();
      for (Production p : productionList) {
        request.add(convert(p));

        if (request.size() == 100) {
          client.add(collectionName, request);
          request = new LinkedList<>();
        }
      }

      if (request.size() > 0) {
        client.add(collectionName, request);
      }

      client.commit(collectionName);

    } catch (SolrServerException ex) {
      throw new IOException(ex);
    }
  }

  public void onProductionUpdated(@Observes ItemChangeEvent event) {

    // ignore non production related events
    if (!event.isInstanceOf(Production.class)) {
      return;
    }

    try {
      // get meta values
      Production production = (Production) event.getDataItem();

      // execute index changes
      switch (event.getType()) {
        case UPDATED:
          update(production);
          break;
        case CREATED:
          create(production);
          break;
        case DELETED:
          delete(production);
          break;
      }
    } catch (IOException ex) {

    }
  }

  private SolrClient connect() {
    return new HttpJdkSolrClient.Builder("http://localhost:8983/solr")
            .withConnectionTimeout(10000, TimeUnit.MILLISECONDS)
            .withIdleTimeout(60000, TimeUnit.MILLISECONDS)
            .build();
  }

  private void create(Production production) throws IOException {
    try (SolrClient client = connect()) {
      client.add(collectionName, convert(production));
      client.commit(collectionName);
    } catch (SolrServerException ex) {
      throw new IOException(ex);
    }
  }

  private void update(Production production) throws IOException {
    try (SolrClient client = connect()) {
      client.add(collectionName, convert(production));
      client.commit(collectionName);
    } catch (SolrServerException ex) {
      throw new IOException(ex);
    }
  }

  private void delete(Production production) throws IOException {
    try (SolrClient client = connect()) {
      client.deleteById(collectionName, String.valueOf(production.getId()));
      client.commit(collectionName);
    } catch (SolrServerException ex) {
      throw new IOException(ex);
    }
  }

  private SolrInputDocument convert(Production production) {

    // get meta values
    ProductionMetaValueFilter valueFilter = new ProductionMetaValueFilter();
    valueFilter.setProduction(production);
    List<ProductionMetaValue> valueList = metaValues.list(valueFilter).getItems();

    StringBuilder fulltextIndex = new StringBuilder();

    SolrInputDocument doc = new SolrInputDocument();
    doc.setField("id", String.valueOf(production.getId()));
    doc.setField("title_s", production.getTitle());
    fulltextIndex.append(" ").append(production.getTitle());

    if (production.getSubtitle() != null) {
      doc.setField("subtitle_s", production.getSubtitle());
      fulltextIndex.append(" ").append(production.getSubtitle());
    }

    if (production.getDescription() != null && !production.getDescription().isBlank()) {
      fulltextIndex.append(" ").append(production.getDescription());
    }

    if (production.getDurationInMinutes() != null) {
      doc.setField("durationInMinutes_i", production.getDurationInMinutes());
    }

    if (production.getRating() != null) {
      doc.setField("rating_i", production.getRating());
    }

    if (production.getRecommendedAge() != null) {
      doc.setField("recommendedAge_i", production.getRecommendedAge());
    }

    for (ProductionMetaValue v : valueList) {
      ProductionMetaField field = v.getProductionMetaField();

      if (!field.isIndexed() && !field.isFacet()) {
        continue;
      }

      if (v.getValue() == null || v.getValue().isBlank()) {
        continue;
      }

      if (field.getFieldType() == FieldType.TAG) {
        List<String> tags = (List<String>) v.getFormattedValue();
        fulltextIndex.append(" ")
                .append(String.join(" ", tags.toArray(new String[tags.size()])));

        doc.setField(toFacetName(field), tags.stream().map(t -> t.trim()).toList());
      } else {
        doc.setField(toFacetName(field), v.getFormattedValue());
      }

      if (FULL_TEXT_TYPES.contains(field.getFieldType())) {
        fulltextIndex.append(" ").append(v.getValue());
      }
    }

    doc.setField(allFields, fulltextIndex.toString());

    return doc;
  }

  public String toFacetName(ProductionMetaField field) {
    switch (field.getFieldType()) {
      case BOOLEAN:
        return field.getName() + "_b";
      case DATE:
      case DATETIME:
        return field.getName() + "_dt";
      case DECIMAL:
        return field.getName() + "_f";
      case INTEGER:
        return field.getName() + "_i";
      case MULTILINE:
      case RICHTEXT:
        return field.getName() + "_txt";
      case TEXT:
        return field.getName() + "_s";
      case TAG:
        return field.getName() + "_ss";
      default:
        return field.getName() + "_s";
    }
  }

  private SearchResult convert(QueryResponse response, SearchQuery originalQuery) {
    SearchResult<Production> result = new SearchResult<>();

    SolrDocumentList docs = response.getResults();
    result.setTotal(docs.getNumFound());

    List<Production> items = new LinkedList<>();
    result.setItems(items);
    for (SolrDocument d : docs) {
      Production p = new Production();
      p.setId(Long.parseLong((String) d.getFieldValue("id")));
      p.setTitle((String) d.getFieldValue("title_s"));

      if (d.containsKey("subtitle_s")) {
        p.setSubtitle((String) d.getFieldValue("subtitle_s"));
      }

      if (d.containsKey("durationInMinutes_i")) {
        p.setDurationInMinutes((Integer) d.getFieldValue("durationInMinutes_i"));
      }

      if (d.containsKey("rating_i")) {
        p.setRating((Integer) d.getFieldValue("rating_i"));
      }

      if (d.containsKey("recommendedAge_i")) {
        p.setRecommendedAge((Integer) d.getFieldValue("recommendedAge_i"));
      }

      items.add(p);
    }

    SearchQuery searchQuery = new SearchQuery();
    result.setSearchQuery(searchQuery);
    searchQuery.setKeyword(originalQuery.getKeyword());
    searchQuery.setOffset(originalQuery.getOffset());
    searchQuery.setLimit(originalQuery.getLimit());

    List<SearchFacet> searchFacets = new LinkedList<>();
    searchQuery.setFacets(searchFacets);

    Map<String, List<String>> selectedFacets = originalQuery.getFacets().stream()
            .map(f -> new AbstractMap.SimpleEntry<String, List<String>>(
            f.getName(), f.getSelected()))
            .collect(Collectors.toMap(m -> m.getKey(), m -> m.getValue()));

    for (FacetField f : response.getFacetFields()) {
      SearchFacet rf = new SearchFacet();
      rf.setName(f.getName());
      rf.setLabel(originalQuery.getFacets().stream()
              .filter(of -> of.getName().equals(f.getName()))
              .map(of -> of.getLabel())
              .findFirst()
              .get());
      rf.setAvailable(new LinkedList<>());
      rf.setSelected(new LinkedList<>());
      for (Count c : f.getValues()) {
        SearchFacetValue rv = new SearchFacetValue();
        rv.setName(c.getName());
        rv.setCount(c.getCount());
        rf.getAvailable().add(rv);

        if (selectedFacets.containsKey(f.getName())
                && selectedFacets.get(f.getName()).contains(c.getName())) {
          rf.getSelected().add(c.getName());
        }
      }

      searchQuery.getFacets().add(rf);
    }

    return result;
  }

}
