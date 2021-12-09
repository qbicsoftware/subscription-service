package life.qbic.subscriptions;

import java.util.Optional;

public interface ProjectRepository {

  Optional<Project> findProject(String code) ;

}
