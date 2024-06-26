package presentation.controller.api;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.demo.domain.model.Users;
import com.example.demo.domain.service.UsersService;
import com.example.demo.dto.api.request.UsersRequest;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/api/v1/users")
public class UsersController {
  @Autowired
  private HttpServletRequest request;
  private UsersService usersService;

  @GetMapping
  public List<Users> getAllUsers() {
    return usersService.getAllUsers();
  }

  @GetMapping("/{id}")
  public ResponseEntity<Users> getUserById(@PathVariable Long id) {
    String url = request.getRequestURL()
        .toString();
    log.info("リクエストURL：{url}    パラメータ:ID： {}", id);
    Users users = usersService.getUserById(id);
    return ResponseEntity.ok(users);
  }

  @PostMapping
  public void postUsers(@RequestBody UsersRequest usersRequest) {
    usersService.postUser(usersRequest);
  }

  @PatchMapping("/{id}")
  public ResponseEntity<Users> updateUser(@PathVariable Long id,
      @RequestBody UsersRequest usersRequest) {
    Users updatedUser = usersService.updateUser(id, usersRequest);
    return ResponseEntity.ok(updatedUser);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
    usersService.deleteByID(id);
    return ResponseEntity.noContent()
        .build();
  }

}
