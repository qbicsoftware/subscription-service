package life.qbic.subscriptions.subscriptions.entities;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Schema
@Entity
@Table(name = "subscriptions")
public class Subscription {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private int id;

  @Column(name = "project_code")
  private String projectCode;

  @ManyToOne(
      fetch = FetchType.LAZY,
      cascade = {CascadeType.DETACH, CascadeType.MERGE,
          CascadeType.REFRESH, CascadeType.PERSIST})
  @JoinColumn(name = "person_id")
  private Person person;

  public Subscription() {}

  public Subscription(String projectCode) {
    this.projectCode = projectCode;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getProjectCode() {
    return projectCode;
  }

  public void setProjectCode(String project) {
    this.projectCode = project;
  }

  public Person getPerson() {
    return person;
  }

  public void setPerson(Person person) {
    this.person = person;
  }

  @Override
  public String toString() {
    return "Subscription{" +
        "id=" + id +
        ", project='" + projectCode + '\'' +
        '}';
  }
}
