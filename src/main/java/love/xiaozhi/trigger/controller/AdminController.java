package love.xiaozhi.trigger.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 管理端控制器
 *
 * @author Jason Zong
 * @since 2024/9/26
 */
@Controller
public class AdminController {

    @GetMapping("/api/tp")
    public String tp() {
        // 重定向到静态资源目录下的index.html
        return "redirect:/index.html";
    }

}
