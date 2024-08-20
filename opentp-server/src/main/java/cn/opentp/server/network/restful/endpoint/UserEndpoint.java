package cn.opentp.server.network.restful.endpoint;

import cn.opentp.server.network.restful.http.ResponseEntity;
import cn.opentp.server.network.restful.annotation.*;
import cn.opentp.server.network.restful.http.HttpStatus;

import java.util.List;

//默认为单例，singleton = false表示启用多例。
//@RestController(singleton = false)
@RestController
@RequestMapping("/users")
public class UserEndpoint {

    @GetMapping("")
    public ResponseEntity<List<String>> listUser() {
        // 查询用户
        List<String> users = List.of("a", "b", "c");
        return ResponseEntity.ok().build(users);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> putMethod(@PathVariable("id") int id, @RequestBody String body) {
        // 更新用户
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

//    @DeleteMapping("/{id}")
//    public ResponseEntity<?> deleteMethod(@PathVariable int id) {
//        // 删除用户
//        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
//    }
//
//    @PostMapping("")
//    public ResponseEntity<?> postMethod(@RequestBody String body, @RequestHeader String a) {
//        // 添加用户
//        JSONObject json = JSONObject.parseObject(body);
//        User user = new User();
//        user.setId(json.getIntValue("id"));
//        user.setName(json.getString("name"));
//        user.setAge(json.getShortValue("age"));
//        return ResponseEntity.status(HttpStatus.CREATED).build(user);
//    }

}
