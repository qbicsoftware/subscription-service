package life.qbic.subscriptions.subscriptions.entities;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Schema
@Entity
@Table(name = "person")
public class Person {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  Integer id;

  @Column(name = "user_id")
  String userId;

  @Column(name = "first_name")
  String firstName;

  @Column(name = "last_name")
  String lastName;

  @Column(name = "title")
  String title;

  @Column(name = "email")
  String email;

  @Column(name = "active")
  Integer active;

  @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @JoinColumn(name = "user_id")
  List<Subscription> subscriptions;

  public Person() {
  }

  public Person(String userId, String firstName, String lastName, String title, String email,
      Integer active) {
    this.userId = userId;
    this.firstName = firstName;
    this.lastName = lastName;
    this.title = title;
    this.email = email;
    this.active = active;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  String getFirstName() {
    return firstName;
  }

  void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  String getLastName() {
    return lastName;
  }

  void setLastName(String lastName) {
    this.lastName = lastName;
  }

  String getTitle() {
    return title;
  }

  void setTitle(String title) {
    this.title = title;
  }

  String getEmail() {
    return email;
  }

  void setEmail(String email) {
    this.email = email;
  }

  Integer getActive() {
    return active;
  }

  void setActive(Integer active) {
    this.active = active;
  }

  public List<Subscription> getSubscriptions() {
    return subscriptions;
  }

  public void setSubscriptions(
      List<Subscription> subscriptions) {
    this.subscriptions = subscriptions;
  }

  @Override
  public String toString() {
    return "Person{" +
        "id=" + id +
        ", userId='" + userId + '\'' +
        ", firstName='" + firstName + '\'' +
        ", lastName='" + lastName + '\'' +
        ", title='" + title + '\'' +
        ", email='" + email + '\'' +
        ", active=" + active +
        ", subscriptions=" + subscriptions +
        '}';
  }
}

