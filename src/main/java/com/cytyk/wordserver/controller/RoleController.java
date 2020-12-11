import com.cytyk.wordserver.controller;

@RequestMapping("/api/wordserver/role")
@RestController
public class RoleController{


  @GetMapping("/get")
  public String get(String userId) {
    return "admin";
  }

}
