package edu.cnm.deepdive.qodclient.model;

import java.net.URI;
import java.util.Date;
import java.util.UUID;

public class Source {

  private UUID id;

  private Date created;

  private String name;

  private URI href;

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public Date getCreated() {
    return created;
  }

  public void setCreated(Date created) {
    this.created = created;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public URI getHref() {
    return href;
  }

  public void setHref(URI href) {
    this.href = href;
  }

}
