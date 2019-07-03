package edu.cnm.deepdive.qodclient.model;

import java.net.URI;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class Quote {

  private static final String UNKNOWN_SOURCE = "(unknown)";
  protected static final String DELIMITER = ", ";

  private UUID id;

  private String text;

  private Date created;

  private URI href;

  private List<Source> sources = new LinkedList<>();

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public Date getCreated() {
    return created;
  }

  public void setCreated(Date created) {
    this.created = created;
  }

  public URI getHref() {
    return href;
  }

  public void setHref(URI href) {
    this.href = href;
  }

  public List<Source> getSources() {
    return sources;
  }

  public void setSources(List<Source> sources) {
    this.sources = sources;
  }

  public String getCombinedSources() {
    StringBuilder builder = new StringBuilder(" \u2014 "); // TODO Extract.
    if (sources.isEmpty()) {
      builder.append(UNKNOWN_SOURCE);
    } else {
      for (Source source : sources) {
        builder
            .append(source.getName())
            .append(DELIMITER);
      }
      builder.delete(builder.length() - DELIMITER.length(), builder.length());
    }
    return builder.toString();
  }

}
